package com.adapto.panc.Models.Database;

import java.util.List;
import java.util.Map;

public class Restaurante {
    private String numContato, localizacao, usuarioID, nomeRestaurante;
    private String questionarioURL;
    private Map<String, String> horarios;

    public Restaurante(String numContato, String localizacao, String nomeRestaurante, String usuarioID, String questionarioURL) {
        this.numContato = numContato;
        this.localizacao = localizacao;
        this.usuarioID = usuarioID;
        this.nomeRestaurante = nomeRestaurante;
        this.questionarioURL = questionarioURL;
    }

    public String getNumContato() {
        return numContato;
    }

    public void setNumContato(String numContato) {
        this.numContato = numContato;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getNomeRestaurante() {
        return nomeRestaurante;
    }

    public void setNomeRestaurante(String nomeRestaurante) {
        this.nomeRestaurante = nomeRestaurante;
    }

    public String getQuestionarioURL() {
        return questionarioURL;
    }

    public void setQuestionarioURL(String questionarioURL) {
        this.questionarioURL = questionarioURL;
    }

    public Map<String, String> getHorarios() {
        return horarios;
    }

    public void setHorarios(Map<String, String> horarios) {
        this.horarios = horarios;
    }
}
