package com.adapto.panc.Models.Database;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Keep
public class Usuario {

    private String identificador, nome, senha, id;
    private boolean docLido;
    public Usuario(String identificador, String senha, String nome) {
        this.identificador = identificador;
        this.senha = senha;
        this.nome = nome;
    }

    public Usuario(){}

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDocLido() {
        return docLido;
    }

    public void setDocLido(boolean docLido) {
        this.docLido = docLido;
    }
}
