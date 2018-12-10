package com.learnforward.edgelearner.downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.learnforward.edgelearner.Models.Book.Book;
import com.learnforward.edgelearner.Models.Book.Bookmark;
import com.learnforward.edgelearner.Models.BookModel;
import com.learnforward.edgelearner.utils.ApplicationHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DownloadItemHelper {

    public static ArrayList<DownloadableItem> loadDownloadItems(SharedPreferences mPrefs) {
        Gson gson = new Gson();
        String json = mPrefs.getString("downloadItems", "");

        Type listType = new TypeToken<ArrayList<DownloadableItem>>() {}.getType();
        ArrayList<DownloadableItem> downloadItems = gson.fromJson(json, listType);
        if(downloadItems==null){
            downloadItems = new ArrayList<DownloadableItem>();
        }
        return downloadItems;
    }

    public static void saveDownloadItems(SharedPreferences mPrefs, ArrayList<DownloadableItem> downloadItems){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(downloadItems); // myObject - instance of MyObject
        prefsEditor.putString("downloadItems", json);
        prefsEditor.commit();
    }
}
