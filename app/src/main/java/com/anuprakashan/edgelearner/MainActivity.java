package com.anuprakashan.edgelearner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.anuprakashan.edgelearner.Models.BookDetails;
import com.anuprakashan.edgelearner.Models.BookModel;
import com.anuprakashan.edgelearner.utils.ApplicationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String LIBRARY = "BOOKS";
    private static final  int HANDLE_PERM = 1;
    File booksFolder,zipFolder;
    BookDetails bookDetails;
    ArrayList<BookModel> books;
    ListView listView;
    private static CustomAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences mPrefs;

    private boolean cameraPermission=false,writePermission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  My Library");
        getSupportActionBar().setLogo(R.drawable.ic_library);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivityForResult(intent,1);
            }
        });

        listView= findViewById(R.id.list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStart(){
        super.onStart();
        checkPermission();
        zipFolder = ApplicationHelper.zipFolder;
        if(!zipFolder.exists()){
            zipFolder.mkdirs();
        }

        booksFolder = ApplicationHelper.booksFolder;
        if (!booksFolder.exists()) {
            booksFolder.mkdirs();
        }

        books = getLibrary();
        if(books==null){
            books = new ArrayList<>();
        }

        adapter= new CustomAdapter(books,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookModel dataModel= books.get(position);

                Intent intent = new Intent(MainActivity.this, BookReaderActivity.class);
                intent.putExtra("bookId",dataModel.getBookId());
                intent.putExtra("bookName",dataModel.getBookName());
                intent.putExtra("bookPath",dataModel.getBookPath());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivityForResult(intent,1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                final String bookCode=data.getStringExtra("code");
                if(bookCode!=null){
                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setMessage("Searching book...");
                    dialog.setCancelable(false);
                    dialog.show();
                    DocumentReference docRef = db.collection("books").document(bookCode);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            if(task.isSuccessful()){
                                Log.d(TAG,"Book found "+bookCode);
                                bookDetails = task.getResult().toObject(BookDetails.class);
                                startDownloadActivity(bookDetails);
                            }
                            else{
                                Log.d(TAG,"Failed to find Book "+bookCode);
                                Snackbar.make(listView,"This book is not available",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                String message =data.getStringExtra("message");
                if(result.equals("success")){
                    String bookLocalPath = booksFolder+File.separator+bookDetails.getBookId();
                    books.add(new BookModel(bookDetails.getBookId(),bookDetails.getBookName(),bookLocalPath,bookDetails.getPages()));
                    saveLibrary();
                    Log.d(TAG,"Zip file Downloaded and Extracted to "+message);

                    Snackbar.make(listView,message,Snackbar.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != HANDLE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if(requestCode == HANDLE_PERM && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            writePermission =true;
            cameraPermission =true;
        }

        if(writePermission !=true || cameraPermission != true) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edge Learner")
                    .setMessage("This application cannot run because it does not have the camera permission.\n" +
                            "The application will now exit.")
                    .setPositiveButton("OK", listener)
                    .show();
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        else{
            writePermission=true;
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        else{
            cameraPermission=true;
        }
    }

    private void requestPermissions() {
        final String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA) ||
            !ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, HANDLE_PERM);
        }
    }

    void startDownloadActivity(BookDetails bookDetails){
        Intent intent = new Intent(MainActivity.this, BookDownloadActivity.class);
        //intent.putExtra("url",bookDetails.downloadUrl);
        intent.putExtra("book", bookDetails);
        startActivityForResult(intent, 2);
    }

    void saveLibrary(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(books);
        prefsEditor.putString(LIBRARY, json);
        prefsEditor.commit();
    }
    ArrayList<BookModel> getLibrary(){
        Gson gson = new Gson();
        String json = mPrefs.getString(LIBRARY, null);
        Type type = new TypeToken<ArrayList<BookModel>>() {}.getType();
        books = new Gson().fromJson(json, type);
        return books;
    }
}
