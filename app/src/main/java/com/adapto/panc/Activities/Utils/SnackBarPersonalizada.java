package com.adapto.panc.Activities.Utils;

import android.content.Context;
import android.view.View;

import com.adapto.panc.Repository.LoginSharedPreferences;
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

    public void showMensagemLongaClose(View v, String mensagem, final Context context){
        Snackbar.make(v, mensagem, Snackbar.LENGTH_INDEFINITE)
                .setMaxInlineActionWidth(4)
                .setAction("FECHAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new LoginSharedPreferences(context).logoutUser();
                    }
                })
                .show();
    }
}
