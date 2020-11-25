package com.adapto.panc.Models.Database;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Keep
public class Usuario {

    private String identificador, nome, senha, id;
    private List<String> cargos;

    public Usuario(String identificador, String senha, String nome) {
        this.identificador = identificador;
        this.senha = senha;
        this.cargos = new ArrayList<>();
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


    public List<String> getCargos() {
        return cargos;
    }

    public void setCargos(List<String> cargos) {
        this.cargos = cargos;
    }

    public void addCargo(String cargo){
        cargos.add(cargo);
    }

    public void removeCargo(String cargo){
        cargos.remove(cargo);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
