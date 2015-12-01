package com.workuments.workuments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class webViewActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "WebViewActivity";

    private webViewActivity act;

    private WebView workumentsWebView;
    private WebSettings workumentsWebSettings;
    private Intent previousViewIntent;
    private String workumentsUrl;
    private String workumentsUsername;
    private String workumentsPassword;
    private ProgressDialog progressBar;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        this.init();

        previousViewIntent = getIntent();

        workumentsUrl = previousViewIntent.getStringExtra(logInActivity.EXTRA_URL);
        workumentsUsername = previousViewIntent.getStringExtra(logInActivity.EXTRA_USERNAME);
        workumentsPassword = previousViewIntent.getStringExtra(logInActivity.EXTRA_PASSWORD);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        progressBar = ProgressDialog.show(this, "Workuments", "Loading...");

        workumentsWebView = (WebView) findViewById(R.id.workumentsWebView);
        workumentsWebSettings = workumentsWebView.getSettings();
        workumentsWebSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        workumentsWebView.setWebViewClient(new WorkumentsWebViewClient(this, progressBar, alertDialog, workumentsUsername, workumentsPassword));
        workumentsWebView.loadUrl("https://" + workumentsUrl + "/services/login/login2.aspx");

        act = this;
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
    protected void onStop(){
        super.onStop();
        workumentsWebView.loadUrl("https://" + workumentsUrl + "/services/app/mobile/Logout.aspx");
    }

    public void hideToolbar() {
        if(getSupportActionBar() != null)
            if(getSupportActionBar().isShowing())
                this.getSupportActionBar().hide();
    }

    public void showToolbar() {
        if(getSupportActionBar() != null)
            if(!getSupportActionBar().isShowing())
                this.getSupportActionBar().show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        private static final int SWIPE_MIN_DISTANCE = 150;

        private static final int SWIPE_MAX_OFF_PATH = 100;

        private static final int SWIPE_THRESHOLD_VELOCITY = 1500;
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if(Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                if (event1.getY() > event2.getY()) {
                    Log.d(DEBUG_TAG, "onFlingUp: " + velocityY + event1.toString() + event2.toString());
                    act.hideToolbar();
                } else {
                    Log.d(DEBUG_TAG, "onFlingDown: " + velocityY + event1.toString() + event2.toString());
                    act.showToolbar();
                }
            }
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
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

    private void settingsRedirect(){
        Intent i = new Intent(this, settingsActivity.class);
        startActivity(i);
    }

}
