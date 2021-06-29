<?php

require "db.php";

#on page load return all fixtures
if($_SERVER['REQUEST_METHOD'] === 'GET'){

    global $dbConn;

    $sql = $dbConn -> prepare("SELECT f.id AS 'fixture_id', f.round , f.stadium_fixture AS 'stadium',
                                            f.date_fixture AS 'date', f.time_fixture AS 'time',
                                            c1.name AS 'home', c1.code AS 'home_code',c1.logo AS 'home_logo',
                                            c2.name AS 'away',c2.code AS 'away_code', c2.logo AS 'away_logo'
                                     FROM fixture f, club c1, club c2
                                     WHERE f.club_host_id = c1.id AND f.club_away_id = c2.id
                                     ORDER BY f.round");
    $sql -> execute();
    $response = $sql -> fetchAll();

    #if there is no any fixtures
    if(!$response){

        $resp['fixtures'] = "No data";

        $error['status'] = 404;
        $error['message'] = "No data";
        $error['error'] = true;

        $resp['error'] = $error;

        echo json_encode($resp);
        die();
    }

    $resp['fixtures'] = $response;

    $error['status'] = 200;
    $error['message'] = "Success";
    $error['error'] = false;

    $resp['error'] = $error;

    echo json_encode($resp);
    die();
}
















