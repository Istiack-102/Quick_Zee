package com.quickzee.common.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Integer semester;
    private String role;

    // No-arg constructor
    public User() {}

    // Full constructor
    public User(Long id, String name, String email, String password, Integer semester, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.semester = semester;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", semester=" + semester +
                ", role='" + role + '\'' +
                '}';
    }
}