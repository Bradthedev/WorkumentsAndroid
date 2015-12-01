package com.workuments.workuments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by bradcollins on 11/18/15.
 */
public class HelperFunctions {

    private static final String TAG = "HelperFunctions";

    public static ArrayList<SiteResults> GetSiteResultsArray(Cursor cursor){
        ArrayList<SiteResults> results = new ArrayList<SiteResults>();

        if(cursor.moveToFirst()) {
            int i = 0;
            do {
                String id = cursor.getString(DBAdapter.COL_ROWID);
                byte[] icon = cursor.getBlob(DBAdapter.COL_ICON);
                String name = cursor.getString(DBAdapter.COL_NAME);
                String url = cursor.getString(DBAdapter.COL_URL);
                String username = cursor.getString(DBAdapter.COL_USERNAME);

                results.add(i ,new SiteResults(id, icon, name, url, username));

                i++;
            } while(cursor.moveToNext());
            cursor.close();
        }

        return results;
    }

    public static byte[] LoadImageFromWebOperations(String url) {
        try {
            return HelperFunctions.getBitmapAsByteArray(BitmapFactory.decodeStream((InputStream) new URL(url).getContent()));
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
