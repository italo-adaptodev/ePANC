package com.adapto.panc;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirestoreForumComentarioHolder extends RecyclerView.ViewHolder{

    public TextView comentarioForumNome, comentarioForumTexto;

    public FirestoreForumComentarioHolder(@NonNull View itemView) {
        super(itemView);
        comentarioForumNome = itemView.findViewById(R.id.comentarioForumNome);
        comentarioForumTexto = itemView.findViewById(R.id.comentarioForumTexto);

    }
}
