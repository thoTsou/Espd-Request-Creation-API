package com.espd.builder.model;


/**
 * POJO User class
 * This class is used for the creation of a new user ,
 * from --> api/users/createNewUser
 */
public class User {
    private String username;
    private String password;
    //role can take two values , CA or CE --> CA(as Contracting Authority) , CE( as Contracting Entity)
    private String role ;

    public User(){};

    User(String username , String password , String role){
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
