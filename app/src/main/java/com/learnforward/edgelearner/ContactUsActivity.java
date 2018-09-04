package com.learnforward.edgelearner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Us");

        TextView txtAddress = findViewById(R.id.txtAddress);

        txtAddress.setText(Html.fromHtml("<br/><br/><b><h3>Learn Forward</h3></b>" +
                "G-8, Narain Manzil,<br/>" +
                "23-Barakhambha Road,<br/>" +
                "Connaught Place,<br/>" +
                "New Delhi-110 001<br/>" +
                "(INDIA)<br/><br/>" +
                "E: info@learnforward.in<br/>" +
                "W: https://learnforward.in/"));
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