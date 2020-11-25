package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private MaterialTextView cadastrarTextview;
    private MaterialButton loginButton;
    Intent cadastroIntent;
    Intent telaInicialIntent;
    private FirebaseAuth mAuth;
    private TextInputLayout loginTextField;
    private TextInputLayout senhaTexfield;
    private SnackBarPersonalizada snackbar;
    private LoginSharedPreferences loginSessionManager;
    private View v;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        cadastrarTextview = findViewById(R.id.cadastrarTextView);
        mAuth = FirebaseAuth.getInstance();
        loginTextField = findViewById(R.id.loginTextField);
        senhaTexfield = findViewById(R.id.senhaTexfield);
        cadastroIntent = new Intent(this, CadastroActivity.class);
        telaInicialIntent = new Intent(this, TelaInicialActivity.class);
        snackbar = new SnackBarPersonalizada();
        v = findViewById(android.R.id.content);
        loginSessionManager = new LoginSharedPreferences(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        //region LISTENERS

        cadastrarTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(cadastroIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(verificarCampos()) {
                    db.collection("Usuarios")
                            .whereEqualTo("identificador", loginTextField.getEditText().getText().toString())
                            .whereEqualTo("senha", senhaTexfield.getEditText().getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                                        loginSessionManager.createLoginSession(snap.getString("identificador"));
                                        telaInicialIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        telaInicialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(telaInicialIntent);
                                    }
                                    snackbar.showMensagemLonga(v, "Não foi possivel realizar login. Verifique suas credenciais e tente novamente");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackbar.showMensagemLonga(v, "Não foi possível realizar login. ERROR - " + e.getLocalizedMessage());
                        }
                    });
                }
            }
        });

    }

    private boolean verificarCampos() {
        if(loginTextField.getEditText().getText().toString().isEmpty() || senhaTexfield.getEditText().getText().toString().isEmpty()){
            snackbar.showMensagemLonga(v, "Preencha todos os campos corretamente!");
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        //loginSessionManager.checkLogin();
    }


    private void updateUI(@Nullable String mensagem) {
            startActivity(telaInicialIntent);
            finish();
    }
}
