package com.adapto.panc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.adapto.panc.Models.Database.Usuario;
import com.adapto.panc.Repository.ReferenciaDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;

public class CadastroActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private FirebaseAuth mAuth;
    private TextInputLayout nomeCadastro;
    private TextInputLayout numeroCadastro;
    private TextInputLayout codConfirmacaoField;
    private SnackBarPersonalizada snackbar;
    private MaterialButton cadastroButton;
    private MaterialButton confirmarCodButton;
    private MaterialButton reenviarCodButton;
    private static String URL_PRODUTOR = "https://docs.google.com/forms/d/e/1FAIpQLScutVCI0iaI5-tzGDIfo6x7XlczL35043m0XpVrloLk2BKdMA/viewform?usp=sf_link";
    private static String URL_CONSUMIDOR = "https://docs.google.com/forms/d/e/1FAIpQLSc1AZ97PEwRDvcZHsPY9jmumsMseXdLq4Ha7uh6TNH8VGpdPQ/viewform?usp=sf_link";
    private FirebaseUser user;
    private DatabaseReference dbRefUsuarios = new ReferenciaDatabase().getDatabaseReference("USUARIOS");
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeCadastro = findViewById(R.id.nomeCadastro);
        numeroCadastro = findViewById(R.id.numeroCadastro);
        codConfirmacaoField = findViewById(R.id.codConfirmacaoField);
        cadastroButton = findViewById(R.id.cadastroButton);

        View codigoView = getLayoutInflater().inflate(R.layout.confirmar_telefone, null);
        confirmarCodButton = codigoView.findViewById(R.id.confirmarCodButton);
        reenviarCodButton = codigoView.findViewById(R.id.reenviarCodButton);


        mAuth = FirebaseAuth.getInstance();
        snackbar = new SnackBarPersonalizada();
        user = FirebaseAuth.getInstance().getCurrentUser();
        numeroCadastro.setHelperTextEnabled(true);
        numeroCadastro.setHelperText("Ex: 7199999999");
        v = findViewById(android.R.id.content);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // It will be invoked in two situations, i.e., instant verification and auto-retrieval:
                // 1 - In few of the cases, the phone number can be instantly verified without needing to  enter or send a verification code.
                // 2 - On some devices, Google Play services can automatically detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    numeroCadastro.setError("Numero inválido.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    snackbar.showMensagemLonga(v,"Cota máxima atingida. Entre em contato com o administrador do aplicativo e informe este erro.");
                }
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code will be sent to the provided phone number
                // Now need to ask the user for entering the code and then construct a credential
                // through integrating the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save the verification ID and resend token to use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        cadastroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(numeroCadastro.getEditText().getText().toString());
                setContentView(R.layout.confirmar_telefone);
            }
        });

        confirmarCodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codConfirmacaoField.getEditText().getText().toString();
                if (TextUtils.isEmpty(code)) {
                    codConfirmacaoField.setError("Campo não pode estar vazio");
                    return;
                }
                //Call the verifyPhoneNumberWithCode () method.
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        reenviarCodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(numeroCadastro.getEditText().getText().toString(), mResendToken);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        //check if a verification is in progress. If it is then we have to re verify.
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(numeroCadastro.getEditText().getText().toString());
        }
    }

    private void updateUI(FirebaseUser user, String mensagem) {
        if(user == null){
            snackbar.showMensagemLonga(v, mensagem);
        }else{
            showAlertDialogButtonClicked(v);
        }
    }

    public void showAlertDialogButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seja bem vindo!!");
        builder.setMessage("Você agora será direcionado para um questionário inicial. " +
                "Por favor, preencha o questionário, depois é só clicar para voltar e ser direcionado a tela incial. " +
                "Escolha qual cargo no aplicativo você deseja exercer inicialmente");
        builder.setPositiveButton("Produtor!", new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CadastroActivity.this, WebViewConfig.class);
                intent.putExtra("URL", URL_PRODUTOR);
                createUsuarioProdutor();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Consumidor!", new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CadastroActivity.this, WebViewConfig.class);
                intent.putExtra("URL", URL_CONSUMIDOR);
                createUsuarioConsumidor();
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createUsuarioConsumidor() {
        Usuario novoUsuario = new Usuario(user.getUid(), false, true);
        dbRefUsuarios.push().setValue(novoUsuario);
    }

    private void createUsuarioProdutor() {
        Usuario novoUsuario = new Usuario(user.getUid(), true, false);
        dbRefUsuarios.push().setValue(novoUsuario);
    }

    //Implementing SaveInstanceState to save the flag.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    //Implementing RestoreInstanceState to restore the flag.
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    // Creating startPhoneNumberVerification() method
    //Getting text code sent. So we can use it to sign-in.
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        //Setting flag to say that the verification is in process.
        mVerificationInProgress = true;
    }

    //Creating a helper method for verification of phone number with code.
    // Entering code and manually signing in with that code
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Creating helper method signInWithPhoneAuthCredential().
    //Use text to sign-in.
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        //Adding onCompleteListener to signInWithCredential.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {
                            // If the Sign-In fails, it will display a message and also update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                codConfirmacaoField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }

    // Creating helper method for validating phone number.
    private boolean validatePhoneNumber() {
        String phoneNumber = numeroCadastro.getEditText().getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            numeroCadastro.setError("Numero invalido");
            return false;
        }

        return true;
    }

    //Creating helper method for resending verification code.
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}