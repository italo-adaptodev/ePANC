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
import com.adapto.panc.Activities.ForumReceita.DetalharReceitaActivity
import com.adapto.panc.Models.Database.Receita
import com.adapto.panc.R
import com.bumptech.glide.Glide

class ReceitaAdapter(private val postagensList : ArrayList<Receita>, private val context: Context) :
    RecyclerView.Adapter<ReceitaAdapter.TelaInicialReceitaViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TelaInicialReceitaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_telainicial,parent, false)

        return TelaInicialReceitaViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: TelaInicialReceitaViewHolder,
        position: Int
    ) {
        val postagem : Receita = postagensList[position]
        val imgID: String = postagem.imagensID[0]
        Glide.with(context)
            .load(imgID)
            .into(holder.telaInicialImagem)
        holder.telaInicialTitulo.text = postagem.nomeReceita
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalharReceitaActivity::class.java)
            intent.putExtra("receitaIDDetalhe", postagem.receitaID)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
       return postagensList.size
    }

    class TelaInicialReceitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val telaInicialTitulo : TextView = itemView.findViewById(R.id.telaInicialTitulo)
        val telaInicialImagem : ImageView = itemView.findViewById(R.id.telaInicialImagem)

    }

}