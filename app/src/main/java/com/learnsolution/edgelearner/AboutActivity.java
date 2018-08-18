package com.learnsolution.edgelearner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.learnsolution.edgelearner.MyViews.DTextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Us");

        DTextView txtAbout = findViewById(R.id.txtAbout);
        txtAbout.setText(Html.fromHtml("<b>Welcome to TDK Learning Solutions</b><br><br>" +
                "Our organization, tdk learning solutions, is glad to serve the publication sector with the help of a team of profound experts. Our publication has designed a professional platform which has a capacity to fulfill the customer requirements on time.\n" +
                "True to its name, tdk learning solutions brings the proficiency to managethe changingpatterns in education industry.All kinds of study materialsare equallysignificant for us; therefore we achieve our targets with a balanced approach and handle the growing demand. \n" +
                "Our purpose is to provide a broad set of learning resources to our stakeholders; therefore we have included not only the print material but also the electronic mode of learning. Our team ensures the most flawless amalgamation of content and worth in each output. \n" +
                "tdk learning solutions is dealing in a broad spectrum of academic text books and is committed to expand its reach to diverse reader’s groups.<br><br>" +
                "Mission<br><br>" +
                "To lead the publication sector by setting the new milestones and providing the unmatched services and products.<br><br>" +
                "Vision<br><br>" +
                "To design the good quality learning materials in way that can be easily comprehended by the learners and win the support of our stakeholders. <br><br>"));
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
