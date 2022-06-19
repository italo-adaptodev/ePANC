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
import com.adapto.panc.Activities.Produto.Produtor_DetalharProdutoActivity
import com.adapto.panc.Models.Database.Produtor_Produto
import com.adapto.panc.R
import com.bumptech.glide.Glide

class ProdutosAdapter(private val postagensList : ArrayList<Produtor_Produto>, private val context: Context) :
    RecyclerView.Adapter<ProdutosAdapter.TelaInicialProdutoViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TelaInicialProdutoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_telainicial,parent, false)

        return TelaInicialProdutoViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: TelaInicialProdutoViewHolder,
        position: Int
    ) {
        val postagem : Produtor_Produto = postagensList[position]
        val imgID: String = postagem.imagensID[0]
        Glide.with(context)
            .load(imgID)
            .into(holder.telaInicialImagem)
        holder.telaInicialTitulo.text = postagem.nome
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Produtor_DetalharProdutoActivity::class.java)
            intent.putExtra("postagemIDDetalhe", postagem.postagemID)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
       return postagensList.size
    }

    class TelaInicialProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val telaInicialTitulo : TextView = itemView.findViewById(R.id.telaInicialTitulo)
        val telaInicialImagem : ImageView = itemView.findViewById(R.id.telaInicialImagem)

    }

}