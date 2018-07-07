package com.learnforward.edgelearner.Models;

public class Audio {
    private String correct;

    private String incorrect;

    private String clapping;

    public String getCorrect ()
    {
        return correct;
    }

    public void setCorrect (String correct)
    {
        this.correct = correct;
    }

    public String getIncorrect ()
    {
        return incorrect;
    }

    public void setIncorrect (String incorrect)
    {
        this.incorrect = incorrect;
    }

    public String getClapping ()
    {
        return clapping;
    }

    public void setClapping (String clapping)
    {
        this.clapping = clapping;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [correct = "+correct+", incorrect = "+incorrect+", clapping = "+clapping+"]";
    }
}
