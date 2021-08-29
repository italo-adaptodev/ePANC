package com.adapto.panc.Activities.Restaurante;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Adapters.MeuRestaurantePratosAdapter;
import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Restaurante_DetalharRestauranteDONOActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private String usuarioID;
    private Restaurante restauranteDocRef;
    private TextView nomeRestauranteMeuRest, localizacaoRestaurante, numContatoRestaurante;
    private ProgressBar spinner;
    private ConstraintLayout infosMeuRest;
    private RecyclerView recyclerViewMeuRest;
    private FloatingActionButton addPrato;
    private MeuRestaurantePratosAdapter pratosAdapter;
    private DocumentReference docRef;
    private View v;
    private String restauranteID;
    private MaterialTextView textViewRecycler;
    private boolean isUsuarioDonoRestaurante = false;
    private ImageButton editarRestaurante;
    private String donoRestauranteID;
    private Intent addPratoIntent;
    private String restauranteListaID;
    private LifecycleObserver mObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_detalharrestaurantedono);
        db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.progressBarMeuRest);
        spinner.setVisibility(View.VISIBLE);
        infosMeuRest = findViewById(R.id.infosMeuRest);
        recyclerViewMeuRest = findViewById(R.id.recyclerViewMeuRest);
        recyclerViewMeuRest.setLayoutManager(new LinearLayoutManager(this));
        textViewRecycler = findViewById(R.id.emptyRecyclerViewTXT);
        nomeRestauranteMeuRest = findViewById(R.id.nomeRestauranteMeuRest);
        localizacaoRestaurante = findViewById(R.id.localizacaoRestaurante);
        numContatoRestaurante = findViewById(R.id.numContatoRestaurante);
        editarRestaurante = findViewById(R.id.editarRestaurante);
        addPrato = findViewById(R.id.addPratoMeuRest);
        v = findViewById(android.R.id.content);
        addPratoIntent = new Intent(this.getBaseContext(), AdicionarPratoActivity.class);
        donoRestauranteID = new LoginSharedPreferences(getApplicationContext()).getIdentifier();
        restauranteDocRef = getRestaurante(donoRestauranteID);
        addPrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPratoIntent.putExtra("restauranteID", restauranteID);
                startActivityForResult(addPratoIntent, Activity.RESULT_OK);
            }
        });


    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    private Restaurante getRestaurante(String identificador) {
        final Restaurante[] restaurante = new Restaurante[1];
        db.collection(firestoreReferences.getRestauranteCOLLECTION())
                .whereEqualTo("usuarioID", identificador).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            restaurante[0] = snap.toObject(Restaurante.class);
                            nomeRestauranteMeuRest.setText(snap.getString("nomeRestaurante"));
                            localizacaoRestaurante.setText(snap.getString("localizacao"));
                            numContatoRestaurante.setText(snap.getString("numContato"));
                            buildRecyclerView(restaurante[0]);
                        }

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    editarRestaurante.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                    recyclerViewMeuRest.setVisibility(View.VISIBLE);
                }
            }
        });
        return restaurante[0];
    }

    private void buildRecyclerView(Restaurante restaurante){
            pratosAdapter = new MeuRestaurantePratosAdapter(getLayoutInflater(), restaurante, getBaseContext(), true, Restaurante_DetalharRestauranteDONOActivity.this);
            if(restaurante.getPratos().size() == 0)
                textViewRecycler.setVisibility(View.VISIBLE);
            else
                textViewRecycler.setVisibility(View.INVISIBLE);
            recyclerViewMeuRest.setAdapter(pratosAdapter);
            restauranteID = restaurante.getRestauranteID();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    } //onActivityResult

    @Override
    protected void onRestart() {
        super.onRestart();
        atualizarActivity();
    }

    private void atualizarActivity() {
        finish();
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
        startActivity(new Intent(this, Restaurante_DetalharRestauranteDONOActivity.class));
    }
}