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

import com.learnforward.edgelearner.Models.BookModel;
import com.learnforward.edgelearner.Models.ChapterModel;
import com.learnforward.edgelearner.utils.ApplicationHelper;

import java.io.File;
import java.util.ArrayList;

public class ChaptersActivity extends AppCompatActivity {
    File bookFolder;
    ArrayList<ChapterModel> dataModels;
    ListView listView;

    private static ChapterAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");

        if(bookId.equals("")){
            setResult("");
        }

        listView=(ListView)findViewById(R.id.list);
        dataModels= new ArrayList<ChapterModel>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pages - "+bookId);

        bookFolder = ApplicationHelper.booksFolder;
        File bookFiles =new File(bookFolder,bookId);

        dataModels.clear();
        for (File file: bookFiles.listFiles()) {
            if(file.isFile()){
                dataModels.add(new ChapterModel(file.getName(),file.getAbsolutePath()));
            }
        }

        adapter= new ChapterAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChapterModel dataModel= dataModels.get(position);
                setResult(dataModel.getChapterPath());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setResult("");
        return super.onKeyDown(keyCode, event);
    }

    private void setResult(String page){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("page",page);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


    private class ChapterAdapter extends ArrayAdapter<ChapterModel> implements View.OnClickListener{

        private ArrayList<ChapterModel> dataSet;
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView txtChapter;
        }

        public ChapterAdapter(ArrayList<ChapterModel> data, Context context) {
            super(context, R.layout.row_chapter, data);
            this.dataSet = data;
            this.mContext=context;

        }

        @Override
        public void onClick(View v) {

            int position=(Integer) v.getTag();
            Object object= getItem(position);
            BookModel BookModel=(BookModel)object;

            switch (v.getId())
            {
                case R.id.item_info:
                    Snackbar.make(v, "Book Details" +BookModel.getBookName(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                    break;
            }
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ChapterModel chapterModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ChapterAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ChapterAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.row_chapter, parent, false);
                viewHolder.txtChapter = (TextView) convertView.findViewById(R.id.txtChapter);

                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChapterAdapter.ViewHolder) convertView.getTag();
                result=convertView;
            }

            viewHolder.txtChapter.setText(chapterModel.getChapterName());
            viewHolder.txtChapter.setTag(chapterModel.getChapterPath());
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
