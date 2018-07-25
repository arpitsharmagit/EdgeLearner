package com.anuprakashan.edgelearner;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.anuprakashan.edgelearner.utils.ApplicationHelper;

import java.io.File;
import java.lang.reflect.Method;

public class BookReaderActivity extends AppCompatActivity {

    WebView myWebView;
    File bookFolder;
    String bookId;
    String bookName;
    String bookPath;

    ImageButton back,forward,list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_reader);

        bookFolder = ApplicationHelper.booksFolder;
        myWebView = findViewById(R.id.webview);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        }else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        CookieManager.setAcceptFileSchemeCookies(true);

        back = findViewById(R.id.btnBack);
        list = findViewById(R.id.btnList);
        forward = findViewById(R.id.btnForward);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myWebView.canGoBack()){
                    myWebView.goBack();
                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myWebView.canGoForward()){
                    myWebView.goForward();
                }
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookReaderActivity.this, ChaptersActivity.class);
                intent.putExtra("bookId",bookId);
                startActivityForResult(intent,1);
            }
        });

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        bookName = intent.getStringExtra("bookName");
        bookPath = intent.getStringExtra("bookPath");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book - "+bookName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String bookIndexPage =bookFolder+File.separator+bookId;

        File lFile = new File(bookIndexPage,"index.html");

        WebSettings webSettings = myWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 16) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(false);

        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
        myWebView.loadUrl("file:///"+ lFile.getAbsolutePath()+"#p=1");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        myWebView.evaluateJavascript("$('#BGSound')[0].pause();", null);
                    } else {
                        myWebView.loadUrl("javascript:$('#BGSound')[0].pause();");
                    }
                        finish();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                String page=data.getStringExtra("page");
                if(!page.equals("")){
                    myWebView.loadUrl("file:///"+page);
                    Toast.makeText(getApplicationContext(), "Page Loaded",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }
}
