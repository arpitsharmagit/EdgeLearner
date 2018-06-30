package com.learnforward.edgelearner.Models;

public class Book
{
    private String id;

    private String audio;

    private String[] pages;

    private String name;

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

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", audio = "+audio+", pages = "+pages+", name = "+name+"]";
    }
}
