package com.adapto.panc.Models.Database;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Keep
public class ItemBiblioteca {

    private String itemBibliotecaDescricao, itemBibliotecaTitulo;
    private String usuarioID;
    private List<String> imagensID;
    private String itemID;
    private Timestamp timestamp;
    private List<FirestoreForumComentario> comentarios;

    public ItemBiblioteca(String itemBibliotecaTitulo, String itemBibliotecaDescricao, String usuarioID, List<String> imagensID, Timestamp timestamp) {
        this.itemBibliotecaDescricao = itemBibliotecaDescricao;
        this.itemBibliotecaTitulo = itemBibliotecaTitulo;
        this.usuarioID = usuarioID;
        this.imagensID = imagensID;
        this.timestamp = timestamp;
        this.comentarios = new ArrayList<>();
    }

    public ItemBiblioteca(){}


    public String getItemBibliotecaDesc() {
        return itemBibliotecaDescricao;
    }

    public void setItemBibliotecaDesc(String itemBibliotecaDescricao) {
        this.itemBibliotecaDescricao = itemBibliotecaDescricao;
    }

    public String getItemBibliotecaTitulo() {
        return itemBibliotecaTitulo;
    }

    public void setItemBibliotecaTitulo(String itemBibliotecaTitulo) {
        this.itemBibliotecaTitulo = itemBibliotecaTitulo;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public List<String> getImagensID() {
        return imagensID;
    }

   public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public void setImagensID(List<String> imagensID) {
        this.imagensID = imagensID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
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
