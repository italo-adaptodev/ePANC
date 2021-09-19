package com.adapto.panc.Models.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Restaurante {
    private String numContato;
    private String localizacao;
    private String usuarioID;
    private String nomeRestaurante;


    private String id;
    private String questionarioURL;
    private Map<String, String> horarios;
    private List<Prato> pratos;
    private List<String> ingredientesPANC;

    public Restaurante(String numContato, String localizacao, String nomeRestaurante, String usuarioID, String questionarioURL) {
        this.numContato = numContato;
        this.localizacao = localizacao;
        this.usuarioID = usuarioID;
        this.nomeRestaurante = nomeRestaurante;
        this.questionarioURL = questionarioURL;
        this.pratos = new ArrayList<>();
        this.ingredientesPANC = new ArrayList<>();
    }

    public Restaurante() {
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

    public List<Prato> getPratos() {
        return pratos;
    }

    public void setPratos(List<Prato> pratos) {
        this.pratos = pratos;
    }

    public int getCountPratos(){
        return pratos.size();
    }

    public List<String> getIngredientesPANC() {
        return ingredientesPANC;
    }

    public void setIngredientesPANC(List<String> ingredientesPANC) {
        this.ingredientesPANC = ingredientesPANC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
