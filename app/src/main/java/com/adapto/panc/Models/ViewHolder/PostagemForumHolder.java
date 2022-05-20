package com.adapto.panc.Models.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.R;

public class PostagemForumHolder extends RecyclerView.ViewHolder{

    public TextView  postagemForumTitulo;
    public ImageView postagemForumImagem;
    public ImageButton btnExcluirPostagem;
    public LinearLayout linearLayoutBtn, linearLayoutCard;

    public PostagemForumHolder(@NonNull View itemView) {
        super(itemView);
        postagemForumImagem = itemView.findViewById(R.id.postagemForumImagem);
        postagemForumTitulo = itemView.findViewById(R.id.postagemForumTitulo);
        btnExcluirPostagem = itemView.findViewById(R.id.deletePostButton);
        linearLayoutBtn = itemView.findViewById(R.id.linearLayoutBtn);
        linearLayoutCard = itemView.findViewById(R.id.linearLayoutCard);

    }

    public void setConfigsView(boolean adm, Context baseContext){
        if(adm){
            linearLayoutBtn.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams. MATCH_PARENT ,
            LinearLayout.LayoutParams. MATCH_PARENT );
            layoutParams.setMargins(0, 50 , 0 , 0 ) ;
            linearLayoutCard.setLayoutParams(layoutParams);
        }else{
            linearLayoutBtn.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams. MATCH_PARENT ,
                    LinearLayout.LayoutParams. MATCH_PARENT );
            layoutParams.setMargins(0, 0 , 0 , 0 ) ;
            linearLayoutCard.setLayoutParams(layoutParams);
        }
    }
}
