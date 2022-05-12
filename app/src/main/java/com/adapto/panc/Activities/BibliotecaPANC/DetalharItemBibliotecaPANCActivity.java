package com.adapto.panc.Activities.BibliotecaPANC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.R;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetalharItemBibliotecaPANCActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private TextView tituloItemBiblioteca, descricaoItemBiblioteca;
    private ImageView imagemItemBiblioteca;
    private FirebaseFirestore db;
    private FirestoreReferences collections;
    private ScrollView scrollView;
    private List<String> uris;
    private View v;
    private DocumentReference documentReferenceItemBiblioteca;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private LinearLayoutCompat linearLayoutImagem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_item_biblioteca_panc);

        tituloItemBiblioteca = findViewById(R.id.tituloItemBiblioteca);
        descricaoItemBiblioteca = findViewById(R.id.descricaoItemBiblioteca);
        linearLayoutImagem = findViewById(R.id.imagemItemBiblioteca);
        toolbar = findViewById(R.id.toolbarDetalharItemBiblioteca);
        v = findViewById(android.R.id.content);
        uris = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        collections = new FirestoreReferences();
        Intent intent = getIntent();
        final String itemBibliotecaKey = intent.getStringExtra("itemIDDetalhe");
        documentReferenceItemBiblioteca = recuperarItemBiblioteca(itemBibliotecaKey);

        //region Toolbar
        toolbar.setTitle("Detalhar Item Biblioteca");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        //endregion

    }

    private DocumentReference recuperarItemBiblioteca(String itemBibliotecaKey) {
        DocumentReference reference = db.collection(collections.getBibliotecaCOLLECTION())
                                        .document(itemBibliotecaKey);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                uris = (List<String>) documentSnapshot.get("imagensID");
                tituloItemBiblioteca.setText(documentSnapshot.getString("itemBibliotecaTitulo"));
                descricaoItemBiblioteca.setText(documentSnapshot.getString("itemBibliotecaDesc"));
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
                    if(uris.size() > 1)
                        setImagesCarousel(uris);
                    else
                        setImage(uris.get(0));
            }
        });
        return reference;
    }

    @SuppressLint("CheckResult")
    public void setImagesCarousel(List<String> images) {
        SliderLayout sliderLayout = new SliderLayout(this);
        sliderLayout.stopAutoCycle();
        sliderLayout.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        sliderLayout.setMinimumHeight(250);
        sliderLayout.setMinimumHeight(250);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.itemBibliotecaProgress);

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
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.itemBibliotecaProgress);

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

    @Override
    public void onSliderClick(BaseSliderView slider) {

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