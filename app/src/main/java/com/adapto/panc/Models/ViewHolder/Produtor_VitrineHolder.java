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

public class Produtor_VitrineHolder extends RecyclerView.ViewHolder{

    public TextView  nome, preco;
    public ImageView vitrineProdutoImagem;
    public ImageButton btnExcluirPostagem;
    public LinearLayout linearLayoutBtn, linearLayoutCard;

    public Produtor_VitrineHolder(@NonNull View itemView) {
        super(itemView);
        vitrineProdutoImagem = itemView.findViewById(R.id.vitrineProdutoImagem);
        nome = itemView.findViewById(R.id.vitrineProdutoNome);
        preco = itemView.findViewById(R.id.vitrineProdutoPreco);
        btnExcluirPostagem = itemView.findViewById(R.id.vitrineProdutoLinearLayoutDeleteBtn);
        linearLayoutBtn = itemView.findViewById(R.id.vitrineProdutoLinearLayout);
        linearLayoutCard = itemView.findViewById(R.id.vitrineProdutoLinearLayoutCard);

    }

    public void setConfigsView(boolean adm, boolean dono, Context baseContext){
        if(adm || dono){
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
