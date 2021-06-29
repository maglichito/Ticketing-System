<?php

require "db.php";

#validate fields
function validate(){
    global $errors;

    if(empty($_POST['firstname'])){
        return "Firstname is required";
    }

    if(empty($_POST['lastname'])){
        return "Lastname is required";
    }

    if(empty($_POST['email'])){
       return "Email is required";
    }

    if(empty($_POST['username'])){
        return "Username is required";
    }

    if(empty($_POST['password']) || strlen($_POST['password']) < 5 || strlen($_POST['password']) > 15){
        return "Password is required";
    }
}

#insert new user
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    global $dbConn;
    #check if username already exist in db
    if(isset($_POST['username'])){

        $username = $_POST['username'];

        $sql = $dbConn->prepare("select * from user where username = :username");
        $sql->execute([
            'username' => $_POST['username'],
        ]);

        $user_exist = $sql->fetchAll();

        if ($user_exist) {

            $error["status"] = "409";
            $error["message"] = "Username is already in use";
            $error["error"] = true;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }
    }

    #check if email already exist
    if(isset($_POST['email'])){

        $email = $_POST['email'];

        $sql = $dbConn -> prepare("select * from user where email = :email");
        $sql -> execute([
            'email' => $email,
        ]);

        $email_exists = $sql -> fetchAll();

        if($email_exists){
            $error["status"] = 409;
            $error["message"] = "Email is already in use";
            $error["error"] = true;

            $response["response"] = $error;

            echo json_encode($response);
            die();
        }
    }

    #validation successfull
    if(!validate()){
        global $dbConn;
        $sql = $dbConn->
        prepare("insert into user(first_name, last_name, username, email, password) values (:firstname, :lastname, :username, :email, :password)");
        try {
            $sql->execute([
                'firstname' => $_POST['firstname'],
                'lastname' => $_POST['lastname'],
                'username' => $_POST['username'],
                'email' => $_POST['email'],
                'password' => password_hash($_POST['password'], PASSWORD_BCRYPT),
            ]);

            $error["status"] = 200;
            $error["message"] = "Succcess";
            $error["error"] = false;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }catch (Exception $e){
            $error["status"] = 500;
            $error["message"] = "Error";
            $error["error"] = true;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }
    }else{
        #if not all fields filled
        $error["status"] = 405;
        $error["message"] = validate();
        $error["error"] = true;

        $response["response"] = $error;
        echo json_encode($response);
    }
}
