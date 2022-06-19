package com.adapto.panc.Activities.TelaInicial.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.adapto.panc.Activities.ForumDuvida.DetalharPostagemForumActivity
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteActivity
import com.adapto.panc.Models.Database.Restaurante
import com.adapto.panc.R
import com.bumptech.glide.Glide

class RestauranteAdapter(private val postagensList : ArrayList<Restaurante>, private val context: Context) :
    RecyclerView.Adapter<RestauranteAdapter.TelaInicialRestauranteViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TelaInicialRestauranteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_telainicial,parent, false)

        return TelaInicialRestauranteViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: TelaInicialRestauranteViewHolder,
        position: Int
    ) {
        val postagem : Restaurante = postagensList[position]
        val imgID: String? = if(postagem.pratos.isNullOrEmpty()) null else postagem.pratos[0].imagensID[0]
        if(!imgID.isNullOrEmpty())
            Glide.with(context)
            .load(imgID)
            .into(holder.telaInicialImagem)

        holder.telaInicialTitulo.text = postagem.nomeRestaurante
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Restaurante_DetalharRestauranteActivity::class.java)
            intent.putExtra("restauranteIDDetalhe", postagem.id)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
       return postagensList.size
    }

    class TelaInicialRestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val telaInicialTitulo : TextView = itemView.findViewById(R.id.telaInicialTitulo)
        val telaInicialImagem : ImageView = itemView.findViewById(R.id.telaInicialImagem)

    }

}