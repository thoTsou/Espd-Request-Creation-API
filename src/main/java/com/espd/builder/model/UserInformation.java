package com.espd.builder.model;


public class UserInformation {
    private int user_id;
    private String username;
    private String password;
    private String role;
    private int enabled ;

    public UserInformation(){};

    UserInformation(int user_id , String username , String password , String role , int enabled){
        this.setUser_id(user_id);
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
        this.setEnabled(enabled);
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getEnabled() {
        return enabled;
    }


}
