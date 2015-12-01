package com.workuments.workuments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class logInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public final static String PREFS_NAME = "com.workuments.workuments";

    public final static String EXTRA_URL = "com.workuments.workuments.siteUrl";
    public final static String EXTRA_USERNAME = "com.workuments.workuments.username";
    public final static String EXTRA_PASSWORD = "com.workuments.workuments.password";
    public final static String EXTRA_KEEP_LOGGED_IN = "com.workuments.workuments.keepLoggedIn";

    private static final String TAG = "logInActivity";

    private ArrayList<SiteResults> SiteList;

    private DBAdapter database;

    private Spinner site;
    private EditText username;
    private EditText password;
    private Switch keepLoggedIn;

    private String url;

    private CustomBaseAdapter arrayAdapter;

    private SharedPreferences prefs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        site = (Spinner) findViewById(R.id.siteNameSpinner);
        username = (EditText) findViewById(R.id.usernameTextBox);
        password = (EditText) findViewById(R.id.passwordTextBox);
        keepLoggedIn = (Switch) findViewById(R.id.keepLoggedInSwitch);
        prefs = getSharedPreferences(PREFS_NAME,0);

        this.init();

        boolean kli = prefs.getBoolean(EXTRA_KEEP_LOGGED_IN, false);
        keepLoggedIn.setChecked(kli);

        if(kli) {
            automaticLogIn();
        }

        arrayAdapter = new CustomBaseAdapter(this, SiteList);
        site.setOnItemSelectedListener(this);
        viewDatabase();


    }

    private void init(){
        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);

        this.setSupportActionBar(tb);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setLogo(R.mipmap.ic_launcher);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/century_gothic.ttf");
        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        tv.setTypeface(tf);
        tv.setText("Workuments");

    }

    @Override
    protected void onResume(){
        super.onResume();
        getSharedPreferences(PREFS_NAME, 0);
        boolean kli = prefs.getBoolean(EXTRA_KEEP_LOGGED_IN, false);
        keepLoggedIn.setChecked(kli);
    }

    public void logInButton_Click(View view) {
        Log.d(TAG, "Button 'logInButton' Clicked");
        this.logIn();
    }

    private void logIn(){
        Log.d(TAG, "Function 'logIn()' started");
        new Thread(){
            WorkumentsAPI api = new WorkumentsAPI(url);
            boolean loginValid = api.Login(username.getText().toString(), password.getText().toString());
        }.start();
        this.logInRedirect();
        Log.d(TAG, "Function 'logIn()' ended");
    }

    private void automaticLogIn(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,0);
        logInRedirect(sp.getString(EXTRA_USERNAME, ""), sp.getString(EXTRA_PASSWORD, ""), sp.getString(EXTRA_URL, ""));
    }

    private void logInRedirect(){
        Log.d(TAG, "Function 'logInRedirect()' started");

        if(keepLoggedIn.isChecked()) {
            SharedPreferences sp = getSharedPreferences(PREFS_NAME,0);
            SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean(EXTRA_KEEP_LOGGED_IN, true);
            spe.putString(EXTRA_USERNAME, username.getText().toString());
            spe.putString(EXTRA_PASSWORD, password.getText().toString());
            spe.putString(EXTRA_URL, url);
            spe.commit();
        }

        Intent logInIntent = new Intent(this, webViewActivity.class);
        logInIntent.putExtra(EXTRA_URL, url);
        logInIntent.putExtra(EXTRA_USERNAME, username.getText().toString());
        logInIntent.putExtra(EXTRA_PASSWORD, password.getText().toString());
        this.startActivity(logInIntent);

        Log.d(TAG, "Function 'logInRedirect()' ended");
    }

    private void logInRedirect(final String _username, final String _password, final String _url){
        Log.d(TAG, "Function 'logInRedirect(String _username, String _password, String _url)' started");

        Intent logInIntent = new Intent(this, webViewActivity.class);
        logInIntent.putExtra(EXTRA_URL, _url);
        logInIntent.putExtra(EXTRA_USERNAME, _username);
        logInIntent.putExtra(EXTRA_PASSWORD, _password);
        this.startActivity(logInIntent);
        Log.d(TAG, "Function 'logInRedirect(String _username, String _password, String _url)' ended");
    }

    private void settingsRedirect(){
        Intent settingsIntent = new Intent(this, sitesTableViewActivity.class);
        startActivity(settingsIntent);
    }

    public void openDB() {
        database = new DBAdapter(this);
        database.open();
    }

    public void closeDB() {
        database.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                settingsRedirect();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object i = parent.getSelectedItem();
        if(i.getClass() == SiteResults.class){
            SiteResults sr = (SiteResults)i;
            url = sr.getUrl();
            username.setText(sr.getUsername());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){
        SiteResults sr = SiteList.get(0);
        url = sr.getUrl();
    }

    private void viewDatabase(){
        openDB();
        Cursor databaseCursor = database.getAllRows();
        SiteList = HelperFunctions.GetSiteResultsArray(databaseCursor);
        arrayAdapter = new CustomBaseAdapter(this, SiteList);
        site.setAdapter(arrayAdapter);
        //final byte[] img = HelperFunctions.LoadImageFromWebOperations("http://workuments.com/images/workuments_transparent.gif");
        //database.insertRow( img, "Workuments", "www.workuments.com", "bradthedev@gmail.com");
        closeDB();
    }



}
