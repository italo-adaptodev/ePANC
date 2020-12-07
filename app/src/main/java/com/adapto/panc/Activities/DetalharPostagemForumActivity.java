package com.adapto.panc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Adapters.ForumComentarioAdapter;
import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.Database.PostagemForum;
import com.adapto.panc.Models.ViewHolder.FirestoreEquipeAdministrativaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DetalharPostagemForumActivity extends AppCompatActivity {

    private ArrayList<FirestoreForumComentario> arrayList;
    private TextView postagemDetalhadaAutor, postagemDetalhadaData,postagemDetalhadaTexto;
    private ImageView postagemForumDetalharImagem;
    private RecyclerView recyclerViewComentarios;
    private EditText comentario;
    private ImageButton btnEnviarComentario;
    private int qtdComentarios;
    private Boolean check = false;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private DocumentReference postagem;
    private  FirebaseFirestore db;
    private View v;
    private PostagemForum postagemForum = new PostagemForum();
    private ForumComentarioAdapter forumAdapter;
    private String nomeUsuarioComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_postagem_forum);
        postagemDetalhadaAutor = findViewById(R.id.detalhar_forum_autor);
        postagemDetalhadaData = findViewById(R.id.detalhar_forum_data);
        postagemForumDetalharImagem = findViewById(R.id.postagemForumDetalharImagem);
        recyclerViewComentarios = findViewById(R.id.recyclerview_detalhar_respostas_forum);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        postagemDetalhadaTexto = findViewById(R.id.detalhar_forum_texto);
        comentario = findViewById(R.id.forum_detalhar_comentario);
        btnEnviarComentario = findViewById(R.id.forum_btn_enviar_comentario);
        v = findViewById(android.R.id.content);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        final String postagemKey = intent.getStringExtra("postagemIDDetalhe");
        ScrollView sv = findViewById(R.id.scrollView);
        sv.scrollTo(0, 0);
        postagem = recuperarPostagem(postagemKey);
        getNomeUsuarioAtual(new LoginSharedPreferences(getApplicationContext()).getKEYUSER());
        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(comentario.getText())) {
                    comentario.requestFocus();
                } else {
                   /* if(checkIfAutor())
                        nome += " (Autor)";*/
                    enviarComentario(new FirestoreForumComentario(nomeUsuarioComentario, comentario.getText().toString()));
                }
            }
        });

        listenToDiffs();
    }

    private void getNomeUsuarioAtual(String identificador) {
        db.collection("Usuarios").whereEqualTo("identificador", identificador).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            nomeUsuarioComentario = snap.getString("nome");
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
                        atualizarActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void atualizarActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private DocumentReference recuperarPostagem(String postagemKey) {
        DocumentReference docRef = db.collection("PostagensForumPANC").document(postagemKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot DS) {
               getNomeAutorPostagem(DS.getString("usuarioID"));
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
                postagemForum = DS.toObject(PostagemForum.class);
                forumAdapter = new ForumComentarioAdapter(getLayoutInflater(), postagemForum.getComentarios());
                recyclerViewComentarios.setAdapter(forumAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível recuperar a postagem. ERRO - " + e.getLocalizedMessage());
            }
        });
        return docRef;
    }

    private void getNomeAutorPostagem(String usuarioID) {
        db.collection("Usuarios").whereEqualTo("identificador", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            postagemDetalhadaAutor.setText(snap.getString("nome"));
                        }
                    }
                });
    }

    public void listenToDiffs() {
        // [START listen_diffs]
        db.collection("PostagensForumPANC")
                .whereEqualTo("postagemID", postagemForum.getPostagemID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    atualizarActivity();
                                    break;
                                case MODIFIED:
                                    atualizarActivity();
                                    break;
                                case REMOVED:
                                    atualizarActivity();
                                    break;
                            }
                        }

                    }
                });
        // [END listen_diffs]
    }
}

