package com.adapto.panc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.adapto.panc.FirestoreReferences;
import com.adapto.panc.Models.Database.Produtor;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.R;
import com.adapto.panc.Repository.LoginSharedPreferences;
import com.adapto.panc.SnackBarPersonalizada;
import com.adapto.panc.WebViewConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout nomeCadastroUsuario, loginCadastroUsuario, senhaCadastroUsuario, contatoUsuario;
    private TextInputLayout telInfoProdutor, emailInfoProdutor, localInfoProdutor;
    private TextInputLayout telInfoRestaurante, nomeInfoRestaurante, localInfoRestaurante;
    private SnackBarPersonalizada snackbar;
    private MaterialButton prosseguirButton2, prosseguirButton, cadastroProdutorInfo, cadastroRestauranteInfo;
    private RadioGroup radioGroup;
    private RadioButton radioProdutor, radioConsumidor, radioRestaurante, radioCultivare;
    private static String URL_PRODUTOR = "https://docs.google.com/forms/d/e/1FAIpQLScutVCI0iaI5-tzGDIfo6x7XlczL35043m0XpVrloLk2BKdMA/viewform?usp=sf_link";
    private static String URL_CONSUMIDOR = "https://docs.google.com/forms/d/e/1FAIpQLSc1AZ97PEwRDvcZHsPY9jmumsMseXdLq4Ha7uh6TNH8VGpdPQ/viewform?usp=sf_link";
    private View v;
    private LoginSharedPreferences loginSessionManager;
    private FirebaseFirestore db;
    private String usuarioID;
    private FirestoreReferences fsRefs;
    private View cadastro2 = null, infoProdutor = null, infoRestaurante = null, infoConsumidor = null, infoCultivare = null;
    private AlertDialog alertDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        cadastro2 = getLayoutInflater().inflate(R.layout.activity_cadastro2, null);
        infoProdutor = getLayoutInflater().inflate(R.layout.activity_cadastro_produtor_infos, null);
        infoRestaurante = getLayoutInflater().inflate(R.layout.activity_cadastro_restaurante_infos, null);

        //region Primeira view
        nomeCadastroUsuario = findViewById(R.id.nomeCadastro);
        loginCadastroUsuario = findViewById(R.id.loginCadastro);
        senhaCadastroUsuario = findViewById(R.id.senhaCadastro);
        prosseguirButton = findViewById(R.id.prosseguirButton1);
        //endregion

        prosseguirButton2 = cadastro2.findViewById(R.id.prosseguirButton2);

        //region Views especificas com campos complementares
        cadastroProdutorInfo = infoProdutor.findViewById(R.id.cadastroProdutorInfo);
        cadastroRestauranteInfo = infoRestaurante.findViewById(R.id.cadastroRestauranteInfo);
        //endregion

        snackbar = new SnackBarPersonalizada();
        loginCadastroUsuario.setHelperTextEnabled(true);
        loginCadastroUsuario.setHelperText("Telefone com DDD ou email");
        v = findViewById(android.R.id.content);
        radioProdutor = cadastro2.findViewById(R.id.radioProdutor);
        radioConsumidor = cadastro2.findViewById(R.id.radioConsumidor);
        radioRestaurante = cadastro2.findViewById(R.id.radioRestaurante);
        radioCultivare = cadastro2.findViewById(R.id.radioCultivare);
        radioGroup = cadastro2.findViewById(R.id.radioGroup);
        fsRefs = new FirestoreReferences();

        loginSessionManager = new LoginSharedPreferences(getApplicationContext());

        prosseguirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificarCamposUsuario())
                    setContentView(cadastro2);

            }
        });

        prosseguirButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == radioConsumidor.getId()) {
                    createUsuario();
                    startIntentQuestionario(URL_CONSUMIDOR);
//                    setContentView(infoProdutor);
                } /*else if (selectedId == radioCultivare.getId()) {
                    setContentView(infoProdutor);
                }*/ else if (selectedId == radioProdutor.getId()) {
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
                createProdutor();
            }
        });

        cadastroRestauranteInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRestaurente();
            }
        });


        //endregion

        db = FirebaseFirestore.getInstance();

    }


    //A CHAMADA DO INTENT QUESTIONARIO SERÁ REALIZADA NA FUNÇÃO DE CRIAÇÃO DE FUNÇÃO ESPECÍFICA
    //region Creates
    private void createRestaurente() {
        createUsuario();
        Restaurante novoRestaurante = getCamposInfoRestaurante();
        createFirestoreRestaurante(novoRestaurante);
    }

    private void createCultivare() {
    }

    private void createConsumidor() {
    }

    private void createProdutor() {
        createUsuario();
        Produtor novoProdutor = getCamposInfoProdutor();
        createFirestoreProdutor(novoProdutor);
    }

    private void createUsuario() {
        Usuario novoUsuario = getCamposUsuario();
        createFirestoreUsuario(novoUsuario);
    }
