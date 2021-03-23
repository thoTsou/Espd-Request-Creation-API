package com.espd.builder;

import eu.esens.espdvcd.codelist.CodelistsV1;

public class Codes {

    public String[] regulatedV1GetCodelistsTypes(String version){

        String[] codeListTypes;

        if(version.equals("v1")) {
            codeListTypes = new String[20];
            codeListTypes[0]="ActivityType";
            codeListTypes[1]="AmountType";
            codeListTypes[2]="ContractType";
            codeListTypes[3]="CountryIdentification";
            codeListTypes[4]="CriteriaType";
            codeListTypes[5]="CriterionJurisdictionLevel";
            codeListTypes[6]="CustomizationID";
            codeListTypes[7]="Currency";
            codeListTypes[8]="DocumentReferenceContentType";
            codeListTypes[9]="EORole";
            codeListTypes[10]="LanguageCodeEU";
            codeListTypes[11]="ResponseDataType";
            codeListTypes[12]="PeriodMeasureType";
            codeListTypes[13]="PeriodType";
            codeListTypes[14]="ProcedureType";
            codeListTypes[15]="ProfileExecutionID";
            codeListTypes[16]="ProjectType";
            codeListTypes[17]="ServicesProjectSubType";
            codeListTypes[18]="TechnicalCapabilityType";
            codeListTypes[19]="TenderingRole";

        }else{
            codeListTypes = new String[20];
        }
        return codeListTypes;
    }

}
