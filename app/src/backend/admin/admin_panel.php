<?php

require "ticketing_api/db.php";

session_start();

if (!isset($_SESSION['username']) || $_SESSION['username'] != 'admin') {
    header("Location: admin_login.php");
    die();
}

$isAdmin = $_SESSION['username'] === 'admin';

global $dbConn;
$results = [];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    global $results;

    #uzemmo vrijednosti iz filtera
    $firstname = $_POST['firstname'] ?? '';
    $lastname = $_POST['lastname'] ?? '';
    $username = $_POST['username'] ?? '';
    $email = $_POST['email'] ?? '';
    $created_at = $_POST['created_at'] ?? '';

    $statement = "SELECT * FROM user WHERE first_name LIKE '" . $firstname . "%' AND last_name LIKE '" . $lastname . "%' AND username LIKE '" . $username . "%' AND email LIKE '" . $email . "%' AND created_at LIKE '" . $created_at . "%'";

    #ako se is_active ne postavi da vraca sve
    #inace vrati od vrijednosti koja je proslijedjena
    if ($_POST['is_active'] !== '') {
        $is_active = intval($_POST['is_active']);
        $statement .= " and is_active = " . $is_active;
    }

    $sql = $dbConn->prepare($statement);

    $sql->execute();

    $results = $sql->fetchAll();

    $echo_string = '';
    if(!$results){
        $echo_string .= '<tr> <td colspan="6"><b> No data available </b></td></tr>';
    }

    foreach ($results as $result) {

        $echo_string .= '<tr>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['first_name'] . '</td>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['last_name'] . '</td>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['username'] . '</td>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['email'] . '</td>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['created_at'] . '</td>
                <td style="overflow: hidden; white-space: nowrap; width: 165px">' . $result['is_active'] . '</td>';
                if($result['is_active'] === 1){
                    $echo_string .= '<td><a href="ban_user.php?id='.$result['id'].'"><input type="button" value="Deactivate User"></a></td>';
                }else{
                    $echo_string .= '<td><a href="ban_user.php?id='.$result['id'].'"><input type="button" value="Activate User"></a></td>';
                }
              $echo_string .= '</tr>';
    }

    echo $echo_string;
    die();
}

?>

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
    <h2> Ticketing System </h2>
</div>
<div style="text-align: center">
    <a href="database_settings.php" style="display: inline-block; padding-right: 5%; padding-left:  5%;"> Database
        Settings </a>
    <a href="logout.php" style="display: inline-block; padding-right: 5%; padding-left:  5%;"> Logout </a>
</div>
<hr>
<table style="margin: auto; table-layout:fixed; width: 100%; max-width: 165px">
    <tr>
        <td style="white-space: nowrap; width: 165px"><b>Firstname</b></td>
        <td style="white-space: nowrap; width: 165px"><b>Lastname</b></td>
        <td style="white-space: nowrap; width: 165px"><b>Username</b></td>
        <td style="white-space: nowrap; width: 165px"><b>Email</b></td>
        <td style="white-space: nowrap; width: 165px"><b>CreatedAt</b></td>
        <td style="white-space: nowrap; width: 165px"><b>Is Active</b></td>
    </tr>
    <tr>
        <td style="white-space: nowrap; width: 165px"><input type="text" id="firstname" placeholder="Firstname" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"><input type="text" id="lastname" placeholder="Lastname" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"><input type="text" id="username" placeholder="Username" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"><input type="text" id="email" placeholder="Email" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"><input type="date" id="created_at" placeholder="Created at" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"><input type="text" id="is_active" placeholder="Is active" oninput="filter_data()"></td>
        <td style="white-space: nowrap; width: 165px"></td>
    </tr>
</table>
<table id="data" style="margin: auto; table-layout:fixed; width: 100%; max-width: 165px">


</table>
<hr>
</body>
<script>
    const $$ = e => document.querySelector(e);

    const filter_data = function () {

        $.ajax({
            type: 'post',
            url: 'admin_panel.php',
            data: {
                firstname: $$('#firstname').value,
                lastname: $$('#lastname').value,
                username: $$('#username').value,
                email: $$('#email').value,
                created_at: $$('#created_at').value,
                is_active: $$('#is_active').value
            },
            success: function (response) {
                $$('#data').innerHTML = response;
            }
        })
    }

    window.onload = () => filter_data();

</script>
</html>
