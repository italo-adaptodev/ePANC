package com.adapto.panc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.adapto.panc.Models.Database.MembroEquipe;
import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConvidarActivity extends AppCompatActivity {

    private CheckBox CBadministrador,  CBmoderador;
    private TextInputLayout identificadorConvidado;
    private Button btn_send;
    private FirebaseFirestore db;
    private SnackBarPersonalizada snackBarPersonalizada;
    private LoginSharedPreferences loginSharedPreferences;

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

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(!CBadministrador.isChecked() && !CBmoderador.isChecked()){
                    snackBarPersonalizada.showMensagemLonga(v, "Marque alguma das opções!");
                }else {
                    final List<String> cargos = new ArrayList<>();
                    if (CBadministrador.isChecked()) {
                        cargos.add("ADMINISTRADOR");
                    }
                    if (CBmoderador.isChecked()) {
                        cargos.add("MODERADOR");
                    }

                    db.collection("Usuarios")
                            .whereEqualTo("identificador", identificadorConvidado.getEditText().getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.size() == 0){
                                        snackBarPersonalizada.showMensagemLonga(v, "Usuário não encontrado ou identificador incorreto");
                                    }else {
                                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                                            MembroEquipe membroEquipe = new MembroEquipe(snap.getString("identificador"), loginSharedPreferences.getKEYUSER(),  cargos);
                                            FirebaseFirestore.getInstance().collection("EQUIPE")
                                                    .add(membroEquipe)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    documentReference.update("timestamp", new Timestamp(new Date()));
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }


}