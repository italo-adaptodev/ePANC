package com.adapto.panc.Activities.Restaurante;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.R;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.adapto.panc.SnackBarPersonalizada;
import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AdicionarPratoActivity extends AppCompatActivity {

    private CurrencyEditText precoPrato;
    private ImageView img1, img2, img3, img4, img5, img6 ;
    private List<ImageView> imageViews;
    private final int PICK_IMAGE_REQUEST = 22;
    private SnackBarPersonalizada snackBarPersonalizada;
    private ReferenciaDatabase referenciaDatabase;
    private View v;
    private List<Uri> filepaths;
    private StorageReference storageReference;
    private MaterialButton btnSelect, btnUpload;
    private TextInputLayout nomePrato, descricaoPrato, ingredientesPANC;
    private FirestoreReferences firestoreReferences =  new FirestoreReferences();
    private String restauranteID;
    private DocumentReference restauranteDocRef;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_prato_a_restaurante);
        addImageviews();
        precoPrato = (CurrencyEditText) findViewById(R.id.precoPrato);
        btnSelect = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        v = findViewById(android.R.id.content);
        nomePrato =  findViewById(R.id.nomePrato);
        descricaoPrato =  findViewById(R.id.descricaoPrato);
        ingredientesPANC = findViewById(R.id.ingredientesPANCPrato);
        referenciaDatabase = new ReferenciaDatabase();
        storageReference = referenciaDatabase.getFirebaseStorage();
        snackBarPersonalizada = new SnackBarPersonalizada();
        filepaths = new ArrayList<>();
        intent = getIntent();
        restauranteID = intent.getStringExtra("restauranteID");
        restauranteDocRef = getRestauranteByID();

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
                if(filepaths.size() == 0)
                    snackBarPersonalizada.showMensagemLonga(v, "Não foi possível adiciona o prato. Por favor, selecione pelo menos 1 imagem para visualização dos usuários");
                else
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
        final String nome, ingredientes;
        nome = nomePrato.getEditText().getText().toString();
        ingredientes = ingredientesPANC.getEditText().getText().toString();
        Double preco = precoPrato.getNumericValue();
        Prato novoPrato = new Prato(nome,ingredientes, restauranteID, preco.toString() , imagens, Timestamp.now());
        restauranteDocRef
                .update("pratos", FieldValue.arrayUnion(novoPrato))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        restauranteDocRef
                                .update("ingredientesPANC", FieldValue.arrayUnion(ingredientes))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        atualizarActivity();
                                    }
                                });
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
        onBackPressed();
    }

    private DocumentReference getRestauranteByID() {
        return referenciaDatabase.getDatabaseFirestore().collection(firestoreReferences.getRestauranteCOLLECTION()).document(restauranteID);

    }

    private boolean verificarCamposUsuario() {

        if(nomePrato.getEditText().getText().toString().isEmpty() || descricaoPrato.getEditText().getText().toString().isEmpty()
                || precoPrato.getText().toString().isEmpty()) {
            new SnackBarPersonalizada().showMensagemLonga(v, "Preencha todos os campos corretamente!");
            return false;
        }
        return true;
    }
}