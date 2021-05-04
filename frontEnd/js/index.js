//GLOABAL VARIABLES

//json formatted espd template
var espdRequestAsJson = {
    "id": null,
    "localId": null,
    "modelType": "ESPD_REQUEST_DRAFT",
    "serviceProviderDetails": {
        "name": "e-SENS",
        "id": "N/A",
        "endpointID": "N/A",
        "websiteURI": "N/A",
        "postalAddress": {
            "addressLine1": null,
            "city": null,
            "postCode": null,
            "countryCode": "NO"
        }
    },
    "documentDetails": {
        "version": "V1",
        "type": "ESPD_REQUEST",
        "qualificationApplicationType": "REGULATED"
    },
    "fullCriterionList": [],
    "cadetails": {
        "procurementProcedureTitle": null,
        "procurementProcedureDesc": null,
        "procurementProcedureFileReferenceNo": null,
        "procurementPublicationNumber": "0000/S 000-0000000",
        "electronicAddressID": null,
        "webSiteURI": null,
        "postalAddress": {
            "addressLine1": null,
            "city": null,
            "postCode": null,
            "countryCode": null
        },
        "contactingDetails": {
            "contactPointName": null,
            "faxNumber": null,
            "telephoneNumber": null,
            "emailAddress": null
        },
        "procurementPublicationURI": null,
        "procurementProcedureType": null,
        "weightingType": null,
        "projectType": null,
        "classificationCodes": [],
        "receivedNoticeNumber": null,
        "nationalOfficialJournal": null,
        "procurementProjectLots": 0,
        "id": null,
        "caofficialName": null,
        "cacountry": null,
        "weightScoringMethodologyNote": ""
    }
};

//document version
var documentVersion;

