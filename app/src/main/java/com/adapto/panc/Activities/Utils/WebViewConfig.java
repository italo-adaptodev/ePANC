package com.adapto.panc.Activities.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adapto.panc.Activities.CadastroActivity;
import com.adapto.panc.Activities.TelaInicial.TelaInicial;
import com.adapto.panc.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class WebViewConfig extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private WebView mWebView;
    private Toolbar toolbar;
    private String nomeJanela, URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_config);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = this;
        mWebView = findViewById(R.id.web_view);
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        toolbar = findViewById(R.id.toolbar);


        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        nomeJanela = intent.getStringExtra("nomeJanela");
        if(nomeJanela == null)
            toolbar.setTitle("Formulário");
        else {
            toolbar.setTitle(nomeJanela);

        }
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        mWebView.loadUrl(URL);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TelaInicial.class);
        startActivity(intent);
    }


}