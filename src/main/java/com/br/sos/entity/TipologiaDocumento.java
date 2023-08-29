package com.br.sos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipifica", schema = "EVENTUS")
public class TipologiaDocumento {

    @Id
    @Column(name = "id_tipologia_documento")
    private Integer id;

    @Column(name = "nu_tipo")
    private Integer nuTipo;

    @Column(name = "no_tipo")
    private String noTipo;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNuTipo() {
        return nuTipo;
    }

    public void setNuTipo(Integer nuTipo) {
        this.nuTipo = nuTipo;
    }

    public String getNoTipo() {
        return noTipo;
    }

    public void setNoTipo(String noTipo) {
        this.noTipo = noTipo;
    }

    public String getDsPalavraChave() {
        return dsPalavraChave;
    }

    public void setDsPalavraChave(String dsPalavraChave) {
        this.dsPalavraChave = dsPalavraChave;
    }

    @Column(name = "ds_palavra_chave")
    private String dsPalavraChave;

}
