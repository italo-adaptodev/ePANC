package com.adapto.panc.Activities.BibliotecaPANC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.adapto.panc.Activities.ForumDuvida.ForumDuvidasActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Adapters.ItemBibliotecaPANCAdapter;
import com.adapto.panc.Models.Database.ItemBiblioteca;
import com.adapto.panc.Models.ViewHolder.ItemBibliotecaPANCViewHolder;
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

import java.util.ArrayList;
import java.util.List;

public class ListarItensBibliotecaPANCActivity extends AppCompatActivity {

    private FloatingActionButton criarPostagemFAB;
    private Intent criarPostagemIntent;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    private boolean isUsuarioAdminstrador = false;
    private View v;
    private FirestoreRecyclerOptions<ItemBiblioteca> options;
    private FirestoreReferences collections;
    private Handler handler = new Handler();
    private ProgressBar spinner;
    private boolean isUsuarioAutorReceita = false;
    private MaterialTextView textViewRecycler;
    private List<ItemBiblioteca> itemBibliotecaList;
    private Toolbar toolbar;
    private ItemBibliotecaPANCAdapter itensFiltradosAdapter;
    private String identifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_itens_biblioteca_panc);
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        getPermissaoAutorReceita();
        getCargosUsuarioSolicitante();
        recyclerView = findViewById(R.id.listarItensBibliotecaPANCRecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        v = findViewById(android.R.id.content);
        spinner = findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        identifier = new LoginSharedPreferences(this).getIdentifier();


        //region RECYCLER VIEW POSTAGENS
        Query query = db
                .collection(collections.getBibliotecaCOLLECTION()).orderBy("timestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                itemBibliotecaList = queryDocumentSnapshots.toObjects(ItemBiblioteca.class);
            }
        });

        options = new FirestoreRecyclerOptions.Builder<ItemBiblioteca>()
                .setQuery(query, ItemBiblioteca.class)
                .setLifecycleOwner(this)
                .build();
        //endregion

        criarPostagemFAB = findViewById(R.id.criarPostagemFAB);
        criarPostagemIntent = new Intent(this.getBaseContext(), CriarItemBibliotecaPANCActivity.class );
        criarPostagemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(criarPostagemIntent);
            }
        });

        //region Toolbar
        toolbar = findViewById(R.id.toolbar_listar_itens_biblioteca_PANC);
        toolbar.setTitle("Biblioteca Científica");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        //endregion

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
                .whereEqualTo("usuarioID",   identifier)
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
        db.collection(collections.getBibliotecaCOLLECTION()).document(postagemID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Item excluído com sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir este item. ERRO: " + e.getLocalizedMessage());
            }
        });
    }

    private void getPermissaoAutorReceita() {
        db.collection(collections.getBibliotecaCOLLECTION())
                .whereEqualTo("autor_usuarioID", identifier)
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
            adapter = new FirestoreRecyclerAdapter<ItemBiblioteca, ItemBibliotecaPANCViewHolder>(options) {
                @Override
                public void onBindViewHolder(ItemBibliotecaPANCViewHolder holder, int position, final ItemBiblioteca model) {
                    if(identifier.equals(model.getUsuarioID()))
                        holder.setConfigsView(isUsuarioAdminstrador, true, getBaseContext());
                    else
                        holder.setConfigsView(isUsuarioAdminstrador, false, getBaseContext());
                    String imgID = model.getImagensID().get(0);
                    if(imgID != null)
                        Glide.with(getBaseContext())
                                .load(imgID)
                                .into(holder.itemBibliotecaImagem);
                    holder.itemBibliotecaTitulo.setText(model.getItemBibliotecaTitulo());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), DetalharItemBibliotecaPANCActivity.class);
                            intent.putExtra("itemIDDetalhe", model.getItemID());
                            startActivity(intent);
                        }
                    });
                    holder.btnExcluirPostagem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            excluirPostagem(model.getItemID());
                        }
                    });

                }

                @Override
                public ItemBibliotecaPANCViewHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.cardview_item_biblioteca_panc, group, false);
                    return new ItemBibliotecaPANCViewHolder(view);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                List<ItemBiblioteca> escolhidas = new ArrayList<>();
                for (ItemBiblioteca itemBiblioteca : itemBibliotecaList) {
                    if (itemBiblioteca.getItemBibliotecaDesc().contains(query))
                        if (!escolhidas.contains(itemBiblioteca))
                            escolhidas.add(itemBiblioteca);
                }
                if(escolhidas.size() == 0)
                    textViewRecycler.setVisibility(View.VISIBLE);
                itensFiltradosAdapter = new ItemBibliotecaPANCAdapter(getLayoutInflater(), escolhidas, getBaseContext(), ListarItensBibliotecaPANCActivity.this, isUsuarioAdminstrador, identifier);
                recyclerView.setAdapter(itensFiltradosAdapter);
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setAdapter(adapter);
                textViewRecycler.setVisibility(View.INVISIBLE);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}