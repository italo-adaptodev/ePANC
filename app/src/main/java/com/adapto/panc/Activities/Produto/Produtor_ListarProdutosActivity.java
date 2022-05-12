package com.adapto.panc.Activities.Produto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adapto.panc.Activities.ForumDuvida.ForumDuvidasActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Produtor_Produto;
import com.adapto.panc.Models.ViewHolder.Produtor_VitrineHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
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

public class Produtor_ListarProdutosActivity extends AppCompatActivity {

    private FloatingActionButton criarPostagemFAB;
    private Intent criarPostagemIntent;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private View v;
    private FirestoreRecyclerOptions<Produtor_Produto> options;
    private FirestoreReferences collections;
    private Handler handler = new Handler();
    private ProgressBar spinner;
    private boolean isUsuarioProdutor = false;
    private boolean isUsuarioDonoProduto = false;
    private MaterialTextView textViewRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtor__lista_produtos);
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        getCargosUsuarioSolicitante();
        getPermissaoProdutor();
        getPermissaoDonoProduto();
        recyclerView = findViewById(R.id.recyclerViewVitrine);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        v = findViewById(android.R.id.content);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection(collections.getVitrineProdutosCOLLECTION()).orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        options = new FirestoreRecyclerOptions.Builder<Produtor_Produto>()
                .setQuery(query, Produtor_Produto.class)
                .setLifecycleOwner(this)
                .build();
        //endregion

        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        criarPostagemIntent = new Intent(this.getBaseContext(), Produtor_CadastrarProdutosActivity.class );
        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUsuarioProdutor)
                    new SnackBarPersonalizada().showMensagemLonga(v, "Você não possui permissão para cadastrar um produto!");
                else
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

    private void getPermissaoProdutor() {
        db.collection(collections.getProdutorCOLLECTION())
                .whereEqualTo("usuarioID",   new LoginSharedPreferences(this).getIdentifier())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            isUsuarioProdutor = true;

                        }
                    }
                });
    }

    private void excluirPostagem(String postagemID) {
        db.collection(collections.getVitrineProdutosCOLLECTION()).document(postagemID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Produto excluída com sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir este produto. ERRO: " + e.getLocalizedMessage());
            }
        });
    }

    private void getPermissaoDonoProduto() {
        db.collection(collections.getVitrineProdutosCOLLECTION())
                .whereEqualTo("produtorID",   new LoginSharedPreferences(this).getIdentifier())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            isUsuarioDonoProduto = true;

                        }
                        handler.postDelayed(task, 1000);
                    }
                });
    }

    private Runnable task = new Runnable() {
        public void run() {

            adapter = new FirestoreRecyclerAdapter<Produtor_Produto, Produtor_VitrineHolder>(options) {
                @Override
                public void onBindViewHolder(Produtor_VitrineHolder holder, int position, final Produtor_Produto model) {

                    holder.setConfigsView(isUsuarioAdminstrador, isUsuarioDonoProduto, getBaseContext());
                    String imgID = model.getImagensID().get(0);
                    if(imgID != null)
                        Glide.with(getBaseContext())
                            .load(imgID)
                            .into(holder.vitrineProdutoImagem);
                    holder.nome.setText(model.getNome());
                    holder.preco.setText("R$ " + model.getPrecoString());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Produtor_DetalharProdutoActivity.class);
                            intent.putExtra("postagemIDDetalhe", model.getPostagemID());
                            startActivity(intent);
                        }
                    });
                    holder.btnExcluirPostagem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            excluirPostagem(model.getPostagemID());
                        }
                    });

                }

                @Override
                public Produtor_VitrineHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.cardview_vitrine_produto, group, false);
                    return new Produtor_VitrineHolder(view);
                }
            };

            recyclerView.setAdapter(adapter);
            spinner.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    };


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ForumDuvidasActivity.class));
    }


}