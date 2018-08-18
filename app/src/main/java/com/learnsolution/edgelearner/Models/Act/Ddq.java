package com.learnsolution.edgelearner.Models.Act;

public class Ddq {
    private String id;

    private String[] answer;

    private String question;

    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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
