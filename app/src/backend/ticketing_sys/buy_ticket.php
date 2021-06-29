<?php

require "db.php";

#returns no of available tickets for fixture
function check_available_tickets(){

    global $dbConn;

    #get capacity for stadium based on fixture_id
    $sql = $dbConn -> prepare("SELECT capacity 
                                     FROM stadium s, fixture f
                                     WHERE f.id = :fixture_id AND f.stadium_fixture = s.name;");
    $sql -> execute([
        "fixture_id" => $_POST['fixture_id']
    ]);

    $capacity = $sql -> fetchColumn();

    #count tickets for fixture
    $sql = $dbConn -> prepare("SELECT COUNT(t.fixture_id) 
                                     FROM ticket t, fixture f 
                                     WHERE t.fixture_id = f.id AND 
                                            f.id = :fixture_id AND 
                                            f.round = (SELECT round 
                                                       FROM fixture 
                                                       WHERE id = :fixture_id1 AND is_active = 1);");
    $sql -> execute([
        "fixture_id" => $_POST['fixture_id'],
        "fixture_id1" => $_POST['fixture_id']
    ]);

    $ticket_bought = $sql -> fetchColumn();

    return $capacity - $ticket_bought;
}

#user buy ticket
if($_SERVER['REQUEST_METHOD'] === 'POST'){

    if(empty($_POST['fixture_id']) || empty($_POST['ticket_type_id']) || empty($_POST['user_id'])){
        $err['status'] = 400;
        $err['message'] = "Fill all required fields";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }

    global $dbConn;

    $tickets_left = check_available_tickets();

    #if there is no available tickets
    if($tickets_left <= 0){
        $err['status'] = 401;
        $err['message'] = "Sold out";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }

    #insert ticket into db
    $sql = $dbConn -> prepare("INSERT INTO ticket(user_id,fixture_id, ticket_type_id) VALUES (:user_id, :fixture_id, :ticket_type_id);");

    try{
        $sql -> execute([
            "user_id" => $_POST['user_id'],
            "fixture_id" => $_POST['fixture_id'],
            "ticket_type_id" => $_POST['ticket_type_id']
        ]);

        $err['status'] = 200;
        $err['message'] = "Success";
        $err['error'] = false;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }catch (Exception $e){

        $err['status'] = 400;
        $err['message'] = "Failed";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }
}
