package com.espd.builder.controllers;


import com.espd.builder.model.UserInformation;
import eu.esens.espdvcd.builder.*;
import eu.esens.espdvcd.codelist.enums.EULanguageCodeEnum;
import eu.esens.espdvcd.model.ESPDRequestImpl;
import eu.esens.espdvcd.transformation.TransformationService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/exportEspdRequestDocument")
public class ExportEspd {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * ENDPOINTS FOR EXPORTING ESPD REQUEST DOCS
     *
     * Espd Request extraction endpoints.
     * Each endpoint receives an espd request in json format from the frontend
     * and generates-returns an official espd request document  , formated as xml or pdf or html .
     */

    @ApiOperation(value = "Receive espd request as json and generates-returns it as xml , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ Parameter {language} must equals to one of the available Languages Codes *(etc --> EN ) **(api user can retrieve them by hitting api/codelists/regulated/{version}/LanguageCodeEU/codelist ) ")
    @PostMapping(path = "/regulated/{version}/{language}/asXML" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public byte[] exportEspdRequestDocAsXml(@RequestBody ESPDRequestImpl espdRequest , @PathVariable String version , @PathVariable String language ) throws IOException {
        if(version.equals("v1")) {
                return IOUtils.toByteArray(new XMLDocumentBuilderV1(espdRequest).getAsInputStream());
            }else if (version.equals("v2")){
                return IOUtils.toByteArray(new XMLDocumentBuilderV2(espdRequest).getAsInputStream());
            }
        return null;
    }

    @ApiOperation(value = "Receive espd request as json and generates-returns it as pdf, based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ Parameter {language} must equals to one of the available Languages Codes *(etc --> EN ) **(api user can retrieve them by hitting api/codelists/regulated/{version}/LanguageCodeEU/codelist ) ")
    @PostMapping(path = "/regulated/{version}/{language}/asPDF" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public byte[] exportEspdRequestDocAsPdf(@RequestBody ESPDRequestImpl espdRequest , @PathVariable String version , @PathVariable String language ) throws IOException {
        if(version.equals("v1")) {
            PDFDocumentBuilderV1 pdfDocumentBuilderV1 = BuilderFactory.EDM_V1
                            .createPDFDocumentBuilderFor(espdRequest);
            TransformationService transformationService = new TransformationService();
            InputStream pdfStream = transformationService.createPdfStream(new StreamSource(new ByteArrayInputStream(pdfDocumentBuilderV1.getAsString().getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
            return IOUtils.toByteArray(pdfStream);
        }else if (version.equals("v2")){
            PDFDocumentBuilderV2 pdfDocumentBuilderV2 = BuilderFactory.EDM_V2
                            .createPDFDocumentBuilderFor(espdRequest);
            TransformationService transformationService = new TransformationService();
            InputStream pdfStream = transformationService.createPdfStream(new StreamSource(new ByteArrayInputStream(pdfDocumentBuilderV2.getAsString().getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
            return IOUtils.toByteArray(pdfStream);
        }
        return null;
    }

    @ApiOperation(value = "Receive espd request as json and generates-returns it as html , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ Parameter {language} must equals to one of the available Languages Codes *(etc --> EN ) **(api user can retrieve them by hitting api/codelists/regulated/{version}/LanguageCodeEU/codelist ) ")
    @PostMapping(path = "/regulated/{version}/{language}/asHTML" , produces = "application/html")
    public byte[] exportEspdRequestDocAs(@RequestBody ESPDRequestImpl espdRequest , @PathVariable String version , @PathVariable String language ) throws IOException {
        if(version.equals("v1")) {
                    String theXML = BuilderFactory.EDM_V1.createXMLDocumentBuilderFor(espdRequest).getAsString();
                    TransformationService transformationService1 = new TransformationService();
                    InputStream htmlStream = transformationService1.createHtmlStream(new StreamSource(new ByteArrayInputStream(theXML.getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(htmlStream);
            } else if (version.equals("v2")){
                    String theXML = BuilderFactory.EDM_V2.createXMLDocumentBuilderFor(espdRequest).getAsString();
                    TransformationService transformationService1 = new TransformationService();
                    InputStream htmlStream = transformationService1.createHtmlStream(new StreamSource(new ByteArrayInputStream(theXML.getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(htmlStream);
        }
        return null;
    }

    @ApiOperation(value = "Save user's created espd request document into database , as text",
    notes="Parameter {version} must equals to v1 OR v2 ___ Parameter {username} must equals with one registered user's username ___" +
            "Request body parameter espdRequest must be an espd request document , in json format !!and stringified!! ")
    @PostMapping(path = "/regulated/{version}/saveDocument/{username}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String saveCreatedEspdDocumentIntoDB(@RequestBody String espdRequest , @PathVariable String version , @PathVariable String username ) {

        //retrieve id of user
        String sql = "SELECT * FROM users WHERE username='" + username + "'";
        UserInformation userInformation = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(UserInformation.class));

        //insert created espd into our db
        String sqlInsert = "INSERT INTO espddocuments (user_id, EspddocumentAsJson) VALUES (?, ?)";

        jdbcTemplate.update(sqlInsert, userInformation.getUser_id() , espdRequest );


        return "Successfully added espd document into DB";
    }

}
