package com.ninjas.edgelearner.utils;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class ApplicationHelper extends Application {
    public static Context mCtx;
    public static File booksFolder;
    public static File zipFolder;

    @Override
    public void onCreate(){
        super.onCreate();
        mCtx = getApplicationContext();
        zipFolder = getApplicationContext().getExternalFilesDir("zips");
        booksFolder = getApplicationContext().getExternalFilesDir("books");
    }
}
