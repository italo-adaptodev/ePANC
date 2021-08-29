package com.adapto.panc.Activities.Restaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.abhinay.input.CurrencyEditText;

public class EditarPrato extends AppCompatActivity {
    private Toolbar toolbar;
    private Prato pratoEscolhido;
    private TextInputLayout editarPratoNome, editarPratoIngredientes;
    private CurrencyEditText editarPratoPreco;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_prato);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar informações do prato");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        editarPratoIngredientes = findViewById(R.id.editarPratoIngredientes);
        editarPratoNome = findViewById(R.id.editarPratoNome);
        editarPratoPreco = findViewById(R.id.editarPratoPreco);

        //region intent
        Intent intent = getIntent();
        final String restauranteID = intent.getStringExtra("restauranteID");
        final int ListaPratoID = intent.getIntExtra("ListaPratoID", 0);
        getPrato(restauranteID,ListaPratoID);
        //endregion



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

    private void getPrato(String restauranteID, final int listaPratoID) {
        DocumentReference docRef = db.collection(firestoreReferences.getRestauranteCOLLECTION()).document(restauranteID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurante restaurante = documentSnapshot.toObject(Restaurante.class);
                pratoEscolhido = restaurante.getPratos().get(listaPratoID);
                editarPratoNome.getEditText().setText(pratoEscolhido.getNome());
                editarPratoIngredientes.getEditText().setText(pratoEscolhido.getIngredientes());
               editarPratoPreco.setText("" + pratoEscolhido.getPreco());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}