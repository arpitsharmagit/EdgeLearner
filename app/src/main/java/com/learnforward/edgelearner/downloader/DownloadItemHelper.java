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

public class DownloadItemHelper {

    public static ArrayList<DownloadableItem> loadDownloadItems() {
        String downloadStatusFile = ApplicationHelper.booksFolder + "/download.json";
        Gson gson = new Gson();
        try {
            Type listType = new TypeToken<ArrayList<DownloadableItem>>() {}.getType();
            ArrayList<DownloadableItem> downloadItems = gson.fromJson(new FileReader(downloadStatusFile), listType);
            if(downloadItems==null){
                return new ArrayList<DownloadableItem>();
            }
            return  downloadItems;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<DownloadableItem>();
        }
    }

    public static void saveDownloadItems(ArrayList<DownloadableItem> downloadItems){
        try {
            String downloadStatusFile = ApplicationHelper.booksFolder + "/download.json";
            FileWriter writer = new FileWriter(downloadStatusFile);

            Gson objGson = new GsonBuilder().setPrettyPrinting().create();
            objGson.toJson(downloadItems,writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
