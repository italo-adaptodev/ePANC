package com.adapto.panc.Models.Database;

import com.google.firebase.Timestamp;

import java.util.List;

public class Prato {

    private String nome, descricao, restauranteID, preco;
    private List<String> imagensID;
    private Timestamp timestamp;

    public Prato(String nome, String descricao, String restauranteID, String preco, List<String> imagensID, Timestamp timestamp) {
        this.nome = nome;
        this.descricao = descricao;
        this.restauranteID = restauranteID;
        this.preco = preco;
        this.imagensID = imagensID;
        this.timestamp = timestamp;
    }

    public Prato() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRestauranteID() {
        return restauranteID;
    }

    public void setRestauranteID(String restauranteID) {
        this.restauranteID = restauranteID;
    }

    public boolean hasIngrediente(String ingrediente){
        return this.descricao.contains(ingrediente);
    }

    public String getPreco() {
        return preco;
    }


    public void setPreco(String preco) {
        this.preco = preco;
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
}
