package com.adapto.panc.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.R;
import com.adapto.panc.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Produtor_DetalharProdutoActivity extends AppCompatActivity {

    private TextView nomeProdutorDetalhar, nomeProdutoDetalhar, precoProdutoDetalhar, telefoneProdutorDetalhar, emailProdutorDetalhar, enderecoProdutorDetalhar,
            descricaoProdutoDetalhar;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private DocumentReference postagem;
    private FirebaseFirestore db;
    private View v;
    private CarouselView carouselView;
    private List<Drawable> sampleImages;
    private List<String> uris;
    private FirestoreReferences collections;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_produto_vitrine);
        nomeProdutorDetalhar = findViewById(R.id.nomeProdutorDetalhar);
        nomeProdutoDetalhar = findViewById(R.id.nomeProdutoDetalhar);
        precoProdutoDetalhar = findViewById(R.id.precoProdutoDetalhar);
        telefoneProdutorDetalhar = findViewById(R.id.telefoneProdutorDetalhar);
        emailProdutorDetalhar = findViewById(R.id.emailProdutorDetalhar);
        enderecoProdutorDetalhar = findViewById(R.id.enderecoProdutorDetalhar);
        descricaoProdutoDetalhar = findViewById(R.id.descricaoProdutoDetalhar);
        carouselView = findViewById(R.id.imagensProdutoDetalhar);
        sampleImages = new ArrayList<>();
        uris = new ArrayList<>();
        v = findViewById(android.R.id.content);
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        Intent intent = getIntent();
        final String postagemKey = intent.getStringExtra("postagemIDDetalhe");
        ScrollView sv = findViewById(R.id.scrollViewProduto);
        sv.scrollTo(0, 0);
        postagem = recuperarPostagem(postagemKey);
    }

    private DocumentReference recuperarPostagem(String postagemKey) {
        DocumentReference docRef = db.collection(collections.getVitrineProdutosCOLLECTION()).document(postagemKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot DS) {
                getNomeAutorPostagem(DS.getString("usuarioID"));
                getInfoProdutor(DS.getString("usuarioID"));
                uris = (List<String>) DS.get("imagensID");
                Timestamp timestamp = (Timestamp) DS.get("timestamp");
                Date data = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
                dateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
                nomeProdutoDetalhar.setText(DS.getString("nome"));
                precoProdutoDetalhar.setText(DS.getDouble("preco").toString());
                descricaoProdutoDetalhar.setText(DS.getString("descricao"));
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
        db.collection(collections.getUsuariosCOLLECTION()).whereEqualTo("identificador", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            nomeProdutorDetalhar.setText(snap.getString("nome"));
                        }
                    }
                });
    }

    private void getInfoProdutor(String usuarioID) {
        db.collection(collections.getPRODUTOR()).whereEqualTo("usuarioID", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            telefoneProdutorDetalhar.setText(snap.getString("numContato"));
                            emailProdutorDetalhar.setText(snap.getString("email"));
                            enderecoProdutorDetalhar.setText(snap.getString("localizacao"));
                        }
                    }
                });
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
