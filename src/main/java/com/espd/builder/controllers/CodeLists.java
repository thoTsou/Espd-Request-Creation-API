package com.espd.builder.controllers;

import eu.esens.espdvcd.codelist.Codelists;
import eu.esens.espdvcd.codelist.CodelistsV1;
import eu.esens.espdvcd.codelist.CodelistsV2;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/codelists")
public class CodeLists {

    /**
     * CODELISTS ENDPOINTS
     */

    /**
     * ENDPOINT
     *  get a list which contains the names of all codelists available (codelist for BidType,codelist for CountryIdentification   etc...)
     * @return
     */
    @ApiOperation(value = "Get a list which contains the names of all codelists available , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @GetMapping(path = "/regulated/{version}/types")
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
    @GetMapping(path = "/regulated/{version}/{type}/codelist")
    public String getCertainCodelist(@PathVariable String  version , @PathVariable String  type) {

        if (version.equals("v1")) {

                switch (type) {
                    case "ActivityType":
                        return MapToJson(CodelistsV1.ActivityType.getDataMap()).toString();
                    case "AmountType":
                        return MapToJson(CodelistsV1.AmountType.getDataMap()).toString();
                    case "ContractType":
                        return MapToJson(CodelistsV1.ContractType.getDataMap()).toString();
                    case "CountryIdentification":
                        return MapToJson(CodelistsV1.CountryIdentification.getDataMap()).toString();
                    case "CriteriaType":
                        return MapToJson(CodelistsV1.CriteriaType.getDataMap()).toString();
                    case "CriterionJurisdictionLevel":
                        return MapToJson(CodelistsV1.CriterionJurisdictionLevel.getDataMap()).toString();
                    case "CustomizationID":
                        return MapToJson(CodelistsV1.CustomizationID.getDataMap()).toString();
                    case "Currency":
                        return MapToJson(CodelistsV1.Currency.getDataMap()).toString();
                    case "DocumentReferenceContentType":
                        return MapToJson(CodelistsV1.DocumentReferenceContentType.getDataMap()).toString();
                    case "EORole":
                        return MapToJson(CodelistsV1.EORole.getDataMap()).toString();
                    case "LanguageCodeEU":
                        return MapToJson(CodelistsV1.LanguageCodeEU.getDataMap()).toString();
                    case "ResponseDataType":
                        return MapToJson(CodelistsV1.ResponseDataType.getDataMap()).toString();
                    case "PeriodMeasureType":
                        return MapToJson(CodelistsV1.PeriodMeasureType.getDataMap()).toString();
                    case "PeriodType":
                        return MapToJson(CodelistsV1.PeriodType.getDataMap()).toString();
                    case "ProcedureType":
                        return MapToJson(CodelistsV1.ProcedureType.getDataMap()).toString();
                    case "ProfileExecutionID":
                        return MapToJson(CodelistsV1.ProfileExecutionID.getDataMap()).toString();
                    case "ProjectType":
                        return MapToJson(CodelistsV1.ProjectType.getDataMap()).toString();
                    case "ServicesProjectSubType":
                        return MapToJson(CodelistsV1.ServicesProjectSubType.getDataMap()).toString();
                    case "TechnicalCapabilityType":
                        return MapToJson(CodelistsV1.TechnicalCapabilityType.getDataMap()).toString();
                    case "TenderingRole":
                        return MapToJson(CodelistsV1.TenderingRole.getDataMap()).toString();
                }
            }else if(version.equals("v2")){
                    switch (type) {
                        case "BidType":
                            return MapToJson(CodelistsV2.BidType.getDataMap()).toString();
                        case "ConfidentialityLevel":
                            return MapToJson(CodelistsV2.ConfidentialityLevel.getDataMap()).toString();
                        case "CountryIdentification":
                            return MapToJson(CodelistsV2.CountryIdentification.getDataMap()).toString();
                        case "CriterionElementType":
                            return MapToJson(CodelistsV2.CriterionElementType.getDataMap()).toString();
                        case "Currency":
                            return MapToJson(CodelistsV2.Currency.getDataMap()).toString();
                        case "DocumentReferenceContentType":
                            return MapToJson(CodelistsV2.DocumentReferenceContentType.getDataMap()).toString();
                        case "EOIDType":
                            return MapToJson(CodelistsV2.EOIDType.getDataMap()).toString();
                        case "EOIndustryClassification":
                            return MapToJson(CodelistsV2.EOIndustryClassification.getDataMap()).toString();
                        case "LanguageCodeEU":
                            return MapToJson(CodelistsV2.LanguageCodeEU.getDataMap()).toString();
                        case "EvaluationMethodType":
                            return MapToJson(CodelistsV2.EvaluationMethodType.getDataMap()).toString();
                        case "FinancialRatioType":
                            return MapToJson(CodelistsV2.FinancialRatioType.getDataMap()).toString();
                        case "QualificationApplicationType":
                            return MapToJson(CodelistsV2.QualificationApplicationType.getDataMap()).toString();
                        case "WeightingType":
                            return MapToJson(CodelistsV2.WeightingType.getDataMap()).toString();
                        case "ResponseDataType":
                            return MapToJson(CodelistsV2.ResponseDataType.getDataMap()).toString();
                        case "ProcedureType":
                            return MapToJson(CodelistsV2.ProcedureType.getDataMap()).toString();
                        case "ProfileExecutionID":
                            return MapToJson(CodelistsV2.ProfileExecutionID.getDataMap()).toString();
                        case "ProjectType":
                            return MapToJson(CodelistsV2.ProjectType.getDataMap()).toString();
                        case "ServicesProjectSubType":
                            return MapToJson(CodelistsV2.ServicesProjectSubType.getDataMap()).toString();
                        case "EORoleType":
                            return MapToJson(CodelistsV2.EORoleType.getDataMap()).toString();
                        case "PropertyGroupType":
                            return MapToJson(CodelistsV2.PropertyGroupType.getDataMap()).toString();
                    }
            }
            return null;
    }

    /**
     * convert Map<String,String> to Json Array
     */
    public JSONArray MapToJson(Map<String,String> map){

        JSONArray ja = new JSONArray();

        for (Map.Entry<String,String> entry : map.entrySet()) {
            //create json object
            JSONObject jo = new JSONObject();
            jo.put("code", entry.getKey() );
            jo.put("plain", entry.getValue() );

            //add it to json array
            ja.put(jo);
            }
            return ja;
    }

}
