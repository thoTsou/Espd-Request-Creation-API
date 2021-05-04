package com.espd.builder.controllers;

import com.espd.builder.model.User;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/users")
public class Users {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * ENDPOINTS FOR INTERACTION WITH DB
     */

    /**
     * ENDPOINT
     * Create new user
     * This endpoint does not have to be secured (no user authentication needed)
     */
    @ApiOperation(value = "Insert a new user into DB")
    @PostMapping(path = "/createNewUser")
    public String createUser(User user ){


        //Check if username is available ( Maybe someone else has this username)
        List<User> usersList = this.jdbcTemplate.query(
                "select username from users",
                new RowMapper<User>() {
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setUsername(rs.getString("username"));
                        return user;
                    }
                });

        //if username is not available
        for(User userInList : usersList){
            if( userInList.getUsername().equals(user.getUsername()) ){
                return "user with given username already exists";
            }
        }

        //if username is available
        BCryptPasswordEncoder bCryptPasswordEncodernew = new BCryptPasswordEncoder();
        String encryptedPassword = bCryptPasswordEncodernew.encode(user.getPassword());

        jdbcTemplate.update("insert into users (username,password,role,enabled) values(?,?,?,?)", user.getUsername(), encryptedPassword, user.getRole() , 1);

        return "successfully created user";

    }

    /**
     * ENDPOINT
     * Validate user's given credentials
     * If given combination of username and password is invalid then server's response is going to be 401
     */
    @ApiOperation(value = "When user tries to login , check his/her credentials",
            notes="If given credentials are invalid,server is going to response with error 401")
    @GetMapping(path = "/validateUser")
    public String checkCredentials(){

        return "Valid Credentials";
    }

}
