package com.adapto.panc.Models.Database;

public class Produtor {
    private String numContato, localizacao, email, usuarioID, id;
    private String questionarioURL;

    public Produtor(String numContato, String localizacao, String email, String usuarioID) {
        this.numContato = numContato;
        this.localizacao = localizacao;
        this.email = email;
        this.usuarioID = usuarioID;
    }

    public Produtor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public String getQuestionarioURL() {
        return questionarioURL;
    }

    public void setQuestionarioURL(String questionarioURL) {
        this.questionarioURL = questionarioURL;
    }
}
