package com.learnforward.edgelearner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");

        TextView txtAbout = findViewById(R.id.txtAbout);
        txtAbout.setText(Html.fromHtml("<br/><br/>" +
                "<b>LEARN FORWARD App</b> is a digital learning tool for smart learners. It simplifies learning " +
                "and make it fun in the digital age. With our easy to access digital E-books explore " +
                "anytime anywhere learning with additional voice chapter, animation, activities, puzzles " +
                "that helps build our readers and learner a better understanding and a longer retention of " +
                "the content.<br/><br/>" +
                "It's simple.<br/><br/>" +
                "It's Interactive.<br/><br/>" +
                "It's Fun.<br/><br/>" +
                "Experience learning like never before from one of India's Leading Digital Technology " +
                "Provider Learn Forward."));
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
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
}
