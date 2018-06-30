package com.learnforward.edgelearner;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.learnforward.edgelearner.Models.Book;
import com.learnforward.edgelearner.utils.ApplicationHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BookViewActivity extends AppCompatActivity {


    ImageSwitcher slider;
    TextView bookPage;
    ImageView btnAudio;
    ImageButton btnBack,btnPlay;
    LinearLayout playLayout;
    SeekBar seekBar;

    Book book;

    String bookPath;
    int currPage =0;
    float x1,x2;

    MediaPlayer mp;
    boolean isPlaying =false,isAudioVisible=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);

        //setup controls
        TextView bookTitle = findViewById(R.id.bookTitle);
        bookPage = findViewById(R.id.bookPage);
        slider = findViewById(R.id.imageSwitch);
        seekBar = findViewById(R.id.seekBar);
        playLayout = findViewById(R.id.play_layout);
        btnBack = findViewById(R.id.btn_back);
        btnPlay = findViewById(R.id.btn_play);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    pause();
                    isPlaying=false;
                }
                else{
                    btnPlay.setImageResource(R.drawable.ic_play);
                    play();
                    isPlaying=true;
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seek(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                play();
            }
        });

        //Setup Book details
        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");

        book = getBook(bookId);
        if(book == null){
            finish();
        }
        bookTitle.setText(book.getName());

        //setup slider
        slider.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        slider.setOnTouchListener(new View.OnTouchListener() {
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
                            //Right to left
                            slideToLeft();

                        }else if(deltaX >0){
                            //left to Right
                            slideToRight();
                        }
                        return false;
                }
                return false;
            }
        });

        btnAudio = findViewById(R.id.btnAudio);
        btnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(isAudioVisible){
                        playLayout.setVisibility(View.GONE);
                        isAudioVisible=false;
                        pause();
                    }
                    else {
                        Uri myUri = Uri.fromFile(new File(String.valueOf(btnAudio.getTag())));
                        initMediaPlayer(myUri);
                        btnPlay.setImageResource(R.drawable.ic_pause);
                        play();
                        playLayout.setVisibility(View.VISIBLE);
                        isAudioVisible=true;
                        isPlaying=true;
                    }
                }
                return false;
            }
        });

        initializeBook();
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp!=null){
            mp.release();
            mp = null;
        }
    }

    void initializeBook(){
        currPage = 0;
        String page = book.getPages()[currPage];
        bookPage.setText((currPage+1) +"/"+book.getPages().length);
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/pages/").append(page).append(".jpg");
        slider.setImageURI(Uri.fromFile(new File(builder.toString())));

        //set activity button
        showSpeechButton();
    }
    void slideToLeft(){
        currPage++;
        //set image and text
        if(currPage == book.getPages().length ){
            currPage = book.getPages().length-1;
            return;
        }
        String page = book.getPages()[currPage];
        bookPage.setText((currPage+1) +"/"+book.getPages().length);
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/pages/").append(page).append(".jpg");
        slider.setInAnimation(this,R.anim.slide_in_right);
        slider.setOutAnimation(this,R.anim.slide_out_left);
        slider.setImageURI(Uri.fromFile(new File(builder.toString())));

        if(mp!=null)
            mp.pause();

        //show speech button
        showSpeechButton();
        //set activity button

    }
    void slideToRight(){
        currPage--;
        if(currPage < 0){
            currPage=0;
            return;
        }
        String page = book.getPages()[currPage];
        bookPage.setText((currPage+1) +"/"+book.getPages().length);
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/pages/").append(page).append(".jpg");
        slider.setInAnimation(this,R.anim.slide_in_left);
        slider.setOutAnimation(this,R.anim.slide_out_right);
        slider.setImageURI(Uri.fromFile(new File(builder.toString())));

        if(mp!=null)
            mp.pause();

        //show speech button
        showSpeechButton();
        //set activity button
    }
    void showSpeechButton(){
        String page = book.getPages()[currPage];
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/audio/").append(page).append(".mp3");
        String audioPath = builder.toString();
        File audio = new File(audioPath);
        if(audio.exists()){
            btnAudio.setVisibility(View.VISIBLE);
            btnAudio.setTag(builder.toString());
        }
        else{
            btnAudio.setVisibility(View.GONE);
        }
    }
    Book getBook(String bookId){
        Gson gson =new Gson();
        bookPath = ApplicationHelper.booksFolder+"/"+bookId;
        try {
            return gson.fromJson(new FileReader(bookPath+"/book.json"),Book.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    void play(){
        if(mp!=null){
            mp.start();
        }
    }
    void seek(int ms){
        mp.seekTo(ms);
    }
    void pause(){
        if(mp!=null){
            mp.pause();
        }
    }
    void initMediaPlayer(Uri uri){
        if(mp!=null){
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(getApplicationContext(), uri);
            mp.prepare();
            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
