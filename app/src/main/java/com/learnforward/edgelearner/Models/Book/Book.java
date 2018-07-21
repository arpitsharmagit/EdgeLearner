package com.learnforward.edgelearner.Models.Book;

public class Book
{
    private String id;

    private String audio;

    private String[] pages;

    private String name;

    private String activityImg;

    private String soundImg;

    private Chapters[] chapters;

    private int [] bookmarks;

    public int[] getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(int[] bookmarks) {
        this.bookmarks = bookmarks;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAudio ()
    {
        return audio;
    }

    public void setAudio (String audio)
    {
        this.audio = audio;
    }

    public String[] getPages ()
    {
        return pages;
    }

    public void setPages (String[] pages)
    {
        this.pages = pages;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public void setActivityImg(String activityImg){
        this.activityImg = activityImg;
    }
    public String getActivityImg(){
        return this.activityImg;
    }
    public void setSoundImg(String soundImg){
        this.soundImg = soundImg;
    }
    public String getSoundImg(){
        return this.soundImg;
    }
    public void setChapters(Chapters[] chapters){
        this.chapters = chapters;
    }
    public Chapters[] getChapters(){
        return this.chapters;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", audio = "+audio+", pages = "+pages+", name = "+name+"]";
    }
}