package com.adapto.panc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.adapto.panc.Activities.TelaInicialActivity;

public class WebViewConfig extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private WebView mWebView;

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
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setEnableSmoothTransition(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setSavePassword(true);
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Toast.makeText(WebViewConfig.this,"Carregando tela", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
                Toast.makeText(WebViewConfig.this, "Tela carregada com sucesso", Toast.LENGTH_SHORT).show();

            }
        });

        Intent intent = getIntent();
        String URL = intent.getStringExtra("URL");

        mWebView.loadUrl(URL);

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TelaInicialActivity.class);
        startActivity(intent);
    }
}