package com.quickzee.common.model;

import java.util.List;

public class Question {
    private Long id;
    private Long quiz_id;
    private Integer ordinal;
    private String text;
    private List<Option> options;

    // No-arg constructor
    public Question() {}

    // Constructor without options
    public Question(Long id, Long quiz_id, Integer ordinal, String text) {
        this.id = id;
        this.quiz_id = quiz_id;
        this.ordinal = ordinal;
        this.text = text;
    }

    // Full constructor
    public Question(Long id, Long quiz_id, Integer ordinal, String text, List<Option> options) {
        this.id = id;
        this.quiz_id = quiz_id;
        this.ordinal = ordinal;
        this.text = text;
        this.options = options;
    }

    // Getters and Setters
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

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", quiz_id=" + quiz_id +
                ", ordinal=" + ordinal +
                ", text='" + text + '\'' +
                ", options=" + (options != null ? options.size() + " options" : "not loaded") +
                '}';
    }
}