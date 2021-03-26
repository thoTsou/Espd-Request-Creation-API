package com.espd.builder.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.espd.builder.model.User;
import eu.esens.espdvcd.builder.exception.BuilderException;
import eu.esens.espdvcd.codelist.Codelists;
import eu.esens.espdvcd.codelist.CodelistsV1;
import eu.esens.espdvcd.codelist.CodelistsV2;
import eu.esens.espdvcd.model.ESPDRequest;
import eu.esens.espdvcd.model.ESPDRequestImpl;
import eu.esens.espdvcd.model.SelectableCriterion;
import eu.esens.espdvcd.retriever.criteria.CriteriaExtractor;
import eu.esens.espdvcd.retriever.exception.RetrieverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import eu.esens.espdvcd.builder.*;


import eu.esens.espdvcd.schema.enums.EDMVersion;
import eu.esens.espdvcd.retriever.criteria.RegulatedCriteriaExtractorBuilder;

/**
 * This API helps its users to create regulated v1 or regulated v2 Espd Request Documents
 */
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
     *  get a list which contains all criteria types (typeCodes) , based on espd regulated request document version
     */
    @GetMapping(path = "/criteria/regulated/{version}/typesOfCriteria")
    public ArrayList<String> typesOfCriteria(@PathVariable String  version) throws RetrieverException {

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
         * collect criteria type codes from the above criteriaExtractor
         * and add them into an ArrayList
         */

        ArrayList<String> typeCodes = new ArrayList<String>();

        for(int i=0 ; i<criteriaExtractor.getFullList().size() ; i++){
            if(!typeCodes.contains( criteriaExtractor.getFullList().get(i).getTypeCode() )){
                typeCodes.add(criteriaExtractor.getFullList().get(i).getTypeCode());
            }
        }

        /**
         * Alphabetically sort the above list
         */
        Collections.sort(typeCodes, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        /**
         * return the above list to API user
         */
        return typeCodes;
    }

    /**
     * ENDPOINT
     *  get criteria list , based on espd regulated request document version
     */
    @GetMapping(path = "/criteria/regulated/{version}/getList")
    public List<SelectableCriterion> getList(@PathVariable String  version) throws RetrieverException {
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
         * return a list with criteria
         */
        return criteriaExtractor.getFullList();

    }

    /**
     * ENDPOINT
     * get criteria with certain typeCode , based on path parameter --> {criteriaTypeCode}
     */
    @GetMapping(path = "/criteria/regulated/{version}/getCertainCriteria/{criteriaTypeCode}")
    public ArrayList<SelectableCriterion> getCertainCriteria(@PathVariable String  version , @PathVariable String  criteriaTypeCode) throws RetrieverException {

        ArrayList<SelectableCriterion> list = new ArrayList<SelectableCriterion>();
        CriteriaExtractor criteriaExtractor;

        if(version.equals("v1")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();
            for(int counter=0;counter<criteriaExtractor.getFullList().size();counter++){
                if(criteriaExtractor.getFullList().get(counter).getTypeCode().equals(criteriaTypeCode)){
                    list.add(criteriaExtractor.getFullList().get(counter));
                }
            }
        }else if(version.equals("v2")){
            criteriaExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();
            for(int counter=0;counter<criteriaExtractor.getFullList().size();counter++){
                if(criteriaExtractor.getFullList().get(counter).getTypeCode().equals(criteriaTypeCode)){
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
    @GetMapping(path = "/codelists/regulated/{version}/types")
    public Codelists[] codelistsTypes(@PathVariable String  version) {

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
    @GetMapping(path = "/codelists/regulated/{version}/{type}/codelist")
    public Map<String,String> getCodelist(@PathVariable String  version , @PathVariable String  type) {

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
     * ENDPOINT
     *
     * Espd Request creation and extraction endpoint.
     * Receives an espd request in json format from the frontend
     * and generates an official espd request document ,with certain criteria selected , formated as xml or pdf or html
     */
    @PostMapping(path = "/exportEspdRequestDocument")
    public String getEspdRequestDoc(@RequestBody ESPDRequestImpl espdRequest ){

        //just trying to serialize request body parameter --> ESPDRequestImpl espdRequest
        //and return "success" if everything goes well
        //NOT WORKING
        return "success";
    }

    /**
     * ENDPOINTS FOR INTERACTION WITH DB
     */

    /**
     * ENDPOINT
     * Create new user
     * This is the only endpoint that does not have to be secured (no user authentication needed)
     */
    @PostMapping(path = "/users/createNewUser")
    public String createUser(@RequestBody User user ){

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

        jdbcTemplate.update("insert into users (username,password,role,enabled) values(?,?,?,?)", user.getUsername(), encryptedPassword, "ROLE_USER", 1);

        return "successfully created user";

    }



    /**
     * TEST ENDPOINTS
     */

    //trying to get espd request as json
    //works
    @GetMapping(path = "/getDefaultEspdRequest/regulated/{version}")
    public ESPDRequest getDefaultEspdRequest(@PathVariable String version) throws BuilderException, RetrieverException {

        CriteriaExtractor regulatedExtractor ;
        ESPDRequest espdRequest;

        if(version.equals("v1")){
            regulatedExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V1).build();

            espdRequest = BuilderFactory.EDM_V1
                    .createRegulatedModelBuilder()
                    .createESPDRequest();

            espdRequest.setCriterionList(regulatedExtractor.getFullList());
            return espdRequest;

        }else if(version.equals("v2")){
            regulatedExtractor = new RegulatedCriteriaExtractorBuilder(EDMVersion.V2).build();

            espdRequest = BuilderFactory.EDM_V2
                    .createRegulatedModelBuilder()
                    .createESPDRequest();

            espdRequest.setCriterionList(regulatedExtractor.getFullList());
            return espdRequest;
        }

        return null;
    }




}
