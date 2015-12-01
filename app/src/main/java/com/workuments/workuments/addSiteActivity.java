package com.workuments.workuments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class addSiteActivity extends AppCompatActivity {

    private EditText siteName;
    private EditText siteUrl;
    private EditText siteUsername;
    private DBAdapter database;

    private Intent previousViewIntent;
    private String id;
    private String workumentsUrl;
    private String workumentsUsername;
    private String workumentsName;
    private WorkumentsApplication app;
    private WorkumentsAPI api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site);

        this.init();

        siteName = (EditText) findViewById(R.id.siteNameSpinner);
        siteUrl = (EditText) findViewById(R.id.siteUrlTextBox);
        siteUsername = (EditText) findViewById(R.id.siteUsernameTextBox);

        previousViewIntent = getIntent();

        id = previousViewIntent.getStringExtra(sitesTableViewActivity.EXTRA_ID);
        workumentsUrl = previousViewIntent.getStringExtra(sitesTableViewActivity.EXTRA_URL);
        workumentsUsername = previousViewIntent.getStringExtra(sitesTableViewActivity.EXTRA_USERNAME);
        workumentsName = previousViewIntent.getStringExtra(sitesTableViewActivity.EXTRA_NAME);

        siteName.setText(workumentsName);
        siteUsername.setText(workumentsUsername);
        siteUrl.setText(workumentsUrl);
        app = (WorkumentsApplication)getApplication();
        api = app.apiConnection;

        this.openDB();
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
    protected void onDestroy() {
        super.onDestroy();

        closeDB();
    }

    public void openDB() {
        database = new DBAdapter(this);
        database.open();
    }

    public void closeDB() {
        database.close();
    }

    public void saveSiteButton_Click(View v) {
        final Intent i = new Intent(this, sitesTableViewActivity.class);
        if(siteName.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a Name", Toast.LENGTH_SHORT).show();
        } else if(siteUrl.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a URL", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(){
                @Override
                public void run(){
                    if(app.isConnectedToNetwork) {
                        final byte[] img = api.GetSiteLogo(siteUrl.getText().toString());
                        if (id != null) {
                            Long tableId = Long.valueOf(id);
                            database.updateRow(tableId, img, siteName.getText().toString(), siteUrl.getText().toString(), siteUsername.getText().toString());
                        } else {
                            database.insertRow(img, siteName.getText().toString(), siteUrl.getText().toString(), siteUsername.getText().toString());
                        }
                    } else {
                        final byte[] img = new byte[0];
                        if (id != null) {
                            Long tableId = Long.valueOf(id);
                            database.updateRow(tableId, img, siteName.getText().toString(), siteUrl.getText().toString(), siteUsername.getText().toString());
                        } else {
                            database.insertRow(img, siteName.getText().toString(), siteUrl.getText().toString(), siteUsername.getText().toString());
                        }
                    }


                    startActivity(i);
                }
            }.start();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                backNavigationHandler();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void backNavigationHandler(){
        Intent i = new Intent(this, sitesTableViewActivity.class);
        this.startActivity(i);
    }
}
