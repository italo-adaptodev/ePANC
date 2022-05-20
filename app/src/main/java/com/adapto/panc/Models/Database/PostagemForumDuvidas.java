package com.adapto.panc.Models.Database;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Keep
public class PostagemForumDuvidas {

    private String postagemForumTexto, postagemForumTitulo;
    private String usuarioID;
    private List<String> imagensID;
    private String postagemID;
    private Timestamp timestamp;
    private List<FirestoreForumComentario> comentarios;

    public PostagemForumDuvidas(String postagemForumTexto, String usuarioID, List<String> imagensID, Timestamp timestamp, String postagemForumTitulo) {
        this.postagemForumTexto = postagemForumTexto;
        this.usuarioID = usuarioID;
        this.imagensID = imagensID;
        this.timestamp = timestamp;
        this.comentarios = new ArrayList<>();
        this.postagemForumTitulo = postagemForumTitulo;
    }

    public String getPostagemForumTitulo() {
        return postagemForumTitulo;
    }

    public void setPostagemForumTitulo(String postagemForumTitulo) {
        this.postagemForumTitulo = postagemForumTitulo;
    }

    public PostagemForumDuvidas(){}

    public String getPostagemForumTexto() {
        return postagemForumTexto;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public List<String> getImagensID() {
        return imagensID;
    }

    public void setPostagemForumTexto(String postagemForumTexto) {
        this.postagemForumTexto = postagemForumTexto;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public void setImagensID(List<String> imagensID) {
        this.imagensID = imagensID;
    }

    public String getPostagemID() {
        return postagemID;
    }

    public void setPostagemID(String postagemID) {
        this.postagemID = postagemID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<FirestoreForumComentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<FirestoreForumComentario> comentarios) {
        this.comentarios = comentarios;
    }

    public int getCountComentarios(){
        return comentarios.size();
    }
}
