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
import com.adapto.panc.Models.Database.PostagemForumDuvidas
import com.adapto.panc.R
import com.bumptech.glide.Glide

class PostagensDuvidaAdapter(private val postagensList : ArrayList<PostagemForumDuvidas>, private val context: Context) :
    RecyclerView.Adapter<PostagensDuvidaAdapter.PostagensViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostagensViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_telainicial,parent, false)

        return PostagensViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: PostagensViewHolder,
        position: Int
    ) {
        val postagem : PostagemForumDuvidas = postagensList[position]
        val imgID: String = postagem.getImagensID().get(0)
        if(imgID != null) {
            Glide.with(context)
                .load(imgID)
                .into(holder.postagemForumImagem)
        }
        holder.postagemForumTitulo.text = postagem.postagemForumTitulo
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalharPostagemForumActivity::class.java)
            intent.putExtra("postagemIDDetalhe", postagem.postagemID)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
       return postagensList.size
    }

    class PostagensViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val postagemForumTitulo : TextView = itemView.findViewById(R.id.telaInicialTitulo)
        val postagemForumImagem : ImageView = itemView.findViewById(R.id.telaInicialImagem)

    }

}