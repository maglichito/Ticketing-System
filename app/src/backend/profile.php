<?php

require 'db.php';

if(isset($_SESSION['username'])){
    die();
}

if($_SERVER['REQUEST_METHOD'] === 'POST'){

    if(isset($_POST['old_password']) && isset($_POST['new_password']) && isset($_POST['id'])){
        global $dbConn;

        $sql = $dbConn -> prepare("select password from user where id = :id");
        $sql -> execute([
            "id" => $_POST['id'],
        ]);

        #uzeli smo polje password iz baze
        $pass = $sql -> fetchColumn();

        #provjera da li je sifra u redu
        if(password_verify($_POST['old_password'], $pass)){
            try{
                $sql = $dbConn -> prepare("update user set password = :password where id = :id");
                $sql -> execute([
                    'password' => password_hash($_POST['new_password'], PASSWORD_BCRYPT),
                    'id' => $_POST['id'],
                ]);
                $error["status"] = "200";
                $error["message"] = "Uspjesno ste promjenili lozinku!";
                $error["error"] = false;
                $response["response"] = $error;

                echo json_encode($response);
                die();
            }catch (Exception $e){
                #ukoliko dodje do greske pri upisu u bazu nove lozinke
                $error["status"] = "401";
                $error["message"] = "Lozinka nije promjenjena!";
                $error["error"] = true;
                $response["response"] = $error;

                echo json_encode($response);
                die();
            }
        }else{
            #korisnik je pogrijesio staru lozinku
            $error["status"] = "402";
            $error["message"] = "Lozinka nije tacna!";
            $error["error"] = true;
            $response["response"] = $error;

            echo json_encode($response);
            die();
        }
    }else{
        $error["status"] = "400";
        $error["message"] = "Morate popuniti obavezna polja!";
        $error["error"] = true;

        $response["response"] = $error;
        echo json_encode($response);
        die();
    }


}