//endregion

    private boolean verificarCamposUsuario() {

        if(loginCadastroUsuario.getEditText().getText().toString().isEmpty() || nomeCadastroUsuario.getEditText().getText().toString().isEmpty()
            || senhaCadastroUsuario.getEditText().getText().toString().isEmpty()) {
            snackbar.showMensagemLonga(v, "Preencha todos os campos corretamente!");
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createFirestoreProdutor(final Produtor novoProdutor) {
        Task<QuerySnapshot> query = db.collection(fsRefs.getProdutorCOLLECTION()).get();
        if(query.isSuccessful()) {
            query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (final QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.getString("identificador").equals(novoProdutor.getUsuarioID()))
                            snackbar.showMensagemLonga(v, "Identificador em uso ou inválido");
                        else
                            db.collection(fsRefs.getUsuariosCOLLECTION())
                                    .add(novoProdutor)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            startIntentQuestionario(URL_PRODUTOR);
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
        }else{
            FirebaseFirestore.getInstance().collection(fsRefs.getProdutorCOLLECTION())
                    .add(novoProdutor)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            startIntentQuestionario(URL_PRODUTOR);
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

    private void createFirestoreRestaurante(final Restaurante novoRestaurante) {
        Task<QuerySnapshot> query = db.collection(fsRefs.getRestauranteCOLLECTION()).get();
        if(query.isSuccessful()) {
            query.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (final QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        if (snap.getString("identificador").equals(novoRestaurante.getUsuarioID()))
                            snackbar.showMensagemLonga(v, "Identificador em uso ou inválido");
                        else
                            db.collection(fsRefs.getRestauranteCOLLECTION())
                                    .add(novoRestaurante)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(final DocumentReference documentReference) {
                                            documentReference.update("restauranteID", documentReference.getId());
//                                            startIntentQuestionario(URL_RESTAURANTE);
                                            startActivity(new Intent(CadastroActivity.this, TelaInicialActivity.class));
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
        }else{
            FirebaseFirestore.getInstance().collection(fsRefs.getRestauranteCOLLECTION())
                    .add(novoRestaurante)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
//                            startIntentQuestionario(URL_RESTAURANTE);
                            startActivity(new Intent(CadastroActivity.this, TelaInicialActivity.class));
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

    private void createFirestoreUsuario(final Usuario novoUsuario) {
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
                                                            usuarioID = novoUsuario.getIdentificador();
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
        }else{
           FirebaseFirestore.getInstance().collection(fsRefs.getUsuariosCOLLECTION())
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
                                            usuarioID = novoUsuario.getIdentificador();
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
                .setMessage("Você agora será redirecionado para preencher um formulário direcionado ao projeto de mestrado. Por favor, preencha todos os campos e confirme o envio." +
                        "Caso não queira preencher o envio, basta clicar no botão 'Voltar' do seu celular para sair da página")
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




