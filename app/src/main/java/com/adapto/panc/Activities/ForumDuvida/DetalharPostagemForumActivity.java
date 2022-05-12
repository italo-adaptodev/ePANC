package com.adapto.panc.Activities.ForumDuvida;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Adapters.ForumComentarioAdapter;
import com.adapto.panc.Adapters.RestaurantesFiltradosAdapter;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DetalharPostagemForumActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private ArrayList<FirestoreForumComentario> arrayList;
    private TextView postagemDetalhadaAutor, postagemDetalhadaData,postagemDetalhadaTexto;
    private RecyclerView recyclerViewComentarios;
    private EditText comentario;
    private ImageButton btnEnviarComentario;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private DocumentReference postagem;
    private  FirebaseFirestore db;
    private View v;
    private PostagemForumDuvidas postagemForumDuvidas = new PostagemForumDuvidas();
    private ForumComentarioAdapter forumAdapter;
    private String nomeUsuarioComentario;
    private List<Bitmap> sampleImages;
    private List<String> uris;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private ProgressBar spinner;
    private ConstraintLayout constraintLayoutDetalharForum;
    private LinearLayoutCompat linearLayoutImagem;
    private String usuarioID;
    private boolean isUsuarioAdminstrador = false;
    private String postagemKey = null;
    private boolean isUsuarioMembroEquipe = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_postagem_forum);
        postagemDetalhadaAutor = findViewById(R.id.detalhar_forum_autor);
        postagemDetalhadaData = findViewById(R.id.detalhar_forum_data);
        recyclerViewComentarios = findViewById(R.id.recyclerview_detalhar_respostas_forum);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutImagem = findViewById(R.id.linearlayoutImagem);
        postagemDetalhadaTexto = findViewById(R.id.detalhar_forum_texto);
        comentario = findViewById(R.id.forum_detalhar_comentario);
        btnEnviarComentario = findViewById(R.id.forum_btn_enviar_comentario);
        toolbar = findViewById(R.id.toolbarDetalharPostagemForum);
        sampleImages = new ArrayList<>();
        uris = new ArrayList<>();
        v = findViewById(android.R.id.content);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        postagemKey = intent.getStringExtra("postagemIDDetalhe");
        ScrollView sv = findViewById(R.id.scrollView);
        sv.scrollTo(0, 0);
        usuarioID = new LoginSharedPreferences(getApplicationContext()).getIdentifier();
        getCargosUsuarioSolicitante();
        getNomeUsuarioAtual(usuarioID);
        spinner = findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);
        constraintLayoutDetalharForum = findViewById(R.id.ConstraintLayoutDetalharForum);
        btnEnviarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(comentario.getText())) {
                    comentario.requestFocus();
                } else {
                    if(isUsuarioMembroEquipe)
                        nomeUsuarioComentario += " (Administração)";
                    enviarComentario(new FirestoreForumComentario(nomeUsuarioComentario, comentario.getText().toString()));
                }
            }
        });

        listenToDiffs();

        //region Toolbar
        toolbar = findViewById(R.id.toolbarDetalharPostagemForum);
        toolbar.setTitle("Detalhar Postagem");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        //endregion


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
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
        startActivity(getIntent());
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
    }

    private DocumentReference recuperarPostagem(final String postagemKey) {
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
                forumAdapter = new ForumComentarioAdapter(getLayoutInflater(), postagemForumDuvidas.getComentarios(),getBaseContext(), isUsuarioAdminstrador, DetalharPostagemForumActivity.this, postagemKey);
                recyclerViewComentarios.setAdapter(forumAdapter);
                if(uris.size() > 1 ) {
                    setImagesCarousel(uris);
                }else {
                    setImage(uris.get(0));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível recuperar a postagem. ERRO - " + e.getLocalizedMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                spinner.setVisibility(View.INVISIBLE);
                constraintLayoutDetalharForum.setVisibility(View.VISIBLE);
            }
        });
        return docRef;
    }

    @SuppressLint("CheckResult")
    public void setImagesCarousel(List<String> images) {
        SliderLayout sliderLayout = new SliderLayout(this);
        sliderLayout.stopAutoCycle();
        sliderLayout.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        sliderLayout.setMinimumHeight(250);
        sliderLayout.setMinimumHeight(250);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        linearLayoutImagem.addView(sliderLayout);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.pancdefault);

        for (int i = 0; i < images.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView
                    .image(images.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this)
                    .setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                        @Override
                        public void onStart(BaseSliderView target) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onEnd(boolean result, BaseSliderView target) {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onDrawableLoaded(Drawable drawable) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            sliderView.bundle(new Bundle());
            sliderLayout.addSlider(sliderView);
        }
        sliderLayout.setCurrentPosition(0);
        sliderLayout.addOnPageChangeListener(this);
    }

    private void setImage(String uri){
        ImageView imageView = new ImageView(this);
        imageView.setMaxWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setMaxHeight(250);
        linearLayoutImagem.addView(imageView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        Glide.with(getBaseContext())
                .load(uri)
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
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

    private boolean getCargosUsuarioSolicitante() {
        db.collection(firestoreReferences.getEquipeCOLLECTION())
                .whereEqualTo("usuarioID", usuarioID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() > 0) {
                            isUsuarioMembroEquipe = true;
                            for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                                String cargos = snap.get("cargosAdministrativos").toString();
                                if(cargos.contains("ADMINISTRADOR")) {
                                    isUsuarioAdminstrador = true;
                                }
                            }
                            postagem = recuperarPostagem(postagemKey);

                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                postagem = recuperarPostagem(postagemKey);
            }
        });
        return isUsuarioAdminstrador;
    }


/*
    private void excluirPostagem(String comentarioID) {
        db.collection(firestoreReferences.getPostagensForumPANCCOLLECTION()).document(postagemID)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Postagem excluída com sucesso!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SnackBarPersonalizada().showMensagemLonga(v, "Não foi possível excluir esta postagem. ERRO: " + e.getLocalizedMessage());
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        new SnackBarPersonalizada().showMensagemLonga(v,   slider.getUrl());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

