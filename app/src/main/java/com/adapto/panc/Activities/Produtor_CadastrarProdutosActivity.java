package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Currency;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.adapto.panc.Models.Database.PostagemForumDuvidas;
import com.adapto.panc.Models.Database.Produtor_Produto;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.adapto.panc.SnackBarPersonalizada;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import me.abhinay.input.CurrencyEditText;

public class Produtor_CadastrarProdutosActivity extends AppCompatActivity {

    private CurrencyEditText etInput;
    private ImageView img1, img2, img3, img4, img5, img6 ;
    private List<ImageView> imageViews;
    private final int PICK_IMAGE_REQUEST = 22;
    private SnackBarPersonalizada snackBarPersonalizada;
    private ReferenciaDatabase referenciaDatabase;
    private View v;
    private List<Uri> filepaths;
    private StorageReference storageReference;
    private MaterialButton btnSelect, btnUpload;
    private TextInputLayout nomeProduto, descricaoProduto, observacoesProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtor_cadastrar_produtos);
        addImageviews();
        etInput = (CurrencyEditText) findViewById(R.id.precoProduto);
        etInput.setDelimiter(false);
        etInput.setSpacing(false);
        etInput.setDecimals(true);
        //Make sure that Decimals is set as false if a custom Separator is used
        etInput.setSeparator(".");

        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        v = findViewById(android.R.id.content);
        nomeProduto =  findViewById(R.id.nomeProduto);
        descricaoProduto =  findViewById(R.id.descricaoProduto);
        observacoesProduto =  findViewById(R.id.observacoesProduto);
        referenciaDatabase = new ReferenciaDatabase();
        storageReference = referenciaDatabase.getFirebaseStorage();
        snackBarPersonalizada = new SnackBarPersonalizada();
        filepaths = new ArrayList<>();

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
                uploadImages();
            }
        });
    }

    private void addImageviews() {
        imageViews = new ArrayList<>();
        img1 = findViewById(R.id.cadastroProdutoimg1);
        img2 = findViewById(R.id.cadastroProdutoimg2);
        img3 = findViewById(R.id.cadastroProdutoimg3);
        img4 = findViewById(R.id.cadastroProdutoimg4);
        img5 = findViewById(R.id.cadastroProdutoimg5);
        img6 = findViewById(R.id.cadastroProdutoimg6);
        imageViews.add(img1);
        imageViews.add(img2);
        imageViews.add(img3);
        imageViews.add(img4);
        imageViews.add(img5);
        imageViews.add(img6);
    }

    private void SelectImage()
    {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            // Get the Uri of data
            int ocunt = data.getClipData().getItemCount();
            for(int i = ocunt; i < 6; i++){
                imageViews.get(i).setImageBitmap(null);
            }
            for(int position = 0;position<ocunt;position++){
                try {

                    filepaths.add(position, data.getClipData().getItemAt(position).getUri());
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(getContentResolver(), filepaths.get(position));

                    imageViews.get(position).setImageBitmap(bitmap);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
                                    if(imagens.size() == filepaths.size())
                                        uploadPostagem(imagens);
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

    }

    private void uploadPostagem(final List<String> imagens) {
        String nome, desc, obs;
        nome = nomeProduto.getEditText().getText().toString();
        desc = descricaoProduto.getEditText().getText().toString();
        obs = observacoesProduto.getEditText().getText().toString();
        Produtor_Produto produto = new Produtor_Produto(nome, new LoginSharedPreferences(getApplicationContext()).getKEYUSER(),desc, obs, etInput.getCleanDoubleValue(), imagens, Timestamp.now());
        FirebaseFirestore.getInstance().collection("VitrineProdutos")
                .add(produto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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
                });
    }
}