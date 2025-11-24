package com.quickzee.common.model;

public class User {
    private Long  id;
    private String name;
    private  String email;
    private String password;
    private Integer semester;
    private String role ;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public User (){

    }

    public User(Long id, String name, String email, String passWord, Integer semester, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.semester = semester;
        this.role= role;
    }



    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String passWord) {
        this.password = passWord;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", passWord='" + password + '\'' +
                ", semester=" + semester +
                '}';
    }
}


//id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- not null+ unique
//name VARCHAR(255) NOT NULL,
//email VARCHAR(255) NOT NULL UNIQUE,
//password VARCHAR(255) NOT NULL,        -- TODO: hash passwords in production
//semester INT,