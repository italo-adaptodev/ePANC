package com.adapto.panc.Activities.Restaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class EditarInformacoesRestauranteDONOActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String restauranteID;
    private DocumentReference restauranteDocRef;
    private Intent intent;
    private ReferenciaDatabase referenciaDatabase;
    private FirestoreReferences firestoreReferences =  new FirestoreReferences();
    private TextInputLayout nomeRestaurante, localizacaoRestaurante, telRestaurante;
    private Restaurante restaurante;
    private View v;
    private MaterialButton btnConfirmarAlteracaoRestauranteInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_informacoes_restaurante_dono);
        referenciaDatabase = new ReferenciaDatabase();
        intent = getIntent();
        restauranteID = intent.getStringExtra("restauranteID");
        getRestauranteByID();
        nomeRestaurante = findViewById(R.id.nomeRestauranteEditarInfo);
        localizacaoRestaurante = findViewById(R.id.localizacaoRestauranteEditarInfo);
        telRestaurante = findViewById(R.id.numeroRestauranteEditarInfo);
        v = findViewById(android.R.id.content);
        btnConfirmarAlteracaoRestauranteInfo =  findViewById(R.id.btnConfirmarAlteracaoRestauranteInfo);


        //region Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar informações do restaurante");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        //endregion

        btnConfirmarAlteracaoRestauranteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInformacoesRestaurante();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, Restaurante_DetalharRestauranteDONOActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void getRestauranteByID() {
        restauranteDocRef = referenciaDatabase.getDatabaseFirestore().collection(firestoreReferences.getRestauranteCOLLECTION()).document(restauranteID);
        restauranteDocRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        restaurante = documentSnapshot.toObject(Restaurante.class);
                        nomeRestaurante.getEditText().setText(restaurante.getNomeRestaurante());
                        localizacaoRestaurante.getEditText().setText(restaurante.getLocalizacao());
                        telRestaurante.getEditText().setText(restaurante.getNumContato());
                    }
                });
    }

    private void updateInformacoesRestaurante() {
        referenciaDatabase.getDatabaseFirestore().collection(firestoreReferences.getRestauranteCOLLECTION())
                .document(restauranteID)
                .update("nomeRestaurante", nomeRestaurante.getEditText().getText().toString()
                        , "localizacao", localizacaoRestaurante.getEditText().getText().toString(),
                        "numContato", telRestaurante.getEditText().getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        atualizarActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível editar as informações do restaurante. Tente novamente mais tarde");
            }
        });
    }

    private void atualizarActivity() {
        finish();
        onBackPressed();
        new SnackBarPersonalizada().showMensagemLonga(v, "Prato editado com sucesso.");
    }
}