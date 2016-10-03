
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);


// create database related variables.
$dbhost = "localhost";
$dbuser = "root";
$dbpass = "123";
$dbdb = "buysell";
$tablename = "profile";

// connect to database
$connect = mysql_connect( $dbhost, $dbuser, $dbpass) or die ( "connection error");

// select the database
mysql_select_db($dbdb) or die ("database selection error");


?>