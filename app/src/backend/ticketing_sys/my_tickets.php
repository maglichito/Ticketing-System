<?php

require "db.php";

if($_SERVER['REQUEST_METHOD'] === 'POST'){

    #check if user id is set
    if(empty($_POST['user_id'])){
        $err['status'] = 400;
        $err['message'] = "Fill all required fields";
        $err['error'] = true;

        $response['error'] = $err;
        echo json_encode($response);
        die();
    }

    global $dbConn;

    $sql = $dbConn -> prepare("SELECT t.id AS 'ticket_no', u.first_name AS 'firstname',
                                    u.last_name AS 'lastname',f.id AS 'fixture_id', f.round, f.stadium_fixture AS'stadium',
                                    f.date_fixture AS 'date', f.time_fixture AS 'time',
                                    c1.name AS 'home', c1.code AS 'home_code', c1.logo AS 'home_logo',
                                    c2.name AS 'away', c2.code AS 'away_code', c2.logo AS 'away_logo', 
                                    tt.stand, tt.price, tt.created_at 
                                    FROM user u, club c1, club c2, fixture f, ticket t, ticket_type tt 
                                    WHERE t.user_id = u.id AND
                                    u.id = :user_id AND 
                                    c1.id = f.club_host_id AND
                                    c2.id = f.club_away_id AND
                                    tt.id = t.ticket_type_id AND
                                    t.fixture_id = f.id");


    try{
        $sql -> execute([
            "user_id" => $_POST['user_id']
        ]);

        $results = $sql -> fetchAll();

        if($results){
            $err['status'] = 200;
            $err['message'] = "Success";
            $err['error'] = false;

            $response['error'] = $err;
            $response['response'] = $results;
        }else{
            $err['status'] = 404;
            $err['message'] = "You have not bought any tickets yet";
            $err['error'] = true;

            $response['error'] = $err;
            $response['response'] = $err['message'];
        }

        echo json_encode($response);
        die();
    }catch (Exception $e){
        $err['status'] = 400;
        $err['message'] = "Failed";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = null;
        echo json_encode($response);
        die();
    }
}






