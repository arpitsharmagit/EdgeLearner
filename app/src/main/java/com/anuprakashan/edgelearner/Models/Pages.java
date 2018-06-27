package com.anuprakashan.edgelearner.Models;

public class Pages
{
    private String id;

    private String audio;

    private String path;

    private Activity[] activity;

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

    public String getPath ()
    {
        return path;
    }

    public void setPath (String path)
    {
        this.path = path;
    }

    public Activity[] getActivity ()
    {
        return activity;
    }

    public void setActivity (Activity[] activity)
    {
        this.activity = activity;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", audio = "+audio+", path = "+path+", activity = "+activity+"]";
    }
}

