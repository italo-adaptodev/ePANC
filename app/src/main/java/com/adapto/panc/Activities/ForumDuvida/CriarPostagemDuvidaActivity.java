package com.adapto.panc.Activities.ForumDuvida;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CriarPostagemDuvidaActivity extends AppCompatActivity {

    private MaterialButton btnSelect, btnUpload;
    private ImageView forumDuvidaimg1, forumDuvidaimg2, forumDuvidaimg3, forumDuvidaimg4, forumDuvidaimg5, forumDuvidaimg6 ;
    private final int PICK_IMAGE_REQUEST = 22;
    private SnackBarPersonalizada snackBarPersonalizada;
    private ReferenciaDatabase referenciaDatabase;
    private TextInputLayout textoPostagemForum, tituloPostagemForum;
    private View v;
    StorageReference storageReference;
    private List<ImageView> imageViews;
    private List<Uri> filepaths;
    private String postagemID;
    private FirestoreReferences firestoreReferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_postagem_forum_duvida);

        // initialise views
        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        addImageviews();
        textoPostagemForum =  findViewById(R.id.postagem_duvida_text);
        tituloPostagemForum =  findViewById(R.id.postagem_duvida_titulo);
        v = findViewById(android.R.id.content);
        referenciaDatabase = new ReferenciaDatabase();
        storageReference = referenciaDatabase.getFirebaseStorage();
        snackBarPersonalizada = new SnackBarPersonalizada();
        filepaths = new ArrayList<>();
        firestoreReferences = new FirestoreReferences();

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((tituloPostagemForum.getEditText().getText().toString().isEmpty() || textoPostagemForum.getEditText().getText().toString().isEmpty())) {
                    new SnackBarPersonalizada().showMensagemLonga(v, "Preencha todos os campos obrigatórios!");
                    return;
                }
                uploadImages();

            }
        });

        //region Toolbar
        toolbar = findViewById(R.id.toolbarCriarPostagemForum);
        toolbar.setTitle("Criar Postagem");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        //endregion
    }

    private void addImageviews() {
        imageViews = new ArrayList<>();
        forumDuvidaimg1 = findViewById(R.id.forumDuvidaimg1);
        forumDuvidaimg2 = findViewById(R.id.forumDuvidaimg2);
        forumDuvidaimg3 = findViewById(R.id.forumDuvidaimg3);
        forumDuvidaimg4 = findViewById(R.id.forumDuvidaimg4);
        forumDuvidaimg5 = findViewById(R.id.forumDuvidaimg5);
        forumDuvidaimg6 = findViewById(R.id.forumDuvidaimg6);
        imageViews.add(forumDuvidaimg1);
        imageViews.add(forumDuvidaimg2);
        imageViews.add(forumDuvidaimg3);
        imageViews.add(forumDuvidaimg4);
        imageViews.add(forumDuvidaimg5);
        imageViews.add(forumDuvidaimg6);
    }

    // Select Image method
    private void SelectImage()
    {
        for(int i = 0; i < 6; i++){
            imageViews.get(i).setImageBitmap(null);
            filepaths.clear();
        }
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Selecione a imagem "),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the Uri of data
            ClipData mult = data.getClipData();
            Uri unique = data.getData();

            if(mult != null) {
                for (int position = 0; position < mult.getItemCount(); position++) {
                    try {

                        filepaths.add(position, data.getClipData().getItemAt(position).getUri());
                        // Setting image on image view using Bitmap
                        Bitmap bitmap = MediaStore
                                .Images
                                .Media
                                .getBitmap(getContentResolver(), filepaths.get(position));

                        imageViews.get(position).setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                filepaths.add(0, unique);
                // Setting image on image view using Bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(getContentResolver(), unique);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imageViews.get(0).setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
            }
        }
    }

    // UploadImage method
    private void uploadImages() {
        final List<String> imagens = new ArrayList<>();
        for (int i = 0; i < filepaths.size(); i++){
                // Code for showing progressDialog while uploading
                final ProgressDialog progressDialog
                        = new ProgressDialog(this);
                progressDialog.setTitle("Upload da imagem");
                progressDialog.show();

                // Defining the child of storageReference
                String imageREF = UUID.randomUUID().toString();

                final StorageReference ref = storageReference.child("images/" + imageREF);

                ref.putFile(filepaths.get(i)).addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                snackBarPersonalizada.showMensagemLonga(v, "Imagem carregada com sucesso!");
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagens.add(uri.toString());

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {

                                // Error, Image not uploaded
                                progressDialog.dismiss();
                                snackBarPersonalizada.showMensagemLonga(v, "Falha ao carregar imagem: " + e.getMessage());
                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage("Carregamento " + (int)progress + "%");
                                    }
                                });

        }
        uploadPostagem(imagens);
    }

    private void uploadPostagem(final List<String> imagens) {
            PostagemForumDuvidas postagemForumDuvidas = new PostagemForumDuvidas(textoPostagemForum.getEditText().getText().toString(),
                    new LoginSharedPreferences(getApplicationContext()).getIdentifier(), imagens, Timestamp.now(),  tituloPostagemForum.getEditText().getText().toString());
            FirebaseFirestore.getInstance().collection(firestoreReferences.getPostagensForumPANCCOLLECTION())
                    .add(postagemForumDuvidas)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            postagemID = documentReference.getId();
                            documentReference
                                    .update("postagemID", documentReference.getId())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackBarPersonalizada.showMensagemLonga(v, e.getMessage());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Intent intent = new Intent(CriarPostagemDuvidaActivity.this, ForumDuvidasActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });
    }
}