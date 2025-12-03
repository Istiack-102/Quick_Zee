package com.quickzee.common.model;

public class QuizResultAnswer {
    private Long id;
    private Long resultId;
    private Long questionId;
    private Long selectedOptionId;

    // No-arg constructor
    public QuizResultAnswer() {}

    // Full constructor
    public QuizResultAnswer(Long id, Long resultId, Long questionId, Long selectedOptionId) {
        this.id = id;
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    @Override
    public String toString() {
        return "QuizResultAnswer{" +
                "id=" + id +
                ", resultId=" + resultId +
                ", questionId=" + questionId +
                ", selectedOptionId=" + selectedOptionId +
                '}';
    }
}