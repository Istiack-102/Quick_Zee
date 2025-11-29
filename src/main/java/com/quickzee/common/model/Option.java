package com.quickzee.common.model;

public class Option {
    private Long id;
    private Long question_id;
    private Integer ordinal;
    private String text;
    private Integer is_correct;  // 0 or 1

    // No-arg constructor ← ADDED
    public Option() {}

    // Full constructor
    public Option(Long id, Long question_id, Integer ordinal, String text, Integer is_correct) {
        this.id = id;
        this.question_id = question_id;
        this.ordinal = ordinal;
        this.text = text;
        this.is_correct = is_correct;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
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

    public Integer getIs_correct() {
        return is_correct;
    }

    // FIXED: Added parameter ← BUG FIX
    public void setIs_correct(Integer is_correct) {
        this.is_correct = is_correct;
    }

    // Convenience methods for boolean conversion
    public boolean isCorrect() {
        return is_correct != null && is_correct == 1;
    }

    public void setCorrect(boolean correct) {
        this.is_correct = correct ? 1 : 0;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", question_id=" + question_id +
                ", ordinal=" + ordinal +
                ", text='" + text + '\'' +
                ", is_correct=" + is_correct +
                '}';
    }
}