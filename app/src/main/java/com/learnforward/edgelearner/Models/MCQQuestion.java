package com.learnforward.edgelearner.Models;

public class MCQQuestion {
    String id;
    String question;
    String [] options;
    String answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
