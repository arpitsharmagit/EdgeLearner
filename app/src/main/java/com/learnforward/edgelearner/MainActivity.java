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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    SharedPreferences mPrefs;

    private boolean cameraPermission=false,writePermission=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        itemsListView = findViewById(R.id.download_items_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mPrefs = getSharedPreferences("pref",MODE_PRIVATE);
        downloadableItems = DownloadItemHelper.loadDownloadItems(mPrefs);

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
        DownloadItemHelper.saveDownloadItems(mPrefs, itemListAdapter.getDownloadItems());
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
            String bookUrl = data.getStringExtra("code");
            if (bookUrl != null && URLUtil.isValidUrl(bookUrl)) {
                //process extract code
                int startIndex = bookUrl.lastIndexOf("/") + 1;
                int endIndex = bookUrl.lastIndexOf(".");
                if (endIndex > startIndex) {
                    bookCode = bookUrl.substring(startIndex, endIndex);
                } else {
                    bookCode = bookUrl.substring(startIndex);
                }

                File bookFolder = new File(ApplicationHelper.booksFolder+"/"+ bookCode);
                if(bookFolder.exists()){
                    Snackbar.make(itemsListView, "This book is already in library", Snackbar.LENGTH_LONG).show();
                    return;
                }

                DownloadableItem downloadableItem = new DownloadableItem();
                downloadableItem.setId(String.valueOf(downloadableItems.size() + 1));
                downloadableItem.setBookId(bookCode);
                downloadableItem.setDownloadingStatus(DownloadingStatus.NOT_DOWNLOADED);
                downloadableItem.setBookName("Processing Book...");
                downloadableItem.setPages("");
                downloadableItem.setBookDownloadUrl(bookUrl);

                itemListAdapter.addDownload(downloadableItem);
            }
            else{
                Snackbar.make(itemsListView, "This book is not available", Snackbar.LENGTH_LONG).show();
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
            itemListAdapter.removeDownloadItem(deletedIndex);
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Do you want to Delete ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //delete file
                            File dir = new File(deletedItem.getBookPath());
                            try {
                                if(dir.exists()) {
                                    FileUtils.deleteDirectory(dir);
                                    Log.i(TAG,"Dir Removed");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            itemListAdapter.restoreDownloadItem(deletedItem,deletedIndex);
                        }
                    }).show();
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
        if(writePermission && cameraPermission){
            //test code
            //downloadBook("10000","http://learnforward.in/for_android_app/21165.zip");
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

    private void downloadBook(String bookId,String bookUrl){
        DownloadableItem downloadableItem = new DownloadableItem();
        downloadableItem.setId(String.valueOf(downloadableItems.size() + 1));
        downloadableItem.setBookId(bookId);
        downloadableItem.setDownloadingStatus(DownloadingStatus.NOT_DOWNLOADED);
        downloadableItem.setBookName("Processing Book...");
        downloadableItem.setPages("");
        downloadableItem.setBookDownloadUrl(bookUrl);

        itemListAdapter.addDownload(downloadableItem);
    }
}
