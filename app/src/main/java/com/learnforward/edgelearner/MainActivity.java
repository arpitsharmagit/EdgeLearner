package com.learnforward.edgelearner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.learnforward.edgelearner.downloader.DownloadItemHelper;
import com.learnforward.edgelearner.downloader.DownloadableItem;
import com.learnforward.edgelearner.downloader.DownloadingStatus;
import com.learnforward.edgelearner.downloader.ItemDetailsViewHolder;
import com.learnforward.edgelearner.downloader.ItemListAdapter;
import com.learnforward.edgelearner.utils.ApplicationHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.learnforward.edgelearner.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements DownloadItemTouchHelper.DownloadItemTouchHelperListener{

    private static final String TAG = "MainActivity";
    public static final String LIBRARY = "BOOKS";
    private static final  int HANDLE_PERM = 1;
    File booksFolder,zipFolder;
    static BookDetails bookDetails;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String bookCode;

    private RecyclerView itemsListView ;
    private ItemListAdapter itemListAdapter;
    private ArrayList<DownloadableItem> downloadableItems;

    private boolean cameraPermission=false,writePermission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG,"onCreate");

        setContentView(R.layout.activity_main);
        itemsListView = findViewById(R.id.download_items_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        downloadableItems = DownloadItemHelper.loadDownloadItems();

        itemListAdapter = new ItemListAdapter(this,
                downloadableItems,
                itemsListView,
                new ItemListAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(DownloadableItem item) {
                if(item.getDownloadingStatus() == DownloadingStatus.EXTRACTED) {
                    Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                    intent.putExtra("bookId", item.getBookId());
                    startActivity(intent);
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemsListView.setLayoutManager(linearLayoutManager);
        itemsListView.setAdapter(itemListAdapter);

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

        zipFolder = ApplicationHelper.zipFolder;
        if(!zipFolder.exists()){
            zipFolder.mkdirs();
        }

        booksFolder = ApplicationHelper.booksFolder;
        if (!booksFolder.exists()) {
            booksFolder.mkdirs();
        }

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new DownloadItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(itemsListView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadItemHelper.saveDownloadItems(itemListAdapter.getDownloadItems());
    }

    @Override
    public void onStart(){
        super.onStart();
        checkPermission();
    }

    @Override
    public void onStop(){
        super.onStop();

        if (isFinishing() && itemListAdapter != null) {
            itemListAdapter.performCleanUp();
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

                            DownloadableItem downloadableItem = new DownloadableItem();
                            downloadableItem.setId(String.valueOf(downloadableItems.size()+1));
                            downloadableItem.setBookId(bookDetails.getBookId());
                            downloadableItem.setDownloadingStatus(DownloadingStatus.NOT_DOWNLOADED);
                            downloadableItem.setBookName(bookDetails.getBookName());
                            downloadableItem.setPages("");
                            downloadableItem.setBookDownloadUrl(bookDetails.getDownloadUrl());

                            itemListAdapter.addDownload(downloadableItem);
                            itemListAdapter.onDownloadStarted(downloadableItem);

                        } else {
                            Log.d(TAG, "Failed to find Book " + bookCode);
                            Snackbar.make(itemsListView, "This book is not available", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ItemDetailsViewHolder) {
            final DownloadableItem  deletedItem = downloadableItems.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            itemListAdapter.removeDownloadItem(viewHolder.getAdapterPosition());

            Snackbar snackbar = Snackbar
                    .make(itemsListView, deletedItem.getBookName()+ " removed from Library!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    itemListAdapter.restoreDownloadItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
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
}
