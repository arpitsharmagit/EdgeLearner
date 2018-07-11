package com.learnforward.edgelearner.Models.Book;

public class Chapters
{
    private int id;

    private String name;

    private int page;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPage(int page){
        this.page = page;
    }
    public int getPage(){
        return this.page;
    }
}
