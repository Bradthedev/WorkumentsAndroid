package com.workuments.workuments;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

/**
 * Created by Bradley on 12/1/2015.
 */
public class WorkumentsApplication extends Application implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = "Application Class";

    //Shared Prefrences final strings
    public final String SHARED_PREFRENCES_NAME = "com.workuments.workuments";
    public final String SP_URL = "com.workuments.workuments.siteUrl";
    public final String SP_USERNAME = "com.workuments.workuments.username";
    public final String SP_PASSWORD = "com.workuments.workuments.password";
    public final String SP_KEEPED_LOGGED_IN = "com.workuments.workuments.keepLoggedIn";
    public final String SP_FIRST_OPENED = "com.workuments.workuments.firstopened";
    public final String SP_LOGOS_LAST_UPDATED = "com.workuments.workuments.lastupdate";

    //Extra Strings
    public final String EXTRA_URL = "url";
    public final String EXTRA_USERNAME = "username";
    public final String EXTRA_PASSWORD = "password";

    public WorkumentsAPI apiConnection;
    public DBAdapter siteDatabase;
    public SharedPreferences workumentsPrefrences;
    public SharedPreferences.Editor workumentsPrefrencesEditor;

    public boolean isConnectedToNetwork;

    @Override
    public void onCreate(){
        super.onCreate();
        this.init();
    }

    private void init(){
        siteDatabase = new DBAdapter(this);
        apiConnection = new WorkumentsAPI(this);
        workumentsPrefrences = getSharedPreferences(SHARED_PREFRENCES_NAME, MODE_PRIVATE);
        workumentsPrefrencesEditor = workumentsPrefrences.edit();

        siteDatabase.open();

        this.registerActivityLifecycleCallbacks(this);

        //check network status
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnectedToNetwork = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        this.firstInit();
        this.update();
    }

    private void firstInit(){
        if(workumentsPrefrences.getBoolean(SP_FIRST_OPENED, true)){
            workumentsPrefrencesEditor.putBoolean(SP_FIRST_OPENED, false);
            workumentsPrefrencesEditor.putBoolean(SP_KEEPED_LOGGED_IN, false);
            workumentsPrefrencesEditor.putString(SP_URL, "");
            workumentsPrefrencesEditor.putString(SP_USERNAME, "");
            workumentsPrefrencesEditor.putString(SP_PASSWORD,"");
            workumentsPrefrencesEditor.putLong(SP_LOGOS_LAST_UPDATED, new Date().getTime());

            apiConnection.setUrl("www.workuments.com");

            siteDatabase.insertRow(apiConnection.GetSiteLogo(), "Workuments", "www.workuments.com","");
            workumentsPrefrencesEditor.commit();
            Log.i(TAG,"First Time Opened firstInit() ran");
        }
    }

    public boolean keepLoggedIn(){
        return workumentsPrefrences.getBoolean(SP_KEEPED_LOGGED_IN,false);
    }

    public void setKeepedLoggedIn(boolean val){
        workumentsPrefrencesEditor.putBoolean(SP_KEEPED_LOGGED_IN,val);
        workumentsPrefrencesEditor.commit();
    }

    public Credentials getCredentialsFromSavedPrefs() {
        Credentials result = new Credentials();
        result.setUrl(workumentsPrefrences.getString(SP_URL, ""));
        result.setUsername(workumentsPrefrences.getString(SP_USERNAME, ""));
        result.setPassword(workumentsPrefrences.getString(SP_PASSWORD,""));
        return result;
    }

    public void updateSavedPrefsCredentials(Credentials credentials){
        workumentsPrefrencesEditor.putString(SP_URL, credentials.getUrl());
        workumentsPrefrencesEditor.putString(SP_USERNAME, credentials.getUsername());
        workumentsPrefrencesEditor.putString(SP_PASSWORD, credentials.getPassword());
        workumentsPrefrencesEditor.commit();
    }

    public Date lastLogoUpdate(){
        long milli = workumentsPrefrences.getLong(SP_LOGOS_LAST_UPDATED,0);
        return new Date(milli);
    }

    private void update(){
        if(isConnectedToNetwork){
            //logo update
            this.updateSiteLogos();
        } else {
            Log.e(TAG,"No Connection to Network");
        }
    }

    public void updateSiteLogos(){
        Log.i(TAG, lastLogoUpdate().toString());
        try {
            apiConnection.UpdateLocalDatabaseLogos();
            workumentsPrefrencesEditor.putLong(SP_LOGOS_LAST_UPDATED, new Date().getTime());
            workumentsPrefrencesEditor.commit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "ACTIVITY CREATED");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "ACTIVITY STARTED");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "ACTIVITY RESUMED");
        this.update();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "ACTIVITY PAUSED");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "ACTIVITY STOPPED");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "ACTIVITY SAVEINSTANCESTATE");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "ACTIVITY DESTORYED");
    }
}
