package com.adapto.panc.Models.Database;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Produtor_Produto {

    private String nome, produtorID, postagemID, descricao, observacao;
    private double preco;
    private List<String> imagensID;
    private Timestamp timestamp;

    public Produtor_Produto(String nome, String produtorID, String descricao, String observacao, double preco, List<String> imagensID, Timestamp timestamp) {
        this.nome = nome;
        this.produtorID = produtorID;
        this.descricao = descricao;
        this.observacao = observacao;
        this.preco = preco;
        this.imagensID = imagensID;
        this.timestamp = timestamp;
    }

    public Produtor_Produto(){}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProdutorID() {
        return produtorID;
    }

    public void setProdutorID(String produtorID) {
        this.produtorID = produtorID;
    }

    public String getPostagemID() {
        return postagemID;
    }

    public void setPostagemID(String postagemID) {
        this.postagemID = postagemID;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
