package com.espd.builder.controllers;

import eu.esens.espdvcd.builder.*;
import eu.esens.espdvcd.builder.exception.BuilderException;
import eu.esens.espdvcd.model.ESPDRequest;
import eu.esens.espdvcd.retriever.criteria.CriteriaExtractor;
import eu.esens.espdvcd.retriever.criteria.RegulatedCriteriaExtractorBuilder;
import eu.esens.espdvcd.retriever.exception.RetrieverException;
import eu.esens.espdvcd.schema.enums.EDMVersion;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@CrossOrigin("*")
@RestController
@RequestMapping(path="/api/importEspdRequestDocument")
public class ImportEspd {

    /**
     * ENDPOINT
     *
     * Import an espd request .xml file and convert it to json format
     */
    @ApiOperation(value = "Import espd request from a .xml file , based on document version",
            notes = "Parameter {version} must equals to v1 OR v2")
    @PostMapping(path = "/regulated/{version}" , consumes = "multipart/form-data")
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

}
