<?php

require "db.php";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    if (empty($_POST['fixture_id'])) {
        $err['status'] = 401;
        $err['message'] = 'Failed';
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }
    global $dbConn;

    #check if fixture_id exist
    $sql = $dbConn->prepare("SELECT id FROM fixture WHERE id = :fixture_id ");
    $sql->execute([
        "fixture_id" => $_POST['fixture_id'],
    ]);

    $exist = $sql->fetchAll();

    if (!$exist) {
        $err['status'] = 404;
        $err['message'] = 'Fixture not found';
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }

    try {
        #count bought tickets for fixture
        $sql = $dbConn->prepare("SELECT COUNT(t.fixture_id) AS 'bought_tickets', s.capacity AS 'stadium_capacity'
                                     FROM ticket t, fixture f, stadium s
                                     WHERE t.fixture_id = f.id AND 
                                            f.id = :fixture_id AND
                                            f.stadium_fixture = s.name AND 
                                            f.round = (SELECT round 
                                                       FROM fixture 
                                                       WHERE id = :fixture_id1 AND is_active = 1);");
        $sql->execute([
            "fixture_id" => $_POST['fixture_id'],
            "fixture_id1" => $_POST['fixture_id']
        ]);

        $result = $sql->fetch();

        $err['status'] = 200;
        $err['message'] = 'Success';
        $err['error'] = false;

        $response['error'] = $err;
        $response['response'] = $result;
        echo json_encode($response);
        die();

    } catch (Exception $e) {
        $err['status'] = 400;
        $err['message'] = 'Failed';
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }
}



