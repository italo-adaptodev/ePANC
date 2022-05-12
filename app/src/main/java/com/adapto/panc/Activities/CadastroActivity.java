package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.adapto.panc.Activities.ForumReceita.DetalharReceitaActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Produtor;
import com.adapto.panc.Models.Database.Receita;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.Models.ViewHolder.ReceitaViewHolder;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Activities.Utils.WebViewConfig;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout nomeCadastroUsuario, loginCadastroUsuario, senhaCadastroUsuario, contatoUsuario;
    private TextInputLayout telInfoProdutor, emailInfoProdutor, localInfoProdutor;
    private TextInputLayout telInfoRestaurante, nomeInfoRestaurante, localInfoRestaurante;
    private SnackBarPersonalizada snackbar;
    private MaterialButton prosseguirButton2, prosseguirButton, cadastroProdutorInfo, cadastroRestauranteInfo;
    private RadioGroup radioGroup;
    private RadioButton radioProdutor, radioConsumidor, radioRestaurante, radioCultivare;
    private static final String URL_PRODUTOR = "https://docs.google.com/forms/d/e/1FAIpQLScutVCI0iaI5-tzGDIfo6x7XlczL35043m0XpVrloLk2BKdMA/viewform?usp=sf_link";
    private static final String URL_CONSUMIDOR = "https://docs.google.com/forms/d/e/1FAIpQLSc1AZ97PEwRDvcZHsPY9jmumsMseXdLq4Ha7uh6TNH8VGpdPQ/viewform?usp=sf_link";
    private static final String PRODUTOR_FLAG = "PRODUTOR";
    private static final String CONSUMIDOR_FLAG = "CONSUMIDOR";
    private static final String RESTAURANTE_FLAG = "RESTAURANTE";
    private static final String CULTIVARE_FLAG = "CULTIVARE";
    private View v;
    private LoginSharedPreferences loginSessionManager;
    private FirebaseFirestore db;
    private String usuarioID;
    private FirestoreReferences fsRefs;
    private View cadastro_cargos = null, infoProdutor = null, infoRestaurante = null, infoConsumidor = null, infoCultivare = null;
    private AlertDialog alertDialog = null;
    private boolean ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        cadastro_cargos = getLayoutInflater().inflate(R.layout.activity_cadastro_cargos, null);
        infoProdutor = getLayoutInflater().inflate(R.layout.activity_cadastro_produtor_infos, null);
        infoRestaurante = getLayoutInflater().inflate(R.layout.activity_cadastro_restaurante_infos, null);

        //region Primeira view
        nomeCadastroUsuario = findViewById(R.id.nomeCadastro);
        loginCadastroUsuario = findViewById(R.id.loginCadastro);
        senhaCadastroUsuario = findViewById(R.id.senhaCadastro);
        prosseguirButton = findViewById(R.id.prosseguirButton1);
        //endregion

        prosseguirButton2 = cadastro_cargos.findViewById(R.id.prosseguirButton2);

        //region Views especificas com campos complementares
        cadastroProdutorInfo = infoProdutor.findViewById(R.id.cadastroProdutorInfo);
        cadastroRestauranteInfo = infoRestaurante.findViewById(R.id.cadastroRestauranteInfo);
        //endregion

        snackbar = new SnackBarPersonalizada();
        loginCadastroUsuario.setHelperTextEnabled(true);
        loginCadastroUsuario.setHelperText("Telefone com DDD ou email");
        v = findViewById(android.R.id.content);
        radioProdutor = cadastro_cargos.findViewById(R.id.radioProdutor);
        radioConsumidor = cadastro_cargos.findViewById(R.id.radioConsumidor);
        radioRestaurante = cadastro_cargos.findViewById(R.id.radioRestaurante);
        radioCultivare = cadastro_cargos.findViewById(R.id.radioCultivare);
        radioGroup = cadastro_cargos.findViewById(R.id.radioGroup);
        fsRefs = new FirestoreReferences();

        loginSessionManager = new LoginSharedPreferences(getApplicationContext());

        prosseguirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificarCamposUsuario()) {
                    checkIfIdentificadorExists(loginCadastroUsuario.getEditText().getText().toString());
                }
            }
        });

        prosseguirButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == radioConsumidor.getId()) {
                    createFirestoreCommonUser(getCamposUsuario(), CONSUMIDOR_FLAG);
                } else if (selectedId == radioCultivare.getId()) {
                    createFirestoreCommonUser(getCamposUsuario(), CULTIVARE_FLAG);
                } else if (selectedId == radioProdutor.getId()) {
                    setContentView(infoProdutor);
                } else if (selectedId == radioRestaurante.getId()) {
                    setContentView(infoRestaurante);
                }
            }
        });

        //region Botões individuais para cada tipo de usuario
        cadastroProdutorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFirestoreCommonUser(getCamposUsuario(), PRODUTOR_FLAG);
            }
        });

        cadastroRestauranteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFirestoreCommonUser(getCamposUsuario(), RESTAURANTE_FLAG);
            }
        });


        //endregion

        db = FirebaseFirestore.getInstance();



    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean verificarCamposUsuario() {
        if(loginCadastroUsuario.getEditText().getText().toString().isEmpty() || nomeCadastroUsuario.getEditText().getText().toString().isEmpty()
            || senhaCadastroUsuario.getEditText().getText().toString().isEmpty()) {
            snackbar.showMensagemLonga(v, "Preencha todos os campos corretamente!");
            return false;
        }
        return true;
    }

    private void checkIfIdentificadorExists(String identificador) {
        ok = true;
        Task<QuerySnapshot> query = db.collection(fsRefs.getUsuariosCOLLECTION()).get();
            query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (final QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.getString("identificador").equals(identificador)) {
                            snackbar.showMensagemLonga(v, "Identificador em uso ou inválido");
                            ok = false;
                            return;
                        }
                    }
                    setContentView(cadastro_cargos);
                }
            });
    }

    private void createFirestoreCommonUser(Usuario novoUsuario, String userSpecificClass){
        db.collection(fsRefs.getUsuariosCOLLECTION()).add(novoUsuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        documentReference
                                .update("id", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                                        usuarioID = novoUsuario.getIdentificador();

                                        switch (userSpecificClass) {
                                            case CONSUMIDOR_FLAG:
                                                startIntentQuestionario(URL_CONSUMIDOR);
                                            case PRODUTOR_FLAG:
                                                createFirestoreProdutor(getCamposInfoProdutor());
                                            case RESTAURANTE_FLAG:
                                                createFirestoreRestaurante(getCamposInfoRestaurante());
                                            case CULTIVARE_FLAG:
                                                createFirestoreCultivare();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        snackbar.showMensagemLonga(v, e.getMessage());

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbar.showMensagemLonga(v, e.getMessage());
                    }
                });
    }

    private void createFirestoreCultivare() {
    }

    private void createFirestoreProdutor(final Produtor novoProdutor) {
        Task<DocumentSnapshot> query = db.collection(fsRefs.getUsuariosCOLLECTION()).document(usuarioID).get();
        db.collection(fsRefs.getProdutorCOLLECTION())
                .add(novoProdutor)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        documentReference
                                .update("id", documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startIntentQuestionario(URL_PRODUTOR);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbar.showMensagemLonga(v, e.getMessage());
                    }
                });
    }

    private void createFirestoreRestaurante(final Restaurante novoRestaurante) {
        Task<DocumentSnapshot> query = db.collection(fsRefs.getUsuariosCOLLECTION()).document(usuarioID).get();
            db.collection(fsRefs.getRestauranteCOLLECTION())
                    .add(novoRestaurante)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            documentReference
                                    .update("id", documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startIntentQuestionario(URL_CONSUMIDOR);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackbar.showMensagemLonga(v, e.getMessage());
                        }
                    });
    }

    /*private void createFirestoreConsumidor(final Usuario novoUsuario) {
        Task<QuerySnapshot> query = db.collection(fsRefs.getUsuariosCOLLECTION()).get();
        if(query.isSuccessful()) {
                query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (final QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.getString("identificador").equals(novoUsuario.getIdentificador()))
                            snackbar.showMensagemLonga(v, "Identificador em uso ou inválido");
                        else
                            db.collection(fsRefs.getUsuariosCOLLECTION())
                                    .add(novoUsuario)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            documentReference
                                                    .update("id", documentReference.getId())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                                                            usuarioID = documentReference.getId();
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
                                            snackbar.showMensagemLonga(v, e.getMessage());
                                        }
                                    });
                    }
                }
            });
        }
    }*/

    private void startIntentQuestionario(final String URL) {
        showAlerta();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(CadastroActivity.this, WebViewConfig.class);
                intent.putExtra("URL", URL);
                startActivity(intent);
            }
        });

    }

    private void showAlerta() {
        alertDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("SEJA BEM VINDO!")
                .setMessage("Você agora será redirecionado para preencher um formulário direcionado " +
                        "ao projeto de mestrado. Por favor, preencha todos os campos e confirme o envio." +
                        "Caso não queira preencher o envio, basta clicar no botão 'Voltar' no topo da página")
                .setCancelable(false)
                .setPositiveButton("Ok", null)
                .show();
    }

    private Usuario getCamposUsuario() {
        String identificador = Objects.requireNonNull(loginCadastroUsuario.getEditText()).getText().toString();
        String senha = Objects.requireNonNull(senhaCadastroUsuario.getEditText()).getText().toString();
        String nome = Objects.requireNonNull(nomeCadastroUsuario.getEditText()).getText().toString();
        return new Usuario(identificador, senha, nome);
    }

    private Produtor getCamposInfoProdutor() {
        telInfoProdutor = infoProdutor.findViewById(R.id.contatoInfoProdutor);
        localInfoProdutor = infoProdutor.findViewById(R.id.localInfoProdutor);
        emailInfoProdutor = infoProdutor.findViewById(R.id.emailInfoProdutor);

        return new Produtor(Objects.requireNonNull(telInfoProdutor.getEditText()).getText().toString(),
                Objects.requireNonNull(localInfoProdutor.getEditText()).getText().toString(),
                Objects.requireNonNull(emailInfoProdutor.getEditText()).getText().toString(), Objects.requireNonNull(loginCadastroUsuario.getEditText()).getText().toString());
    }

    private Restaurante getCamposInfoRestaurante() {
        telInfoRestaurante = infoRestaurante.findViewById(R.id.contatoInfoRestaurante);
        localInfoRestaurante = infoRestaurante.findViewById(R.id.localInfoRestaurante);
        nomeInfoRestaurante = infoRestaurante.findViewById(R.id.nomeInfoRestaurante);

        return new Restaurante(Objects.requireNonNull(telInfoRestaurante.getEditText()).getText().toString(),
                Objects.requireNonNull(localInfoRestaurante.getEditText()).getText().toString(),
                Objects.requireNonNull(nomeInfoRestaurante.getEditText()).getText().toString(),
                Objects.requireNonNull(loginCadastroUsuario.getEditText()).getText().toString(),
                "");
    }

}




