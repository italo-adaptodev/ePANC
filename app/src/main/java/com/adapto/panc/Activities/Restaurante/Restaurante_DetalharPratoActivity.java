package com.adapto.panc.Activities.Restaurante;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.Models.Database.Restaurante;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Restaurante_DetalharPratoActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Prato pratoEscolhido;
    private TextView detalharPrato_nome,  detalharPrato_ingredientes, detalharPrato_preco;
    private LinearLayoutCompat detalharPrato_imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante__detalhar_prato);
        detalharPrato_nome = findViewById(R.id.detalharPrato_nome);
        detalharPrato_ingredientes = findViewById(R.id.detalharPrato_ingredientes);
        detalharPrato_preco = findViewById(R.id.detalharPrato_preco);
        detalharPrato_imagem = findViewById(R.id.detalharPrato_imagem);
        Intent intent = getIntent();
        final String restauranteID = intent.getStringExtra("restauranteID");
        final int ListaPratoID = intent.getIntExtra("ListaPratoID", 0);
        getPrato(restauranteID,ListaPratoID);
    }

    private void getPrato(String restauranteID, final int listaPratoID) {
        DocumentReference docRef = db.collection(firestoreReferences.getRestauranteCOLLECTION()).document(restauranteID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Restaurante restaurante = documentSnapshot.toObject(Restaurante.class);
                pratoEscolhido = restaurante.getPratos().get(listaPratoID);
                detalharPrato_nome.setText(pratoEscolhido.getNome());
                detalharPrato_ingredientes.setText(pratoEscolhido.getDescricao());
                detalharPrato_preco.setText("R$ " + pratoEscolhido.getPreco());
                if(pratoEscolhido.getImagensID().size() > 1 ) {
                    setImagesCarousel(pratoEscolhido.getImagensID());
                }else {
                    setImage(pratoEscolhido.getImagensID().get(0));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @SuppressLint("CheckResult")
    public void setImagesCarousel(List<String> images) {
        SliderLayout sliderLayout = new SliderLayout(this);
        sliderLayout.startAutoCycle();
        sliderLayout.stopCyclingWhenTouch(false);
        sliderLayout.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        sliderLayout.setMinimumHeight(250);
        sliderLayout.setMinimumHeight(250);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        detalharPrato_imagem.addView(sliderLayout);
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
        detalharPrato_imagem.addView(imageView);
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