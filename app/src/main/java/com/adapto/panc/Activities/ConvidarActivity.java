package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.ConviteEquipeAdministrativa;
import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
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

    private RadioButton CBadministrador,  CBmoderador, RBpesquisador;
    private TextInputLayout identificadorConvidado, justificativa;
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
        CBmoderador = findViewById(R.id.checkBox_moderador);
        RBpesquisador = findViewById(R.id.checkBox_pesquisador);
        btn_send = findViewById(R.id.btn_send);
        identificadorConvidado =  findViewById(R.id.idetificadorConvidado);
        justificativa =  findViewById(R.id.justificativaConvite);
        snackBarPersonalizada = new SnackBarPersonalizada();
        loginSharedPreferences = new LoginSharedPreferences(this);
        firestoreReferences =  new FirestoreReferences();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(!CBadministrador.isChecked() && !CBmoderador.isChecked()  && !RBpesquisador.isChecked()){
                    snackBarPersonalizada.showMensagemLonga(v, "Marque alguma das opções!");
                }else{
                    final List<String> cargos = new ArrayList<>();
                    if (CBadministrador.isChecked()) {
                        cargos.add("ADMINISTRADOR");
                    }
                    if (CBmoderador.isChecked()) {
                        cargos.add("MODERADOR");
                    }
                    if (RBpesquisador.isChecked()) {
                        cargos.add("PESQUISADOR");
                    }

                    final String identificadorConvidado = ConvidarActivity.this.identificadorConvidado.getEditText().getText().toString();
                    final String justificativaConvite = ConvidarActivity.this.justificativa.getEditText().getText().toString();
                    db.collection(firestoreReferences.getUsuariosCOLLECTION())
                            .whereEqualTo("identificador", identificadorConvidado)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.size() == 0){
                                        snackBarPersonalizada.showMensagemLonga(v, "Usuário não encontrado ou identificador incorreto");
                                    }else {
                                        final CollectionReference equipeCollection = db.collection(firestoreReferences.getEquipeCOLLECTION());
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
                                                            ConviteEquipeAdministrativa conviteEquipeAdministrativa =
                                                                    new ConviteEquipeAdministrativa(identificadorConvidado, justificativaConvite, cargos, loginSharedPreferences.getIdentifier());
                                                            FirebaseFirestore.getInstance().collection(firestoreReferences.getConviteEquipeAdministrativaCOLLECTION())
                                                                    .add(conviteEquipeAdministrativa)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            documentReference.update("timestamp", new Timestamp(new Date()));
                                                                            documentReference.update("id", documentReference.getId());
                                                                            snackBarPersonalizada.showMensagemLonga(v, "O convite foi enviado para análise." );
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
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