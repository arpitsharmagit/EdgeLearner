package com.learnforward.edgelearner;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.learnforward.edgelearner.Models.Book.Book;
import com.learnforward.edgelearner.Models.BookDetails;
import com.learnforward.edgelearner.Models.BookModel;
import com.learnforward.edgelearner.utils.ApplicationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String LIBRARY = "BOOKS";
    private static final  int HANDLE_PERM = 1;
    File booksFolder,zipFolder;
    static BookDetails bookDetails;
    ArrayList<BookModel> books;
    ListView listView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences mPrefs;
    CustomAdapter adapter;
    String bookCode;

    ArrayList<String> library; //save downloaded book ids

    private boolean cameraPermission=false,writePermission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"onCreate");

        setContentView(R.layout.activity_main);

        mPrefs = getPreferences(MODE_PRIVATE);
        library = getLibrary();
        if(!library.contains("new-001")) {
            library.add("new-001");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageButton aboutUs = findViewById(R.id.aboutus);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(aboutIntent);
            }
        });
        ImageButton contactUs = findViewById(R.id.contactus);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactUsIntent = new Intent(MainActivity.this,ContactUsActivity.class);
                startActivity(contactUsIntent);
            }
        });

        LinearLayout scan =findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentScan = new Intent(MainActivity.this, ScannerActivity.class);
                startActivityForResult(intentScan,1);
            }
        });

        listView= findViewById(R.id.list);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG,"onNewIntent");
        if (intent != null)
            setIntent(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG,"onResume");

        String result = getIntent().getStringExtra("result");
        String message = getIntent().getStringExtra("message");
        if (result!=null && result.equals("success")) {
            if (library.indexOf(bookDetails.getBookId()) == -1) {
                addBookToList(bookDetails.getBookId());
                Snackbar.make(listView, "Book added to the library.", Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(listView, "Book is already in the library.", Snackbar.LENGTH_LONG).show();
            }
        }
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

        loadBooks();
        adapter = new CustomAdapter(books,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookModel dataModel= books.get(position);
                Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                intent.putExtra("bookId",dataModel.getBookId());
                startActivity(intent);
            }
        });
        Intent data = getIntent();
        if(data.getStringExtra("result")!=null){
            String result = data.getStringExtra("result");
            String message = data.getStringExtra("message");
            if (result.equals("success")) {
                if (library.indexOf(bookDetails.getBookId()) == -1) {
                    //save to library
                    library.add(bookDetails.getBookId());
                    saveLibrary();
                    //add to adapter
                    addBookToList(bookDetails.getBookId());
                    Snackbar.make(listView, "Book added to the library.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    Snackbar.make(listView, "Book is already in the library.", Snackbar.LENGTH_LONG).show();
                }
            } else {

                Snackbar.make(listView, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            bookCode = data.getStringExtra("code");
            if (bookCode != null) {
                if(URLUtil.isValidUrl(bookCode)){
                    //process extract code
                    int startIndex = bookCode.lastIndexOf("/")+1;
                    int endIndex = bookCode.lastIndexOf(".");
                    if(endIndex>startIndex) {
                        bookCode = bookCode.substring(startIndex, endIndex);
                    }
                    else{
                        bookCode = bookCode.substring(startIndex);
                    }
                }
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
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Book found " + bookCode);
                            bookDetails = task.getResult().toObject(BookDetails.class);
                            startDownloadActivity(bookDetails);
                        } else {
                            Log.d(TAG, "Failed to find Book " + bookCode);
                            Snackbar.make(listView, "This book is not available", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            String message = data.getStringExtra("message");
            if (result.equals("success")) {
                if (library.indexOf(bookDetails.getBookId()) == -1) {
                    addBookToList(bookDetails.getBookId());
                    Snackbar.make(listView, "Book added to the library.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    Snackbar.make(listView, "Book is already in the library.", Snackbar.LENGTH_LONG).show();
                }
            } else {

                Snackbar.make(listView, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    boolean bookExists(BookModel book){
        boolean result = false;
        for (BookModel lbook:books) {
            if(lbook.getBookId().equals(book.getBookId())){
                result =true;
                break;
            }
        }
        return result;
    }

    void addBookToList(String bookId) {
        File bookFolder = new File(ApplicationHelper.booksFolder,bookId);
        if(bookFolder.exists()){
            try {
                Book jsonBook =  (new Gson()).fromJson(new FileReader(bookFolder.getAbsolutePath()+"/book.json"),Book.class);
                adapter.add(new BookModel(jsonBook.getId(),jsonBook.getName(),bookFolder.getAbsolutePath(), String.valueOf(jsonBook.getPages().length)));
                adapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    void loadBooks(){
        books = new ArrayList<BookModel>();
        for(String bookId:library){
            File bookFolder = new File(ApplicationHelper.booksFolder,bookId);
            if(bookFolder.exists()){
                try {
                    Book jsonBook =  (new Gson()).fromJson(new FileReader(bookFolder.getAbsolutePath()+"/book.json"),Book.class);
                    books.add(new BookModel(jsonBook.getId(),jsonBook.getName(),bookFolder.getAbsolutePath(), String.valueOf(jsonBook.getPages().length)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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
        intent.putExtra("book", bookDetails);
        startActivity(intent);
    }

    void saveLibrary(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(library);
        prefsEditor.putString(LIBRARY, json);
        prefsEditor.commit();
    }
    void removeBookFromLibrary(String bookId){
        library.remove(bookId);
        saveLibrary();
    }
    void clearLibrary(){
        library.clear();
        saveLibrary();
    }

    ArrayList<String> getLibrary(){
        try {
            String json = mPrefs.getString(LIBRARY, null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            library = new Gson().fromJson(json, type);
            if(library == null){
                library=new ArrayList<String>();
            }
        }
        catch (Exception e){
            library=new ArrayList<String>();
        }
        return library;
    }
}
