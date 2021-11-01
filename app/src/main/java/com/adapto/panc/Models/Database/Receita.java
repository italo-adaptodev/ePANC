package com.adapto.panc.Models.Database;

import com.google.firebase.Timestamp;

import java.util.List;

public class Receita {

    private String nome, autor_usuarioID, receitaID, ingredientes, modoPreparo, rendimento, infos;
    private List<String> imagensID;
    private Timestamp timestamp;

    public Receita(String nome, String autor_usuarioID, String ingredientes, String modoPreparo, String rendimento, List<String> imagensID, Timestamp timestamp) {
        this.nome = nome;
        this.autor_usuarioID = autor_usuarioID;
        this.ingredientes = ingredientes;
        this.modoPreparo = modoPreparo;
        this.rendimento = rendimento;
        this.imagensID = imagensID;
        this.timestamp = timestamp;
        this.infos = infos;
    }

    public Receita() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor_usuarioID() {
        return autor_usuarioID;
    }

    public void setAutor_usuarioID(String autor_usuarioID) {
        this.autor_usuarioID = autor_usuarioID;
    }

    public String getReceitaID() {
        return receitaID;
    }

    public void setReceitaID(String receitaID) {
        this.receitaID = receitaID;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getModoPreparo() {
        return modoPreparo;
    }

    public void setModoPreparo(String modoPreparo) {
        this.modoPreparo = modoPreparo;
    }

    public String getRendimento() {
        return rendimento;
    }

    public void setRendimento(String rendimento) {
        this.rendimento = rendimento;
    }

    public List<String> getImagensID() {
        return imagensID;
    }

    public void setImagensID(List<String> imagensID) {
        this.imagensID = imagensID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }
}
