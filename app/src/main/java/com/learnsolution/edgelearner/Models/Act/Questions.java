package com.learnsolution.edgelearner.Models.Act;

public class Questions {

    private Ddq[] ddq;

    private Mcq[] mcq;

    private String title;
    private String titleImage;
    private String helpboxImg;

    private String audio;

    private String background;

    private String type;

    private String[] helpbox;

    public String getHelpboxImg() {
        return helpboxImg;
    }

    public void setHelpboxImg(String helpboxImg) {
        this.helpboxImg = helpboxImg;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public Ddq[] getDdq ()
    {
        return ddq;
    }

    public void setDdq (Ddq[] ddq)
    {
        this.ddq = ddq;
    }

    public Mcq[] getMcq ()
    {
        return mcq;
    }

    public void setMcq (Mcq[] mcq)
    {
        this.mcq = mcq;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getAudio ()
    {
        return audio;
    }

    public void setAudio (String audio)
    {
        this.audio = audio;
    }

    public String getBackground ()
    {
        return background;
    }

    public void setBackground (String background)
    {
        this.background = background;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String[] getHelpbox()
    {
        return helpbox;
    }

    public void setHelpbox(String[] helpbox)
    {
        this.helpbox = helpbox;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [mcq = "+mcq+", title = "+title+", audio = "+audio+", background = "+background+", type = "+type+"]";
    }
}
