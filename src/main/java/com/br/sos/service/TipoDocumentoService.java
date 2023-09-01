package com.br.sos.service;

import com.br.sos.entity.TipologiaDocumento;

public class TipoDocumentoService {

    public TipologiaDocumento identificarCapa(String linha, TipologiaDocumento tipo){

        boolean matches = linha.matches("processo [A-Z]{2}");
        if(matches){
            return tipo;
        }

        return null;
    }

    public TipologiaDocumento identificarRequerimento(String[] linhaTexto, TipologiaDocumento tipo){

        if(linhaTexto.length < 10){
            return null;
        }

        for (int i = 0; i < 10; i++) {
            String[] match = linhaTexto[i].split(" ");
            if(match.length > 0 && match[0].equals("requerimento")){
                return tipo;
            }
        }

        return null;
    }

    public TipologiaDocumento identificarAnalise(String[] linhaTexto, TipologiaDocumento tipo){

        boolean matches = false;
        boolean contem = false;
        for (int i = 0; i < 5; i++) {
            if(!matches)
                matches = linhaTexto[i].matches("^(analise|análise) (.*)");

            if(!contem)
                contem = linhaTexto[i].contains("análise");

        }

        if(matches || contem){
            return tipo;
        }

        return null;
    }

}
