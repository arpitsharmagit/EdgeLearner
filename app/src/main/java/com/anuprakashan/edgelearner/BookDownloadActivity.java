package com.anuprakashan.edgelearner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anuprakashan.edgelearner.Models.BookDetails;
import com.anuprakashan.edgelearner.utils.ApplicationHelper;

import com.anuprakashan.edgelearner.utils.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class BookDownloadActivity extends AppCompatActivity {

    private static final String TAG = "BookDownloadActivity";
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    ProgressBar progressBar;
    ImageButton btnCancel;
    TextView txtStatus,txtBookName,txtBookSize;
    DownloaderTask downloadTask;

    int count;
    BookDetails bookDetails;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_download);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        txtStatus = findViewById(R.id.txtStatus);
        txtBookName =findViewById(R.id.txt_bookname);
        txtBookSize = findViewById(R.id.txt_booksize);
        progressBar = findViewById(R.id.progressBarDownload);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadTask!=null)
                    downloadTask.cancel(true);
            }
        });

        Bundle data = getIntent().getExtras();
        bookDetails = data.getParcelable("book");

        if(bookDetails.getBookId()!=null && bookDetails.getDownloadUrl() !=null){
            txtBookSize.setText("Download Size: Calculating...");
            txtBookName.setText(bookDetails.getBookName());
            File file = new File(ApplicationHelper.zipFolder,bookDetails.getBookId()+".zip") ;
            if(file.exists()){
                file.delete();
            }
            startDownload();
        }
        else{
            sendResult(ERROR,"Download url is Empty.");
        }
    }

    private void extractFile(String extractFile){
        Utilities.unzip(extractFile,ApplicationHelper.booksFolder.getAbsolutePath()+"/"+bookDetails.getBookId());
        txtStatus.setText("Extracted");
        sendResult(SUCCESS,"Download Completed Successfully!!!");
    }

    private void startDownload(){
        downloadTask= new DownloaderTask(BookDownloadActivity.this);
        downloadTask.execute(bookDetails.getBookId(),bookDetails.getDownloadUrl());
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    private void sendResult(String result,String message){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        returnIntent.putExtra("message",message);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    class DownloaderTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        File file;
        String fileSize;
        boolean sizeKnown=false;

        public DownloaderTask(Context context) {
            this.context = context;
        }
        public String getStringSizeLengthFile(long size) {

            DecimalFormat df = new DecimalFormat("0.00");

            float sizeKb = 1024.0f;
            float sizeMo = sizeKb * sizeKb;
            float sizeGo = sizeMo * sizeKb;
            float sizeTerra = sizeGo * sizeKb;


            if(size < sizeMo)
                return df.format(size / sizeKb)+ " Kb";
            else if(size < sizeGo)
                return df.format(size / sizeMo) + " Mb";
            else if(size < sizeTerra)
                return df.format(size / sizeGo) + " Gb";

            return "";
        }
        @Override
        protected String doInBackground(String... sUrl) {
            String finalStatus = "";
            String downloadUrl =sUrl[1];
            String bookId = sUrl[0];
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String fileName = bookId +".zip";

            file = new File(ApplicationHelper.zipFolder,fileName);
            try {
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if(connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
                    finalStatus = connection.getResponseMessage();
                    return finalStatus;
                }
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    finalStatus = connection.getResponseMessage();
                    return finalStatus;
                }

                int fileLength = connection.getContentLength();

                fileSize = getStringSizeLengthFile(fileLength);

                input = connection.getInputStream();
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        output.close();
                        finalStatus = "Download Cancelled";
                        return finalStatus;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                    {
                        sizeKnown =true;
                        publishProgress((int) (total * 100 / fileLength));
                    }
                    else {
                        publishProgress((int)total);
                        sizeKnown =false;
                    }
                    output.write(data, 0, count);
                }
                finalStatus = file.getAbsolutePath();
            } catch (Exception e) {
                finalStatus = e.toString();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();

                return finalStatus;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            if(sizeKnown) {
                progressBar.setProgress(progress[0]);
                txtBookSize.setText("Download Size: " + fileSize);
                txtStatus.setText("Downloading..." + progress[0] + "%");
            }
            else {
                progressBar.setIndeterminate(true);
                txtBookSize.setText("");
                txtStatus.setText("Downloading..." + getStringSizeLengthFile(progress[0]));
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null){
                //Start Extraction Task
                txtStatus.setText("Download Complete.");
                Snackbar.make(progressBar,"Book Download result "+result,Snackbar.LENGTH_LONG).show();
                File file = new File(result);
                if(file.exists()) {
                    txtStatus.setText("Extracting...");
                    extractFile(result);
                }
                else{
                    sendResult(ERROR,result);
                }
            }
        }
        @Override
        protected void onCancelled (String result) {
            super.onCancelled(result);
            //anything else you want to do after the task was cancelled, maybe delete the incomplete download.
            if(file!=null && file.exists())
                file.delete();
            sendResult(ERROR,result);
        }
    }
}


