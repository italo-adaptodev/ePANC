package com.adapto.panc.Activities.Produto;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.R;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Produtor_DetalharProdutoActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private TextView nomeProdutorDetalhar, nomeProdutoDetalhar, precoProdutoDetalhar, emailProdutorDetalhar, enderecoProdutorDetalhar,
            descricaoProdutoDetalhar;
    public static final String DATE_FORMAT_1 = "dd MMM yyyy";
    private DocumentReference postagem;
    private FirebaseFirestore db;
    private View v;
    private List<Drawable> sampleImages;
    private List<String> uris;
    private FirestoreReferences collections;
    private MaterialButton btnExpand;
    private final static int MAX_LINES_COLLAPSED = 3;
    private final boolean INITIAL_IS_COLLAPSED = true;
    private boolean isCollapsed = INITIAL_IS_COLLAPSED;
    private ScrollView scrollViewProduto;
    private Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
    private ImageButton whatsappBtn;
    private LinearLayoutCompat linearLayoutImagem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhar_produto_vitrine);
        nomeProdutoDetalhar = findViewById(R.id.nomeProdutoDetalhar);
        precoProdutoDetalhar = findViewById(R.id.precoProdutoDetalhar);
        emailProdutorDetalhar = findViewById(R.id.emailProdutorDetalhar);
        enderecoProdutorDetalhar = findViewById(R.id.enderecoProdutorDetalhar);
        descricaoProdutoDetalhar = findViewById(R.id.descricaoProdutoDetalhar);
        linearLayoutImagem = findViewById(R.id.imagensProdutoDetalhar);
        scrollViewProduto = findViewById(R.id.scrollViewProduto);
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
        btnExpand = findViewById(R.id.btnExpand);
        whatsappBtn = findViewById(R.id.whatsappbtn);

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCollapsed){
                    if(canBeCollapsed())
                        btnExpand.setClickable(false);
                    else
                        btnExpand.setClickable(true);

                    descricaoProdutoDetalhar.setMaxLines(Integer.MAX_VALUE);
                    setTextWithSmoothAnimation(btnExpand, "Encolher", R.drawable.ic_uparrow);
                }else{
                    descricaoProdutoDetalhar.setMaxLines(MAX_LINES_COLLAPSED);
                    setTextWithSmoothAnimation(btnExpand, "Expandir", R.drawable.ic_down_arrow);
                }
                isCollapsed = !isCollapsed;
            }
        });

        applyLayoutTransition();

        whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Ola! Me interessei pelo produto " +nomeProdutoDetalhar.getText() + " que vi no aplicativo PANCApp." +
                        "Como podemos acertar uma compra? Aguardo resposta.");

                if (whatsappIntent.resolveActivity(getPackageManager()) == null) {
                    Toast.makeText(Produtor_DetalharProdutoActivity.this, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(whatsappIntent);

            }
        });
    }

    private DocumentReference recuperarPostagem(String postagemKey) {
        DocumentReference docRef = db.collection(collections.getVitrineProdutosCOLLECTION()).document(postagemKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot DS) {
                getNomeAutorPostagem(DS.getString("usuarioID"));
                getInfoProdutor(DS.getString("produtorID"));
                uris = (List<String>) DS.get("imagensID");
                Timestamp timestamp = (Timestamp) DS.get("timestamp");
                Date data = timestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_1);
                dateFormat.setTimeZone(TimeZone.getTimeZone("BRT"));
                nomeProdutoDetalhar.setText(DS.getString("nome"));
                precoProdutoDetalhar.setText(String.format("R$ %s", DS.getDouble("preco").toString()));
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
                    if(uris.size() > 1 ) {
                        setImagesCarousel(uris);
                    }else {
                        setImage(uris.get(0));
                    }
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
        Log.i("teste", "1");
        db.collection(collections.getProdutorCOLLECTION()).whereEqualTo("usuarioID", usuarioID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            createIntentMensagem(snap.getString("numContato"));
                            emailProdutorDetalhar.setText(snap.getString("email"));
                            enderecoProdutorDetalhar.setText(snap.getString("localizacao"));
                        }
                    }
                });
    }

    private void createIntentMensagem(String numContato) {

        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        String smsNumber = numContato; //Number without with country code and without '+' prifix
        whatsappIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");


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


    private void applyLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(600);
        transition.enableTransitionType(LayoutTransition.CHANGING);
        scrollViewProduto.setLayoutTransition(transition);
    }

    private boolean canBeCollapsed() {
        return descricaoProdutoDetalhar.getLineCount() <= MAX_LINES_COLLAPSED;
    }

    private void setTextWithSmoothAnimation(final MaterialButton btn, final String message, final int icon) {
        btn.animate().setDuration(100).setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }


            @Override
            public void onAnimationEnd(Animator animation) {
                btn.setText(message);
                btn.setIcon(ContextCompat.getDrawable(getApplicationContext(), icon));
                btn.animate().setListener(null).setDuration(100).alpha(1);
            }


            @Override
            public void onAnimationCancel(Animator animation) {

            }


            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).alpha(0);
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Produtor_ListarProdutosActivity.class));
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
