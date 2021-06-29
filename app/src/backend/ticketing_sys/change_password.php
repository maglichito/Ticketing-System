<?php

require 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    #id not set
    if (!isset($_POST['id'])) {
        die();
    }

    #user changes password
    if (isset($_POST['old_password']) && isset($_POST['new_password'])) {
        global $dbConn;

        $sql = $dbConn->prepare("select password from user where id = :id");
        $sql->execute([
            "id" => $_POST['id'],
        ]);

        #take password field from db
        $pass = $sql->fetchColumn();

        #check password verification
        if (password_verify($_POST['old_password'], $pass)) {
            try {
                $sql = $dbConn->prepare("update user set password = :password where id = :id");
                $sql->execute([
                    'password' => password_hash($_POST['new_password'], PASSWORD_BCRYPT),
                    'id' => $_POST['id'],
                ]);

                $error["status"] = "200";
                $error["message"] = "Success";
                $error["error"] = false;
                $response["response"] = $error;

                echo json_encode($response);
                die();
            } catch (Exception $e) {
                #db error while updating password
                $error["status"] = "401";
                $error["message"] = "Fail";
                $error["error"] = true;
                $response["response"] = $error;
                echo json_encode($response);
                die();
            }
        } else {
            #user mistakes old password
            $error["status"] = "402";
            $error["message"] = "Password is incorrect";
            $error["error"] = true;
            $response["response"] = $error;
            echo json_encode($response);
            die();
        }
    } else {
        #required fields not filled
        $error["status"] = "400";
        $error["message"] = "Fill required fields";
        $error["error"] = true;

        $response["response"] = $error;
        echo json_encode($response);
        die();
    }
}
