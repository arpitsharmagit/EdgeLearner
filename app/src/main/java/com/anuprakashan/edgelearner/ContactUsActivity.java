package com.anuprakashan.edgelearner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        TextView txtTelephone = findViewById(R.id.txtTelephone);
        TextView txtEmail = findViewById(R.id.txtEmail);

        txtAddress.setText("Learning Solutions\n" +
                "807/2, Sant Vihar, Old Vidya Prakashan Mandir\n" +
                "Office,\n" +
                "Transport Nagar, Meerut City.\n");
        txtTelephone.setText("0121-3192323, 0121-2401096");
        txtEmail.setText("anuprakashanmandir.co.in");
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