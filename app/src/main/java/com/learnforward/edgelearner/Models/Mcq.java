package com.learnforward.edgelearner.Models;

public class Mcq {
    private String id;

    private String answer;

    private String question;

    private String[] options;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
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
        return "ClassPojo [id = "+id+", answer = "+answer+", question = "+question+", options = "+options+"]";
    }
}
