<?php

require "ticketing_api/db.php";
session_start();

if (!isset($_SESSION['username']) || $_SESSION['username'] != 'admin') {
    header("Location: admin_login.php");
    die();
}

if($_SERVER['REQUEST_METHOD'] === 'GET'){

    #if id is empty
    if(empty($_GET['id'])){
        die();
    }

    global $dbConn;

    #check if user is active or not
    $sql = $dbConn -> prepare("SELECT is_active FROM user WHERE id = :id  ");
    $sql -> execute([
        "id" => $_GET['id']
    ]);

    $isActive = $sql -> fetchColumn();

    #if user is_active == 0 then activate
    #if user is_active == 1 then deactivate
    if($isActive === 1){
        $sql = $dbConn -> prepare("UPDATE user SET is_active = 0 WHERE id = :id  ");
    }else{
        $sql = $dbConn -> prepare("UPDATE user SET is_active = 1 WHERE id = :id  ");
    }

    try{
        $sql -> execute([
            "id" => $_GET['id']
        ]);

        $err['message'] = "Success";
        $err['status'] = 200;
        $err['error'] = false;

        $response['response'] = $err;
        header("Location: admin_panel.php");
        echo json_encode($response);
    }catch (Exception $e){
        $err['message'] = "Failed";
        $err['status'] = 400;
        $err['error'] = true;

        $response['response'] = $err;
        header("Location: admin_panel.php");
        echo json_encode($response);
    }
}

