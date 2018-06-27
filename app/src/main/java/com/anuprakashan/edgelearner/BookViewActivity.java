package com.anuprakashan.edgelearner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anuprakashan.edgelearner.Models.BookView;
import com.anuprakashan.edgelearner.Models.Pages;
import com.anuprakashan.edgelearner.utils.ApplicationHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BookViewActivity extends AppCompatActivity {

    private float x1,x2;

    private ImageView imageView;
    private int animationCounter = 1;
    BookView bookView;
    String bookPath;
    int currBookPage=1,totalPages=0;
    Pages currentPage;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);

        final TextView bookTitle = findViewById(R.id.bookTitle);
        final TextView bookPage = findViewById(R.id.bookPage);
        final ImageView pageSpeech = findViewById(R.id.sound);
        pageSpeech.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mp.release();
                    mp = null;
                    Uri myUri = Uri.fromFile(new File(String.valueOf(pageSpeech.getTag())));
                    mp = new MediaPlayer();
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mp.setDataSource(getApplicationContext(), myUri);
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");

        Gson gson =new Gson();
        bookPath = ApplicationHelper.booksFolder+"/"+bookId;
        try {
            bookView = gson.fromJson(new FileReader(bookPath+"/book.json"),BookView.class);
            bookTitle.setText(bookView.getBook().getName());
            totalPages = bookView.getBook().getPages().length;
            bookPage.setText(currBookPage+"/"+totalPages);


            //playmusic if exits
            if(!bookView.getBook().getAudio().equals("")){
                Uri myUri = Uri.fromFile(new File(bookPath+"/"+bookView.getBook().getAudio()));
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setLooping(true);
                try {
                    mp.setDataSource(getApplicationContext(), myUri);
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String pageImage = bookPath+"/pages/"+currBookPage+"/"+currBookPage+".jpg";

        final Animation in  = AnimationUtils.loadAnimation(this, R.anim.left_to_right_in);
        final Animation out = AnimationUtils.loadAnimation(this, R.anim.left_to_right_out);

        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(pageImage));;
        imageView.setOnTouchListener(
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = event.getX();
                            return true;
                        case MotionEvent.ACTION_UP:
                            x2 = event.getX();
                            float deltaX = x2 - x1;
                            if (deltaX < 0) {
                                if(currBookPage < totalPages ){
                                    currBookPage++;
                                }
                                Toast.makeText(BookViewActivity.this,
                                        "Right to Left swipe",
                                        Toast.LENGTH_SHORT).show();
                                bookPage.setText(currBookPage+"/"+totalPages);
                                String currentImage = bookPath+"/pages/"+currBookPage+"/"+currBookPage+".jpg";
                                imageView.setImageBitmap(BitmapFactory.decodeFile(currentImage));;
                                imageView.startAnimation(out);
                            }else if(deltaX >0){
                                if(currBookPage > 1 ){
                                    currBookPage--;
                                }

                                Toast.makeText(BookViewActivity.this,
                                        "Left to Right swipe",
                                        Toast.LENGTH_SHORT).show();
                                bookPage.setText(currBookPage+"/"+totalPages);
                                String currentImage = bookPath+"/pages/"+currBookPage+"/"+currBookPage+".jpg";
                                imageView.setImageBitmap(BitmapFactory.decodeFile(currentImage));;
                                imageView.startAnimation(in);
                            }
                            currentPage  = bookView.getBook().getPages()[currBookPage-1];
                            if(!currentPage.getAudio().equals("")){
                                pageSpeech.setVisibility(View.VISIBLE);
                                pageSpeech.setTag(bookPath+"/pages/"+currBookPage+"/"+currBookPage+".mp3");
                            }
                            else{
                                pageSpeech.setVisibility(View.GONE);
                            }
                            return false;
                    }
                    return false;
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }
}
