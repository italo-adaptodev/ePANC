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
import com.adapto.panc.Activities.BibliotecaPANC.DetalharItemBibliotecaPANCActivity
import com.adapto.panc.Models.Database.ItemBiblioteca
import com.adapto.panc.R
import com.bumptech.glide.Glide

class BibliotecaAdapter(private val postagensList : ArrayList<ItemBiblioteca>, private val context: Context) :
    RecyclerView.Adapter<BibliotecaAdapter.ItemBibliotecaViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemBibliotecaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_telainicial,parent, false)

        return ItemBibliotecaViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: ItemBibliotecaViewHolder,
        position: Int
    ) {
        val postagem : ItemBiblioteca = postagensList[position]
        val imgID: String? = if(postagem.imagensID.isNullOrEmpty()) null else postagem.imagensID[0]
        if(!imgID.isNullOrEmpty())
            Glide.with(context)
                .load(imgID)
                .into(holder.telaInicialImagem)
        holder.telaInicialTitulo.text = postagem.itemBibliotecaTitulo
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalharItemBibliotecaPANCActivity::class.java)
            intent.putExtra("itemIDDetalhe", postagem.itemID)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
       return postagensList.size
    }

    class ItemBibliotecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val telaInicialTitulo : TextView = itemView.findViewById(R.id.telaInicialTitulo)
        val telaInicialImagem : ImageView = itemView.findViewById(R.id.telaInicialImagem)

    }

}