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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassificadorService{

    private static final String FILE_DIR = "D:\\sos\\documentos\\";
    Logger logger = Logger.getLogger(ClassificadorService.class);

    public ClassificadorService() {
        logger.setLevel(Level.ERROR);
    }

    public static List<String> listarArquivos() {

        List<File> files = Objects.requireNonNull(Arrays.stream(new File(FILE_DIR).listFiles()).toList());
        List<String> arquivos = new ArrayList<>();
        files.stream().filter(file -> file.isFile()).forEach(file -> arquivos.add(file.getName()));
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

                int valido;
                String texto = buscarDocumento(doc);
                for (TipologiaDocumento tipo: listaDistancia ) {

                    String encontrado = null;
                    try {

                        String[] linhaTexto = texto.split("\r\n");
                        String[] palavrasChave = tipo.getDsPalavraChave().split(",");

                        if(!texto.isBlank() && !texto.isEmpty() && texto.contains(palavrasChave[0])) {

                            valido = 0;
                            for (int i = 0; i < 10; i++) {
                                for (String chave : palavrasChave) {
                                    if (linhaTexto[i].contains(chave)) {
                                        int index = linhaTexto[i].indexOf(chave);

                                        if(index == 0)
                                            valido += 1;
                                        break;
                                    }
                                }
                            }

                            if(valido == palavrasChave.length){
                                encontrado = doc + " # " + tipo.getNoTipo();
                                listaFinal.add(encontrado);
                                break;
                            }

                        }

                    } catch (Exception e) {
                        System.out.println("Comparando: " + doc + " # " + "##########" + Arrays.stream(e.getStackTrace()).toList());
                        logger.info(e.getMessage());
                    }

                    if(Objects.nonNull(encontrado)){
                        listaArquivo.remove(doc);
                    }

                }

        }

        return listaFinal.stream().sorted().collect(Collectors.toList());

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
