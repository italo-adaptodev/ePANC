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

public class ReceitaViewHolder extends RecyclerView.ViewHolder{

    public TextView  nome, forumReceitaInfos;
    public ImageView forumReceitaImagem;
    public ImageButton btnExcluirPostagem;
    public LinearLayout linearLayoutBtn, linearLayoutCard;

    public ReceitaViewHolder(@NonNull View itemView) {
        super(itemView);
        forumReceitaImagem = itemView.findViewById(R.id.forumReceitaImagem);
        nome = itemView.findViewById(R.id.forumReceitaNome);
        forumReceitaInfos = itemView.findViewById(R.id.forumReceitaInfos);
        btnExcluirPostagem = itemView.findViewById(R.id.deletePostButton);
        linearLayoutBtn = itemView.findViewById(R.id.linearLayoutBtn);
        linearLayoutCard = itemView.findViewById(R.id.linearLayoutCard);

    }

    public void setConfigsView(boolean adm, boolean autor, Context baseContext){
        if(adm || autor){
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
