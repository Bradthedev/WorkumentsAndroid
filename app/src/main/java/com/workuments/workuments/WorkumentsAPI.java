package com.workuments.workuments;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bradcollins on 12/1/15.
 */
public class WorkumentsAPI{

    private static final String TAG = "WorkumentsApi.Class";

    private String Url;
    private String tempUrl;

    public WorkumentsAPI(String _url){
        Url = _url;
    }

    public Boolean Login(String _username, String _password){
        Boolean result = false;

        tempUrl = "http://" + Url  + "/services/app/mobile/login.aspx?onerror=friendly&username=" + _username + "&password=" + _password;
        try{
            URL siteUrl = new URL(tempUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) siteUrl.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            Log.d(TAG,"Orignal URL: " + urlConnection.getURL());
            urlConnection.connect();
            Log.d(TAG, "Connected URL: " + urlConnection.getURL());
            InputStream is = urlConnection.getInputStream();
            Log.d(TAG, "Redirected URL: " + urlConnection.getURL());
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public byte[] GetSiteLogo(){

        byte[] response = null;

        tempUrl = "http://" + Url  + "/services/app/mobile/get_login_logo_url.ashx";
        String inputLine = "";

        try {
            URL siteUrl = new URL(tempUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) siteUrl.openConnection();
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            while((inputLine = streamReader.readLine()) != null ){
                Log.d(TAG, inputLine);
                response = HelperFunctions.LoadImageFromWebOperations(inputLine.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
