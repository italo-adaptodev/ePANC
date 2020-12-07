package com.adapto.panc.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.Models.Database.PostagemForum;
import com.adapto.panc.Models.ViewHolder.PostagemForumHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

public class TelaInicialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton criarPostagemFAB, listarEquipeFAB;
    private Intent criarPostagemIntent, listarEquipeIntent;
    private FirestoreRecyclerAdapter adapter;
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();;
        setContentView(R.layout.activity_tela_inicial);
        getCargosUsuarioSolicitante();
        recyclerView = findViewById(R.id.feedPrincipalRecV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        listarEquipeFAB = findViewById(R.id.listarEquipe);
        criarPostagemIntent = new Intent(this.getBaseContext(), CriarPostagemDuvidaActivity.class );
        listarEquipeIntent = new Intent(this.getBaseContext(), ListarEquipeActivity.class );
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Panc - APP");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        v = findViewById(android.R.id.content);
        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(criarPostagemIntent);
            }
        });
        listarEquipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(listarEquipeIntent);
            }
        });

        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection("PostagensForumPANC").orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostagemForum> options = new FirestoreRecyclerOptions.Builder<PostagemForum>()
                .setQuery(query, PostagemForum.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<PostagemForum, PostagemForumHolder>(options) {
            @Override
            public void onBindViewHolder(PostagemForumHolder holder, int position, final PostagemForum model) {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(!getCargosUsuarioSolicitante())
            menu.getItem(2).setVisible(false);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                //startActivity(new Intent(this, SobreOProjetoActivity.class));
                break;
            case R.id.item2:
                //startActivity(new Intent(this, FaleConoscoActivity.class));
                break;
            case R.id.item3:
                startActivity(new Intent(this, ConvidarActivity.class));
                break;
            case R.id.item4:
                new LoginSharedPreferences(this).logoutUser();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private boolean getCargosUsuarioSolicitante() {
        db.collection("EQUIPE")
                .whereEqualTo("usuarioID",   new LoginSharedPreferences(this).getKEYUSER())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                                String cargos = snap.get("cargosAdministrativos").toString();
                                if(cargos.contains("ADMINISTRADOR")) {
                                    isUsuarioAdminstrador = true;

                                }
                            }
                        }
                    }
                });
        return isUsuarioAdminstrador;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}