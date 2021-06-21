<?php

require 'db.php';

session_start();

if (!isset($_SESSION["username"])) {
    die();
}

#vraca sve ekipe
function get_teams(): array
{
    global $dbConn;
    $sql = $dbConn->prepare("select c.id, c.name as 'club', s.name as 'stadium', s.city from club c, stadium s where c.stadium_id = s.id");
    $sql->execute();

    return $sql->fetchAll();
}

#vraca sve utakmice u jednom kolu
function get_rounds() :array
{
    $all_teams = get_teams();
    $counter = 0;
    $fixtures = [];

    //generisanje svih utakmica u jedan niz ($fixtures)
    for ($i = 0; $i < count($all_teams); $i++) {
        for ($j = 0; $j < count($all_teams); $j++) {
            if ($i !== $j) {
                $fixture['home'] = $all_teams[$i];
                $fixture['away'] = $all_teams[$j];
                $fixtures[$counter] = $fixture;
                $counter++;
            }
        }
    }

    $fixtures_for_single_round = []; //utakmice u jednom kolu
    $team_played = []; //klubovi koji su vec izabrani da igraju

    #kreiranje rasporeda za kolo
    while (count($fixtures_for_single_round) < 6) {
        $br = rand(0, count($fixtures) - 1);
        if (!in_array($fixtures[$br]['home'], $team_played) && !in_array($fixtures[$br]['away'], $team_played)) {
            array_push($team_played, $fixtures[$br]['home']); //dodajemo domacina
            array_push($team_played, $fixtures[$br]['away']); //dodajemo gosta
            array_push($fixtures_for_single_round, $fixtures[$br]); //update utakmica za jedno kolo
            array_splice($fixtures, $br, 1); //uklonimo izabranu utakmicu
        }
    }

    return $fixtures_for_single_round;
}

#vraca kompletan raspored
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    global $fixtures;
    $round = 0; //broj kola
    $date = date_create("2021-08-15"); //datum pocetka sezone
    $time = new DateTime("15:00"); //vrijeme utakmice
    $time = date_format($time, "H:i"); //formatiranje vremena
    $all_rounds = []; //raspored utakmica za sezonu
    while($round < 24){
        $all_rounds[$round]['round'] = $round + 1; //setujemo kolo
        $all_rounds[$round]['date'] = date_format($date, "d-m-Y"); //setujemo datum
        $all_rounds[$round]['time'] = $time; //setujemo vrijeme
        $all_rounds[$round]['fixtures'] = get_rounds(); //setujemo utakmice za dato kolo
        $date->modify('+7 day'); //povecaj datum
        $round++; //povecaj broj kola
    }

    echo json_encode($all_rounds);
}
