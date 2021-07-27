package com.adapto.panc.Activities;

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

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.Models.ViewHolder.Restaurante_ListaHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Restaurante_ListarRestaurantesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private View v;
    private FirestoreRecyclerOptions<Restaurante> options;
    private FirestoreReferences collections;
    private Handler handler = new Handler();
    private ProgressBar spinner;
    private MaterialTextView textViewRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_listar_restaurantes);
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        getCargosUsuarioAtivo();
        recyclerView = findViewById(R.id.recyclerview_restaurante_lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        v = findViewById(android.R.id.content);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection(collections.getRestauranteCOLLECTION()).orderBy("nomeRestaurante", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        options = new FirestoreRecyclerOptions.Builder<Restaurante>()
                .setQuery(query, Restaurante.class)
                .setLifecycleOwner(this)
                .build();
        //endregion
        handler.postDelayed(task, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(task, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.postDelayed(task, 1);
    }

    private boolean getCargosUsuarioAtivo() {
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
        db.collection(collections.getRestauranteCOLLECTION()).document(postagemID)
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

    private Runnable task = new Runnable() {
        public void run() {

            adapter = new FirestoreRecyclerAdapter<Restaurante, Restaurante_ListaHolder>(options) {
                @Override
                public void onBindViewHolder(Restaurante_ListaHolder holder, int position, final Restaurante model) {
                    holder.setConfigsView(isUsuarioAdminstrador, getBaseContext());
                    String imgID = model.getPratos().get(0).getImagensID().get(0);
                    Glide.with(getBaseContext())
                            .load(imgID)
                            .into(holder.restauranteListaImagem);
                    holder.nome.setText(model.getNomeRestaurante());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Restaurante_DetalharRestauranteActivity.class);
                            intent.putExtra("restauranteIDDetalhe", model.getRestauranteID());
                            startActivity(intent);
                        }
                    });
                    holder.btnExcluirPostagem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            excluirPostagem(model.getRestauranteID());
                        }
                    });

                }

                @Override
                public Restaurante_ListaHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.cardview_restaurante_lista, group, false);
                    return new Restaurante_ListaHolder(view);
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