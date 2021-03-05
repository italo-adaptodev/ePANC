package com.adapto.panc.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private PostagemForumDuvidas postagemForumDuvidas = new PostagemForumDuvidas();
    private ForumComentarioAdapter forumAdapter;
    private String nomeUsuarioComentario;
    private CarouselView carouselView;
    private List<Drawable> sampleImages;
    private List<String> uris;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    Handler mHandler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_postagem_forum);
        postagemDetalhadaAutor = findViewById(R.id.detalhar_forum_autor);
        postagemDetalhadaData = findViewById(R.id.detalhar_forum_data);
        recyclerViewComentarios = findViewById(R.id.recyclerview_detalhar_respostas_forum);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        postagemDetalhadaTexto = findViewById(R.id.detalhar_forum_texto);
        comentario = findViewById(R.id.forum_detalhar_comentario);
        btnEnviarComentario = findViewById(R.id.forum_btn_enviar_comentario);
        carouselView = findViewById(R.id.postagemForumDetalharImagens);
        sampleImages = new ArrayList<>();
        uris = new ArrayList<>();
        v = findViewById(android.R.id.content);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        final String postagemKey = intent.getStringExtra("postagemIDDetalhe");
        ScrollView sv = findViewById(R.id.scrollView);
        sv.scrollTo(0, 0);
        postagem = recuperarPostagem(postagemKey);
        getNomeUsuarioAtual(new LoginSharedPreferences(getApplicationContext()).getIdentifier());
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
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", identificador).get()
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
        DocumentReference docRef = db.collection(firestoreReferences.getPostagensForumPANCCOLLECTION()).document(postagemKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot DS) {
               getNomeAutorPostagem(DS.getString("usuarioID"));
                uris = (List<String>) DS.get("imagensID");
                Timestamp timestamp = (Timestamp) DS.get("timestamp");
                Date data = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
                dateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
                postagemDetalhadaData.setText(dateFormat.format(data));
                postagemDetalhadaTexto.setText(DS.get("postagemForumTexto").toString());
                postagemForumDuvidas = DS.toObject(PostagemForumDuvidas.class);
                forumAdapter = new ForumComentarioAdapter(getLayoutInflater(), postagemForumDuvidas.getComentarios());
                recyclerViewComentarios.setAdapter(forumAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível recuperar a postagem. ERRO - " + e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete())
                    setImagesCarousel(uris);
            }
        });
        return docRef;
    }

    private void getNomeAutorPostagem(String usuarioID) {
        db.collection(firestoreReferences.getUsuariosCOLLECTION()).whereEqualTo("identificador", usuarioID).get()
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
        db.collection(firestoreReferences.getPostagensForumPANCCOLLECTION())
                .whereEqualTo("postagemID", postagemForumDuvidas.getPostagemID())
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

    public void setImagesCarousel(List<String> images) {
        final Bitmap[] bitmap = {null};
        for(String uri: images){
            Glide.with(getBaseContext()).asBitmap()
                    .load(uri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            bitmap[0] = resource;
                        }
                    });
            Drawable d = new BitmapDrawable(getResources(), bitmap[0]);
            sampleImages.add(d);
        }
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(sampleImages.size());
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageDrawable(sampleImages.get(position));
        }
    };



    // Create an interface to respond with the result after processing
    
}

