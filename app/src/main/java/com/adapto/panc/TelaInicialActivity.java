package com.adapto.panc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapto.panc.Models.Database.PostagemForum;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

public class TelaInicialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton criarPostagemFAB;
    private Intent criarPostagemIntent;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        recyclerView = (RecyclerView) findViewById(R.id.feedPrincipalRecV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        criarPostagemIntent = new Intent(this.getBaseContext(), CriarPostagemDuvidaActivity.class );

        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(criarPostagemIntent);
            }
        });

        //region RECYCLER VIEW POSTAGENS
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        Query query = db
                .collection("PostagensForumPANC").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostagemForum> options = new FirestoreRecyclerOptions.Builder<PostagemForum>()
                .setQuery(query, PostagemForum.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<PostagemForum, PostagemForumHolder>(options) {
            @Override
            public void onBindViewHolder(PostagemForumHolder holder, int position, final PostagemForum model) {
                holder.postagemForumTitulo.setText("TESTE DE TITULO " + position);
                String imgID = model.getImagensID().get(0);
                Glide.with(getBaseContext())
                        .load(imgID)
                        .into(holder.postagemForumImagem);
                holder.postagemForumDesc.setText(model.getPostagemForumTexto());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), DetalharPostagemForumActivity.class);
                        intent.putExtra("postagemIDDetalhe", model.getPostagemID());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public PostagemForumHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cardview_feedprincipal, group, false);
                return new PostagemForumHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        //endregion


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }@Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}