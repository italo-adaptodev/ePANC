package com.adapto.panc;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackBarPersonalizada {

    public void showMensagemLonga(View v, String mensagem){
        Snackbar.make(v, mensagem, Snackbar.LENGTH_LONG)
                .show();
    }

    public void showMensagemCurta(View v, String mensagem){
        Snackbar.make(v, mensagem, Snackbar.LENGTH_SHORT)
                .show();
    }
}
