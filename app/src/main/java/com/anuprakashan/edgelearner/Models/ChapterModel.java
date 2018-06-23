package com.anuprakashan.edgelearner.Models;

public class ChapterModel {
    String chapterName;
    String chapterPath;
    public ChapterModel(String chapterName,String chapterPath){
        this.chapterName =chapterName;
        this.chapterPath= chapterPath;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getChapterPath() {
        return chapterPath;
    }
}
