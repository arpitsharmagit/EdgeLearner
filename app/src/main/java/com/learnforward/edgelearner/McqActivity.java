package com.learnforward.edgelearner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.learnforward.edgelearner.Models.Book;
import com.learnforward.edgelearner.Models.MCQModel;
import com.learnforward.edgelearner.Models.MCQQuestion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Locale;

import static android.support.v4.content.res.ResourcesCompat.getFont;

public class McqActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout questionsContainer;
    MCQModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);

        //Setup Book details
        Intent intent = getIntent();
        String activityPath = intent.getStringExtra("activity");

        try {
            model =  (new Gson()).fromJson(new FileReader(activityPath),MCQModel.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
        }

        //model = mockData();
        questionsContainer=findViewById(R.id.questions_container);

        generateView();
        overrideFonts(this,questionsContainer);
    }

    void generateView(){
        int padding5 =convertToDp(5);
        int padding10 = convertToDp(10);
        int imgSize = convertToDp(20);
        int questionCounter = 1;

        for (MCQQuestion qModel:model.getMcqQuestions()) {

            //create question view
            String counter = questionCounter +". ";
            String question = counter + qModel.getQuestion();
            TextView questionView = new TextView(this);
            questionView.setPadding(padding10,0,0,0);
            questionView.setTextColor(Color.BLACK);
            questionView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            questionView.setText(makeSectionOfTextBold(question,counter));

            LinearLayout optionsContainer = new LinearLayout(this);
            optionsContainer.setPadding(imgSize,0,0,0);
            optionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

            int optionCounter = 97;
            //create options
            for (String option:qModel.getOptions()) {
                LinearLayout optionContainer = new LinearLayout(this);
                optionContainer.setPadding(padding5,padding5,padding5,padding5);
                optionsContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        imgSize,imgSize);
                params.gravity=Gravity.CENTER;
                optionButton.setLayoutParams(params);
                optionButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                optionButton.setBackgroundColor(Color.TRANSPARENT);
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
    }
    MCQModel mockData(){
        MCQQuestion question1 = new MCQQuestion();
        question1.setId("1");
        question1.setQuestion("The child wished to be");
        question1.setAnswer("an orange");
        question1.setOptions(new String[]{"an orange","an apple","a tree"});

        MCQQuestion question2 = new MCQQuestion();
        question2.setId("2");
        question2.setQuestion("The child wished to be grow on");
        question2.setAnswer("a tree");
        question2.setOptions(new String[]{"a tree","a brush","a vine"});

        MCQModel model =new MCQModel();
        model.setMcqQuestions(new MCQQuestion[] {question1,question2});
        return model;
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
            String questionId = data[0];
            String selectedOption = data[1];
            for (MCQQuestion question:model.getMcqQuestions()) {
                if(question.getId().equals(questionId)){
                    if(question.getAnswer().equals(selectedOption)) {
                        //right ans
                        optionClicked.setImageResource(R.drawable.mcq_right);
                    }
                    else{
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
}

