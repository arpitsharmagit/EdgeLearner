package com.learnsolution.edgelearner.Models.Act;

public class QuestionsModel {
    private Audio audio;

    private Questions[] Questions;

    public Audio getAudio ()
    {
        return audio;
    }

    public void setAudio (Audio audio)
    {
        this.audio = audio;
    }

    public Questions[] getQuestions ()
    {
        return Questions;
    }

    public void setQuestions (Questions[] Questions)
    {
        this.Questions = Questions;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [audio = "+audio+", Questions = "+Questions+"]";
    }
}
