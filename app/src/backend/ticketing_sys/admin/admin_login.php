<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Ticketing System</title>
</head>
<script type="text/javascript" src="../jquery-3.6.0.js" type="module"></script>
<body>
<div style="text-align: center">
    <h2>Ticketing System</h2>
    <form action="ticketing_api/login.php" method="post">
        <table style="margin: auto">
            <tr>
                <td><label> Username </label></td>
                <td><input id="username" type="text"></td>
            </tr>
            <tr>
                <td><label for=""> Password </label></td>
                <td><input id="password" type="password"></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align: center"> <input type="button" id="btnSubmit" value="Login"></td>
            </tr>
            <tr>
                <td> <span id="error"></span></td>
            </tr>
        </table>
    </form>
</div>
</body>
<script>
    const $$ = e => document.querySelector(e);

    //validation
    const validate_fields = function (){

        const username = $$('#username').value;
        const password = $$('#password').value;

        if(!username){
            $$('#username').style = 'border-color:red';
        }else{
            $$('#username').style = "";
        }

        if(!password){
            $$('#password').style = 'border-color:red';
        }else{
            $$('#password').style = "";
        }

        return username && password;
    }

    //check admin
    const login = function () {
        $.ajax({
            type: 'post',
            url: 'ticketing_api/login.php',
            data: {
                username: $$('#username').value,
                password: $$('#password').value,
            },
            success: function (response) {
                let resp = JSON.parse(response);
                if(!resp.user){
                    alert(resp.response.message);
                    return;
                }else{
                    window.location = "admin_panel.php";
                    return;
                }
            }
        })
    }

    $$('#btnSubmit').addEventListener('click', () => {
        if(validate_fields()){
            login();
        }
    })

</script>
</html>
