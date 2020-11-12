package com.adapto.panc;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostagemForumHolder extends RecyclerView.ViewHolder{

    public TextView postagemForumTitulo, postagemForumDesc;
    public ImageView postagemForumImagem;

    public PostagemForumHolder(@NonNull View itemView) {
        super(itemView);
        postagemForumTitulo = itemView.findViewById(R.id.postagemForumTitulo);
        postagemForumImagem = itemView.findViewById(R.id.postagemForumImagem);
        postagemForumDesc = itemView.findViewById(R.id.postagemForumDesc);

    }
}
