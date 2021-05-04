$(document).ready(function () {

    //on register
    $("#registerUserBtn").click(function () {
        
        let username = $("#username").val();
        let password = $("#password1").val();
        let passwordTypedAgain = $("#password2").val();
        let role = $("#role").val();

        //check if given credentials are not null
        if(username == '' || password == '' || passwordTypedAgain == ''){
            
            $("#nullCredentials").show();

            return false;
        }

        //check if password and passwordTypedAgain are the same
        if(password != passwordTypedAgain){
            $('#password1').val('');
            $('#password2').val('');

            $("#passwordsDontMatch").show();
            return false;
        }

        $("#nullCredentials").hide();
        $("#passwordsDontMatch").hide();
        //submit form
        //use API and save user 

        $.post(
            "http://localhost:8080/api/users/createNewUser",
            {
                username: username,
                password: password,
                role: role
            },
            function (response) {

                console.log(response);
                $("#mainDiv").hide();

                if(response == 'user with given username already exists'){
                    $("#userAleadyExist").show();
                }else if (response == 'successfully created user'){
                    $("#success").show();
                }else{
                    $("#error").show(); 
                }
            }
        );

    });



});