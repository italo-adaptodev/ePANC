package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConvidarActivity extends AppCompatActivity {

    private CheckBox CBadministrador,  CBmoderador;
    private TextInputLayout identificadorConvidado;
    private Button btn_send;
    private FirebaseFirestore db;
    private SnackBarPersonalizada snackBarPersonalizada;
    private LoginSharedPreferences loginSharedPreferences;
    private FirestoreReferences firestoreReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convidar);

        db = FirebaseFirestore.getInstance();
        CBadministrador = findViewById(R.id.checkBox_administrador);
        CBmoderador = findViewById(R.id.checkBox3_moderador);
        btn_send = findViewById(R.id.btn_send);
        identificadorConvidado =  findViewById(R.id.idetificadorConvidado);
        snackBarPersonalizada = new SnackBarPersonalizada();
        loginSharedPreferences = new LoginSharedPreferences(this);
        firestoreReferences =  new FirestoreReferences();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(!CBadministrador.isChecked() && !CBmoderador.isChecked()){
                    snackBarPersonalizada.showMensagemLonga(v, "Marque alguma das opções!");
                }else
                    {
                    final List<String> cargos = new ArrayList<>();
                    if (CBadministrador.isChecked()) {
                        cargos.add("ADMINISTRADOR");
                    }
                    if (CBmoderador.isChecked()) {
                        cargos.add("MODERADOR");
                    }

                    final String identificadorConvidado = ConvidarActivity.this.identificadorConvidado.getEditText().getText().toString();
                    db.collection(firestoreReferences.getUsuariosCOLLECTION())
                            .whereEqualTo("identificador", identificadorConvidado)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.size() == 0){
                                        snackBarPersonalizada.showMensagemLonga(v, "Usuário não encontrado ou identificador incorreto");
                                    }else {
                                        final CollectionReference equipeCollection = db.collection(firestoreReferences.getEQUIPECOLLECTION());
                                        final DocumentSnapshot snap = queryDocumentSnapshots.getDocuments().get(0);
                                        equipeCollection.whereEqualTo("usuarioID", identificadorConvidado)
                                        .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if(queryDocumentSnapshots.size() > 0) {
                                                            DocumentSnapshot snap1 = queryDocumentSnapshots.getDocuments().get(0);
                                                            snap1.getReference().update("cargosAdministrativos", cargos);
                                                            snackBarPersonalizada.showMensagemLonga(v, "Cargos atualizados com sucesso" );
                                                        }else{
                                                            MembroEquipe membroEquipe = new MembroEquipe(snap.getString("identificador"), loginSharedPreferences.getIdentifier(),  cargos);
                                                            FirebaseFirestore.getInstance().collection(firestoreReferences.getEQUIPECOLLECTION())
                                                                    .add(membroEquipe)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            documentReference.update("timestamp", new Timestamp(new Date()));
                                                                            snackBarPersonalizada.showMensagemLonga(v, "O usuario foi cadastrado como membro da Equipe Administrativa" );
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("doc", "no");
                                            }
                                        });


                                    }
                                }
                            });
                }
            }
        });
    }




}