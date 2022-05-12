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
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditarPratoActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Prato pratoEscolhido, pratoAlterado;
    private TextInputLayout editarPratoNome, editarPratoIngredientes, editarPratoDescricao;
    private CurrencyEditText editarPratoPreco;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MaterialButton btnConfirmarAlteraçõesPrato;
    private ReferenciaDatabase referenciaDatabase;
    private String restauranteID;
    private Restaurante restaurante;
    private int listaPratoID;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_prato);
        editarPratoIngredientes = findViewById(R.id.editarPratoingredientesPANC);
        editarPratoDescricao = findViewById(R.id.editarPratodescricao);
        editarPratoNome = findViewById(R.id.editarPratoNome);
        editarPratoPreco = findViewById(R.id.editarPratoPreco);
        btnConfirmarAlteraçõesPrato = findViewById(R.id.btnConfirmarAlteraçõesPrato);
        referenciaDatabase = new ReferenciaDatabase();
        v = findViewById(android.R.id.content);

        //region Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar informações do prato");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        //endregion

        //region intent
        Intent intent = getIntent();
        restauranteID = intent.getStringExtra("restauranteID");
        listaPratoID = intent.getIntExtra("ListaPratoID", 0);
        getPratoEscolhido(restauranteID, listaPratoID);
        //endregion

        //region ClickListener
        btnConfirmarAlteraçõesPrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfosAlteradas();
                updateListaPratosFirestore();
            }
        });
        //endregion



    }

    private void getInfosAlteradas() {
        pratoAlterado = new Prato();
        pratoAlterado.setNome(editarPratoNome.getEditText().getText().toString());
        pratoAlterado.setIngredientesPANC(editarPratoIngredientes.getEditText().getText().toString());
        pratoAlterado.setDescricao(editarPratoDescricao.getEditText().getText().toString());
        Double precoDouble = editarPratoPreco.getNumericValue();
        pratoAlterado.setPreco(precoDouble.toString());
        pratoAlterado.setImagensID(pratoEscolhido.getImagensID());
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

    private void getPratoEscolhido(String restauranteID, final int listaPratoID) {
        DocumentReference docRef = db.collection(firestoreReferences.getRestauranteCOLLECTION()).document(restauranteID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restaurante = documentSnapshot.toObject(Restaurante.class);
                pratoEscolhido = restaurante.getPratos().get(listaPratoID);
                editarPratoNome.getEditText().setText(pratoEscolhido.getNome());
                editarPratoIngredientes.getEditText().setText(pratoEscolhido.getIngredientesPANC());
                editarPratoDescricao.getEditText().setText(pratoEscolhido.getDescricao());
                Double precoDouble = Double.parseDouble(pratoEscolhido.getPreco());
                editarPratoPreco.setText(precoDouble.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void updateListaPratosFirestore() {
        restaurante.getPratos().remove(listaPratoID);
        restaurante.getPratos().add(pratoAlterado);
       referenciaDatabase.getDatabaseFirestore().collection(firestoreReferences.getRestauranteCOLLECTION())
                .document(restauranteID)
                .update("pratos",restaurante.getPratos())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        atualizarActivity(true);
                    }
                });
    }

    private void atualizarActivity(boolean isEdited) {
        finish();
        onBackPressed();
        if(isEdited)
            new SnackBarPersonalizada().showMensagemLonga(v, "Prato editado com sucesso.");
        else
            new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível editar o prato. Tente novamente.");

    }
}