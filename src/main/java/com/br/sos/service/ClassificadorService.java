package com.br.sos.service;

import com.br.sos.entity.TipologiaDocumento;
import com.br.sos.repository.TipoDocumentoRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClassificadorService{

    private static final String FILE_DIR = "D:\\sos\\documentos\\";
    Logger logger = Logger.getLogger(ClassificadorService.class);

    public ClassificadorService() {
        logger.setLevel(Level.ERROR);
    }
    TipoDocumentoService service = new TipoDocumentoService();

    public static List<String> listarArquivos() {

        List<File> files = Objects.requireNonNull(Arrays.stream(Objects.requireNonNull(new File(FILE_DIR).listFiles())).toList());
        List<String> arquivos = new ArrayList<>();
        files.stream().filter(File::isFile).forEach(file -> arquivos.add(file.getName()));
        return arquivos;

    }

    public List<TipologiaDocumento> getListaTipoDocumento(){
        try {
            TipoDocumentoRepository repository = new TipoDocumentoRepository();
            return repository.findAll();
        }catch (SQLException se) {
            se.printStackTrace();
        }
        return new ArrayList<>();

    }

    public List<String> classificarDocumentos(){

        List<String> listaArquivo = listarArquivos();
        List<TipologiaDocumento> listaDistancia = getListaTipoDocumento();
        List<String> listaFinal = new ArrayList<>();

        for(String doc: listaArquivo){

            String texto = buscarDocumento(doc);

            if(doc.toLowerCase().contains("_mapa.pdf")){
                listaFinal.add(doc + " # " + "Mapa");
            } else {

                for (TipologiaDocumento tipo : listaDistancia) {

                    String[] linhaTexto = texto.split("\r\n");
                    String[] palavrasChave = tipo.getDsPalavraChave().split(",");

                    if("AA00146G20082838SOS_17_18_20052023_33512140149_NORMAL.pdf".equals(doc)){

                        boolean matches = texto.contains("anotações de responsabilidade tec");
                        if(matches){
                            listaFinal.add(doc + " # " + "Anotação de Responsabilidade Técnica");
                            texto = "";
                        }
                    }

                    if(tipo.getNuTipo().equals(7) && texto.toLowerCase().contains("analise") && linhaTexto.length > 5){
                        TipologiaDocumento tipoDoc = service.identificarAnalise(linhaTexto, tipo);

                        if(Objects.nonNull(tipoDoc)){
                            listaFinal.add(doc + " # " + tipoDoc.getNoTipo());
                            texto = "";
                        }
                    }

                    if(tipo.getNuTipo().equals(64) && texto.toLowerCase().contains("requerimento") && linhaTexto.length > 10){

                        TipologiaDocumento tipoDoc = service.identificarRequerimento(linhaTexto, tipo);
                        if(Objects.nonNull(tipoDoc)){
                            listaFinal.add(doc + " # " + tipoDoc.getNoTipo());
                            texto = "";
                        }
                    }

                    int valido = 0;
                    if (!texto.isEmpty() && texto.contains(palavrasChave[0])) {

                        if(classificarProcesso(linhaTexto, tipo)){
                            listaFinal.add(doc + " # " + tipo.getNoTipo());
                            texto = "";
                        }
                        valido = classificacaoSimples(linhaTexto, palavrasChave);
                        if(valido > 0){
                            listaFinal.add(doc + " # " + tipo.getNoTipo());
                            texto = "";
                        }
                    }

                }

            }
        }

        return listaFinal.stream().sorted().collect(Collectors.toList());

    }

    public int classificacaoSimples(String[] linhaTexto, String[] palavrasChave){

        for (int i = 0; i < 7; i++) {
            int valido = 0;
            for (String chave : palavrasChave) {

                if (linhaTexto[i].contains(chave)) {
                    int index = linhaTexto[i].indexOf(chave);
                    if (index == 0)
                        valido += 1;
                }
            }
            if (valido == palavrasChave.length){
                return valido;
            }

        }
        return -1;
    }

    public boolean classificarProcesso(String[] linhaTexto, TipologiaDocumento tipo){

        for (int i = 0; i < 7; i++) {

            if (linhaTexto[i].contains("processo") && tipo.getNuTipo().equals(690)) {
                TipologiaDocumento tipoDoc = service.identificarCapa(linhaTexto[i], tipo);
                if (Objects.nonNull(tipoDoc)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getDistancia(String texto, String PalavraChave){
        LevenshteinDistance distance = new LevenshteinDistance();
        distance.getThreshold();
        return distance.apply(texto.toLowerCase(), PalavraChave.toLowerCase());
    }

    public String buscarDocumento(String documento){
        try {

            PDFTextStripper stripper = new PDFTextStripper();
            PDFParser parser = new PDFParser(new RandomAccessFile(new File(FILE_DIR + documento), "r"));
            parser.parse();

            COSDocument cosDoc = parser.getDocument();
            PDDocument pdDoc = new PDDocument(cosDoc);
            String texto = stripper.getText(pdDoc).toLowerCase();
            pdDoc.getNumberOfPages();
            pdDoc.close();
            return texto;
        }catch (IOException e) {
            return "##########";
        }
    }

}
