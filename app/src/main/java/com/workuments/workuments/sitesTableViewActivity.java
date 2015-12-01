package com.workuments.workuments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class sitesTableViewActivity extends AppCompatActivity {

    public final static String EXTRA_ID = "com.workuments.workuments.id";
    public final static String EXTRA_URL = "com.workuments.workuments.siteUrl";
    public final static String EXTRA_USERNAME = "com.workuments.workuments.username";
    public final static String EXTRA_NAME = "com.workuments.workuments.siteName";

    private DBAdapter database;
    private CustomBaseAdapter arrayAdapter;
    private ListView siteListView;

    private ArrayList<SiteResults> siteList = new ArrayList<SiteResults>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_table_view);

        siteListView = (ListView) findViewById(R.id.siteListView);
        siteListView.setClickable(true);
        siteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object o = siteListView.getItemAtPosition(position);
                SiteResults sr = siteList.get(position);
                editSiteRedirect(sr.getId(), sr.getName(), sr.getUrl(), sr.getUsername());
            }
        });

        this.init();

        this.openDB();
        this.viewDatabase();
    }

    private void init(){
        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);

        this.setSupportActionBar(tb);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        tb.setLogo(R.mipmap.ic_launcher);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/century_gothic.ttf");
        TextView tv = (TextView) findViewById(R.id.toolbar_title);
        tv.setTypeface(tf);
        tv.setText("Workuments");
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.viewDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_site_table_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_add_site:
                addSiteRedirect();
                return true;
            case android.R.id.home:
                backNavigationHandler();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backNavigationHandler(){
        Intent i = new Intent(this, sitesTableViewActivity.class);
        this.startActivity(i);
    }

    public void openDB() {
        database = new DBAdapter(this);
        database.open();
    }

    public void closeDB() {
        database.close();
    }

    private void addSiteRedirect(){
        Intent addSite = new Intent(this, addSiteActivity.class);
        startActivity(addSite);
    }

    private void editSiteRedirect(String _id, String _name, String _url, String _username){
        Intent editSiteIntent = new Intent(this, addSiteActivity.class);
        editSiteIntent.putExtra(EXTRA_ID, _id);
        editSiteIntent.putExtra(EXTRA_URL, _url);
        editSiteIntent.putExtra(EXTRA_USERNAME, _username);
        editSiteIntent.putExtra(EXTRA_NAME, _name);
        this.startActivity(editSiteIntent);
    }

    private void viewDatabase(){
        Cursor databaseCursor = database.getAllRows();
        siteList = HelperFunctions.GetSiteResultsArray(databaseCursor);
        arrayAdapter = new CustomBaseAdapter(this, siteList);
        siteListView.setAdapter(arrayAdapter);
    }

}
