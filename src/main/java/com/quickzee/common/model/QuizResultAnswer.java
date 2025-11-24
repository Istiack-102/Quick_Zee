package com.quickzee.common.model;

public class QuizResultAnswer {
    private Long id;
    private Long result_id;
    private Long question_id;
    private Long selected_option_id;

    public QuizResultAnswer(Long id, Long result_id, Long question_id, Long selected_option_id) {
        this.id = id;
        this.result_id = result_id;
        this.question_id = question_id;
        this.selected_option_id = selected_option_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResult_id() {
        return result_id;
    }

    public void setResult_id(Long result_id) {
        this.result_id = result_id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public Long getSelected_option_id() {
        return selected_option_id;
    }

    public void setSelected_option_id(Long selected_option_id) {
        this.selected_option_id = selected_option_id;
    }

    @Override
    public String toString() {
        return "QuizResultAnswer{" +
                "id=" + id +
                ", result_id=" + result_id +
                ", question_id=" + question_id +
                ", selected_option_id=" + selected_option_id +
                '}';
    }
}

