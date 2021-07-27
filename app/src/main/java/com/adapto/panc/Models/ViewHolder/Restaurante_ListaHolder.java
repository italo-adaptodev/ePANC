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

public class Restaurante_ListaHolder extends RecyclerView.ViewHolder{

    public TextView  nome;
    public ImageView restauranteListaImagem;
    public ImageButton btnExcluirPostagem;
    public LinearLayout linearLayoutBtn, linearLayoutCard;

    public Restaurante_ListaHolder(@NonNull View itemView) {
        super(itemView);
        restauranteListaImagem = itemView.findViewById(R.id.restauranteListaImagem);
        nome = itemView.findViewById(R.id.restauranteListaNome);
        btnExcluirPostagem = itemView.findViewById(R.id.restauranteListaDeleteBtn);
        linearLayoutBtn = itemView.findViewById(R.id.restauranteListaLinearLayoutBtn);
        linearLayoutCard = itemView.findViewById(R.id.restauranteListaLinearLayoutCard);

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
