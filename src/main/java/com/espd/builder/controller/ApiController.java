package com.espd.builder.controller;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.espd.builder.model.User;
import eu.esens.espdvcd.builder.exception.BuilderException;
import eu.esens.espdvcd.codelist.Codelists;
import eu.esens.espdvcd.codelist.CodelistsV1;
import eu.esens.espdvcd.codelist.CodelistsV2;
import eu.esens.espdvcd.codelist.enums.EULanguageCodeEnum;
import eu.esens.espdvcd.model.ESPDRequest;
import eu.esens.espdvcd.model.ESPDRequestImpl;
import eu.esens.espdvcd.model.SelectableCriterion;
import eu.esens.espdvcd.retriever.criteria.CriteriaExtractor;
import eu.esens.espdvcd.retriever.exception.RetrieverException;
import eu.esens.espdvcd.transformation.TransformationService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import eu.esens.espdvcd.builder.*;


import eu.esens.espdvcd.schema.enums.EDMVersion;
import eu.esens.espdvcd.retriever.criteria.RegulatedCriteriaExtractorBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.stream.StreamSource;

import io.swagger.annotations.*;


@RestController
@RequestMapping(path="/api")
public class ApiController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * CRITERIA ENDPOINTS
     */

    /**
     *   ENDPOINT
     *  get a list which contains all existing criteria groups , based on espd regulated request document  version
     */
    @ApiOperation(value = "Retrieve a list which contains all existing criteria groups , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @GetMapping(path = "/criteria/regulated/{version}/criteriaGroups")
    public ArrayList<String> getCriteriaGroups(@PathVariable String  version) throws RetrieverException {

        /**
         * define criteria extractor  based on PathVariable --> {version}
         */
        CriteriaExtractor criteriaExtractor = null ;

        if(version.equals("v1")) {
             criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();
        }else if(version.equals("v2")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();
        }

        /**
         * collect all criteria groups from the above criteriaExtractor
         * and add them into an ArrayList
         */

        ArrayList<String> criteriaGroups = new ArrayList<String>();

        for(int i=0 ; i<criteriaExtractor.getFullList().size() ; i++){
            if(!criteriaGroups.contains( criteriaExtractor.getFullList().get(i).getCriterionGroup() )){
                criteriaGroups.add(criteriaExtractor.getFullList().get(i).getCriterionGroup());
            }
        }

        /**
         * Alphabetically sort the above list
         */
        Collections.sort(criteriaGroups, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        /**
         * return the above list to API user
         */
        return criteriaGroups;
    }

    /**
     * ENDPOINT
     *  get list of criteria , based on espd request document  version
     */
    @ApiOperation(value = "Get full list of criteria , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @GetMapping(path = "/criteria/regulated/{version}/getList")
    public List<SelectableCriterion> getCriteriaList(@PathVariable String  version) throws RetrieverException {
        /**
         * define criteria extractor  based on PathVariable --> version
         */
        CriteriaExtractor criteriaExtractor = null ;

        if(version.equals("v1")) {
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();
        }else if(version.equals("v2")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();
        }

        /**
         * return a list filled with criteria
         */
        return criteriaExtractor.getFullList();

    }

    /**
     * ENDPOINT
     * get criteria of certain criterion Group , based on path parameter --> {criterionGroup}
     */
    @ApiOperation(value = "Get criteria of certain criterion Group , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ Parameter {criterionGroup} must equals to a certain criteria group ( you can see all criteria groups available by hitting /api/criteria/regulated/{version}/criteriaGroups)")
    @GetMapping(path = "/criteria/regulated/{version}/getCertainCriteria/{criterionGroup}")
    public ArrayList<SelectableCriterion> getCriteriaOfCertainCriteriaGroup(@PathVariable String  version , @PathVariable String  criterionGroup) throws RetrieverException {

        ArrayList<SelectableCriterion> list = new ArrayList<SelectableCriterion>();
        CriteriaExtractor criteriaExtractor;

        if(version.equals("v1")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();
            for(int counter=0;counter<criteriaExtractor.getFullList().size();counter++){
                if(criteriaExtractor.getFullList().get(counter).getCriterionGroup().equals(criterionGroup)){
                    list.add(criteriaExtractor.getFullList().get(counter));
                }
            }
        }else if(version.equals("v2")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();
            for(int counter=0;counter<criteriaExtractor.getFullList().size();counter++){
                if(criteriaExtractor.getFullList().get(counter).getCriterionGroup().equals(criterionGroup)){
                    list.add(criteriaExtractor.getFullList().get(counter));
                }
            }
        }

        return list;
    }


    /**
     * CODELISTS ENDPOINTS
     */

    /**
     * ENDPOINT
     *  get a list which contains the names of all codelists available (codelist for BidType,codelist for CountryIdentification   etc...)
     */
    @ApiOperation(value = "Get a list which contains the names of all codelists available , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @GetMapping(path = "/codelists/regulated/{version}/types")
    public Codelists[] getCodelistsNames(@PathVariable String  version) {

        if (version.equals("v1")) {
            return CodelistsV1.values();
        }else if(version.equals("v2")){
            return CodelistsV2.values();
        }
        return null;
    }

    /**
     * ENDPOINT
     *  get one certain codelist ,based on path parameter --> {type}
     */
    @ApiOperation(value = "Get one certain codelist , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ parameter {type} must equals to a certain codelist name (etc Currency) ")
    @GetMapping(path = "/codelists/regulated/{version}/{type}/codelist")
    public Map<String,String> getCertainCodelist(@PathVariable String  version , @PathVariable String  type) {

        if (version.equals("v1")) {

                switch (type) {
                    case "ActivityType":
                        return CodelistsV1.ActivityType.getDataMap();
                    case "AmountType":
                        return CodelistsV1.AmountType.getDataMap();
                    case "ContractType":
                        return CodelistsV1.ContractType.getDataMap();
                    case "CountryIdentification":
                        return CodelistsV1.CountryIdentification.getDataMap();
                    case "CriteriaType":
                        return CodelistsV1.CriteriaType.getDataMap();
                    case "CriterionJurisdictionLevel":
                        return CodelistsV1.CriterionJurisdictionLevel.getDataMap();
                    case "CustomizationID":
                        return CodelistsV1.CustomizationID.getDataMap();
                    case "Currency":
                        return CodelistsV1.Currency.getDataMap();
                    case "DocumentReferenceContentType":
                        return CodelistsV1.DocumentReferenceContentType.getDataMap();
                    case "EORole":
                        return CodelistsV1.EORole.getDataMap();
                    case "LanguageCodeEU":
                        return CodelistsV1.LanguageCodeEU.getDataMap();
                    case "ResponseDataType":
                        return CodelistsV1.ResponseDataType.getDataMap();
                    case "PeriodMeasureType":
                        return CodelistsV1.PeriodMeasureType.getDataMap();
                    case "PeriodType":
                        return CodelistsV1.PeriodType.getDataMap();
                    case "ProcedureType":
                        return CodelistsV1.ProcedureType.getDataMap();
                    case "ProfileExecutionID":
                        return CodelistsV1.ProfileExecutionID.getDataMap();
                    case "ProjectType":
                        return CodelistsV1.ProcedureType.getDataMap();
                    case "ServicesProjectSubType":
                        return CodelistsV1.ServicesProjectSubType.getDataMap();
                    case "TechnicalCapabilityType":
                        return CodelistsV1.TechnicalCapabilityType.getDataMap();
                    case "TenderingRole":
                        return CodelistsV1.TenderingRole.getDataMap();
                }
            }else if(version.equals("v2")){
                    switch (type) {
                        case "BidType":
                            return CodelistsV2.BidType.getDataMap();
                        case "ConfidentialityLevel":
                            return CodelistsV2.ConfidentialityLevel.getDataMap();
                        case "CountryIdentification":
                            return CodelistsV2.CountryIdentification.getDataMap();
                        case "CriterionElementType":
                            return CodelistsV2.CriterionElementType.getDataMap();
                        case "Currency":
                            return CodelistsV2.Currency.getDataMap();
                        case "DocumentReferenceContentType":
                            return CodelistsV2.DocumentReferenceContentType.getDataMap();
                        case "EOIDType":
                            return CodelistsV2.EOIDType.getDataMap();
                        case "EOIndustryClassification":
                            return CodelistsV2.EOIndustryClassification.getDataMap();
                        case "LanguageCodeEU":
                            return CodelistsV2.LanguageCodeEU.getDataMap();
                        case "EvaluationMethodType":
                            return CodelistsV2.EvaluationMethodType.getDataMap();
                        case "FinancialRatioType":
                            return CodelistsV2.FinancialRatioType.getDataMap();
                        case "QualificationApplicationType":
                            return CodelistsV2.QualificationApplicationType.getDataMap();
                        case "WeightingType":
                            return CodelistsV2.WeightingType.getDataMap();
                        case "ResponseDataType":
                            return CodelistsV2.ResponseDataType.getDataMap();
                        case "ProcedureType":
                            return CodelistsV2.ProcedureType.getDataMap();
                        case "ProfileExecutionID":
                            return CodelistsV2.ProfileExecutionID.getDataMap();
                        case "ProjectType":
                            return CodelistsV2.ProjectType.getDataMap();
                        case "ServicesProjectSubType":
                            return CodelistsV2.ServicesProjectSubType.getDataMap();
                        case "EORoleType":
                            return CodelistsV2.EORoleType.getDataMap();
                        case "PropertyGroupType":
                            return CodelistsV2.PropertyGroupType.getDataMap();
                    }
            }
            return null;
        }

    /**
     * ENPOINTS to import and export Espd Requests
      */


    /**
     * ENDPOINT
     *
     * Espd Request extraction endpoint.
     * Receives an espd request in json format from the frontend
     * and generates-returns an official espd request document  , formated as xml or pdf or html .
     */
    @ApiOperation(value = "Receive espd request as json and generates-returns it as xml or pdf or html , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2 ___ Parameter {exportFormat} must equals to xml or pdf or html ___ Parameter {language} must equals to one of the available Languages Codes *(etc --> EN ) **(api user can retrieve them by hitting api/codelists/regulated/{version}/LanguageCodeEU/codelist ) ")
    @PostMapping(path = "/exportEspdRequestDocument/regulated/{version}/{exportFormat}/{language}")
    public byte[] exportEspdRequestDocAs(@RequestBody ESPDRequestImpl espdRequest , @PathVariable String exportFormat , @PathVariable String version , @PathVariable String language ) throws IOException {
        if(version.equals("v1")) {
            switch (exportFormat) {
                case "xml":
                    return IOUtils.toByteArray(new XMLDocumentBuilderV1(espdRequest).getAsInputStream());
                case "html":
                    String theXML = BuilderFactory.EDM_V1.createXMLDocumentBuilderFor(espdRequest).getAsString();
                    TransformationService transformationService1 = new TransformationService();
                    InputStream htmlStream = transformationService1.createHtmlStream(new StreamSource(new ByteArrayInputStream(theXML.getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(htmlStream);
                case "pdf":
                    PDFDocumentBuilderV1 pdfDocumentBuilderV1 = BuilderFactory.EDM_V1
                            .createPDFDocumentBuilderFor(espdRequest);
                    TransformationService transformationService = new TransformationService();
                    InputStream pdfStream = transformationService.createPdfStream(new StreamSource(new ByteArrayInputStream(pdfDocumentBuilderV1.getAsString().getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(pdfStream);
            }
        }else if (version.equals("v2")){
            switch (exportFormat) {
                case "xml":
                    return IOUtils.toByteArray(new XMLDocumentBuilderV2(espdRequest).getAsInputStream());
                case "html":
                    String theXML = BuilderFactory.EDM_V2.createXMLDocumentBuilderFor(espdRequest).getAsString();
                    TransformationService transformationService1 = new TransformationService();
                    InputStream htmlStream = transformationService1.createHtmlStream(new StreamSource(new ByteArrayInputStream(theXML.getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(htmlStream);
                case "pdf":
                    PDFDocumentBuilderV2 pdfDocumentBuilderV2 = BuilderFactory.EDM_V2
                            .createPDFDocumentBuilderFor(espdRequest);
                    TransformationService transformationService = new TransformationService();
                    InputStream pdfStream = transformationService.createPdfStream(new StreamSource(new ByteArrayInputStream(pdfDocumentBuilderV2.getAsString().getBytes(StandardCharsets.UTF_8))), EULanguageCodeEnum.valueOf(language));
                    return IOUtils.toByteArray(pdfStream);
            }
        }
        return null;
    }

    /**
     * ENDPOINT
     *
     * Import an espd request .xml file and convert it to json format
     */
    @ApiOperation(value = "Import espd request from a .xml file , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @PostMapping(path = "/importEspdRequestDocument/regulated/{version}" , consumes = "multipart/form-data")
    public ESPDRequest importEspdFromXmlFile(@PathVariable String version , @RequestParam("file") MultipartFile file) throws FileNotFoundException, BuilderException, RetrieverException {

        //convert MultipartFile to File
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));

        File convertedFile = null;
        try {
            convertedFile = File.createTempFile(fileName, prefix);
            file.transferTo(convertedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return a json represantation of the content of this file
        InputStream inputStream = new FileInputStream(convertedFile);

        if(version.equals("v1")){
            ESPDRequest espdRequest = BuilderFactory.EDM_V1.createRegulatedModelBuilder().importFrom(inputStream).createESPDRequest();
            CriteriaExtractor extractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();

            //json must include all criteria (selected+unselected)
            espdRequest.setCriterionList(extractor.getFullList(espdRequest.getFullCriterionList()));

            return espdRequest;
        }else if(version.equals("v2")){
            ESPDRequest espdRequest = BuilderFactory.EDM_V2.createRegulatedModelBuilder().importFrom(inputStream).createESPDRequest();
            CriteriaExtractor extractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();

            //json must include all criteria (selected+unselected)
            espdRequest.setCriterionList(extractor.getFullList(espdRequest.getFullCriterionList()));

            return espdRequest;
        }

        return null;
    }


    /**
     * ENDPOINTS FOR INTERACTION WITH DB
     */

    /**
     * ENDPOINT
     * Create new user
     * This is the only endpoint that does not have to be secured (no user authentication needed)
     */
    @ApiOperation(value = "Insert a new user into DB",
            notes = "User role must equals to CA or CE --> CA(as Contracting Authority) , CE( as Contracting Entity)")
    @PostMapping(path = "/users/createNewUser")
    public String createUser(@RequestBody User user ){

        //check if role is CA or CE
        if( !user.getRole().equals("CA") && !user.getRole().equals("CE") ){
            return "role must be CA or CE --> CA(as Contracting Authority) , CE( as Contracting Entity)";
        }

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

}
