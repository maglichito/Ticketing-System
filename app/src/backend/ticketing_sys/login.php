<?php

require "db.php";
global $response;
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    global $dbConn;
    $username = $_POST['username'];
    $pass = $_POST['password'];

    if(!$username || !$pass){
        $err["status"] = "400";
        $err["message"] = "Fill required fields";
        $err["error"] = true;
        $response["response"] = $err;
        $response["user"] = null;
        echo json_encode($response);
        die();
    }

    #if user is banned
    $statement = $dbConn->prepare("select is_active from user where username = :username");
    $statement->execute([
        'username' => $_POST['username'],
    ]);
    $is_active = $statement -> fetchColumn();
    if($is_active === 0){
        $err["status"] = "402";
        $err["message"] = "User is banned, please contact support.";
        $err["error"] = true;

        $response["response"] = $err;
        $response["user"] = null;

        echo json_encode($response);
        die();
    }

    $statement = $dbConn->prepare("select * from user where username = :username and is_active = 1");
    $statement->execute([
        'username' => $_POST['username'],
    ]);

    $user = $statement->fetch();

    #if there is no user with such username
    if (!$user) {
        $err["status"] = "404";
        $err["message"] = "User not found";
        $err["error"] = true;

        $response["response"] = $err;
        $response["user"] = null;

        echo json_encode($response);
        die();
    }

    $password = $user['password'];

    if(password_verify($pass, $password)){

        session_start();
        $_SESSION['username'] = $_POST['username'];

        $err["status"] = "200";
        $err["message"] = "Success";
        $err["error"] = false;

        $user_r["id"] = $user["id"];
        $user_r["username"] = $user["username"];
        $user_r["email"] = $user["email"];
        $user_r["firstname"] = $user["first_name"];
        $user_r["lastname"] = $user["last_name"];

        $response["response"] = $err;
        $response["user"] = $user_r;
    }else{
        $err["status"] = "401";
        $err["message"] = "Password is incorrect";
        $err["error"] = true;

        $user = null;

        $response["response"] = $err;
        $response["user"] = $user;
    }
    echo json_encode($response);
    die();
}
