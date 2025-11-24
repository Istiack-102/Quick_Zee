package com.quickzee.common.model;

import javafx.scene.text.Text;

public class Question {
    private  Long id;

    public Question() {
    }

    private  Long quiz_id;
    private Integer ordinal;
    private String text;

    public Question(Long id, Long quiz_id, Integer ordinal, String text) {
        this.id = id;
        this.quiz_id = quiz_id;
        this.ordinal = ordinal;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Long quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", quiz_id=" + quiz_id +
                ", ordinal=" + ordinal +
                ", text=" + text +
                '}';
    }
}
