package com.espd.builder.model;


/**
 * POJO User class
 * This class is used for the creation of a new user ,
 * from --> api/users/createNewUser
 */
public class User {
    private String username;
    private String password;

    public User(){};

    User(String username , String password){
        this.setUsername(username);
        this.setPassword(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
