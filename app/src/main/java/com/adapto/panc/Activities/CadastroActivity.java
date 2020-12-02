package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.adapto.panc.WebViewConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout nomeCadastroUsuario, loginCadastroUsuario, senhaCadastroUsuario;
    private SnackBarPersonalizada snackbar;
    private MaterialButton cadastroButton, prosseguirButton;
    private RadioGroup radioGroup;
    private RadioButton radioProdutor, radioConsumidor;
    private static String URL_PRODUTOR = "https://docs.google.com/forms/d/e/1FAIpQLScutVCI0iaI5-tzGDIfo6x7XlczL35043m0XpVrloLk2BKdMA/viewform?usp=sf_link";
    private static String URL_CONSUMIDOR = "https://docs.google.com/forms/d/e/1FAIpQLSc1AZ97PEwRDvcZHsPY9jmumsMseXdLq4Ha7uh6TNH8VGpdPQ/viewform?usp=sf_link";
    private View v;
    private LoginSharedPreferences loginSessionManager;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        final View cadastro2 = getLayoutInflater().inflate(R.layout.activity_cadastro2, null);

        nomeCadastroUsuario = findViewById(R.id.nomeCadastro);
        loginCadastroUsuario = findViewById(R.id.loginCadastro);
        senhaCadastroUsuario = findViewById(R.id.senhaCadastro);
        cadastroButton = cadastro2.findViewById(R.id.cadastroButton);
        prosseguirButton = findViewById(R.id.prosseguirButton);
        snackbar = new SnackBarPersonalizada();
        loginCadastroUsuario.setHelperTextEnabled(true);
        loginCadastroUsuario.setHelperText("Telefone com DDD ou email");
        v = findViewById(android.R.id.content);
        radioProdutor = cadastro2.findViewById(R.id.radioProdutor);
        radioConsumidor = cadastro2.findViewById(R.id.radioConsumidor);
        radioGroup = cadastro2.findViewById(R.id.radioGroup);

        loginSessionManager = new LoginSharedPreferences(getApplicationContext());
        prosseguirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setContentView(cadastro2);

            }
        });
        cadastroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificarCampos()){
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if(selectedId == radioProdutor.getId()) {
                       createUsuario("PRODUTOR");
                       startIntentQuestionario(URL_PRODUTOR);
                    } else if(selectedId == radioConsumidor.getId()) {
                        createUsuario("CONSUMIDOR");
                    }
                }
            }
        });


        db = FirebaseFirestore.getInstance();
    }


    private boolean verificarCampos() {

        if(loginCadastroUsuario.getEditText().getText().toString().isEmpty() || nomeCadastroUsuario.getEditText().getText().toString().isEmpty()
            || senhaCadastroUsuario.getEditText().getText().toString().isEmpty()) {
            snackbar.showMensagemLonga(v, "Preencha todos os campos corretamente!");
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createUsuario(String cargo) {
        Usuario novoUsuario = getCamposUsuario();
        novoUsuario.addCargo(cargo);
        createFirestoreUser(novoUsuario, cargo);
    }

    private Usuario getCamposUsuario() {
        String identificador = loginCadastroUsuario.getEditText().getText().toString();
        String senha = senhaCadastroUsuario.getEditText().getText().toString();
        String nome = nomeCadastroUsuario.getEditText().getText().toString();
        return new Usuario(identificador, senha, nome);
    }

    private void createFirestoreUser(final Usuario novoUsuario, final String cargo) {

        Task<QuerySnapshot> query = db.collection("Usuarios").get();
        if(query.isSuccessful()) {
                query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (final QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.getString("identificador").equals(novoUsuario.getIdentificador()))
                            snackbar.showMensagemLonga(v, "Identificador em uso ou inválido");
                        else
                            db.collection("Usuarios")
                                    .add(novoUsuario)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            documentReference
                                                    .update("id", documentReference.getId())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                                                            if(cargo.equals("PRODUTOR"))
                                                                startIntentQuestionario(URL_PRODUTOR);
                                                            else
                                                                startIntentQuestionario(URL_CONSUMIDOR);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            snackbar.showMensagemLonga(v, e.getMessage());
                                        }
                                    });
                    }
                }
            });
        }else{
           FirebaseFirestore.getInstance().collection("Usuarios")
                    .add(novoUsuario)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            documentReference
                                    .update("id", documentReference.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loginSessionManager.createLoginSession(documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackbar.showMensagemLonga(v, e.getMessage());
                        }
                    });
        }
    }

    private void startIntentQuestionario(String URL) {
        Intent intent = new Intent(CadastroActivity.this, WebViewConfig.class);
        intent.putExtra("URL", URL);
        startActivity(intent);
    }
}




