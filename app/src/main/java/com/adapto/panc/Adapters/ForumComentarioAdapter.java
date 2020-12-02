package com.adapto.panc.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.ViewHolder.FirestoreForumComentarioHolder;
import com.adapto.panc.R;

import java.util.List;

public class ForumComentarioAdapter extends RecyclerView.Adapter<ForumComentarioAdapter.FirestoreForumComentarioHolder> {
    private LayoutInflater inflater;
    private List<FirestoreForumComentario> comentarios;

    public ForumComentarioAdapter(LayoutInflater inflater, List<FirestoreForumComentario> comentarios) {
        this.inflater = inflater;
        this.comentarios = comentarios;
    }

    @Override
    public FirestoreForumComentarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_resposta_forum, parent, false);
        FirestoreForumComentarioHolder firestoreForumComentarioHolder = new FirestoreForumComentarioHolder(itemMessage);
        firestoreForumComentarioHolder.view = itemMessage;
        firestoreForumComentarioHolder.comentarioForumTexto = (TextView) itemMessage.findViewById(R.id.comentarioForumTexto);
        firestoreForumComentarioHolder.comentarioForumNome = (TextView) itemMessage.findViewById(R.id.comentarioForumNome);
        return firestoreForumComentarioHolder;
    }

    @Override
    public void onBindViewHolder(FirestoreForumComentarioHolder holder, int position) {
        FirestoreForumComentario comentario = comentarios.get(position);
        holder.comentarioForumNome.setText(comentario.getComentarioNomeUsuario());
        holder.comentarioForumTexto.setText(comentario.getComentarioTexto());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class FirestoreForumComentarioHolder extends RecyclerView.ViewHolder {
        public TextView comentarioForumNome, comentarioForumTexto;
        View view;
        int position;

        public FirestoreForumComentarioHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            comentarioForumNome = itemView.findViewById(R.id.comentarioForumNome);
            comentarioForumTexto = itemView.findViewById(R.id.comentarioForumTexto);

        }
    }


}