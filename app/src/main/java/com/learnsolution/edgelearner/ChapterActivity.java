package com.learnsolution.edgelearner;

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
import com.learnsolution.edgelearner.Models.Book.Book;
import com.learnsolution.edgelearner.Models.Book.Chapters;
import com.learnsolution.edgelearner.utils.ApplicationHelper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ChapterActivity extends AppCompatActivity {

    private ArrayList<Chapters> dataModels;
    private ListView listView;
    private ChapterAdapter adapter;
    Book book;

    Book getBook(String bookId) {
        Gson gson = new Gson();
        String bookPath = ApplicationHelper.booksFolder + "/" + bookId;
        try {
            return gson.fromJson(new FileReader(bookPath + "/book.json"), Book.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Index");

        //Setup Book details
        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");

        if (bookId.equals("")) {
            returnResult(-1);
        }

        book = getBook(bookId);
        if (book == null) {
            returnResult(-1);
        }

        listView = (ListView) findViewById(R.id.list);
        dataModels = new ArrayList<Chapters>(Arrays.asList(book.getChapters()));

        adapter = new ChapterAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chapters dataModel = dataModels.get(position);
                returnResult(dataModel.getPage());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        returnResult(-1);
        return super.onKeyDown(keyCode, event);
    }

    private void returnResult(int page) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("page", page);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private class ChapterAdapter extends ArrayAdapter<Chapters> implements View.OnClickListener {

        private ArrayList<Chapters> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView txtChapter;
        }

        public ChapterAdapter(ArrayList<Chapters> data, Context context) {
            super(context, R.layout.row_chapter, data);
            this.dataSet = data;
            this.mContext = context;

        }

        @Override
        public void onClick(View v) {

            int position = (Integer) v.getTag();
            Object object = getItem(position);
            Chapters chapter = (Chapters) object;

            switch (v.getId()) {
                case R.id.item_info:
                    Snackbar.make(v, "Chapter Details" + chapter.getPage(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Chapters chapter = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ChapterAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ChapterAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_chapter, parent, false);
                viewHolder.txtChapter = (TextView) convertView.findViewById(R.id.txtChapter);

                result = convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChapterAdapter.ViewHolder) convertView.getTag();
                result = convertView;
            }

            viewHolder.txtChapter.setText(chapter.getId()+". "+ chapter.getName());
            viewHolder.txtChapter.setTag(chapter.getId());
            // Return the completed view to render on screen
            return convertView;
        }

    }
}
