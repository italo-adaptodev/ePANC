package com.adapto.panc;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Models.Database.PostagemForum;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DetalharPostagemForumActivity extends AppCompatActivity {

    private ArrayList<FirestoreForumComentario> arrayList;
    private FirestoreRecyclerAdapter adapter;
    private TextView postagemDetalhadaAutor, postagemDetalhadaData,postagemDetalhadaTexto;
    private ImageView postagemForumDetalharImagem;
    private RecyclerView recyclerViewComentarios;
    private EditText comentario;
    private ImageButton btnEnviarComentario;
    private int qtdComentarios;
    private Boolean check = false;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private DocumentReference postagem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_postagem_forum);
        postagemDetalhadaAutor = findViewById(R.id.detalhar_forum_autor);
        postagemDetalhadaData = findViewById(R.id.detalhar_forum_data);
        postagemForumDetalharImagem = findViewById(R.id.postagemForumDetalharImagem);
        recyclerViewComentarios = findViewById(R.id.recyclerview_detalhar_respostas_forum);
        postagemDetalhadaTexto = findViewById(R.id.detalhar_forum_texto);
        comentario = findViewById(R.id.forum_detalhar_comentario);
        btnEnviarComentario = findViewById(R.id.forum_btn_enviar_comentario);
        Intent intent = getIntent();
        final String postagemKey = intent.getStringExtra("postagemIDDetalhe");
        ScrollView sv = findViewById(R.id.scrollView);
        sv.scrollTo(0, 0);
        postagem = recuperarPostagem(postagemKey);
        iniciarRecyclerViewComentarios(postagem.getId());

        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(comentario.getText())) {
                    comentario.requestFocus();
                } else {
                   /* if(checkIfAutor())
                        nome += " (Autor)";*/
                    enviarComentario(new FirestoreForumComentario("teste de nome", comentario.getText().toString()));
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    private void enviarComentario(FirestoreForumComentario comentarioModel) {
        postagem
                .update("comentarios", FieldValue.arrayUnion(comentarioModel))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("teste", "true");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void iniciarRecyclerViewComentarios(String postagemKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        Query query = db.collection("PostagensForumPANC").whereEqualTo("postagemID", postagemKey);

        FirestoreRecyclerOptions<PostagemForum> options = new FirestoreRecyclerOptions.Builder<PostagemForum>()
                .setQuery(query, PostagemForum.class)
                .setLifecycleOwner(this)
                .build();

//        adapter

        recyclerViewComentarios.setAdapter(adapter);
    }

    private DocumentReference recuperarPostagem(String postagemKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        DocumentReference docRef = db.collection("PostagensForumPANC").document(postagemKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot DS) {
                List<String> teste = (List<String>) DS.get("imagensID");
                Glide.with(getBaseContext())
                        .load(teste.get(0))
                        .into(postagemForumDetalharImagem);
                Timestamp timestamp = (Timestamp) DS.get("timestamp");
                Date data = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
                dateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
                postagemDetalhadaData.setText(dateFormat.format(data));
                postagemDetalhadaTexto.setText(DS.get("postagemForumTexto").toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("erro", e.getMessage());
            }
        });
        return docRef;
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/
}

