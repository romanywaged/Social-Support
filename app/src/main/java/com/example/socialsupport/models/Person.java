package com.example.socialsupport.models;

public class Person {
private String user_name;
private String id;
private String user_profile;
private String email;
private String status;

public Person()
{}
    public Person(String user_name, String id, String user_profile, String email,String status1) {
        this.user_name = user_name;
        this.id = id;
        this.user_profile = user_profile;
        this.email = email;
        this.status=status1;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
