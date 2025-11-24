package com.quickzee.common.model;

import java.time.LocalDateTime;

public class QuizResult {
    private Long id;
    private Long user_id;
    private Long quiz_id;
    private Integer score;
    private Integer total_questions;
    private LocalDateTime setSubmittedAt;
    public QuizResult(){

    }
    public LocalDateTime getSubmittedAt() {
        return setSubmittedAt;
    }

    public void setSubmittedAt(LocalDateTime setSubmittedAt) {
        this.setSubmittedAt = setSubmittedAt;
    }

    public QuizResult(Long id, Long user_id, Long quiz_id, Integer score, Integer total_questions, LocalDateTime setSubmittedAt) {
        this.id = id;
        this.user_id = user_id;
        this.quiz_id = quiz_id;
        this.score = score;
        this.total_questions = total_questions;
        this.setSubmittedAt= setSubmittedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public Long getQuiz_id() {
        return quiz_id;
    }

    public void setQuizId(Long quiz_id) {
        this.quiz_id = quiz_id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotal_questions() {
        return total_questions;
    }

    public void setTotalQuestions(Integer total_questions) {
        this.total_questions = total_questions;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", quiz_id=" + quiz_id +
                ", score=" + score +
                ", total_questions=" + total_questions +
                '}';
    }
}

