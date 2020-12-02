package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.Models.Database.PostagemForum;
import com.adapto.panc.Models.ViewHolder.FirestoreEquipeAdministrativaViewHolder;
import com.adapto.panc.Models.ViewHolder.FirestoreEquipeAdministrativaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ListarEquipeActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private SnackBarPersonalizada snackBarPersonalizada;
    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_equipe);
        snackBarPersonalizada = new SnackBarPersonalizada();
        v = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerViewEquipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        Query query = db
                .collection("EQUIPE").orderBy("cargosAdministrativos", Query.Direction.ASCENDING).orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<MembroEquipe> options = new FirestoreRecyclerOptions.Builder<MembroEquipe>()
                .setQuery(query, MembroEquipe.class)
                .setLifecycleOwner(this)
                .build();

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
    }

    private String getDadosUsuario(String usuarioID, String indicadoPor, final FirestoreEquipeAdministrativaViewHolder holder) {
        final String[] nomeUsuario = new String[1];
        db.collection("Usuarios").whereEqualTo("identificador", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            holder.equipeAdministrativaNomeIntegrante.setText(snap.getString("nome"));
                        }
                    }
                });
        db.collection("Usuarios").whereEqualTo("identificador", indicadoPor).get()
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