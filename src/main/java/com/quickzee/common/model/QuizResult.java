package com.quickzee.common.model;

import java.time.LocalDateTime;

public class QuizResult {
    private Long id;
    private Long userId;
    private Long quizId;
    private Integer score;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;

    // No-arg constructor
    public QuizResult() {}

    // Full constructor
    public QuizResult(Long id, Long userId, Long quizId, Integer score,
                      Integer totalQuestions, LocalDateTime submittedAt) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", userId=" + userId +
                ", quizId=" + quizId +
                ", score=" + score +
                ", totalQuestions=" + totalQuestions +
                ", submittedAt=" + submittedAt +
                '}';
    }
}