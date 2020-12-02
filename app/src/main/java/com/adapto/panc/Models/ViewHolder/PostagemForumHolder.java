package com.adapto.panc.Models.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.R;

public class PostagemForumHolder extends RecyclerView.ViewHolder{

    public TextView  postagemForumDesc;
    public ImageView postagemForumImagem;

    public PostagemForumHolder(@NonNull View itemView) {
        super(itemView);
        postagemForumImagem = itemView.findViewById(R.id.postagemForumImagem);
        postagemForumDesc = itemView.findViewById(R.id.postagemForumDesc);

    }
}
