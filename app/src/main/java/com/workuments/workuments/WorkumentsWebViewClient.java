package com.workuments.workuments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by bradcollins on 11/18/15.
 */
class WorkumentsWebViewClient extends WebViewClient {

    private static final String TAG = "WorkumentsWebViewClient";

    private Activity sender;
    private ProgressDialog pd;
    private AlertDialog ad;
    private String un;
    private String pw;
    private WorkumentsApplication app;

    WorkumentsWebViewClient(Activity activity, ProgressDialog progressDialog, AlertDialog alertDialog){

        sender = activity;
        app = (WorkumentsApplication)sender.getApplication();
        pd = progressDialog;
        ad = alertDialog;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i(TAG, "Processing webview url click... " + url);
        Intent i = new Intent(sender.getBaseContext(), logInActivity.class);
        if (url.contains("Logout.aspx")) {
            view.loadUrl(url);
            app.setKeepedLoggedIn(false);
            sender.startActivity(i);
            sender.finish();
        } else if (url.contains("login.aspx")) {
            if (app.keepLoggedIn()) {
                Credentials credentials = app.getCredentialsFromSavedPrefs();
                view.loadUrl("https://" + credentials.getUrl() + "/services/app/mobile/login.aspx?onerror=friendly&login=" + credentials.getUsername() + "&password=" + credentials.getPassword());
            }
        } else {
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "Finished loading URL: " + url);
        if (pd.isShowing()) {
            pd.dismiss();
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

}
