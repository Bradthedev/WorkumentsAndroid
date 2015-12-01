package com.workuments.workuments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
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
import android.widget.Toast;

import java.util.ArrayList;


public class logInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "logInActivity";

    private WorkumentsApplication app;

    private ArrayList<SiteResults> SiteList;

    private DBAdapter database;

    private Spinner site;
    private EditText username;
    private EditText password;
    private Switch keepLoggedIn;

    private String url;
    private WorkumentsAPI api;

    private CustomBaseAdapter arrayAdapter;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        app = (WorkumentsApplication)getApplication();

        site = (Spinner) findViewById(R.id.siteNameSpinner);
        username = (EditText) findViewById(R.id.usernameTextBox);
        password = (EditText) findViewById(R.id.passwordTextBox);
        keepLoggedIn = (Switch) findViewById(R.id.keepLoggedInSwitch);

        this.init();

        api = app.apiConnection;
        database = app.siteDatabase;

        keepLoggedIn.setChecked(app.keepLoggedIn());

        if(app.keepLoggedIn()) {
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
        keepLoggedIn.setChecked(app.keepLoggedIn());
    }

    public void logInButton_Click(View view) {
        Log.d(TAG, "Button 'logInButton' Clicked");
        if(app.isConnectedToNetwork) {
            this.logIn();
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void logIn(){
        Log.d(TAG, "Function 'logIn()' started");
        api.setUrl(url);
        boolean loginValid = api.Login(username.getText().toString(), password.getText().toString());
        if (loginValid) {
            this.logInRedirect();
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Function 'logIn()' ended");
    }

    private void automaticLogIn(){
        Credentials credentials = app.getCredentialsFromSavedPrefs();
        api.setUrl(credentials.getUrl());
        boolean loginValid = api.Login(credentials.getUsername(), credentials.getPassword());
        if (loginValid) {
            logInRedirect(credentials.getUsername(), credentials.getPassword(), credentials.getUrl());
        } else {
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void logInRedirect(){
        Log.d(TAG, "Function 'logInRedirect()' started");

        if(keepLoggedIn.isChecked()) {
            Credentials c = new Credentials(url, username.getText().toString(), password.getText().toString());
            app.updateSavedPrefsCredentials(c);
            app.setKeepedLoggedIn(true);
        }

        Intent logInIntent = new Intent(this, webViewActivity.class);
        logInIntent.putExtra(app.EXTRA_URL, url);
        logInIntent.putExtra(app.EXTRA_USERNAME, username.getText().toString());
        logInIntent.putExtra(app.EXTRA_PASSWORD, password.getText().toString());
        this.startActivity(logInIntent);

        Log.d(TAG, "Function 'logInRedirect()' ended");
    }

    private void logInRedirect(final String _username, final String _password, final String _url){
        Log.d(TAG, "Function 'logInRedirect(String _username, String _password, String _url)' started");
        Intent logInIntent = new Intent(this, webViewActivity.class);
        logInIntent.putExtra(app.EXTRA_URL, _url);
        logInIntent.putExtra(app.EXTRA_USERNAME, _username);
        logInIntent.putExtra(app.EXTRA_PASSWORD, _password);
        this.startActivity(logInIntent);
        Log.d(TAG, "Function 'logInRedirect(String _username, String _password, String _url)' ended");
    }

    private void settingsRedirect(){
        Intent settingsIntent = new Intent(this, sitesTableViewActivity.class);
        startActivity(settingsIntent);
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
        Cursor databaseCursor = database.getAllRows();
        SiteList = HelperFunctions.GetSiteResultsArray(databaseCursor);
        arrayAdapter = new CustomBaseAdapter(this, SiteList);
        site.setAdapter(arrayAdapter);
    }

}
