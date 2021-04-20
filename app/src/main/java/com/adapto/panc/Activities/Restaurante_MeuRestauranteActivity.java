package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapto.panc.Adapters.ForumComentarioAdapter;
import com.adapto.panc.Adapters.MeuRestaurantePratosAdapter;
import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Restaurante_MeuRestauranteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private String usuarioID;
    private Restaurante restauranteDocRef;
    private TextView nomeRestauranteMeuRest;
    private ProgressBar spinner;
    private ConstraintLayout infosMeuRest;
    private RecyclerView recyclerViewMeuRest;
    private FloatingActionButton addPrato;
    private String restauranteID;
    private MeuRestaurantePratosAdapter pratosAdapter;
    private DocumentReference docRef;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_meurestaurante);
        db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.progressBarMeuRest);
        spinner.setVisibility(View.VISIBLE);
        infosMeuRest = findViewById(R.id.infosMeuRest);
        recyclerViewMeuRest = findViewById(R.id.recyclerViewMeuRest);
        recyclerViewMeuRest.setLayoutManager(new LinearLayoutManager(this));
        nomeRestauranteMeuRest = findViewById(R.id.nomeRestauranteMeuRest);
        addPrato = findViewById(R.id.addPratoMeuRest);
        v = findViewById(android.R.id.content);
        final Intent addPratoIntent = new Intent(this.getBaseContext(), AdicionarPratoARestauranteActivity.class);
        restauranteDocRef = getRestauranteByUsuarioID(new LoginSharedPreferences(getApplicationContext()).getIdentifier());
        addPrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPratoIntent.putExtra("restauranteID", restauranteID);
                startActivity(addPratoIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        buildRecyclerView();
    }

    private Restaurante getRestauranteByUsuarioID(String identificador) {
        final Restaurante[] restaurante = new Restaurante[1];
        db.collection(firestoreReferences.getRestauranteCOLLECTION())
                .whereEqualTo("usuarioID", identificador).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                    restaurante[0] = snap.toObject(Restaurante.class);
                    nomeRestauranteMeuRest.setText(snap.getString("nomeRestaurante"));
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
                Restaurante restaurante = documentSnapshot.toObject(Restaurante.class);
                pratosAdapter = new MeuRestaurantePratosAdapter(getLayoutInflater(), restaurante.getPratos());
                recyclerViewMeuRest.setAdapter(pratosAdapter);
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            }
        });


    }

}