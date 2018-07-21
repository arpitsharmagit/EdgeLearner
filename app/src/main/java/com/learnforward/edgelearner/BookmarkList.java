package com.learnforward.edgelearner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.learnforward.edgelearner.Models.Book.Bookmark;
import com.learnforward.edgelearner.utils.ApplicationHelper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;


public class BookmarkList extends AppCompatActivity {

    private ArrayList<Bookmark> bookmarks;
    private ListView listView;
    private BookmarkListAdapter adapter;

    private String bookId,bookmarkPath;

    void loadBookmark() {
        Gson gson = new Gson();
        bookmarkPath = ApplicationHelper.booksFolder + "/" + bookId+"/bookmark.json";
        try {
            Type listType = new TypeToken<ArrayList<Bookmark>>() {}.getType();
            bookmarks = gson.fromJson(new FileReader(bookmarkPath), listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void saveBook(){
        try {
            bookmarkPath = ApplicationHelper.booksFolder + "/" + bookId+"/bookmark.json";
            FileWriter writer = new FileWriter(bookmarkPath);

            Gson objGson = new GsonBuilder().setPrettyPrinting().create();
            objGson.toJson(bookmarks,writer);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");

        //Setup Book details
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        if (bookId.equals("")) {
            returnResult(-1);
        }

        loadBookmark();
        if (bookmarks == null || bookmarks.size()==0) {
            returnResult(-1);
        }
        else {

            listView = findViewById(R.id.list);

            adapter = new BookmarkListAdapter(bookmarks, getApplicationContext());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bookmark dataModel = bookmarks.get(position);
                    returnResult(dataModel.getPageNo());
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        returnResult(-1);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void returnResult(int page) {
        saveBook();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("page", page);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private class BookmarkListAdapter extends ArrayAdapter<Bookmark> implements View.OnClickListener {

        private ArrayList<Bookmark> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView txtPageNo;
        }

        public BookmarkListAdapter(ArrayList<Bookmark> data, Context context) {
            super(context, R.layout.row_chapter, data);
            this.dataSet = data;
            this.mContext = context;

        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            Bookmark pageNo = (Bookmark) object;

            switch (v.getId()) {
                case R.id.item_info:
                    Snackbar.make(v, "Chapter Details" + pageNo, Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Bookmark pageNo = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_chapter, parent, false);
                viewHolder.txtPageNo = (TextView) convertView.findViewById(R.id.txtChapter);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            viewHolder.txtPageNo.setText("Page " + pageNo.getPageNo());
            // Return the completed view to render on screen
            return convertView;
        }

    }
}
