<?php

require "db.php";

if($_SERVER['REQUEST_METHOD'] === 'POST'){
    if(isset($_POST['id']) && isset($_POST['firstname']) && isset($_POST['lastname']) && isset($_POST['username']) && isset($_POST['email'])
        && strlen($_POST['id']) != 0 && strlen($_POST['firstname']) != 0 && strlen($_POST['lastname']) != 0
        && strlen($_POST['email']) != 0 && strlen($_POST['username']) != 0){

        global $dbConn;

        $id = $_POST['id'];
        $firstname = $_POST['firstname'];
        $lastname = $_POST['lastname'];
        $username = $_POST['username'];
        $email = $_POST['email'];

        #check if username is in use
        $sql = $dbConn -> prepare('select username from user where username = '.'\''.$username.'\' and id != '.$id);
        $sql -> execute();

        $exist_username = $sql -> fetchColumn();
        if($exist_username){
            $err['error'] = true;
            $err['message'] = "Username already exist";
            $err['status'] = "405";

            $response['response'] = $err;
            echo json_encode($response);
            die();
        }

        #check if email is already in use
        $sql = $dbConn -> prepare('select email from user where email ='.'\''.$email.'\''.'and id !='. $id);
        $sql -> execute();

        $exist_email = $sql -> fetchColumn();
        if($exist_email){
            $err['error'] = true;
            $err['message'] = "Email already exist";
            $err['status'] = "403";

            $response['response'] = $err;
            echo json_encode($response);
            die();
        }

        $sql = $dbConn
            -> prepare('update user set first_name ='.'\''.$firstname.'\''.' ,last_name ='.'\''.$lastname.'\''.' ,username ='.'\''.$username.'\''.',email ='.'\''.$email.'\''.' where id = '. $id);
        try{
            $sql -> execute();

            $err['error'] = false;
            $err['message'] = "Success";
            $err['status'] = "200";

            #return all for update
            $user = [];
            $user['id'] = $id;
            $user['firstname'] = $firstname;
            $user['lastname'] = $lastname;
            $user['username'] = $username;
            $user['email'] = $email;

            $response['response'] = $err;
            $response['user'] = $user;

            echo json_encode($response);
        }catch(Exception $e){

            $err['error'] = true;
            $err['message'] = "Failed";
            $err['status'] = "400";

            $response['response'] = $err;
            echo json_encode($response);
            die();
        }
    }else{
        #required fields not filled
        $err["status"] = "401";
        $err["message"] = "Fill required fields";
        $err["error"] = true;

        $response["response"] = $err;
        echo json_encode($response);
        die();
    }
}