package com.br.sos.service;

import com.br.sos.entity.TipologiaDocumento;

public class TipoDocumentoService {

    public TipologiaDocumento identificarCapa(String linha){

        boolean matches = linha.matches("processo [A-Z]{2}");
        if(matches){
            TipologiaDocumento tipologiaDocumento = new TipologiaDocumento();
            tipologiaDocumento.setNoTipo("Capa");
            return tipologiaDocumento;
        }

        return null;
    }

    public TipologiaDocumento identificarRequerimento(String[] linhaTexto, TipologiaDocumento tipo){

        for (int i = 0; i < 10; i++) {
            String[] match = linhaTexto[i].split(" ");
            if(match[0].equals("requerimento")){
                System.out.println(linhaTexto[i]);
                return tipo;
            }
        }

        return null;
    }

}
