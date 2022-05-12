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

public class ItemBibliotecaPANCViewHolder extends RecyclerView.ViewHolder {

    public TextView itemBibliotecaTitulo;
    public ImageView itemBibliotecaImagem;
    public ImageButton btnExcluirPostagem;
    public LinearLayout linearLayoutBtn, linearLayoutCard;
    public View view;
    public int position;

    public ItemBibliotecaPANCViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        itemBibliotecaImagem = itemView.findViewById(R.id.itemBibliotecaPANCImagem);
        itemBibliotecaTitulo = itemView.findViewById(R.id.itemBibliotecaPANCTitulo);
        btnExcluirPostagem = itemView.findViewById(R.id.itemBibliotecaPANCDeleteButton);
        linearLayoutBtn = itemView.findViewById(R.id.itemBibliotecaPANCLinearLayoutBtn);
        linearLayoutCard = itemView.findViewById(R.id.itemBibliotecaPANCLinearLayoutCard);


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
