package com.vishalprakashan.edgelearner;

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
        txtAbout.setText(Html.fromHtml("The Journey of <b>VISHAL PRAKASHAN MANDIR</b> started in the year 1980, under the energetic leadership of Mr.Vijay Rastogi with graduation and post graduation level books. The objective to start this publication house is to provide study material at moderate price so that students can get maximum benefit from the study material of this publishing house and serve the mankind.<br><br>"+
        "With the grace of God & tremendous response from the students and learned teachers, we are motivated by them to publish books for Medical & Engineering Entrance Examination. In the year 1995, we have launched our competitive series in both medium (English & Hindi).<br><br>"+
        "After receiving the overwhelming response and demand of the market we introduced our course text book series for XI and XII in the year 2004.<br><br>"+
        "After achieving the tremendous success in above said fields, from the year 2012, we started publishing series for young learners, which follows the syllabi of CBSE, NCERT, NEW DELHI, ICSE and various other state education boards.<br><br>"+
        "Currently, Vishal Prakashan Mandir dealing with 100’s of books. This could not be possible to this house without the heartly supports and valuable suggestions from the learned teachers and students.<br><br>"+
        "We are extremely grateful to them in liking of material.<br><br>"));
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
