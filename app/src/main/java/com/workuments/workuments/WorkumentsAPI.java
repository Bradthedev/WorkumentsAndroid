package com.workuments.workuments;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bradcollins on 12/1/15.
 */
public class WorkumentsAPI{

    private static final String TAG = "WorkumentsApi.Class";
    private static final int NUMBER_OF_WORKER_THREADS = 10;

    private String Url;
    private Boolean LoggedIn;
    private String tempUrl;
    private Context context;
    private WorkumentsAPI localContext;
    private DBAdapter db;
    private ExecutorService threadPool;

    public WorkumentsAPI(Context _context) {
        context = _context;
        localContext = this;
        this.init();
    }

    public WorkumentsAPI(Context _context, String _url) {
        Url = _url;
        context = _context;
        this.init();
    }

    public Boolean Login(final String _username, final String _password) {
        Future result = threadPool.submit(new Callable<Object>() {
            public Object call() throws Exception {
                tempUrl = "https://" + Url + "/services/app/mobile/login.aspx?onerror=friendly&login=" + _username + "&password=" + _password;
                try {
                    URL siteUrl = new URL(tempUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) siteUrl.openConnection();
                    urlConnection.setInstanceFollowRedirects(true);
                    Log.d(TAG, "Orignal URL: " + urlConnection.getURL());
                    urlConnection.connect();
                    Log.d(TAG, "Connected URL: " + urlConnection.getURL());
                    InputStream is = urlConnection.getInputStream();
                    Log.d(TAG, "Redirected URL: " + urlConnection.getURL());
                    LoggedIn = urlConnection.getURL().toString().contains("Default.aspx");
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return LoggedIn;
            }
        });

        while (!result.isDone()) ;

        return LoggedIn;
    }

    public byte[] GetSiteLogo(){
        byte[] result = new byte[0];
        Future logo = threadPool.submit(new Callable<Object>() {
            public Object call() throws Exception {
                byte[] tempIcon =  localContext.GetSiteLogo(Url);
                return tempIcon;
            }
        });

        while (!logo.isDone());

        try {
            result = (byte[])logo.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public byte[] GetSiteLogo(String _url) {
        byte[] response = null;

        tempUrl = "https://" + _url + "/services/app/mobile/get_login_logo_url.ashx";
        String inputLine;

        try {
            URL siteUrl = new URL(tempUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) siteUrl.openConnection();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            if (urlConnection.getURL() == siteUrl) {
                while ((inputLine = streamReader.readLine()) != null) {
                    Log.d(TAG, inputLine);
                    response = HelperFunctions.LoadImageFromWebOperations(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void UpdateLocalDatabaseLogos() throws InterruptedException {
        db = new DBAdapter(context);
        db.open();
        Cursor databaseCursor = db.getAllRows();
        ArrayList<SiteResults> SiteList = HelperFunctions.GetSiteResultsArray(databaseCursor);
        int i = 0;
        for (final SiteResults site : SiteList) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    db.updateRow((long) Integer.parseInt(site.getId()), GetSiteLogo(site.getUrl()), site.getName(), site.getUrl(), site.getUsername());
                    Log.d(TAG, "Update Icon for: " + site.getName());
                }
            });
        }
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String _url) {
        Url = _url;
    }

    public Boolean isLoggedIn() {
        return LoggedIn;
    }

    private void init() {
        threadPool = Executors.newFixedThreadPool(NUMBER_OF_WORKER_THREADS);
        HelperFunctions.disableSSLCertificateChecking();
    }
}
