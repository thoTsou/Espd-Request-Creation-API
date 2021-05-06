package com.espd.builder.controllers;

import com.espd.builder.model.*;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

    /**
     * ENDPOINT
     * return information about logged in user in order to prefill some input fields
     */
    @ApiOperation(value = "Retrieve information about logged in user in order to prefill some input fields")
    @GetMapping(path = "/retrieveInformationAbout/{username}")
    public String retrieveInformationAboutUser(@PathVariable String username){

        //retrieve id of user
        String sql = "SELECT * FROM users WHERE username='" + username + "'";
        UserInformation userInformation = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(UserInformation.class));

        if(userInformation != null) {

            int usersID = userInformation.getUser_id();

            //retrieve information about this user
            String sql2 = "SELECT * FROM userdetails WHERE user_id=" + usersID;
            UserDetails userDetails = jdbcTemplate.queryForObject(sql2, BeanPropertyRowMapper.newInstance(UserDetails.class));

            String sql3 = "SELECT * FROM contactingdetails WHERE contactingDetails_id=" + usersID;
            UserContactingDetails userContactingDetails = jdbcTemplate.queryForObject(sql3, BeanPropertyRowMapper.newInstance(UserContactingDetails.class));

            String sql4 = "SELECT * FROM postaladdresses WHERE postalAddress_Id=" + usersID;
            UserPostalAddress userPostalAddress = jdbcTemplate.queryForObject(sql4, BeanPropertyRowMapper.newInstance(UserPostalAddress.class));

            //construct and return a json array containing all the info

            JSONArray ja = new JSONArray();
            JSONObject jo ;

            //general Details
            jo= new JSONObject();
            jo.put("webSiteURI", userDetails.getWebSiteURI() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("caofficialName", userDetails.getCaofficialName() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("electronicAddressID", userDetails.getElectronicAddressID() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("cacountry", userDetails.getCacountry() );
            ja.put(jo);

            //contacting details
            jo= new JSONObject();
            jo.put("contactPointName", userContactingDetails.getContactPointName()  );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("faxNumber", userContactingDetails.getFaxNumber() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("telephoneNumber", userContactingDetails.getTelephoneNumber() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("emailAddress",  userContactingDetails.getEmailAddress() );
            ja.put(jo);

            //address details
            jo= new JSONObject();
            jo.put("addressLine1", userPostalAddress.getAddressLine1() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("city", userPostalAddress.getCity() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("postcode", userPostalAddress.getPostalAddress_Id() );
            ja.put(jo);

            jo= new JSONObject();
            jo.put("countryCode", userPostalAddress.getCountryCode()  );
            ja.put(jo);

            return ja.toString();

        }else {
            return null;
        }

    }

}
