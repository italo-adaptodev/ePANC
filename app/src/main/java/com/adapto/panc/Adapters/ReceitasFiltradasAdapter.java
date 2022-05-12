package com.adapto.panc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import com.adapto.panc.Activities.ForumReceita.DetalharReceitaActivity;
import com.adapto.panc.Models.Database.Receita;
import com.adapto.panc.Models.Database.Receita;
import com.adapto.panc.Models.ViewHolder.ReceitaViewHolder;
import com.adapto.panc.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ReceitasFiltradasAdapter extends RecyclerView.Adapter<ReceitaViewHolder> {

    private LayoutInflater inflater;
    private List<Receita> receitas;
    private Context context;
    private Activity activity;
    private boolean adm;
    private String identifier;


    public ReceitasFiltradasAdapter(LayoutInflater inflater, List<Receita> receitas, Context context, Activity activity, boolean adm, String identifier) {
        this.inflater = inflater;
        this.receitas = receitas;
        this.context = context;
        this.activity = activity;
        this.adm = adm;
        this.identifier = identifier;
    }

    @NonNull
    @Override
    public ReceitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_forum_receita, parent, false);
        ReceitaViewHolder receitaFiltradaViewHolder = new ReceitaViewHolder(itemMessage);
        receitaFiltradaViewHolder.view = itemMessage;
        receitaFiltradaViewHolder.nome = (TextView) itemMessage.findViewById(R.id.forumReceitaNome);
        receitaFiltradaViewHolder.nomeAutor = (TextView) itemMessage.findViewById(R.id.forumReceitaNomeAutor);
        receitaFiltradaViewHolder.forumReceitaInfos = (TextView) itemMessage.findViewById(R.id.forumReceitaInfos);
        receitaFiltradaViewHolder.forumReceitaImagem = itemMessage.findViewById(R.id.forumReceitaImagem);
        return receitaFiltradaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceitaViewHolder holder, int position) {

        final Receita receita = receitas.get(holder.getAdapterPosition());

        if(identifier.equals(receita.getAutor_usuarioID()))
            holder.setConfigsView(adm, true, context);
        else
            holder.setConfigsView(adm, false, context);
        String imgID = receita.getImagensID().get(0);
        if(imgID != null)
            Glide.with(context)
                    .load(imgID)
                    .into(holder.forumReceitaImagem);
        holder.nome.setText(receita.getNomeReceita());
        holder.nomeAutor.setText("Por: " + receita.getNomeAutor());
        holder.forumReceitaInfos.setText("Tempo de preparo: " + receita.getTempoPreparo());
        holder.position = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalharReceitaActivity.class);
                intent.putExtra("receitaIDDetalhe", receita.getReceitaID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return receitas.size();
    }
}