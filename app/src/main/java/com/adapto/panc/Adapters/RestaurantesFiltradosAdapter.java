package com.adapto.panc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteActivity;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantesFiltradosAdapter extends RecyclerView.Adapter<RestaurantesFiltradosAdapter.RestauranteFiltradoHolder> {
    private LayoutInflater inflater;
    private List<Restaurante> restaurantes;
    private Context context;
    private Activity activity;


    public RestaurantesFiltradosAdapter(LayoutInflater inflater, List<Restaurante> restaurantes, Context context, Activity activity) {
        this.inflater = inflater;
        this.restaurantes = restaurantes;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public RestauranteFiltradoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_restaurante_lista, parent, false);
        RestauranteFiltradoHolder restaraunteFiltradoHolder = new RestauranteFiltradoHolder(itemMessage);
        restaraunteFiltradoHolder.view = itemMessage;
        restaraunteFiltradoHolder.restauranteListaNome = (TextView) itemMessage.findViewById(R.id.restauranteListaNome);
        restaraunteFiltradoHolder.restauranteListaImagem = itemMessage.findViewById(R.id.restauranteListaImagem);
        return restaraunteFiltradoHolder;
    }

    @Override
    public void onBindViewHolder(RestauranteFiltradoHolder holder, int position) {
        final Restaurante restaurante = restaurantes.get(position);
        String imgID = restaurante.getPratos().size() == 0 ? null : restaurante.getPratos().get(0).getImagensID().get(0);
        if(imgID != null)
            Glide.with(context)
                    .load(imgID)
                    .into(holder.restauranteListaImagem);
        holder.restauranteListaNome.setText(restaurante.getNomeRestaurante());
        holder.position = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Restaurante_DetalharRestauranteActivity.class);
                intent.putExtra("restauranteIDDetalhe", restaurante.getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantes.size();
    }

    public static class  RestauranteFiltradoHolder extends RecyclerView.ViewHolder{

        public TextView  restauranteListaNome;
        public ImageView restauranteListaImagem;
        public ImageButton btnExcluirPostagem;
        public LinearLayout linearLayoutBtn, linearLayoutCard;
        View view;
        int position;

        public RestauranteFiltradoHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            restauranteListaImagem = itemView.findViewById(R.id.restauranteListaImagem);
            restauranteListaNome = itemView.findViewById(R.id.restauranteListaNome);
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


}