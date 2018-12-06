package com.learnsolution.edgelearner;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.learnsolution.edgelearner.Models.Act.Audio;
import com.learnsolution.edgelearner.Models.Act.Ddq;
import com.learnsolution.edgelearner.Models.Act.Mcq;
import com.learnsolution.edgelearner.Models.Act.Questions;
import com.learnsolution.edgelearner.Models.Act.QuestionsModel;
import com.learnsolution.edgelearner.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static android.support.v4.content.res.ResourcesCompat.getFont;

public class McqActivity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener {


    QuestionsModel model;
    Questions [] questions;
    int currentQuestion=0, lastQuestion=0;
    Audio audios;
    ImageView background;
    ImageButton prev,next;
    Button dragbtn;
    RelativeLayout container;
    String dataPath,activityFolder;
    MediaPlayer mp;
    TextView drop;
    int blankSize;
    Paint dropperPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);

        //Setup Book details
        Intent intent = getIntent();
        String activityPath = intent.getStringExtra("activity");
        File activity =new File(activityPath);
        activityFolder =  activityPath.replace(".json","/");
        dataPath = activity.getParent().replace("activities","extra");

        container = findViewById(R.id.container);
        background = findViewById(R.id.background);

        prev = findViewById(R.id.btnPrev);

        next = findViewById(R.id.btnNext);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });


        try {
            model =  (new Gson()).fromJson(new FileReader(activityPath),QuestionsModel.class);
            audios = model.getAudio();
            questions = model.getQuestions();
            lastQuestion = questions.length-1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        }

        initialize();

    }
    void setPrevNextButtons(){
        if(currentQuestion == 0){
            prev.setVisibility(View.GONE);
        }
        else{
            prev.setVisibility(View.VISIBLE);
        }
        if(currentQuestion == lastQuestion){
            next.setVisibility(View.GONE);
        }
        else {
            next.setVisibility(View.VISIBLE);
        }
    }
    void initialize(){
        setPrevNextButtons();
        Questions question = questions[currentQuestion];

        File bg=new File(dataPath,question.getBackground());
        background.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        File mainAudio = new File(dataPath,question.getAudio());
        if(mainAudio.exists()) {
            playSound(question.getAudio());
        }

        if(question.getType().equals("mcq")){
            loadMcqView(question);
        }
        if(question.getType().equals("ddq")){
            loadDdqView(question);
        }
        if(question.getType().equals("ddqImg")){
            loadDdqViewImg(question);
        }
    }
    void goPrev(){
        currentQuestion--;
        setPrevNextButtons();
        Questions question = questions[currentQuestion];
        File bg=new File(dataPath,question.getBackground());
        background.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        File mainAudio = new File(dataPath,question.getAudio());
        if(mainAudio.exists()) {
            playSound(question.getAudio());
        }

        if(question.getType().equals("mcq")){
            loadMcqView(question);
        }
        if(question.getType().equals("ddq")){
            loadDdqView(question);
        }
        if(question.getType().equals("ddqImg")){
            loadDdqViewImg(question);
        }
    }
    void goNext(){
        currentQuestion++;
        setPrevNextButtons();
        Questions question = questions[currentQuestion];

        File bg=new File(dataPath,question.getBackground());
        background.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        File mainAudio = new File(dataPath,question.getAudio());
        if(mainAudio.exists()) {
            playSound(question.getAudio());
        }

        if(question.getType().equals("mcq")){
            loadMcqView(question);
        }
        if(question.getType().equals("ddq")){
            loadDdqView(question);
        }
        if(question.getType().equals("ddqImg")){
            loadDdqViewImg(question);
        }
    }

    void loadMcqView(Questions question){
        container.removeAllViews();
        View child = getLayoutInflater().inflate(R.layout.mcq_layout, null);
        container.addView(child);
        LinearLayout questionsContainer=child.findViewById(R.id.questions_container);

        ImageView topbanner =findViewById(R.id.top_banner);
        File bg=new File(dataPath,question.getTitleImage());
        topbanner.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        int padding2 =convertToDp(2);
        int padding5 =convertToDp(5);
        int padding10 = convertToDp(10);
        int imgSize = convertToDp(20);
        int questionCounter = 1;

        for (Mcq qModel:question.getMcq()) {

            //create question view
            String counter = questionCounter +". ";
            String ques = counter + qModel.getQuestion();
            TextView questionView = new TextView(this);
//            questionView.setPadding(padding10,0,0,0);
            questionView.setTextColor(Color.BLACK);
            questionView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            questionView.setText(makeSectionOfTextBold(ques,counter));

            TextView questionTitle =child.findViewById(R.id.questiontitle);
            questionTitle.setText(question.getTitle());

            FlexboxLayout optionsContainer = new FlexboxLayout(this);
            optionsContainer.setPadding(imgSize,0,0,0);
            optionsContainer.setAlignContent(AlignContent.FLEX_START);
            optionsContainer.setFlexWrap(FlexWrap.WRAP);
            optionsContainer.setJustifyContent(AlignContent.FLEX_START);
            optionsContainer.setAlignItems(AlignContent.FLEX_START);
            optionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

//            LinearLayout optionsContainer = new LinearLayout(this);
//            optionsContainer.setPadding(imgSize,0,0,0);
//            optionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//            optionsContainer.setTag(qModel.getBookId());

            int optionCounter = 97;
            //create options
            for (String option:qModel.getOptions()) {
                LinearLayout optionContainer = new LinearLayout(this);
                optionContainer.setPadding(padding5,padding5,padding5,padding5);
//                optionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

                String optCounterStr = (char)optionCounter +".  ";
                String optionStr = optCounterStr + option;
                TextView optionTextView = new TextView(this);
                optionTextView.setPadding(0,0,imgSize,0);
                optionTextView.setTextColor(Color.BLACK);
                optionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                optionTextView.setText(makeSectionOfTextBold(optionStr,optCounterStr));
                optionContainer.addView(optionTextView);

                ImageButton optionButton = new ImageButton(this);
//                optionButton.setBookId();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imgSize,imgSize);
                params.gravity=Gravity.CENTER;
                optionButton.setPadding(padding2,padding2,padding2,padding2);
                optionButton.setLayoutParams(params);
                optionButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                optionButton.setBackgroundColor(Color.WHITE);
                optionButton.setImageResource(R.drawable.mcq_blank);
                optionButton.setTag(qModel.getId()+"@"+option);
                optionButton.setOnClickListener(this);

                optionContainer.addView(optionButton);
                optionsContainer.addView(optionContainer);
                optionCounter++;
            }

            LinearLayout mainContainer = new LinearLayout(this);
            mainContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            mainContainer.setOrientation(LinearLayout.VERTICAL);
            mainContainer.addView(questionView);
            mainContainer.addView(optionsContainer);
            questionsContainer.addView(mainContainer);
            questionCounter++;
        }
//        overrideFonts(this,questionsContainer);
    }
    void loadDdqView(Questions question){
        container.removeAllViews();
        View child = getLayoutInflater().inflate(R.layout.ddq_layout, null);
        container.addView(child);

        ImageView topbanner =findViewById(R.id.top_banner);
        File bg=new File(dataPath,question.getTitleImage());
        topbanner.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        LinearLayout questionsContainer=child.findViewById(R.id.ddq_container);
        questionsContainer.setVisibility(View.VISIBLE);

        FlexboxLayout helpboxLayout =  child.findViewById(R.id.helpbox);
        TextView questionTitle =child.findViewById(R.id.questiontitle);
        questionTitle.setText(question.getTitle());

        ImageView helpboxBg = child.findViewById(R.id.helpboxBg);
        helpboxBg.setScaleType(ImageView.ScaleType.FIT_XY);
        helpboxBg.setImageBitmap(Utilities.loadImage(dataPath+"/"+question.getHelpboxImg()));

        int padding2 =convertToDp(2);
        int questionCounter = 1;
        for (Ddq qModel:question.getDdq()) {

            String counter = questionCounter +". ";
            String ques = counter + qModel.getQuestion();
            String [] questionParts = ques.split("@#",4);

            FlexboxLayout questionContainer = new FlexboxLayout(this);
            questionContainer.setAlignContent(AlignContent.FLEX_START);
            questionContainer.setFlexWrap(FlexWrap.WRAP);
            questionContainer.setJustifyContent(AlignContent.FLEX_START);
            questionContainer.setAlignItems(AlignContent.FLEX_START);
            questionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
//            LinearLayout questionContainer = new LinearLayout(this);
//            questionContainer.setOrientation(LinearLayout.HORIZONTAL);
//            questionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
//            ));
            ViewGroup.LayoutParams  questionViewParams= new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            for(int i=0;i<questionParts.length;i++){
                TextView questionView = new TextView(this);
                questionView.setTextColor(Color.BLACK);
                questionView.setLayoutParams(questionViewParams);
                questionView.setText(makeSectionOfTextBold(questionParts[i],counter));
                questionContainer.addView(questionView);

                if(i < questionParts.length-1) {
                    TextView questionDrop = new TextView(this);
                    questionDrop.setTextColor(Color.BLACK);
                    questionDrop.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    questionDrop.setTag(qModel.getAnswer()[i]);
                    questionDrop.setOnDragListener(this);


                    dropperPaint = questionDrop.getPaint();

                    int textSize = getTextWidth(qModel.getAnswer()[i],dropperPaint);
                    int dotSize = getTextWidth(".",dropperPaint);
                    int noOfDots = textSize/dotSize;
                    int sideSize = 2;
                    int dotCount = (2*sideSize )+ noOfDots;

                    StringBuilder builder = new StringBuilder();
                    for(int j=0;j<dotCount;j++){
                        builder.append(".");
                    }

                    questionDrop.setText(builder.toString());
                    questionContainer.addView(questionDrop);
                }
            }
            questionsContainer.addView(questionContainer);
            questionCounter++;
        }

        for(String ans:question.getHelpbox()){
            int margin3=convertToDp(3);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    convertToDp(25));
            params.setMargins(5,5,5,5);
            AppCompatButton btn = new AppCompatButton(this);
            btn.setPadding(0,0,0,0);
            btn.setLayoutParams(params);
            btn.setMinHeight(0);
            btn.setMinWidth(0);
            btn.setText(ans);
            btn.setTextSize(12);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundResource(R.drawable.round_rect);
            btn.setTag(ans);
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ClipData data = ClipData.newPlainText("label",v.getTag().toString());
                    if (android.os.Build.VERSION.SDK_INT >= 24) {
                        v.startDragAndDrop(data,new View.DragShadowBuilder(v),null,0);
                    }else {
                        v.startDrag(data,new View.DragShadowBuilder(v),null,0);
                    }
                    return true;
                }
            });
            helpboxLayout.addView(btn);
        }
    }
    void loadDdqViewImg(Questions question){
        container.removeAllViews();
        View child = getLayoutInflater().inflate(R.layout.ddq_layout, null);
        container.addView(child);

        ImageView topbanner =findViewById(R.id.top_banner);
        File bg=new File(dataPath,question.getTitleImage());
        topbanner.setImageBitmap(Utilities.loadImage(bg.getAbsolutePath()));

        FlexboxLayout questionsContainer=child.findViewById(R.id.ddqImg_container);
        questionsContainer.setVisibility(View.VISIBLE);

        FlexboxLayout helpboxLayout =  child.findViewById(R.id.helpbox);
        TextView questionTitle =child.findViewById(R.id.questiontitle);
        questionTitle.setText(question.getTitle());

        ImageView helpboxBg = child.findViewById(R.id.helpboxBg);
        helpboxBg.setScaleType(ImageView.ScaleType.FIT_XY);
        helpboxBg.setImageBitmap(Utilities.loadImage(dataPath+"/"+question.getHelpboxImg()));

        int padding2 =convertToDp(2);
        int questionCounter = 1;
        for (Ddq qModel:question.getDdq()) {

            String counter = questionCounter +". ";
            String ques = counter + qModel.getQuestion();

            LinearLayout questionContainer = new LinearLayout(this);
            questionContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            int padding8 =convertToDp(8);
            questionContainer.setPadding(padding8,padding8,padding8,padding8);
            questionContainer.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    convertToDp(150),
                    convertToDp(100));
            imageParams.gravity = Gravity.CENTER;

            ImageView imageView =new ImageView(this);
            imageView.setLayoutParams(imageParams);

            StringBuilder builder = new StringBuilder();
            builder.append(activityFolder).append(qModel.getImage());

            imageView.setImageBitmap(Utilities.loadImage(builder.toString()));// Todo
            questionContainer.addView(imageView);

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.gravity=Gravity.CENTER;

            TextView questionView = new TextView(this);
            questionView.setTextColor(Color.BLACK);
            questionView.setLayoutParams(textParams);
            questionView.setText(makeSectionOfTextBold(ques,counter));
            questionContainer.addView(questionView);

            TextView questionDrop = new TextView(this);
            questionDrop.setTextColor(Color.BLACK);
            questionDrop.setLayoutParams(textParams);
            questionDrop.setTag(qModel.getAnswer()[0]);
            questionDrop.setOnDragListener(this);
            dropperPaint = questionDrop.getPaint();

            int textSize = getTextWidth(qModel.getAnswer()[0],dropperPaint);
            int dotSize = getTextWidth(".",dropperPaint);
            int noOfDots = textSize/dotSize;
            int sideSize = 2;
            int dotCount = (2*sideSize )+ noOfDots;

            StringBuilder builder2 = new StringBuilder();
            for(int j=0;j<dotCount;j++){
                builder2.append(".");
            }

            questionDrop.setText(builder2.toString());
            questionContainer.addView(questionDrop);

            questionsContainer.addView(questionContainer);
            questionCounter++;
        }

        for(String ans:question.getHelpbox()){
            int margin3=convertToDp(3);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    convertToDp(25));
            params.setMargins(5,5,5,5);
            AppCompatButton btn = new AppCompatButton(this);
            btn.setPadding(0,0,0,0);
            btn.setLayoutParams(params);
            btn.setMinHeight(0);
            btn.setMinWidth(0);
            btn.setText(ans);
            btn.setTextSize(12);
            btn.setTextColor(Color.BLACK);
            btn.setBackgroundResource(R.drawable.round_rect);
            btn.setTag(ans);
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ClipData data = ClipData.newPlainText("label",v.getTag().toString());
                    if (android.os.Build.VERSION.SDK_INT >= 24) {
                        v.startDragAndDrop(data,new View.DragShadowBuilder(v),null,0);
                    }else {
                        v.startDrag(data,new View.DragShadowBuilder(v),null,0);
                    }
                    return true;
                }
            });
            helpboxLayout.addView(btn);
        }
    }
    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.left + bounds.width();
        return width;
    }

     SpannableStringBuilder makeSectionOfTextBold(String text, String... textToBold) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        for (String textItem :
                textToBold) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                String testText = text.toLowerCase(Locale.US);
                String testTextToBold = textItem.toLowerCase(Locale.US);
                int startingIndex = testText.indexOf(testTextToBold);
                int endingIndex = startingIndex + testTextToBold.length();

                if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                }
            }
        }

        return builder;
    }

    @Override
    public void onClick(View v) {

        //ontion click
        if(v instanceof ImageButton){
            ImageButton optionClicked = (ImageButton)v;
            String [] data = String.valueOf(optionClicked.getTag()).split("@",2);
            String mcqId = data[0];
            String selectedOption = data[1];
            Questions question = questions[currentQuestion];
            for (Mcq mcq:question.getMcq()) {
                if(mcq.getId().equals(mcqId)){
                    if(mcq.getAnswer().equals(selectedOption)) {
                        //right ans
                        playSound(audios.getCorrect());
                        optionClicked.setImageResource(R.drawable.mcq_right);
                        playSound(audios.getClapping());

                        //disable all other
                        FlexboxLayout optionsContainer = (FlexboxLayout)v.getParent().getParent();
                        for (int i=0;i<optionsContainer.getChildCount();i++){
                            LinearLayout  optionContainer =(LinearLayout) optionsContainer.getChildAt(i);
                            ImageButton optionButton = (ImageButton) optionContainer.getChildAt(1);
                            optionButton.setOnClickListener(null);
                        }

                    }
                    else{
                        playSound(audios.getIncorrect());
                        //wrong ans
                        optionClicked.setImageResource(R.drawable.mcq_wrong);
                    }
                    break;
                }
            }
        }
    }

    int convertToDp(float dpSize){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpSize, getResources().getDisplayMetrics());
    }
    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView ) {

                ((TextView) v).setTypeface(Typeface.SERIF);
            }
        } catch (Exception e) {
        }
    }

    void playSound(String audio){
        Uri uri = Uri.fromFile(new File(dataPath,audio));
//        if(mp!=null){
//            mp.release();
//            mp = null;
//        }
        MediaPlayer mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(getApplicationContext(), uri);
            mp.prepare();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                }
            });
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch(event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundColor(Color.LTGRAY);
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundColor(Color.TRANSPARENT);
                return true;

            case DragEvent.ACTION_DROP:
                v.setBackgroundColor(Color.TRANSPARENT);
                String dragVal = event.getClipData().getItemAt(0).getText().toString();
                TextView textBox = (TextView) v;
                String answer= v.getTag().toString();
                if(answer.equals(dragVal)){


                    StringBuilder builder = new StringBuilder();
                    builder.append("..");
                    builder.append(dragVal);
                    builder.append("..");

                    TextView textView = (TextView) v;
                    textView.setTextColor(Color.argb(255,0,128,0));
                    playSound(audios.getCorrect());
                    playSound(audios.getClapping());
                    textView.setText(builder.toString());
                    textView.setOnDragListener(null);
                }
                else{
                    playSound(audios.getIncorrect());
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                return true;

            default:
                break;
        }
        return false;
    }
}

