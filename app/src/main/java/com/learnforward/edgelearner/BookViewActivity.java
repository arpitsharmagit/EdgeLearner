package com.learnforward.edgelearner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.learnforward.edgelearner.CustomImageView.GestureImageView;
import com.learnforward.edgelearner.Models.Book.Book;
import com.learnforward.edgelearner.Models.Book.Bookmark;
import com.learnforward.edgelearner.paint.DrawingView;
import com.learnforward.edgelearner.utils.ApplicationHelper;
import com.google.gson.Gson;
import com.learnforward.edgelearner.utils.Utilities;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookViewActivity extends AppCompatActivity {

    //Bottom toolbar buttons
    ImageButton btnColor,btnLineSize,btnPen,btnRectangle,btnErase,btnRemove;

    //Top toolbar buttons
    ImageButton btnIndex,btnBookmark,btnBookmarkList,btnEditText,btnGoto,btnZoom,btnHelp;

    //Drawing View
    DrawingView drawingView;

    //preferences
    private int mSelectedColor;

    private ArrayList<Bookmark> bookmarks;

    ImageSwitcher slider;
    TextView bookPage;
    ImageView btnAudio,btnActivity;
    ImageButton btnBack,btnPlay;
    LinearLayout playLayout;
    SeekBar seekBar;
    Spotlight spotlight;

    //color
    int transparent,grey;

    Book book;

    String bookPath;
    int currPage =0;
    float x1,x2;

    MediaPlayer mp;
    private Handler mHandler;
    private Runnable mRunnable;
    boolean isPlaying =false,isAudioVisible=false,isBookmarked=false,isZoom=false,isComment=false,isSpotlight=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);


        //Setup Book details
        Intent intent = getIntent();
        String bookId = intent.getStringExtra("bookId");

        book = getBook(bookId);
        if(book == null){
            finish();
        }

        loadBookmark();
        mHandler = new Handler();

        //top toolbar
        btnIndex = findViewById(R.id.btn_index);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnBookmarkList = findViewById(R.id.btn_bookmark_list);
        btnEditText = findViewById(R.id.btn_text);
        btnGoto=findViewById(R.id.btn_goto);
        btnZoom = findViewById(R.id.btn_zoom);
        btnHelp = findViewById(R.id.btn_help);
        drawingView = findViewById(R.id.draw_overlay);
        bindTopToolbar();

        //bottom toolbar
        btnColor=findViewById(R.id.btn_color);
        btnLineSize = findViewById(R.id.btn_linesize);
        btnPen = findViewById(R.id.btn_pen);
        btnRectangle= findViewById(R.id.btn_rectangle);
        btnErase = findViewById(R.id.btn_erase);
        btnRemove = findViewById(R.id.btn_remove);
        bindBottomToolbar();

        //defaults
        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);
        grey = getResources().getColor(R.color.colorDarkGrey);
        transparent = Color.TRANSPARENT;


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
                    btnPlay.setImageResource(R.drawable.ic_play);
                    pause();
                    isPlaying=false;
                }
                else{
                    btnPlay.setImageResource(R.drawable.ic_pause);
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
                if(mp!=null && fromUser) {
                    seek(progress);
                }
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
                        if (deltaX < -50.0f) {
                            //Right to left
                            slideToLeft();

                        }else if(deltaX >50.0f){
                            //left to Right
                            slideToRight();
                        }
                        return false;
                }
                return false;
            }
        });

        btnAudio = findViewById(R.id.btnAudio);
        btnAudio.setImageBitmap(Utilities.loadImage(bookPath+"/extra/"+book.getSoundImg()));
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

        btnActivity =findViewById(R.id.btnActivity);
        btnActivity.setImageBitmap(Utilities.loadImage(bookPath+"/extra/"+book.getActivityImg()));
        btnActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //check activity type
                    Intent intent = new Intent(BookViewActivity.this, McqActivity.class);
                    intent.putExtra("activity",btnActivity.getTag().toString());
                    startActivity(intent);
                }
                return false;
            }
        });

        initializeBook();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(isSpotlight) {
            spotlight.closeSpotlight();
        }
        else{
            super.onBackPressed();
        }
    }
    void initializeBook(){
        currPage = 0;
        Bookmark bookmark = new Bookmark();
        bookmark.setPageNo(currPage+1);
        if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
            isBookmarked=true;
            btnBookmark.setBackgroundColor(grey);
        }
        else{
            isBookmarked=false;
            btnBookmark.setBackgroundColor(transparent);
        }
        String page = book.getPages()[currPage];
        bookPage.setText((currPage+1) +"/"+book.getPages().length);
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/pages/").append(page).append(".jpg");
        slider.setImageURI(Uri.fromFile(new File(builder.toString())));

        //set activity button
        showSpeechButton();
        showActivityButton();
    }
    void slideToLeft(){
        currPage++;
        Bookmark bookmark = new Bookmark();
        bookmark.setPageNo(currPage+1);
        if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
            isBookmarked=true;
            btnBookmark.setBackgroundColor(grey);
        }
        else{
            isBookmarked=false;
            btnBookmark.setBackgroundColor(transparent);
        }
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

        if(mp!=null) {
            mp.pause();
            playLayout.setVisibility(View.GONE);
        }

        //show speech button
        showSpeechButton();
        //set activity button
        showActivityButton();

    }
    void slideToRight(){
        currPage--;
        Bookmark bookmark = new Bookmark();
        bookmark.setPageNo(currPage+1);
        if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
            isBookmarked=true;
            btnBookmark.setBackgroundColor(grey);
        }
        else{
            isBookmarked=false;
            btnBookmark.setBackgroundColor(transparent);
        }
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

        if(mp!=null) {
            mp.pause();
            playLayout.setVisibility(View.GONE);
        }

        //show speech button
        showSpeechButton();
        //set activity button
        showActivityButton();
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
    void showActivityButton(){
        String page = book.getPages()[currPage];
        StringBuilder builder = new StringBuilder();
        builder.append(bookPath).append("/activities/").append(page).append(".json");
        String actPath = builder.toString();
        File activity = new File(actPath);
        if(activity.exists()){
            btnActivity.setVisibility(View.VISIBLE);
            btnActivity.setTag(builder.toString());
        }
        else{
            btnActivity.setVisibility(View.GONE);
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
            if(mHandler!=null){
                mHandler.removeCallbacks(mRunnable);
            }
        }
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(getApplicationContext(), uri);
            mp.prepare();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playLayout.setVisibility(View.GONE);
                    if(mHandler!=null){
                        mHandler.removeCallbacks(mRunnable);
                    }
                }
            });
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    seekBar.setProgress(0);
                    seekBar.setMax(mp.getDuration());

                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if(mp!=null){
                                int mCurrentPosition = mp.getCurrentPosition(); // In milliseconds
                                seekBar.setProgress(mCurrentPosition);
                            }
                            mHandler.postDelayed(mRunnable,1000);
                        }
                    };
                    mHandler.postDelayed(mRunnable,1000);

                    mp.start();
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    void gotoPage(int pageNo){
        if(pageNo>-1 && pageNo <book.getPages().length) {
            currPage = pageNo;
            String page = book.getPages()[currPage];
            bookPage.setText((currPage + 1) + "/" + book.getPages().length);
            StringBuilder builder = new StringBuilder();
            builder.append(bookPath).append("/pages/").append(page).append(".jpg");
            slider.setImageURI(Uri.fromFile(new File(builder.toString())));

            //set activity button
            showSpeechButton();
            showActivityButton();
        }
    }
    void bindBottomToolbar(){
        //ImageButton btnColor,btnLineSize,btnPen,btnRectangle,btnErase,btnRemove;

        btnPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawingView.getVisibility() == View.GONE){
                    String page = book.getPages()[currPage];
                    StringBuilder builder = new StringBuilder();
                    builder.append(bookPath)
                            .append("/pages/")
                            .append(page)
                            .append("paint")
                            .append(".png");
                    drawingView.setBitmap(builder.toString());
                    drawingView.setVisibility(View.VISIBLE);
                }
                drawingView.setDrawType(DrawingView.ShapeFreehand);
                btnRemove.setBackgroundColor(grey);
                btnPen.setBackgroundColor(grey);
                btnRectangle.setBackgroundColor(transparent);
            }
        });
        btnRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawingView.getVisibility() == View.GONE){
                    String page = book.getPages()[currPage];
                    StringBuilder builder = new StringBuilder();
                    builder.append(bookPath)
                            .append("/pages/")
                            .append(page)
                            .append("paint")
                            .append(".png");
                    drawingView.setBitmap(builder.toString());
                    drawingView.setVisibility(View.VISIBLE);
                }
                drawingView.setDrawType(DrawingView.ShapeRect);
                btnRemove.setBackgroundColor(grey);
                btnRectangle.setBackgroundColor(grey);
                btnPen.setBackgroundColor(transparent);
            }
        });
        btnErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.clear();
                String page = book.getPages()[currPage];
                StringBuilder builder = new StringBuilder();
                builder.append(bookPath)
                        .append("/pages/")
                        .append(page)
                        .append("paint")
                        .append(".png");
                drawingView.savePaint(builder.toString());
                drawingView.setVisibility(View.GONE);
                btnRemove.setBackgroundColor(transparent);
                btnPen.setBackgroundColor(transparent);
                btnRectangle.setBackgroundColor(transparent);
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String page = book.getPages()[currPage];
                StringBuilder builder = new StringBuilder();
                builder.append(bookPath)
                        .append("/pages/")
                        .append(page)
                        .append("paint")
                        .append(".png");
                drawingView.savePaint(builder.toString());
                drawingView.setVisibility(View.GONE);
                btnRemove.setBackgroundColor(transparent);
                btnPen.setBackgroundColor(transparent);
                btnRectangle.setBackgroundColor(transparent);
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] mColors = getResources().getIntArray(R.array.default_rainbow);
                final ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                        mColors,
                        mSelectedColor,
                        5,
                        ColorPickerDialog.SIZE_SMALL,
                        true
                );
                dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColor = color;
                        drawingView.setColor(color);
                        btnColor.setBackgroundColor(transparent);
                    }

                });
                dialog.show(getFragmentManager(), "color_dialog_test");
                btnColor.setBackgroundColor(grey);
            }
        });

        btnLineSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog brushDialog = new Dialog(BookViewActivity.this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                brushDialog.setCancelable(false);
                //listen for clicks on size buttons
                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int strokeSize = getResources().getInteger(R.integer.small_size);
                        drawingView.setBrushSize(strokeSize);
                        brushDialog.dismiss();
                        btnLineSize.setBackgroundColor(transparent);
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int strokeSize = getResources().getInteger(R.integer.medium_size);
                        drawingView.setBrushSize(strokeSize);
                        brushDialog.dismiss();
                        btnLineSize.setBackgroundColor(transparent);
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        int strokeSize = getResources().getInteger(R.integer.large_size);
                        drawingView.setBrushSize(strokeSize);
                        brushDialog.dismiss();
                        btnLineSize.setBackgroundColor(transparent);
                    }
                });
                //show and wait for user interaction
                brushDialog.show();
                btnLineSize.setBackgroundColor(grey);
            }
        });
    }
    void bindTopToolbar(){
        //ImageButton btnIndex,btnBookmark,btnBookmarkList,btnEditText,btnGoto,btnZoom,btnHelp;
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpotLight();
            }
        });
        btnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isZoom){
                    String page = book.getPages()[currPage];
                    bookPage.setText((currPage+1) +"/"+book.getPages().length);
                    StringBuilder builder = new StringBuilder();
                    builder.append(bookPath).append("/pages/").append(page).append(".jpg");

                    FrameLayout frameLayout = findViewById(R.id.content);

                    GestureImageView imageView = new GestureImageView(getApplicationContext());
                    imageView.setTag("zoomView");
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));

                    imageView.setImageBitmap(Utilities.loadImage(builder.toString()));
                    frameLayout.addView(imageView);

                    slider.setVisibility(View.GONE);
                    btnZoom.setBackgroundColor(grey);
                    isZoom=true;
                }
                else {
                    FrameLayout frameLayout = findViewById(R.id.content);
                    frameLayout.removeView(frameLayout.findViewWithTag("zoomView"));

                    slider.setVisibility(View.VISIBLE);
                    btnZoom.setBackgroundColor(transparent);
                    isZoom=false;
                }
            }
        });

        btnEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isComment){
                    String page = book.getPages()[currPage];
                    StringBuilder builder = new StringBuilder();
                    builder.append(bookPath)
                            .append("/pages/")
                            .append(page)
                            .append("paint")
                            .append(".png");
                    drawingView.savePaint(builder.toString());
                    drawingView.setVisibility(View.GONE);
                    btnRemove.setBackgroundColor(transparent);

                    drawingView.setDrawType(DrawingView.ShapeFreehand);

                    btnEditText.setBackgroundColor(transparent);
                    drawingView.setOnTouchListener(null);
                    isComment =false;
                }
                else{
                    final Dialog commentDialog = new Dialog(BookViewActivity.this);
                    commentDialog.setTitle("Comment");
                    commentDialog.setContentView(R.layout.view_edittext);
                    Button btnSave = commentDialog.findViewById(R.id.btnSave);
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editComment = commentDialog.findViewById(R.id.editComment);
                            drawingView.drawText(editComment.getText().toString());
                            editComment.setText("");
                            commentDialog.dismiss();
                        }
                    });
                    drawingView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                                case MotionEvent.ACTION_UP:
                                    commentDialog.show();
                                    break;
                                default:
                                    return false;
                            }
                            return false;
                        }
                    });
                    btnRemove.setBackgroundColor(grey);
                    drawingView.setVisibility(View.VISIBLE);
                    drawingView.setDrawType(DrawingView.ShapeText);
                    btnEditText.setBackgroundColor(grey);
                    isComment =true;
                }
            }
        });

        btnBookmarkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookViewActivity.this, BookmarkList.class);
                intent.putExtra("bookId",book.getId());
                startActivityForResult(intent, 2);
            }
        });
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarks==null){
                    bookmarks=new ArrayList<Bookmark>();
                }
                Bookmark bookmark =new Bookmark();
                bookmark.setPageNo(currPage+1);
                if(isBookmarked){
                    removeIfContains(bookmarks,bookmark.getPageNo());
                    btnBookmark.setBackgroundColor(transparent);
                    isBookmarked=false;
                    Snackbar.make(slider,"Bookmark removed",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    bookmarks.add(bookmark);
                    btnBookmark.setBackgroundColor(grey);
                    isBookmarked=true;
                    Snackbar.make(slider,"Bookmark added",Snackbar.LENGTH_SHORT).show();
                }
                saveBook();
            }
        });
        btnIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookViewActivity.this, ChapterActivity.class);
                intent.putExtra("bookId",book.getId());
                startActivityForResult(intent, 1);
            }
        });
        btnGoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog gotoDialog = new Dialog(BookViewActivity.this);
                gotoDialog.setTitle("Go to");
                gotoDialog.setContentView(R.layout.view_goto);
                //listen for clicks on size buttons
                Button btnGoto = gotoDialog.findViewById(R.id.btnGoto);
                btnGoto.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        EditText edtPageNo = gotoDialog.findViewById(R.id.txtPage);
                        int pageNo =-1;
                        try {
                             pageNo = Integer.parseInt(edtPageNo.getText().toString());
                        }
                        catch (NumberFormatException ex){

                        }
                        pageNo--;
                        gotoPage(pageNo);
                        drawingView.setVisibility(View.GONE);
                        btnRemove.setBackgroundColor(transparent);
                        btnPen.setBackgroundColor(transparent);


                        Bookmark bookmark = new Bookmark();
                        bookmark.setPageNo(currPage+1);
                        if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
                            isBookmarked=true;
                            btnBookmark.setBackgroundColor(grey);
                        }
                        else{
                            isBookmarked=false;
                            btnBookmark.setBackgroundColor(transparent);
                        }
                        gotoDialog.dismiss();

                    }
                });
                gotoDialog.show();
            }
        });
    }
    void loadBookmark() {
        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<ArrayList<Bookmark>>() {}.getType();
            bookmarks = gson.fromJson(new FileReader(bookPath+"/bookmark.json"), listType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void saveBook(){
        try {
            FileWriter writer = new FileWriter(bookPath+"/bookmark.json");

            Gson objGson = new GsonBuilder().setPrettyPrinting().create();
            objGson.toJson(bookmarks,writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
             int pageNo = data.getIntExtra("page",-1);
            pageNo--;
            gotoPage(pageNo);
            drawingView.setVisibility(View.GONE);
            btnRemove.setBackgroundColor(transparent);
            btnPen.setBackgroundColor(transparent);

            Bookmark bookmark = new Bookmark();
            bookmark.setPageNo(currPage+1);
            if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
                isBookmarked=true;
                btnBookmark.setBackgroundColor(grey);
            }
            else{
                isBookmarked=false;
                btnBookmark.setBackgroundColor(transparent);
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            int pageNo = data.getIntExtra("page",-1);
            if(pageNo == -1){
                Snackbar.make(slider,"No Bookmarks",Snackbar.LENGTH_LONG).show();
            }
            pageNo--;
            gotoPage(pageNo);
            drawingView.setVisibility(View.GONE);
            btnRemove.setBackgroundColor(transparent);
            btnPen.setBackgroundColor(transparent);

            Bookmark bookmark = new Bookmark();
            bookmark.setPageNo(currPage+1);
            if(bookmarks!=null && contains(bookmarks,bookmark.getPageNo())){
                isBookmarked=true;
                btnBookmark.setBackgroundColor(grey);
            }
            else{
                isBookmarked=false;
                btnBookmark.setBackgroundColor(transparent);
            }
        }
    }
    void removeIfContains(ArrayList<Bookmark> list, int pageNo) {
        Bookmark itemtoRemove=null;
        for (Bookmark item : list) {
            if (item.getPageNo()==pageNo) {
                itemtoRemove = item;
                break;
            }
        }
        list.remove(itemtoRemove);
    }
    boolean contains(ArrayList<Bookmark> list, int pageNo) {
        Bookmark itemtoRemove=null;
        for (Bookmark item : list) {
            if (item.getPageNo()==pageNo) {
                return true;
            }
        }
        return false;
    }

    void startSpotLight(){
        SimpleTarget zoomTarget = new SimpleTarget.Builder(this)
                .setTitle("Zoom")
                .setDescription("Click here to zoom book page.")
                .setPoint(btnZoom)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget indexTarget = new SimpleTarget.Builder(this)
                .setTitle("Book Index")
                .setDescription("Click here to view book index.")
                .setPoint(btnIndex)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget bookmarkTarget = new SimpleTarget.Builder(this)
                .setTitle("Save Bookmark")
                .setDescription("Click here to save bookmark.")
                .setPoint(btnBookmark)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget bookmarkListTarget = new SimpleTarget.Builder(this)
                .setTitle("Bookmarks List")
                .setDescription("Click here to view bookmark list.")
                .setPoint(btnBookmarkList)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget commentTarget = new SimpleTarget.Builder(this)
                .setTitle("Write Comment")
                .setDescription("Click here to comment on page.")
                .setPoint(btnEditText)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget gotoTarget = new SimpleTarget.Builder(this)
                .setTitle("Go to Page")
                .setDescription("Click here and enter page no to navigate.")
                .setPoint(btnGoto)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget helpTarget = new SimpleTarget.Builder(this)
                .setTitle("View Help")
                .setDescription("Click here to view this help.")
                .setPoint(btnHelp)
                .setShape(new Circle(80f))
                .build();

        SimpleTarget colorTarget = new SimpleTarget.Builder(this)
                .setTitle("Choose Color")
                .setDescription("Click here to choose color.")
                .setPoint(btnColor)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget penSizeTarget = new SimpleTarget.Builder(this)
                .setTitle("Brush Size")
                .setDescription("Click here to choose brush size.")
                .setPoint(btnLineSize)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget penTarget = new SimpleTarget.Builder(this)
                .setTitle("Brush")
                .setDescription("Click here to select pen for drawing.")
                .setPoint(btnPen)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget highlightTarget = new SimpleTarget.Builder(this)
                .setTitle("Shape")
                .setDescription("Click here to draw rectangle shape.")
                .setPoint(btnRectangle)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget eraseTarget = new SimpleTarget.Builder(this)
                .setTitle("Eraser")
                .setDescription("Click here to clear drawing.")
                .setPoint(btnErase)
                .setShape(new Circle(80f))
                .build();
        SimpleTarget removeTarget = new SimpleTarget.Builder(this)
                .setTitle("Close Drawing")
                .setDescription("Click here to close drawing.")
                .setPoint(btnRemove)
                .setShape(new Circle(80f))
                .build();

        spotlight = Spotlight.with(this)
                .setOverlayColor(R.color.black_overlay)
                .setDuration(500L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(zoomTarget,indexTarget,bookmarkTarget,bookmarkListTarget,
                        commentTarget,gotoTarget,helpTarget,colorTarget,penSizeTarget,penTarget,
                        highlightTarget,eraseTarget,removeTarget)
                .setClosedOnTouchedOutside(true)
        .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
            @Override
            public void onStarted() {
                isSpotlight =true;
            }

            @Override
            public void onEnded() {
                isSpotlight =false;
            }
        });
        spotlight.start();

    }
}
