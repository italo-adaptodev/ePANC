package com.adapto.panc.Models.Database;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class Prato {

    private String nome, ingredientes, restauranteID;
    private double preco;
    private List<String> imagensID;
    private Timestamp timestamp;

    public Prato(String nome, String ingredientes, String restauranteID, double preco, List<String> imagensID, Timestamp timestamp) {
        this.nome = nome;
        this.ingredientes = ingredientes;
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

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getRestauranteID() {
        return restauranteID;
    }

    public void setRestauranteID(String restauranteID) {
        this.restauranteID = restauranteID;
    }

    public boolean hasIngrediente(String ingrediente){
        return this.ingredientes.contains(ingrediente);
    }

    public double getPreco() {
        return preco;
    }

    public String getPrecoString() {
        return "" + preco;
    }

    public void setPreco(double preco) {
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
