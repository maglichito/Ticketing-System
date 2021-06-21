<?php

require "db.php";

#validacija polja
function validate(){
    global $errors;
    if(empty($_POST['firstname'])){
        return "Ime je obavezno polje!";
    }

    if(empty($_POST['lastname'])){
        return "Prezime je obavezno polje!";
    }

    if(empty($_POST['email'])){
       return "Email je obavezno polje!";
    }

    if(empty($_POST['username'])){
        return "Korisnicko ime je obavezno polje!";
    }

    if(empty($_POST['password']) || strlen($_POST['password']) < 5 || strlen($_POST['password']) > 15 ){
        return "Lozinka je obavezno polje!";
    }
}

#upisivanje u bazu novog korisnika
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    global $dbConn;
    #provjera da li se username vec koristi
    if(isset($_POST['username'])){

        $username = $_POST['username'];

        $sql = $dbConn->prepare("select * from user where username = :username");
        $sql->execute([
            'username' => $_POST['username'],
        ]);

        $user_exist = $sql->fetchAll();

        if ($user_exist) {

            $error["status"] = "409";
            $error["message"] = "Korisnicko ime je u upotrebi!";
            $error["error"] = true;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }
    }

    #provjera da li se email vec koristi
    if(isset($_POST['email'])){

        $email = $_POST['email'];

        $sql = $dbConn -> prepare("select * from user where email = :email");
        $sql -> execute([
            'email' => $email,
        ]);

        $email_exists = $sql -> fetchAll();

        if($email_exists){
            $error["status"] = "409";
            $error["message"] = "Email je u upotrebi!";
            $error["error"] = true;

            $response["response"] = $error;

            echo json_encode($response);
            die();
        }
    }

    #ukoliko nema gresaka korisnik ce se registrovati
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

            $error["status"] = "200";
            $error["message"] = "Uspjesna registracija!";
            $error["error"] = false;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }catch (Exception $e){
            $error["status"] = "500";
            $error["message"] = "Doslo je do greske!";
            $error["error"] = true;

            $response["response"] = $error;
            echo json_encode($response);
            die();
        }
    }else{
        #ako nisu popunjena sva polja
        $error["status"] = "405";
        $error["message"] = validate();
        $error["error"] = true;

        $response["response"] = $error;
        echo json_encode($response);
    }
}
