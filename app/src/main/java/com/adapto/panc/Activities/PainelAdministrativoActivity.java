package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Models.Database.ConviteEquipeAdministrativa;
import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.Models.ViewHolder.FirestoreConviteEquipeAdministrativaViewHolder;
import com.adapto.panc.Models.ViewHolder.FirestoreEquipeAdministrativaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PainelAdministrativoActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private SnackBarPersonalizada snackBarPersonalizada;
    private View v;
    private FirestoreReferences firestoreReferences;
    private MaterialTextView textViewRecycler;
    private String usuarioID;
    private boolean isUsuarioAdminstrador = false;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel_administrativo);
        usuarioID = new LoginSharedPreferences(getApplicationContext()).getIdentifier();
        firestoreReferences = new FirestoreReferences();
        db = FirebaseFirestore.getInstance();
        getCargosUsuarioSolicitante();
        snackBarPersonalizada = new SnackBarPersonalizada();
        v = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerViewConvites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);

        query = db
                .collection(firestoreReferences.getConviteEquipeAdministrativaCOLLECTION()).orderBy("timestamp", Query.Direction.ASCENDING);

        getItemCountRecyclerView();

        FirestoreRecyclerOptions<ConviteEquipeAdministrativa> options = new FirestoreRecyclerOptions.Builder<ConviteEquipeAdministrativa>()
                .setQuery(query, ConviteEquipeAdministrativa.class)
                .setLifecycleOwner(this)
                .build()
                ;

        adapter = new FirestoreRecyclerAdapter<ConviteEquipeAdministrativa, FirestoreConviteEquipeAdministrativaViewHolder>(options) {
            @Override
            public void onBindViewHolder(FirestoreConviteEquipeAdministrativaViewHolder holder, int position, final ConviteEquipeAdministrativa model) {
                getDadosUsuario(model.getIdentificador(), model.getIndicadoPor(), holder);
                holder.conviteEquipeAdministrativaCargosAdministrativos.setText(model.getCargos().toString());
                holder.conviteJustificativa.setText(model.getJustificativa());
                holder.aceitarconvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aceitarConvite(model);
                    }
                });
                holder.negarconvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negarConvite(model);
                    }
                });
            }

            @Override
            public FirestoreConviteEquipeAdministrativaViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cardview_convite_equipe_adminstrativa, group, false);
                return new FirestoreConviteEquipeAdministrativaViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void getItemCountRecyclerView() {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().size() < 1){
                    textViewRecycler.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void aceitarConvite(final ConviteEquipeAdministrativa model) {
        final CollectionReference equipeCollection = db.collection(firestoreReferences.getEquipeCOLLECTION());
        equipeCollection.whereEqualTo("usuarioID", model.getIdentificador())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            DocumentSnapshot snap1 = queryDocumentSnapshots.getDocuments().get(0);
                            snap1.getReference().update("cargosAdministrativos", model.getCargos());
                            snackBarPersonalizada.showMensagemLonga(v, "Usuario já é membro da equipe, cargos atualizados com sucesso!" );
                        }else{
                            MembroEquipe membroEquipe =
                                    new MembroEquipe(model.getIdentificador(), model.getIndicadoPor(), model.getCargos());
                            FirebaseFirestore.getInstance().collection(firestoreReferences.getEquipeCOLLECTION())
                                    .add(membroEquipe)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.update("timestamp", new Timestamp(new Date()));
                                            snackBarPersonalizada.showMensagemLonga(v, "O usuario foi adicionado a equipe administrativa com o cargo "
                                                    + model.getCargos().toString().toUpperCase() );
                                        }
                                    });
                        }
                        apagarConvite(model, null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void negarConvite(final ConviteEquipeAdministrativa model) {
        apagarConvite(model, "Convite negado");
    }

    private void apagarConvite(ConviteEquipeAdministrativa model, final String mensagem) {
        db.collection(firestoreReferences.getConviteEquipeAdministrativaCOLLECTION()).document(model.getId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(mensagem != null)
                    new SnackBarPersonalizada().showMensagemLonga(v, mensagem);
                getItemCountRecyclerView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir o convite.");
            }
        });
    }

    private String getDadosUsuario(String usuarioID, String indicadoPor, final FirestoreConviteEquipeAdministrativaViewHolder holder) {
        final String[] nomeUsuario = new String[1];
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            holder.conviteNomeConvidado.setText(snap.getString("nome"));
                        }
                    }
                });
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", indicadoPor).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            holder.conviteIndicadoPor.setText(snap.getString("nome"));
                        }
                    }
                });
        return nomeUsuario[0];
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
                                    query = db
                                            .collection(firestoreReferences.getConviteEquipeAdministrativaCOLLECTION()).orderBy("timestamp", Query.Direction.ASCENDING);
                                }else if(cargos.contains("MODERADOR")){
                                    List listaCargos = new ArrayList();
                                    listaCargos.add("MODERADOR");
                                    listaCargos.add("PESQUISADOR");
                                    query = db
                                            .collection(firestoreReferences.getConviteEquipeAdministrativaCOLLECTION()).orderBy("timestamp", Query.Direction.ASCENDING)
                                            .whereIn("cargos", listaCargos);
                                }
                            }

                        }
                    }
                });
        return isUsuarioAdminstrador;
    }
}