$(document).ready(function () {

    //Log In Div
    $("#loginButton").click(function () {
        //retrieve given credentials
        let username = $("#username").val();
        let password = $("#loginPassword").val();

        //ajax call to check if user with given credentials , is registered into our system
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/api/users/validateUser",
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                console.log(result);

                $("#invalidCredentials").hide();
                $("#LoginDiv").hide();
                $("#WelcomeDiv").show();
            },
            error: function (error) {
                console.log(error);

                $("#username").val('');
                $("#loginPassword").val('');

                $("#invalidCredentials").show();
            },
        });



    });


    //What would you like to do div
    $("#toInformationBtn").click(function () {
        let createNewEspd = false;

        if ($('#createEspdRadio').is(':checked')) {
            createNewEspd = true;
        }

        if (createNewEspd === false) {
            $("#noRadioPressed").show();
            return false;
        } else {
            $("#noRadioPressed").hide();
            $("#WelcomeDiv").hide();
            $(".expand").addClass("show");
            $("#InfoDiv").show();
        }

    });

    //espd version selection and authority location selection
    $("#next2Btn").click(function () {

        $("#InfoDiv").hide();

        $("#ProcurmentInfoDiv").show();

        if ($('#v1Radio').is(':checked')) {
            documentVersion = 'v1';
        } else if ($('#v2Radio').is(':checked')) {
            documentVersion = 'v2';
        }


        let username = $("#username").val();
        let password = $("#loginPassword").val();


        //constract selection and exclusion criteria lists
        //Exclusion Criteria list
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/api/criteria/regulated/" + documentVersion + "/criteriaGroups",
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                //clone array
                criteriaGroupsCodes = result.slice();
                exclusionGroundsCodes = [];

                for (i = 0; i < criteriaGroupsCodes.length; i++) {
                    if (criteriaGroupsCodes[i].includes("EXCLUSION")) {
                        exclusionGroundsCodes.push(criteriaGroupsCodes[i]);
                    }
                }

                // ajax call to convert codes to plain text
                $.ajax({
                    type: "GET",
                    url: "http://localhost:8080/api/codelists/regulated/" + documentVersion + "/CriteriaType/codelist",
                    beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
                    success: function (result) {
                        //console.log(result);

                        criteriaGroupsCodelist = JSON.parse(result);

                        for (i = 0; i < exclusionGroundsCodes.length; i++) {
                            for (j = 0; j < criteriaGroupsCodelist.length; j++) {

                                if (exclusionGroundsCodes[i] == criteriaGroupsCodelist[j]['code']) {
                                    $("#exlusionGrounds").append("<h5 style='color:red;'>" + criteriaGroupsCodelist[j]['plain'] + "</h5><hr/><div id='" + exclusionGroundsCodes[i] + "' >  </div><hr/>");
                                    break;
                                }

                            }
                        }


                        //ajax callS to get criteria

                        for (var counter = 0; counter < exclusionGroundsCodes.length; counter++) {

                            let localCounter = counter;

                            $.ajax({
                                type: "GET",
                                url: "http://localhost:8080/api/criteria/regulated/" + documentVersion + "/getCertainCriteria/" + exclusionGroundsCodes[localCounter] + "",
                                beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
                                success: function (result) {
                                    //console.log(result);

                                    for (i = 0; i < result.length; i++) {
                                        //append checkbox
                                        var newCheckBox = document.createElement('input');
                                        newCheckBox.type = 'checkbox';
                                        newCheckBox.id = result[i]['id'];
                                        newCheckBox.addEventListener("click", setCriterionAsSelected(result[i]['name']), false);
                                        newCheckBox.style.margin = "1%";
                                        document.getElementById(exclusionGroundsCodes[localCounter]).appendChild(newCheckBox);

                                        //append criterion title
                                        var node1 = document.createElement("span");
                                        var textnode = document.createTextNode(result[i]['name']);
                                        node1.appendChild(textnode);
                                        node1.style.fontWeight = 'bold';
                                        document.getElementById(exclusionGroundsCodes[localCounter]).appendChild(node1);

                                        //append criterion description
                                        var node2 = document.createElement("p");
                                        var textnode = document.createTextNode(result[i]['description']);
                                        node2.appendChild(textnode);
                                        document.getElementById(exclusionGroundsCodes[localCounter]).appendChild(node2);

                                    }

                                },
                                error: function (error) {
                                    console.log(error);

                                },
                            });

                        }

                    },
                    error: function (error) {
                        console.log(error);

                    },
                });

            },
            error: function (error) {
                console.log(error);

            },
        });

        //Selection Criteria
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/api/criteria/regulated/" + documentVersion + "/criteriaGroups",
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                //clone array
                criteriaGroupsCodes = result.slice();
                selectionGroundsCodes = [];

                for (i = 0; i < criteriaGroupsCodes.length; i++) {
                    if (criteriaGroupsCodes[i].includes("SELECTION")) {
                        selectionGroundsCodes.push(criteriaGroupsCodes[i]);
                    }
                }

                // ajax call to convert codes to plain text
                $.ajax({
                    type: "GET",
                    url: "http://localhost:8080/api/codelists/regulated/" + documentVersion + "/CriteriaType/codelist",
                    beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
                    success: function (result) {
                        //console.log(result);

                        criteriaGroupsCodelist = JSON.parse(result);

                        for (i = 0; i < selectionGroundsCodes.length; i++) {
                            for (j = 0; j < criteriaGroupsCodelist.length; j++) {

                                if (selectionGroundsCodes[i] == criteriaGroupsCodelist[j]['code']) {
                                    $("#selectionGrounds").append("<h5 style='color:green;'>" + criteriaGroupsCodelist[j]['plain'] + "</h5><hr/><div id='" + selectionGroundsCodes[i] + "' >  </div><hr/>");
                                    break;
                                }

                            }
                        }


                        //ajax callS to get criteria

                        for (var counter = 0; counter < selectionGroundsCodes.length; counter++) {

                            let localCounter = counter;

                            $.ajax({
                                type: "GET",
                                url: "http://localhost:8080/api/criteria/regulated/" + documentVersion + "/getCertainCriteria/" + selectionGroundsCodes[localCounter] + "",
                                beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
                                success: function (result) {
                                    //console.log(result);

                                    for (i = 0; i < result.length; i++) {
                                        //append checkbox
                                        var newCheckBox = document.createElement('input');
                                        newCheckBox.type = 'checkbox';
                                        newCheckBox.id = result[i]['id'];
                                        newCheckBox.addEventListener("click", setCriterionAsSelected(result[i]['name']), false);
                                        newCheckBox.style.margin = "1%";
                                        document.getElementById(selectionGroundsCodes[localCounter]).appendChild(newCheckBox);

                                        //append criterion title
                                        var node1 = document.createElement("span");
                                        var textnode = document.createTextNode(result[i]['name']);
                                        node1.appendChild(textnode);
                                        node1.style.fontWeight = 'bold';
                                        document.getElementById(selectionGroundsCodes[localCounter]).appendChild(node1);

                                        //append criterion description
                                        var node2 = document.createElement("p");
                                        var textnode = document.createTextNode(result[i]['description']);
                                        node2.appendChild(textnode);
                                        document.getElementById(selectionGroundsCodes[localCounter]).appendChild(node2);

                                    }

                                },
                                error: function (error) {
                                    console.log(error);

                                },
                            });

                        }

                    },
                    error: function (error) {
                        console.log(error);

                    },
                });

            },
            error: function (error) {
                console.log(error);

            },
        });

        //add criteria into criteria list depend on doc version
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/api/criteria/regulated/" + documentVersion + "/getList",
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                var i;
                for (i = 0; i < result.length; i++) {
                    result[i]['selected'] = false;
                    espdRequestAsJson['fullCriterionList'].push(result[i]);
                }

            },
            error: function (error) {
                console.log(error);
            },
        });


    });


    //add procurement information and display exclusion criteria
    $("#next3Btn").click(function () {

        //append information into the espd template

        //VERSION
        if (documentVersion == 'v1') {
            espdRequestAsJson['documentDetails']['version'] = 'V1'
        } else {
            espdRequestAsJson['documentDetails']['version'] = 'V2'
        }


        // SET CA DETAILS 
        espdRequestAsJson['cadetails']['receivedNoticeNumber'] = $("#receivedNoticeNumber").val();
        espdRequestAsJson['cadetails']['procurementPublicationNumber'] = $("#noticeNumberInTheOJS").val();
        espdRequestAsJson['cadetails']['procurementPublicationURI'] = $("#OJSurl").val();

        espdRequestAsJson['cadetails']['procurementProcedureTitle'] = $("#ProcurementTitle").val();
        espdRequestAsJson['cadetails']['procurementProcedureDesc'] = $('textarea#ProcurementDescr').val();
        espdRequestAsJson['cadetails']['procurementProcedureFileReferenceNo'] = $("#fileReferenceNumber").val();

        espdRequestAsJson['cadetails']['caofficialName'] = $("#ProcurerName").val();
        espdRequestAsJson['cadetails']['cacountry'] = 'GR';

        espdRequestAsJson['cadetails']['webSiteURI'] = $("#ProcurerWebsite").val();
        espdRequestAsJson['cadetails']['postalAddress']['addressLine1'] = $("#ProcurerStreetAndNumber").val();
        espdRequestAsJson['cadetails']['postalAddress']['city'] = $("#ProcurerCity").val();
        espdRequestAsJson['cadetails']['postalAddress']['postCode'] = $("#ProcurerPostcode").val();
        espdRequestAsJson['cadetails']['postalAddress']['countryCode'] = 'GR';

        espdRequestAsJson['cadetails']['contactingDetails']['contactPointName'] = $("#ProcurerContactPerson").val();
        espdRequestAsJson['cadetails']['contactingDetails']['faxNumber'] = $("#ProcurerFax").val();
        espdRequestAsJson['cadetails']['contactingDetails']['telephoneNumber'] = $("#ProcurerTelephone").val();
        espdRequestAsJson['cadetails']['contactingDetails']['emailAddress'] = $("#ProcurerEmail").val();

        //hide this div
        $("#ProcurmentInfoDiv").hide();

        $("#ExclusionGroundsDiv").show();

        window.scrollTo(0, 0);

    });


    //display selection criteria
    $("#next4Btn").click(function () {

        $("#ExclusionGroundsDiv").hide();


        $("#SelectionGroundsDiv").show();


        window.scrollTo(0, 0);

    });

    //set criterion from unselected to selected
    function setCriterionAsSelected(name) {
        return function () {
            //alert(name);

            var i;
            for (i = 0; i < espdRequestAsJson['fullCriterionList'].length; i++) {
                if (name == espdRequestAsJson['fullCriterionList'][i]['name']) {
                    espdRequestAsJson['fullCriterionList'][i]['selected'] = true;
                }
            }
        }
    }

    //Go to finish div
    $('#next5Btn').click(function () {

        $("#SelectionGroundsDiv").hide();

        $("#Finish").show();

        window.scrollTo(0, 0);


    });


    //finish div


    // download as xml
    $('#asXMLbtn').click(function () {

        let username = $("#username").val();
        let password = $("#loginPassword").val();


        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/exportEspdRequestDocument/regulated/" + documentVersion + "/EN/asXML",
            data: JSON.stringify(espdRequestAsJson),
            contentType: 'application/json',
            xhrFields: { responseType: 'blob' },
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                //HERE
                var blob = new Blob([result]);
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = "espd-Request-Requlated-" + documentVersion + ".xml";
                link.click();

            },
            error: function (error) {
                console.log(error);
            },
        });


    });

    // download as html
    $('#asHTMLbtn').click(function () {

        //alert("hi1");

        let username = $("#username").val();
        let password = $("#loginPassword").val();


        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/exportEspdRequestDocument/regulated/" + documentVersion + "/EN/asHTML",
            data: JSON.stringify(espdRequestAsJson),
            contentType: 'application/json',
            xhrFields: { responseType: 'html' },
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                //HERE
                var blob = new Blob([result]);
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = "espd-Request-Requlated-" + documentVersion + ".html";
                link.click();

            },
            error: function (error) {
                console.log(error);
            },
        });


    });

    // download as pdf
    $('#asPDFbtn').click(function () {
        //alert("hi1");

        let username = $("#username").val();
        let password = $("#loginPassword").val();


        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/exportEspdRequestDocument/regulated/" + documentVersion + "/EN/asPDF",
            data: JSON.stringify(espdRequestAsJson),
            contentType: 'application/json',
            xhrFields: { responseType: 'blob' },
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                //console.log(result);

                //HERE
                var blob = new Blob([result]);
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = "espd-Request-Requlated-" + documentVersion + ".pdf";
                link.click();

            },
            error: function (error) {
                console.log(error);
            },
        });

    });



});