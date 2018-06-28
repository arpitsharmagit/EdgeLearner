package com.learnforward.edgelearner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        txtAbout.setText(
                "\n" +
                "   Since child is the father of the nation, we owe our love to children. It is our earnest endeavour to provide best quality books to these little birds who have just begun to fluff their wings for their flight into the infinite. We, at Learning Solutions, cover the complete process of teaching-learning and aim to import knowledge in way which will arouse the curiosity of the young energetic minds. Our books give students the opportunity to understand and enjoy their learning. They help teachers to teach in a simple way.\n" +
                "\n" +
                "\n" +
                "\n" +
                "   With over 400 books written and designed as per NCERT, CBSE and ICSE syllabi Learning Solutions is one of India’s  quality school text book publishers.\n" +
                "\n" +
                "\n" +
                "\n" +
                "   Our team of authors, editors, illustrators and reviewers are well experienced, passionate and committed to publishing the best    for students. Our sales distributors across the country provide support in reaching thousands of schools across the country.");
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
