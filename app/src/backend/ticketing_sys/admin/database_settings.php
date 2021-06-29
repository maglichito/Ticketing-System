<?php

require "ticketing_api/db.php";

session_start();

if (!isset($_SESSION['username']) || $_SESSION['username'] != 'admin') {
    header("Location: admin_login.php");
    die();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    global $dbConn;

    #users report
    if (!empty($_POST['export_user']) && $_POST['export_user'] === 'export user') {

        $sql = $dbConn->prepare("SELECT * FROM user");
        $sql->execute();
        $results = $sql->fetchAll();

        if ($results) {
            $fp = fopen('report_user.csv', 'w') or die("Unable to open file!");

            $write = "";
            foreach ($results as $result) {
                if ($result['is_active'] === 0) {
                    $result['is_active'] = "Banned User";
                } else {
                    $result['is_active'] = "Active User";
                }
                $write .= $result['id'] . ";" . $result['first_name'] . ";" . $result['last_name'] . ";"
                    . $result['username'] . ";" . $result['email'] . ";" . $result['created_at'] . ";" . $result['is_active'] . PHP_EOL;
            }
            try {
                fwrite($fp, $write);

                $err['status'] = 200;
                $err['message'] = "Success";
                $err['error'] = false;

                $response['error'] = $err;
                $response['response'] = $err['message'];
                echo json_encode($response);
                die();
            } catch (Exception $e) {
                $err['status'] = 400;
                $err['message'] = 'Failed';
                $err['error'] = true;

                $response['error'] = $err;
                $response['response'] = $e;
                echo json_encode($response);
                die();
            }
        } else {
            $err['status'] = 404;
            $err['message'] = 'No data available';
            $err['error'] = true;

            $response['error'] = $err;
            $response['response'] = $err['message'];
            echo json_encode($response);
            die();
        }
    }

    #report about sold tickets
    if (!empty($_POST['export_sold_tickets']) && $_POST['export_sold_tickets'] === 'sold tickets') {

        global $dbConn;
        $staement = "SELECT t.id, u.first_name AS 'firstname', u.last_name AS 'lastname',
                     c1.name AS 'home', c2.name AS 'away', f.stadium_fixture AS'stadium',
                     f.round, f.date_fixture AS 'date', tt.stand, tt.price, f.time_fixture AS 'time'
                     FROM user u, club c1, club c2, fixture f, ticket t, ticket_type tt 
                     WHERE t.user_id = u.id AND
                     c1.id = f.club_host_id AND
                     c2.id = f.club_away_id AND
                     tt.id = t.ticket_type_id AND
                     t.fixture_id = f.id";

        $sql = $dbConn->prepare($staement);
        $sql->execute();
        $results = $sql->fetchAll();

        if ($results) {
            $total = 0;
            $fp = fopen('report_tickets.csv', 'w') or die("Unable to open file!");

            $write = "";
            foreach ($results as $result) {
                $write .= $result['id'] . ";" . $result['firstname'] . ";" . $result['lastname'] . ";"
                    . $result['home'] . ";" . $result['away'] . ";" . $result['stadium'] . ";"
                    . $result['round'] . ";" . $result['date'] . ";" . $result['time'] . ";"
                    . $result['stand'] . ";" . $result['price'] . ";" . PHP_EOL;

                $total += $result['price'];
            }

            $write .= "Total tickets sold: ".count($results).PHP_EOL; #ukupan broj prodatih karti
            $write .= "Total: ".$total; #ukupno novca za koji su kupljene karte

            try {
                fwrite($fp, $write);

                $err['status'] = 200;
                $err['message'] = "Success";
                $err['error'] = false;

                $response['error'] = $err;
                $response['response'] = $results;
                echo json_encode($response);
                die();
            } catch (Exception $e) {
                $err['status'] = 400;
                $err['message'] = 'Failed';
                $err['error'] = true;

                $response['error'] = $err;
                $response['response'] = $e;
                echo json_encode($response);
                die();
            }
        } else {
            $err['status'] = 404;
            $err['message'] = 'No tickets purchased';
            $err['error'] = true;

            $response['error'] = $err;
            $response['response'] = $err['message'];
            echo json_encode($response);
            die();
        }
    }
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
    <h2>Ticketing System</h2>
</div>
<div style="text-align: center">
    <a href="admin_panel.php" style="display: inline-block; padding-right: 5%; padding-left:  5%;"> User Settings </a>
    <a href="logout.php" style="display: inline-block; padding-right: 5%; padding-left:  5%;"> Logout </a>
</div>
<hr>
</body>
<table style="margin: auto">
    <tr>
        <td><b> Name </b></td>
        <td><b> Description </b></td>
        <td><b> Action </b></td>
    </tr>
    <tr>
        <td> Generate data</td>
        <td> Fill with data tables: club, stadium, fixture.</td>
        <td><input type="button" id="fill" value="Fill Data"></td>
    </tr>
    <tr>
        <td> Export data</td>
        <td> Export data about users.</td>
        <td><input type="button" id="users" value="Export User Data"></td>
    </tr>
    <tr>
        <td> Export data</td>
        <td> Export data about sold tickets.</td>
        <td><input type="button" id="tickets" value="Export Sold Tickets Data"></td>
    </tr>
</table>
<script>
    const $$ = e => document.querySelector(e);

    //fill db with data
    const fill_database = function () {
        $.ajax({
            type: 'get',
            url: 'ticketing_api/fill_fixtures.php',
            data: {},
            success: function (response) {
                let resp = JSON.parse(response);
                console.log(resp);
                alert(resp.response);
            }
        })
    }
    //users report
    const export_user = function () {
        $.ajax({
            type: 'post',
            url: 'database_settings.php',
            data: {
                export_user: 'export user'
            },
            success: function (response) {
                let resp = JSON.parse(response);
                console.log(response);
                alert(resp.response);
                window.location = 'https://ticketingsystemfscg.000webhostapp.com/report_user.csv'
            }
        })
    }
    //sold tickets report
    const export_tickets = function () {
        $.ajax({
            type: 'post',
            url: 'database_settings.php',
            data: {
                export_sold_tickets: 'sold tickets'
            },
            success: function (response) {
                let resp = JSON.parse(response);
                console.log(resp);
                alert(resp.error.message);
                //if there is no sold tickets dont download report
                if (resp.error.status != 404) {
                    window.location = 'https://ticketingsystemfscg.000webhostapp.com/report_tickets.csv'
                }
            }
        })
    }

    $$('#fill').addEventListener('click', () => fill_database());

    $$('#users').addEventListener('click', () => {
        export_user();
    });

    $$('#tickets').addEventListener('click', () => {
        export_tickets();
    })

</script>
</html>