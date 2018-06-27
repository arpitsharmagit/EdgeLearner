package com.anuprakashan.edgelearner.Models;

public class Book
{
    private String id;

    private String audio;

    private Pages[] pages;

    private String name;

    private String path;

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

    public Pages[] getPages ()
    {
        return pages;
    }

    public void setPages (Pages[] pages)
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

    public String getPath ()
    {
        return path;
    }

    public void setPath (String path)
    {
        this.path = path;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", audio = "+audio+", pages = "+pages+", name = "+name+", path = "+path+"]";
    }
}
