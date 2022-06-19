package com.adapto.panc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Activities.BibliotecaPANC.DetalharItemBibliotecaPANCActivity;
import com.adapto.panc.Models.Database.ItemBiblioteca;
import com.adapto.panc.Models.ViewHolder.ItemBibliotecaPANCViewHolder;
import com.adapto.panc.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemBibliotecaPANCAdapter extends RecyclerView.Adapter<ItemBibliotecaPANCViewHolder> {

    private LayoutInflater inflater;
    private List<ItemBiblioteca> itemBibliotecaList;
    private Context context;
    private Activity activity;
    private boolean adm;
    private String identifier;

    public ItemBibliotecaPANCAdapter(LayoutInflater inflater, List<ItemBiblioteca> itemBibliotecaList, Context context, Activity activity, boolean adm, String identifier) {
        this.inflater = inflater;
        this.itemBibliotecaList = itemBibliotecaList;
        this.context = context;
        this.activity = activity;
        this.adm = adm;
        this.identifier = identifier;
    }

    @NonNull
    @Override
    public ItemBibliotecaPANCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = inflater.inflate(R.layout.cardview_item_biblioteca_panc, parent, false );
        ItemBibliotecaPANCViewHolder itemBibliotecaPANCViewHolder = new ItemBibliotecaPANCViewHolder(item);
        itemBibliotecaPANCViewHolder.view = item;
        itemBibliotecaPANCViewHolder.itemBibliotecaTitulo = (TextView) item.findViewById(R.id.itemBibliotecaPANCTitulo);
        itemBibliotecaPANCViewHolder.itemBibliotecaImagem = item.findViewById(R.id.itemBibliotecaPANCImagem);
        return itemBibliotecaPANCViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemBibliotecaPANCViewHolder holder, int position) {

        final ItemBiblioteca itemBiblioteca = itemBibliotecaList.get(holder.getAdapterPosition());

        if(identifier.equals(itemBiblioteca.getUsuarioID()))
            holder.setConfigsView(adm, true, context);
        else
            holder.setConfigsView(adm, false, context);
        String imgID = (itemBiblioteca.getImagensID() == null || itemBiblioteca.getImagensID().isEmpty()) ? null : itemBiblioteca.getImagensID().get(0) ;
        if(imgID != null)
            Glide.with(context)
            .load(imgID)
            .into(holder.itemBibliotecaImagem);
        holder.itemBibliotecaTitulo.setText(itemBiblioteca.getItemBibliotecaTitulo());
        holder.position = holder.getAbsoluteAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalharItemBibliotecaPANCActivity.class);
                intent.putExtra("itemIDDetalhe", itemBiblioteca.getItemID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemBibliotecaList.size();
    }
}
