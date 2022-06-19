package com.adapto.panc.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adapto.panc.Activities.TelaInicial.TelaInicial;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Activities.Utils.WebViewConfig;
import com.adapto.panc.Models.Database.Produtor;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
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
    private Intent telaInicialIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        cadastro_cargos = getLayoutInflater().inflate(R.layout.activity_cadastro_cargos, null);
        infoProdutor = getLayoutInflater().inflate(R.layout.activity_cadastro_produtor_infos, null);
        infoRestaurante = getLayoutInflater().inflate(R.layout.activity_cadastro_restaurante_infos, null);
        db = FirebaseFirestore.getInstance();
        db.clearPersistence();
        telaInicialIntent = new Intent(this, TelaInicial.class);


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
                    createFirestoreConsumidorUser(getCamposUsuario());
                } else if (selectedId == radioCultivare.getId()) {
                    createFirestoreCultivareUser(getCamposUsuario());
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
                createFirestoreProdutorUser(getCamposUsuario());
            }
        });

        cadastroRestauranteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFirestoreRestauranteUser(getCamposUsuario());
            }
        });


        //endregion



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

    private void createFirestoreConsumidorUser(Usuario novoUsuario){
        db.collection(fsRefs.getUsuariosCOLLECTION()).add(novoUsuario)
                .addOnSuccessListener(documentReference -> documentReference
                        .update("id", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                            usuarioID = novoUsuario.getIdentificador();
                            startIntentQuestionario(URL_CONSUMIDOR);
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                snackbar.showMensagemLonga(v, e.getMessage());

                            }
                        }))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbar.showMensagemLonga(v, e.getMessage());
                    }
                });
    }

    private void createFirestoreProdutorUser(Usuario novoUsuario){
        db.collection(fsRefs.getUsuariosCOLLECTION()).add(novoUsuario)
                .addOnSuccessListener(documentReference -> documentReference
                        .update("id", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                            usuarioID = novoUsuario.getIdentificador();
                            createFirestoreProdutor(getCamposInfoProdutor());
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                snackbar.showMensagemLonga(v, e.getMessage());

                            }
                        }))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbar.showMensagemLonga(v, e.getMessage());
                    }
                });
    }

    private void createFirestoreRestauranteUser(Usuario novoUsuario){
        db.collection(fsRefs.getUsuariosCOLLECTION()).add(novoUsuario)
                .addOnSuccessListener(documentReference -> documentReference
                        .update("id", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                            usuarioID = novoUsuario.getIdentificador();
                            createFirestoreRestaurante(getCamposInfoRestaurante());
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                snackbar.showMensagemLonga(v, e.getMessage());

                            }
                        }))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbar.showMensagemLonga(v, e.getMessage());
                    }
                });
    }

    private void createFirestoreCultivareUser(Usuario novoUsuario){
        db.collection(fsRefs.getUsuariosCOLLECTION()).add(novoUsuario)
                .addOnSuccessListener(documentReference -> documentReference
                        .update("id", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            loginSessionManager.createLoginSession(novoUsuario.getIdentificador());
                            usuarioID = novoUsuario.getIdentificador();
                            createFirestoreCultivare();
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                snackbar.showMensagemLonga(v, e.getMessage());

                            }
                        }))
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
            db.collection(fsRefs.getRestauranteCOLLECTION())
                    .add(novoRestaurante)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            documentReference
                                    .update("id", documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(telaInicialIntent);
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
                .setTitle("SEJA BEM VINDO(A)!")
                .setMessage("Você agora será redirecionado para preencher um formulário direcionado " +
                        "a esse projeto. Por favor, preencha todos os campos e confirme o envio\n\n." +
                        "Caso não queira preencher o formulário, basta clicar no botão 'X' no topo da página")
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

        return new Produtor("55" + telInfoProdutor.getEditText().getText().toString(),
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




