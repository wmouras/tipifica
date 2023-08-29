package com.br.sos;

import com.br.sos.service.ClassificadorService;

import java.util.List;

public class Classficador {

    public static void main(String[] args){

        final ClassificadorService service = new ClassificadorService();
        List<String> listaResultado = service.classificarDocumentos();

        for (String resultado: listaResultado){
            System.out.println(resultado);
        }
    }

}
