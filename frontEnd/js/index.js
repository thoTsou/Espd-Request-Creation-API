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

//do not save same espd many times
var doNotSave = false; 

$(document).ready(function () {
    //FOR CREATING ESPD


    //Log In Div
    $("#loginButton").click(function () {
        //retrieve given credentials
        let username = $("#username").val();
        let password = $("#loginPassword").val();

        // disabled the submit button
        $("#loginButton").prop("disabled", true);

        $("#loginButton").prop('value', 'Please Wait...');

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

                $("#loginButton").prop('value', 'Login');
                $("#loginButton").prop("disabled", false);
            },
        });

        // load espd so that user can review them
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/api/reviewEspdDocuments/" + username + "/getList",
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {


                var i;
                for (i = 0; i < result.length; i++) {
                    let espd = JSON.parse(result[i]);

                    //Row
                    var row = document.createElement("tr");

                    //Title
                    var column = document.createElement("td");
                    var title = document.createElement("p");
                    var textnode = document.createTextNode(result[i]['procurementProcedureTitle']);
                    title.appendChild(textnode);
                    column.appendChild(title);
                    row.appendChild(column);

                    //Button
                    var column = document.createElement("td");
                    var button = document.createElement('button');
                    button.type = 'button';
                    button.classList.add("btn");
                    button.classList.add("btn-success");
                    button.innerHTML = 'Click To Review';
                    button.addEventListener("click", setTemplate(espd), false);
                    column.appendChild(button);
                    row.appendChild(column);

                    document.getElementById('reviewEspdTable').appendChild(row);
                }

            },
            error: function (error) {
                console.log(error);
            },
        });
    });


    //What would you like to do div
    $("#toInformationBtn").click(function () {
        let username = $("#username").val();
        let password = $("#loginPassword").val();

        let createNewEspd = false;
        let reuseEspd = false;
        let viewEspd = false;

        if ($('#createEspdRadio').is(':checked')) {
            createNewEspd = true;
            //prefil some input fields , if user has already made an espd
            $.ajax({
                type: "GET",
                url: "http://localhost:8080/api/users/retrieveInformationAbout/" + username + "",
                beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
                success: function (result) {
                    //console.log(result);

                    informationAboutUser = JSON.parse(result);


                    setInformationFromArray(informationAboutUser);


                },
                error: function (error) {
                    console.log("User has not made any espd request documents yet");
                },
            });
        }

        if ($('#reuseEspdRadio').is(':checked')) {
            reuseEspd = true;
        }

        if ($('#viewEspdRadio').is(':checked')) {
            viewEspd = true;
            doNotSave = true;
        }

        if (createNewEspd === false && reuseEspd === false && viewEspd === false) {
            $("#noRadioPressed").show();
            return false;
        } else if (createNewEspd === true) {
            $("#noRadioPressed").hide();
            $("#WelcomeDiv").hide();
            $(".expand").addClass("show");
            $("#InfoDiv").show();
        } else if (reuseEspd === true) {
            $("#noRadioPressed").hide();
            $("#WelcomeDiv").hide();
            $(".expand").addClass("show");
            $("#UploadEspdDiv").show();
        } else {
            $("#noRadioPressed").hide();
            $("#WelcomeDiv").hide();
            $(".expand").addClass("show");
            $("#viewEspd").show();
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
                    break;
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

        $("#asXMLbtn").prop("disabled", true);

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

                //save espd into db
                if(doNotSave == false){
                saveEspd(username, password, documentVersion, espdRequestAsJson);
                }

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


    //FOR UPLOADING

    //send espd xml file to server
    $("#uploadEspdBtn").click(function (event) {

        //check if version is selected
        $("#noRadioPressedForUploading").hide();
        $("#invalidUpload").hide();

        if (!$('#uploadV1Radio').is(':checked') && !$('#uploadV2Radio').is(':checked')) {
            $("#noRadioPressedForUploading").show();
            return false;
        }

        if ($('#uploadV1Radio').is(':checked')) {
            documentVersion = 'v1';
        } else {
            documentVersion = 'v2';
        }


        let username = $("#username").val();
        let password = $("#loginPassword").val();

        //build my espd template in the background
        buildMyEspdTemplate(documentVersion, username, password);

        //stop submit the form, we will post it manually.
        event.preventDefault();

        // Get form
        var form = $('#fileUploadForm')[0];

        // Create an FormData object 
        var file = new FormData(form);


        // disabled the submit button
        $("#uploadEspdBtn").prop("disabled", true);

        $("#uploadEspdBtn").prop('value', 'Please Wait...');

        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "http://localhost:8080/api/importEspdRequestDocument/regulated/" + documentVersion + "",
            data: file,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (data) {
                console.log(data);

                $("#uploadEspdBtn").prop("disabled", false);

                $("#uploadEspdBtn").prop('value', 'Submit');

                $("#UploadEspdDiv").hide();

                $("#ProcurmentInfoDiv").show();

                setInformation(data);

                //build selection-exclusion criteria lists and check criteria included in the uploaded xml file
                buildLists(documentVersion, username, password, data);

                // set selected criteria at my global variable espd template
                var i;
                for (i = 0; i < data['fullCriterionList'].length; i++) {
                    if (data['fullCriterionList'][i]['selected'] === true) {


                        var criterionName = data['fullCriterionList'][i]['name'];

                        var j;
                        for (j = 0; j < espdRequestAsJson['fullCriterionList'].length; j++) {
                            if (espdRequestAsJson['fullCriterionList'][j]['name'] == criterionName) {
                                //alert(espdRequestAsJson['fullCriterionList'][j]['name']);
                                espdRequestAsJson['fullCriterionList'][j]['selected'] = true;
                            }
                        }

                    }
                }
            },
            error: function (error) {
                console.log(error);

                $("#invalidUpload").show();

                $("#uploadEspdBtn").prop("disabled", false);

                $("#uploadEspdBtn").prop('value', 'Submit');

            }
        });

    });

    function buildLists(documentVersion, username, password, espd) {
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

                                        // check checkboxes
                                        var k
                                        for (k = 0; k < espd['fullCriterionList'].length; k++) {
                                            if (espd['fullCriterionList'][k]['selected'] === true && espd['fullCriterionList'][k]['id'] == result[i]['id']) {

                                                document.getElementById(espd['fullCriterionList'][k]['id']).checked = true;
                                                break;
                                            }
                                        }

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


                                        // check checkboxes
                                        var k
                                        for (k = 0; k < espd['fullCriterionList'].length; k++) {
                                            if (espd['fullCriterionList'][k]['selected'] === true && espd['fullCriterionList'][k]['id'] == result[i]['id']) {

                                                document.getElementById(espd['fullCriterionList'][k]['id']).checked = true;
                                                break;
                                            }
                                        }


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

    }

    function buildMyEspdTemplate(documentVersion, username, password) {
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
    }

    function setInformation(data) {
        // set input values 
        $('#receivedNoticeNumber').val(data['cadetails']['receivedNoticeNumber']);
        $('#noticeNumberInTheOJS').val(data['cadetails']['procurementPublicationNumber']);
        $('#OJSurl').val(data['cadetails']['procurementPublicationURI']);

        $('#ProcurementTitle').val(data['cadetails']['procurementProcedureTitle']);
        $('#ProcurementDescr').html(data['cadetails']['procurementProcedureDesc']);
        $('#fileReferenceNumber').val(data['cadetails']['procurementProcedureFileReferenceNo']);

        $('#ProcurerName').val(data['cadetails']['caofficialName']);
        $('#ProcurerWebsite').val(data['cadetails']['webSiteURI']);
        $('#ProcurerStreetAndNumber').val(data['cadetails']['postalAddress']['addressLine1']);
        $('#ProcurerCity').val(data['cadetails']['postalAddress']['city']);
        $('#ProcurerPostcode').val(data['cadetails']['postalAddress']['postCode']);
        $('#ProcurerCountry').val('GR');

        $('#ProcurerContactPerson').val(data['cadetails']['contactingDetails']['contactPointName']);
        $('#ProcurerFax').val(data['cadetails']['contactingDetails']['faxNumber']);
        $('#ProcurerTelephone').val(data['cadetails']['contactingDetails']['telephoneNumber']);
        $('#ProcurerEmail').val(data['cadetails']['contactingDetails']['emailAddress']);
    }

    function setInformationFromArray(informationAboutUser) {
        // prefill input values

        $('#ProcurerName').val(informationAboutUser[1]['caofficialName']);
        $('#ProcurerWebsite').val(informationAboutUser[0]['webSiteURI']);
        $('#ProcurerStreetAndNumber').val(informationAboutUser[8]['addressLine1']);
        $('#ProcurerCity').val(informationAboutUser[9]['city']);
        $('#ProcurerPostcode').val(informationAboutUser[10]['postcode']);
        $('#ProcurerCountry').val('GR');


        $('#ProcurerContactPerson').val(informationAboutUser[4]['contactPointName']);
        $('#ProcurerFax').val(informationAboutUser[5]['faxNumber']);
        $('#ProcurerTelephone').val(informationAboutUser[6]['telephoneNumber']);
        $('#ProcurerEmail').val(informationAboutUser[7]['emailAddress']);
    }

    function saveEspd(username, password, documentVersion, espdRequestAsJson) {

        //save espd into db
        
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/exportEspdRequestDocument/regulated/" + documentVersion + "/saveDocument/" + username + "",
            data: JSON.stringify(espdRequestAsJson),
            contentType: 'application/json',
            beforeSend: function (xhr) { xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password)); },
            success: function (result) {
                console.log(result);

            },
            error: function (error) {
                console.log(error);
            },
        });
    }

    //FOR REVIEWING
    function setTemplate(espd) {
        return function () {

            // set our template
            espdRequestAsJson = [];

            espdRequestAsJson = JSON.parse(JSON.stringify(espd));

            //set document Version
            if (espdRequestAsJson['documentDetails']['version'] == 'V1') {
                documentVersion = 'v1';
            } else {
                documentVersion = 'v2';
            }


            //redirect
            $('#viewEspd').hide();

            $('#Finish').show();

        }
    }
});