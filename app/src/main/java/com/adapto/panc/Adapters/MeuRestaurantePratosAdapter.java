package com.adapto.panc.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.R;

import java.util.List;

public class MeuRestaurantePratosAdapter extends RecyclerView.Adapter<MeuRestaurantePratosAdapter.FirestoreForumComentarioHolder> {
    private LayoutInflater inflater;
    private List<Prato> pratos;

    public MeuRestaurantePratosAdapter(LayoutInflater inflater, List<Prato> pratos) {
        this.inflater = inflater;
        this.pratos = pratos;
    }

    @Override
    public FirestoreForumComentarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_prato_resurante, parent, false);
        FirestoreForumComentarioHolder firestoreForumComentarioHolder = new FirestoreForumComentarioHolder(itemMessage);
        firestoreForumComentarioHolder.view = itemMessage;
        firestoreForumComentarioHolder.nomePrato = (TextView) itemMessage.findViewById(R.id.restaurantePratoNome);
        firestoreForumComentarioHolder.precoPrato = (TextView) itemMessage.findViewById(R.id.restaurantePratoPreco);
        return firestoreForumComentarioHolder;
    }

    @Override
    public void onBindViewHolder(FirestoreForumComentarioHolder holder, int position) {
        Prato prato = pratos.get(position);
        holder.nomePrato.setText(prato.getNome());
        holder.precoPrato.setText(prato.getPrecoString());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return pratos.size();
    }

    public static class FirestoreForumComentarioHolder extends RecyclerView.ViewHolder {
        public TextView nomePrato, precoPrato;
        View view;
        int position;

        public FirestoreForumComentarioHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            nomePrato = itemView.findViewById(R.id.restaurantePratoNome);
            precoPrato = itemView.findViewById(R.id.restaurantePratoPreco);

        }
    }


}