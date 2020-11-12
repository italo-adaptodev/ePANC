package com.adapto.panc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private MaterialTextView cadastrarTextview;
    private MaterialButton loginButton;
    Intent cadastroIntent;
    Intent telaInicialIntent;
    private FirebaseAuth mAuth;
    private TextInputLayout emailTextfield;
    private TextInputLayout senhaTexfield;
    private SnackBarPersonalizada snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        cadastrarTextview = findViewById(R.id.cadastrarTextView);
        mAuth = FirebaseAuth.getInstance();
        emailTextfield = findViewById(R.id.emailTextfield);
        senhaTexfield = findViewById(R.id.senhaTexfield);
        cadastroIntent = new Intent(this, CadastroActivity.class);
        telaInicialIntent = new Intent(this, TelaInicialActivity.class);
        snackbar = new SnackBarPersonalizada();

        //region LISTENERS

        cadastrarTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(cadastroIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //singIn(emailTextfield.getEditText().getText().toString(), senhaTexfield.getEditText().getText().toString());
                startActivity(telaInicialIntent);
            }
        });

        //endregion

    }

    /*@Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser, null);
    }*/

    private void singIn(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, null);
                        } else {
                            updateUI(null, task.getException().getMessage());
                        }
                    }
                });
    }



    private void updateUI(@Nullable FirebaseUser user, @Nullable String mensagem) {

        if(user == null) {
            snackbar.showMensagemLonga(findViewById(android.R.id.content), mensagem);
        }else{
            startActivity(telaInicialIntent);
            finish();
        }
    }
}
