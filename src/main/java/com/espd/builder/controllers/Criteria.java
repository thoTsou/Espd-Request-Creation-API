package com.espd.builder.controllers;

import eu.esens.espdvcd.model.SelectableCriterion;
import eu.esens.espdvcd.retriever.criteria.CriteriaExtractor;
import eu.esens.espdvcd.retriever.criteria.RegulatedCriteriaExtractorBuilder;
import eu.esens.espdvcd.retriever.exception.RetrieverException;
import eu.esens.espdvcd.schema.enums.EDMVersion;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/criteria")
public class Criteria {

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
    @GetMapping(path = "/regulated/{version}/criteriaGroups")
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
    @GetMapping(path = "/regulated/{version}/getList")
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
    @GetMapping(path = "/regulated/{version}/getCertainCriteria/{criterionGroup}")
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

}
