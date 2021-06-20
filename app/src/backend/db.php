<?php

$host = 'localhost';
$db   = 'id17092144_ticketing_system';
$user = 'id17092144_maglapanco';
$pass = '[Gug@>uTORK8]Fv7';
$charset = 'utf8mb4';
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $dbConn = new PDO("mysql:host=$host;dbname=$db;charset=$charset", $user, $pass, $options);

    //echo 'Veza sa bazom uspjesna';
} catch (Exception $e) {
    die('Baza nije dostupna: ' . $e->getMessage());
}

