package com.quickzee.common.model;

public class QuizResultAnswer {
    private Long id;
    private Long resultId;  // ← FIXED: Changed from result_id
    private Long questionId;  // ← FIXED: Changed from question_id
    private Long selectedOptionId;  // ← FIXED: Changed from selected_option_id

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