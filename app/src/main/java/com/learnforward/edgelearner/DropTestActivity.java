package com.learnforward.edgelearner;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;

public class DropTestActivity extends AppCompatActivity {

    private static int currentImage = 0;
    private float x1,x2;
    ImageSwitcher imageSwitcher;
    private static final Integer[] IMAGES={R.drawable.splash,R.drawable.splash1,R.drawable.splash3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_test);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitch);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        imageSwitcher.setImageResource(IMAGES[currentImage]);
        imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
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
                            currentImage++;
                            if (currentImage== IMAGES.length) {
                                currentImage = 0;
                            }
                            imageSwitcher.setInAnimation(DropTestActivity.this,R.anim.slide_in_right);
                            imageSwitcher.setOutAnimation(DropTestActivity.this,R.anim.slide_out_left);
                            imageSwitcher.setImageResource(IMAGES[currentImage]);
                        }else if(deltaX >0){
                            currentImage--;
                            if (currentImage< 0) {
                                currentImage = IMAGES.length;
                            }
                            imageSwitcher.setInAnimation(DropTestActivity.this,R.anim.slide_in_left);
                            imageSwitcher.setOutAnimation(DropTestActivity.this,R.anim.slide_out_right);
                            imageSwitcher.setImageResource(IMAGES[currentImage]);
                        }
                        return false;
                }
                return false;
            }
        });
    }
}
