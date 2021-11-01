package com.adapto.panc.Activities.Restaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapto.panc.Adapters.MeuRestaurantePratosAdapter;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Restaurante_DetalharRestauranteActivity extends AppCompatActivity {

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
    private Restaurante restaurante;
    private String restauranteID;
    private MaterialTextView textViewRecycler;
    private ImageButton editarRestaurante;
    private Intent addPratoIntent;
    private String restauranteListaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_detalharrestaurante);
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
        v = findViewById(android.R.id.content);
        addPratoIntent = new Intent(this.getBaseContext(), AdicionarPratoActivity.class);
        Intent intent = getIntent();
        restauranteListaID = intent.getStringExtra("restauranteIDDetalhe");
        restauranteDocRef = getRestaurante(restauranteListaID);

    }


    @Override
    protected void onStart() {

        super.onStart();
    }

    private Restaurante getRestaurante(String id) {
        final Restaurante[] restaurante = new Restaurante[1];
        db.collection(firestoreReferences.getRestauranteCOLLECTION())
                .whereEqualTo("id" , id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            restaurante[0] = snap.toObject(Restaurante.class);
                            nomeRestauranteMeuRest.setText(snap.getString("nomeRestaurante"));
                            localizacaoRestaurante.setText(snap.getString("localizacao"));
                            numContatoRestaurante.setText(snap.getString("numContato"));
                            buildRecyclerView(snap.getId());
                        }

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete()){
                    spinner.setVisibility(View.INVISIBLE);
                    infosMeuRest.setVisibility(View.VISIBLE);
                    recyclerViewMeuRest.setVisibility(View.VISIBLE);
                }
            }
        });
        return restaurante[0];
    }

    private void getDocRef (String key){
        docRef = db.collection(firestoreReferences.getRestauranteCOLLECTION()).document(key);
    }

    private void buildRecyclerView(String key){
        getDocRef(key);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restaurante = documentSnapshot.toObject(Restaurante.class);
                pratosAdapter = new MeuRestaurantePratosAdapter(getLayoutInflater(), restaurante, getBaseContext(), false, Restaurante_DetalharRestauranteActivity.this);
                if(restaurante.getPratos().size() == 0)
                    textViewRecycler.setVisibility(View.VISIBLE);
                else
                    textViewRecycler.setVisibility(View.INVISIBLE);
                recyclerViewMeuRest.setAdapter(pratosAdapter);
                restauranteID = documentSnapshot.getId();
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            }
        });


    }

}