package com.quickzee.common.model;

public class Quiz {
    private  Long id;
    private  String title;
    private Integer semester;
    private Integer duration_minutes;
    public Quiz(Long id, String title, Integer semester, Integer duration_minutes) {
        this.id = id;
        this.title = title;
        this.semester = semester;
        this.duration_minutes = duration_minutes;
    }

    public Quiz() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getDuration_minutes() {
        return duration_minutes;
    }

    public void setDuration_minutes(Integer duration_minutes) {
        this.duration_minutes = duration_minutes;
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", semester=" + semester +
                ", duration_minutes=" + duration_minutes +
                '}';
    }
}
