package com.learnsolution.edgelearner.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.learnsolution.edgelearner.R;
import com.learnsolution.edgelearner.utils.Utilities;

public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000, paintAlpha = 255;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap,defaultBitmap;
    //brush sizes
    private float brushSize, lastBrushSize;
    //erase flag
    private boolean erase=false;

    private int width,height;

    private String bitmapToDraw ="";

    private int drawType;

    public static final int ShapeLine = 1;
    public static final int ShapeRect = 2;
    public static final int ShapeCircle = 3;
    public static final int ShapeOval = 4;
    public static final int ShapeFreehand =5;
    public static final int ShapeText =6;

    float left=0,top=0,right=0,bottom=0;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    //setup drawing
    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setTextSize(20);
        drawType = ShapeFreehand;
//        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        defaultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);;

        if(!bitmapToDraw.isEmpty()){
            Bitmap bitmap = Utilities.loadImage(bitmapToDraw);
            if(bitmap !=null){
                canvasBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h)
                        .copy(Bitmap.Config.ARGB_8888, true);
            }
        }

        drawCanvas = new Canvas(canvasBitmap);
    }

    public void setDrawType(int drawType){
        this.drawType = drawType;
    }

    public int getDrawType() {
        return drawType;
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(drawType == ShapeFreehand){
                    drawPath.moveTo(touchX, touchY);
                    drawPaint.setAlpha(255);
                    drawPaint.setStyle(Paint.Style.STROKE);
                }
                if(drawType == ShapeRect){
                    left = right =touchX;
                    top = bottom = touchY;
                    drawPaint.setAlpha(128);
                    drawPaint.setStyle(Paint.Style.FILL);
                }
                if(drawType == ShapeText){
                    left = right =touchX;
                    top = bottom = touchY;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(drawType == ShapeFreehand){
                    drawPath.lineTo(touchX, touchY);
                }
                if(drawType == ShapeRect){
                    right = touchX;
                    bottom = touchY;
                    drawPath.addRect(left,top,right,bottom,Path.Direction.CCW);
                }
                if(drawType == ShapeText){

                }
                break;
            case MotionEvent.ACTION_UP:
                if(drawType == ShapeFreehand){
                    drawPath.lineTo(touchX, touchY);
                }
                if(drawType == ShapeRect){
                    right = touchX;
                    bottom = touchY;
                    drawPath.addRect(left,top,right,bottom,Path.Direction.CCW);
                }
                if(drawType == ShapeText){

                }
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;

    }

    public void drawText(String gText){
        float scale = getResources().getDisplayMetrics().density;

        Paint mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.transparentBlack));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(paintColor);
        paint.setTextSize((int) (16 * scale));
        paint.setShadowLayer(1f, 0f, 1f, paintColor);

        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);

        int noOfLines = gText.split("\n").length;
        float lineHeight =bounds.height();

        float right = 0;
        for (String line: gText.split("\n")) {
            paint.getTextBounds(line, 0, line.length(), bounds);
            if(right<bounds.width()){
                right = bounds.width();
            }
        }
        right = left+10 + right;
        float bottom = top + (lineHeight* (noOfLines+1));
        drawCanvas.drawRect(left, top,right, bottom, mPaint);

        float x = left+10;
        float y = (top + lineHeight);

        for (String line: gText.split("\n")) {
            drawCanvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }
        invalidate();
    }

    public void setBitmap(String path){
        bitmapToDraw = path;

        Bitmap bitmap = Utilities.loadImage(bitmapToDraw);
        if(bitmap !=null){
            canvasBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight())
                    .copy(Bitmap.Config.ARGB_8888, true);
            drawCanvas = new Canvas(canvasBitmap);
        }
        else{
            if(defaultBitmap!=null) {
                canvasBitmap = defaultBitmap;
                drawCanvas = new Canvas(canvasBitmap);
                clear();
                drawPath.reset();
                invalidate();
            }
        }
    }
    //update color
    public void setColor(int paintColor){
        this.paintColor = paintColor;
        drawPaint.setColor(paintColor);
        invalidate();
    }

    //set brush size
    public void setBrushSize(float newSize){
        brushSize=newSize;
        drawPaint.setStrokeWidth(brushSize);
    }

    //start new drawing
    public void clear(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //return current alpha
    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

    public void savePaint(String path){
        if(canvasBitmap!=null) {
            Utilities.savePaint(canvasBitmap, path);
        }
    }

    //set alpha
    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100*255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    private @NonNull Rect getTextBackgroundSize(float x, float y, @NonNull String text, @NonNull TextPaint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float halfTextLength = paint.measureText(text) / 2 + 5;
        return new Rect((int) (x - halfTextLength), (int) (y + fontMetrics.top), (int) (x + halfTextLength), (int) (y + fontMetrics.bottom));
    }
}