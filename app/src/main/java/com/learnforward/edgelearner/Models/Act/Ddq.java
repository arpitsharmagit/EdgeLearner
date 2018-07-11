package com.learnforward.edgelearner.Models.Act;

public class Ddq {
    private String id;

    private String[] answer;

    private String question;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String[] getAnswer ()
    {
        return answer;
    }

    public void setAnswer (String[] answer)
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

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", answer = "+answer+", question = "+question+"]";
    }
}
