package com.ninjas.edgelearner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ninjas.edgelearner.utils.Utilities;

public class QRGeneratorActivity extends AppCompatActivity {
    final String folderPath =  "barcodes";
    ImageView imageView;
    Button btnGenerate;
    EditText edtProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        imageView = findViewById(R.id.qrCode);
        edtProductId = findViewById(R.id.edtProductId);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                imageView.setImageBitmap(null);
                try {
                    String productId = edtProductId.getText().toString();
                    Bitmap qrBitmap = Utilities.TextToImageEncode(productId,500);
                    imageView.setImageBitmap(qrBitmap);
                    Utilities.saveImage(qrBitmap,folderPath,productId);

                    Snackbar.make(v,"QR code saved in "+folderPath,Snackbar.LENGTH_LONG)
                            .show();
                } catch (WriterException e) {
                    Snackbar.make(v,"Unable to process entered data.",Snackbar.LENGTH_LONG)
                            .show();
                }

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    private void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }
}
