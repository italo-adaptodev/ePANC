package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.Models.ViewHolder.FirestoreEquipeAdministrativaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListarEquipeActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private SnackBarPersonalizada snackBarPersonalizada;
    private View v;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private MaterialTextView textViewRecycler;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_equipe);
        snackBarPersonalizada = new SnackBarPersonalizada();
        v = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerViewEquipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        db = FirebaseFirestore.getInstance();
        Query query = db
                .collection(firestoreReferences.getEquipeCOLLECTION()).orderBy("cargosAdministrativos", Query.Direction.ASCENDING).orderBy("timestamp", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        FirestoreRecyclerOptions<MembroEquipe> options = new FirestoreRecyclerOptions.Builder<MembroEquipe>()
                .setQuery(query, MembroEquipe.class)
                .setLifecycleOwner(this)
                .build()
                ;

        adapter = new FirestoreRecyclerAdapter<MembroEquipe, FirestoreEquipeAdministrativaViewHolder>(options) {
            @Override
            public void onBindViewHolder(FirestoreEquipeAdministrativaViewHolder holder, int position, final MembroEquipe model) {
                getDadosUsuario(model.getUsuarioID(), model.getIndicadoPor(), holder);
                holder.equipeAdministrativaCargosAdministrativos.setText(model.getCargosAdministrativos().toString());
            }

            @Override
            public FirestoreEquipeAdministrativaViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cardview_equipe_adminstrativa, group, false);
                return new FirestoreEquipeAdministrativaViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        //region Toolbar
        toolbar = findViewById(R.id.toolbarEquipe);
        toolbar.setTitle("Equipe Administrativa");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        //endregion
    }

    private String getDadosUsuario(String usuarioID, String indicadoPor, final FirestoreEquipeAdministrativaViewHolder holder) {
        final String[] nomeUsuario = new String[1];
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            holder.equipeAdministrativaNomeIntegrante.setText(snap.getString("nome"));
                        }
                    }
                });
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", indicadoPor).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            holder.equipeAdministrativaIndicadoPor.setText(snap.getString("nome"));
                        }
                    }
                });
        return nomeUsuario[0];
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