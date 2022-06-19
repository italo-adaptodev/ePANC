package com.adapto.panc.Activities.ForumDuvida;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adapto.panc.Activities.ConvidarActivity;
import com.adapto.panc.Activities.ForumReceita.ListarReceitasActivity;
import com.adapto.panc.Activities.BibliotecaPANC.ListarItensBibliotecaPANCActivity;
import com.adapto.panc.Activities.ListarEquipeActivity;
import com.adapto.panc.Activities.LoginActivity;
import com.adapto.panc.Activities.PainelAdministrativoActivity;
import com.adapto.panc.Activities.Produto.Produtor_ListarProdutosActivity;
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteDONOActivity;
import com.adapto.panc.Activities.Restaurante.Restaurante_ListarRestaurantesActivity;
import com.adapto.panc.Activities.TelaInicial.TelaInicial;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.Models.ViewHolder.PostagemForumHolder;
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
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListarPostagemForumDuvidaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton criarPostagemFAB;
    private Intent criarPostagemIntent, listarEquipeIntent;
    private FirestoreRecyclerAdapter adapter;
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private boolean isUsuarioRestaurante = false;
    private final boolean response = false;
    private View v;
    private BottomAppBar bottomAppBar;
    private FirestoreRecyclerOptions<PostagemForumDuvidas> options;
    private final FirestoreReferences firestoreReferences = new FirestoreReferences();
    private final Handler handler = new Handler();
    private ProgressBar spinner;
    private MaterialTextView textViewRecycler;
    private String usuarioID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_postagem_forum_duvida);
        db = FirebaseFirestore.getInstance();
        v = findViewById(android.R.id.content);
        usuarioID = new LoginSharedPreferences(this).getIdentifier();
        if(usuarioID.isEmpty()){
            new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível encontrar os dados do usuário. Você será redirecionado a tela de login");
            new LoginSharedPreferences(getBaseContext()).logoutUser();
        }
        isUsuarioAtivo();
        recyclerView = findViewById(R.id.feedPrincipalRecV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.INVISIBLE);
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        criarPostagemIntent = new Intent(this.getBaseContext(), CriarPostagemDuvidaActivity.class);
        listarEquipeIntent = new Intent(this.getBaseContext(), ListarEquipeActivity.class);
        toolbar = findViewById(R.id.toolbarForumDuvida);
        bottomAppBar = findViewById(R.id.bottom_app_bar);


        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        toolbar.setTitle("Fórum de Discussão");
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);


        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                startActivity(criarPostagemIntent);

            }
        });

        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection(firestoreReferences.getPostagensForumPANCCOLLECTION()).orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        options = new FirestoreRecyclerOptions.Builder<PostagemForumDuvidas>()
                .setQuery(query, PostagemForumDuvidas.class)
                .setLifecycleOwner(this)
                .build();

        //endregion
//        setSupportActionBar(bottomAppBar);
    }

    private void excluirPostagem(String postagemID) {
        db.collection(firestoreReferences.getPostagensForumPANCCOLLECTION()).document(postagemID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new SnackBarPersonalizada().showMensagemLonga(v, "Postagem excluída com sucesso!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir esta postagem. ERRO: " + e.getLocalizedMessage());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.postDelayed(task, 1000);
    }

    private boolean getPermissaoRestaurante() {
        db.collection(firestoreReferences.getRestauranteCOLLECTION())
                .whereEqualTo("usuarioID",   new LoginSharedPreferences(this).getIdentifier())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            isUsuarioRestaurante = true;
                        }
                    }
                });
        return isUsuarioRestaurante;
    }



    private boolean getCargosUsuarioSolicitante() {
        db.collection(firestoreReferences.getEquipeCOLLECTION())
                .whereEqualTo("usuarioID", usuarioID)
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

    private void isUsuarioAtivo() {
        db.collection(firestoreReferences.getUsuariosCOLLECTION())
                .whereEqualTo("identificador", usuarioID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int size = queryDocumentSnapshots.size();
                        if( size<= 0) {
                            new SnackBarPersonalizada()
                                    .showMensagemLongaClose(v, "Não foi possível encontrar os dados do usuário.", getApplicationContext());
                        }else{
                            handler.postDelayed(task, 1000);
                            getCargosUsuarioSolicitante();
                            getPermissaoRestaurante();
                        }
                    }
                });
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TelaInicial.class));
    }

    private final Runnable task = new Runnable() {
        public void run() {

            adapter = new FirestoreRecyclerAdapter<PostagemForumDuvidas, PostagemForumHolder>(options) {
                @Override
                public void onBindViewHolder(PostagemForumHolder holder, int position, final PostagemForumDuvidas model) {
                    if(usuarioID.equals(model.getUsuarioID()))
                        holder.setConfigsView(isUsuarioAdminstrador, true, getBaseContext());
                    else
                        holder.setConfigsView(isUsuarioAdminstrador, false, getBaseContext());
                    String imgID = model.getImagensID().get(0);
                    if(imgID != null)
                        Glide.with(getBaseContext())
                                .load(imgID)
                                .into(holder.postagemForumImagem);
                    holder.postagemForumTitulo.setText(model.getPostagemForumTitulo());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), DetalharPostagemForumActivity.class);
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
                public PostagemForumHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.cardview_forum_duvida, group, false);
                    return new PostagemForumHolder(view);
                }

            };
            recyclerView.setAdapter(adapter);
            spinner.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    };


}

