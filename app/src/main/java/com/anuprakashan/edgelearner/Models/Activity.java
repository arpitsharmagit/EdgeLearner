package com.anuprakashan.edgelearner.Models;

public class Activity
{
    private String title;

    private String answer;

    private String question;

    private String type;

    private String[] options;

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getAnswer ()
    {
        return answer;
    }

    public void setAnswer (String answer)
    {
        this.answer = answer;
    }

    public String getQuestion ()
    {
        return question;
    }

    public void setQuestion (String question)
    {
        this.question = question;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String[] getOptions ()
    {
        return options;
    }

    public void setOptions (String[] options)
    {
        this.options = options;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [title = "+title+", answer = "+answer+", question = "+question+", type = "+type+", options = "+options+"]";
    }
}
