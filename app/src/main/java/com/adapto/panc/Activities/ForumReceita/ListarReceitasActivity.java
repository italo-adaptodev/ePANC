package com.adapto.panc.Activities.ForumReceita;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Activities.TelaInicialActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Models.Database.Receita;
import com.adapto.panc.Models.ViewHolder.ReceitaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListarReceitasActivity extends AppCompatActivity {

    private FloatingActionButton criarPostagemFAB;
    private Intent criarPostagemIntent;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private View v;
    private FirestoreRecyclerOptions<Receita> options;
    private FirestoreReferences collections;
    private Handler handler = new Handler();
    private ProgressBar spinner;
    private boolean isUsuarioAutorReceita = false;
    private MaterialTextView textViewRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_receitas);
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        getPermissaoAutorReceita();
        recyclerView = findViewById(R.id.listarReceitasRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        v = findViewById(android.R.id.content);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection(collections.getReceitaCOLLECTION()).orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        options = new FirestoreRecyclerOptions.Builder<Receita>()
                .setQuery(query, Receita.class)
                .setLifecycleOwner(this)
                .build();
        //endregion

        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        criarPostagemIntent = new Intent(this.getBaseContext(), CriarPostagemReceitaActivity.class );
        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(criarPostagemIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(task, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.postDelayed(task, 1000);
    }

    private boolean getCargosUsuarioSolicitante() {
        db.collection(collections.getEquipeCOLLECTION())
                .whereEqualTo("usuarioID",   new LoginSharedPreferences(this).getIdentifier())
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

    private void excluirPostagem(String postagemID) {
        db.collection(collections.getReceitaCOLLECTION()).document(postagemID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Receita excluída com sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir esta receita. ERRO: " + e.getLocalizedMessage());
            }
        });
    }

    private void getPermissaoAutorReceita() {
        db.collection(collections.getReceitaCOLLECTION())
                .whereEqualTo("autor_usuarioID",   new LoginSharedPreferences(this).getIdentifier())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            isUsuarioAutorReceita = true;

                        }
                        handler.postDelayed(task, 1000);
                    }
                });
    }

    private Runnable task = new Runnable() {
        public void run() {

            adapter = new FirestoreRecyclerAdapter<Receita, ReceitaViewHolder>(options) {
                @Override
                public void onBindViewHolder(ReceitaViewHolder holder, int position, final Receita model) {

                    holder.setConfigsView(isUsuarioAdminstrador, isUsuarioAutorReceita, getBaseContext());
                    String imgID = model.getImagensID().get(0);
                    if(imgID != null)
                        Glide.with(getBaseContext())
                                .load(imgID)
                                .into(holder.forumReceitaImagem);
                    holder.nome.setText(model.getNomeReceita());
                    holder.forumReceitaInfos.setText("Tempo de preparo: " + model.getTempoPreparo());
                    holder.nomeAutor.setText("Por: " + model.getNomeAutor());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), DetalharReceitaActivity.class);
                            intent.putExtra("receitaIDDetalhe", model.getReceitaID());
                            startActivity(intent);
                        }
                    });
                    holder.btnExcluirPostagem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            excluirPostagem(model.getReceitaID());
                        }
                    });

                }

                @Override
                public ReceitaViewHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.cardview_forum_receita, group, false);
                    return new ReceitaViewHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
            spinner.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    };


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TelaInicialActivity.class));
    }


}