<?php

require "db.php";

#this scirpt can only be called by administrator
session_start();

if ($_SESSION['username'] !== 'admin' || empty($_SESSION['username'])) {
    die();
}

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    global $dbConn;

    #check if there is data in table club
    $sql = $dbConn->prepare("select * from club");
    $sql->execute();
    $data_in_db = $sql->fetchAll();

    #if there is no data in db fill data
    if (!$data_in_db) {
        $ch = curl_init();

        $url = "https://api-football-v1.p.rapidapi.com/v2/teams/league/3456";

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $headers = [
            "x-rapidapi-key: 005c15c4aamsh857af94067ca918p11535ejsnf9dffe63a1bf",
            "x-rapidapi-host: api-football-v1.p.rapidapi.com"
        ];

        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        $resp = curl_exec($ch);

        if ($e = curl_error($ch)) {
            echo $e;
        } else {
            $response = json_decode($resp, true);
            $api = $response['api'];
            $all_teams = $api['teams'];
            for ($i = 0; $i < count($all_teams); $i++) {

                #data for stadium
                $stadium_name = $all_teams[$i]['venue_name']; #stadium name
                $city = $all_teams[$i]['venue_city']; #stadium city
                $stadium_capacity = $all_teams[$i]['venue_capacity']; #stadium capacity

                #insert stadium first as there is no foreign key in table stadium
                $sql = $dbConn->prepare('insert into stadium(name, city, capacity) values (:name, :city, :capacity);');
                try {
                    $sql->execute([
                        "name" => $stadium_name,
                        "city" => $city,
                        "capacity" => $stadium_capacity

                    ]);
                } catch (Exception $e) {
                    echo $e;
                    die();
                }

                #club data
                $id = $all_teams[$i]['team_id']; #clubs id
                $name = $all_teams[$i]['name']; #clubs name
                $code = $all_teams[$i]['code']; #clubs code

                #if API doesnt return clubs code
                if($code == null){
                    $arr_name = explode(" ", $name);
                    if(count($arr_name) === 1){
                        $code = strtoupper(substr($name, 0, strlen($name) - (strlen($name) - 3)));
                    }else{
                        $final_code = '';
                        $first_word = $arr_name[0];
                        $second_word = $arr_name[1];
                        #first letter from clubs name
                        $final_code .= strtoupper(substr($first_word, 0, strlen($first_word) - (strlen($first_word) - 1)));
                        #first two letters from the second word
                        $final_code .= strtoupper(substr($second_word, 0, strlen($second_word) - (strlen($second_word) - 2)));
                        $code = $final_code;
                    }
                 }

                $stadium = $all_teams[$i]['venue_name']; #clubs stadium
                $logo = $all_teams[$i]['logo']; #clubs logo

                $sql = $dbConn->prepare('insert into club(id, name, code, stadium_name, logo) values (:id, :name, :code, :stadium, :logo);');
                try {
                    $sql->execute([
                        "id" => $id,
                        "name" => $name,
                        "code" => $code,
                        "stadium" => $stadium,
                        "logo" => $logo,
                    ]);
                } catch (Exception $e) {
                    echo $e;
                    die();
                }
            }
        }
    } else {
        $err['status'] = 400;
        $err['message'] = "Database is already filled.";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }

#check if there is data in fixture table
    $sql = $dbConn->prepare("select * from fixture");
    $sql->execute();
    $data_in_db = $sql->fetchAll();

#if there is no fixtures in db fill table fixture
    if (!$data_in_db) {

        $ch = curl_init();

        $url = "https://api-football-v1.p.rapidapi.com/v2/fixtures/league/3456";

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $headers = [
            "x-rapidapi-key: 005c15c4aamsh857af94067ca918p11535ejsnf9dffe63a1bf",
            "x-rapidapi-host: api-football-v1.p.rapidapi.com"
        ];

        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        $resp = curl_exec($ch);

        if ($e = curl_error($ch)) {
            echo $e;
        } else {
            $response = json_decode($resp, true);
            $api = $response['api'];
            $fixtures = $api['fixtures']; #iscupamo sve utakmice
            for ($i = 0; $i < count($fixtures); $i++) {

                #fixture data
                $home = $fixtures[$i]['homeTeam']['team_id']; #vraca nam id domacina
                $away = $fixtures[$i]['awayTeam']['team_id']; #vraca nam id gosta
                $round = explode(" - ", $fixtures[$i]['round'])[1]; #vraca nam Regular Season - 1
                $stadium = $fixtures[$i]['venue']; #vraca nam naziv stadiona
                $date = explode("T", $fixtures[$i]['event_date'])[0]; #vraca nam datum
                $time = explode("T", $fixtures[$i]['event_date'])[1]; #vraca nam vrijeme

                try {
                    $sql = $dbConn->prepare("INSERT INTO fixture (club_host_id, club_away_id, round, stadium_fixture, date_fixture, time_fixture) VALUES (:club_host_id, :club_away_id, :round, :stadium_fixture, :date_fixture, :time_fixture);");
                    $sql->execute([
                        "club_host_id" => $home,
                        "club_away_id" => $away,
                        "round" => $round,
                        "stadium_fixture" => $stadium,
                        "date_fixture" => $date,
                        "time_fixture" => $time,
                    ]);

                } catch (Exception $e) {
                    echo json_encode($e);
                    die();
                }
            }
        }
    } else {
        $err['status'] = 402;
        $err['message'] = "Data is already filled.";
        $err['error'] = true;

        $response['error'] = $err;
        $response['response'] = $err['message'];
        echo json_encode($response);
        die();
    }
    $err['status'] = 200;
    $err['message'] = "Success";
    $err['error'] = false;

    $response['error'] = $err;
    $response['response'] = $err['message'];
    echo json_encode($response);
    die();
}



