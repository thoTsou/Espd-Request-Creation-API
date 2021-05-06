package com.espd.builder.controllers;

import com.espd.builder.model.UserInformation;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/reviewEspdDocuments")
public class ReviewEspdDocuments {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * ENDPOINTS FOR REVIEWING ESPD REQUEST DOCS
     */

    @ApiOperation(value = "Receive user's username and return a list with all his/her created espd documents",
            notes = "Parameter {username} must equals with one registered user's username")
    @PostMapping(path = "/{username}/getList")
    public List<String> returnListWithEspdDocuments( @PathVariable String username ) throws IOException {

        //retrieve id of user
        String sql = "SELECT * FROM users WHERE username='" + username + "'";
        UserInformation userInformation = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(UserInformation.class));

        //create a list with espd request documents , that have been created by the above user
        List<String> espdDocumentsAsString = this.jdbcTemplate.query(
                "select * from espddocuments where user_id="+userInformation.getUser_id()+"",
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("EspddocumentAsJson");
                    }
                });

        espdDocumentsAsString.add("adcwvw");
        espdDocumentsAsString.add("aceqev");

        return espdDocumentsAsString;
    }

}
