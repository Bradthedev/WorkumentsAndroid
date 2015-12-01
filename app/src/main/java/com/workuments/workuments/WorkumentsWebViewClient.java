package com.workuments.workuments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by bradcollins on 11/18/15.
 */
class WorkumentsWebViewClient extends WebViewClient {

    private static final String TAG = "WorkumentsWebViewClient";

    private boolean AlreadyLoggedIn = false;
    private Activity sender;
    private ProgressDialog pd;
    private AlertDialog ad;
    private String un;
    private String pw;

    private int logInAttempts = 0;

    WorkumentsWebViewClient(Activity activity, ProgressDialog progressDialog, AlertDialog alertDialog, String username, String password){
        sender = activity;
        pd = progressDialog;
        ad = alertDialog;
        un = username;
        pw = password;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(TAG, "Processing webview url click...");
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "Finished loading URL: " + url);
        if (pd.isShowing()) {
            pd.dismiss();
        }
        if(url.contains("login.aspx")) {
            if (!AlreadyLoggedIn) {
                injectLoginScript(view);
                logInAttempts++;

                if(logInAttempts > 5)
                    AlreadyLoggedIn = true;
            }
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.e(TAG, "Error: " + description);
        Toast.makeText(sender, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        ad.setTitle("Error");
        ad.setMessage(description);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        ad.show();
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // Ignore SSL certificate errors
    }

    private void injectLoginScript(WebView view){
        String js = "javascript:(function() { " +
                "$('[name=\"login\"]').val('" + un + "');"+
                "$('[name=\"password\"]').val('" + pw + "');"+
                "$('[name=\"submit\"]').click();"+
                "})()";
        view.loadUrl(js);
    }
}
