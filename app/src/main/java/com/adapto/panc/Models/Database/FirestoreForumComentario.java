package com.adapto.panc.Models.Database;


import androidx.annotation.Keep;

@Keep
public class FirestoreForumComentario {

    public String comentarioNomeUsuario, comentarioTexto;

    public FirestoreForumComentario(String respostaNome, String respostaTexto){
        this.comentarioNomeUsuario = respostaNome;
        this.comentarioTexto = respostaTexto;
    }

    public FirestoreForumComentario(){}

    public String getComentarioNomeUsuario() {
        return comentarioNomeUsuario;
    }

    public void setComentarioNomeUsuario(String comentarioNomeUsuario) {
        this.comentarioNomeUsuario = comentarioNomeUsuario;
    }

    public String getComentarioTexto() {
        return comentarioTexto;
    }

    public void setComentarioTexto(String comentarioTexto) {
        this.comentarioTexto = comentarioTexto;
    }
}
