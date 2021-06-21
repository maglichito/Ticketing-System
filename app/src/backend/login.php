<?php

require "db.php";
global $response;
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    global $dbConn;
    $username = $_POST['username'];
    $pass = $_POST['password'];

    if(!$username || !$pass){
        $err["status"] = "400";
        $err["message"] = "Popunite obavezna polja!";
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

    if (!$user) {
        $err["status"] = "404";
        $err["message"] = "Korisnik ne postoji!";
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
        $err["message"] = "Uspjesno!";
        $err["error"] = false;

        $user_r["id"] = $user["id"];
        $user_r["username"] = $user["username"];
        $user_r["email"] = $user["email"];
        $user_r["firstname"] = $user["first_name"];
        $user_r["lastname"] = $user["last_name"];

        $response["response"] = $err;
        $response["user"] = $user_r;

        echo json_encode($response);
        die();
    }else{
        $err["status"] = "401";
        $err["message"] = "Lozinka nije tacna!";
        $err["error"] = true;

        $user = null;

        $response["response"] = $err;
        $response["user"] = $user;
        echo json_encode($response);
        die();
    }
}
