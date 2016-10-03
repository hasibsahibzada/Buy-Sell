
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

// refers to dbconnect.php file where the connection is established.
include "./dbconnect.php";   


$username = $_POST["username"];
$password = $_POST["password"];


// query the database

$query = mysql_query("SELECT * FROM profile WHERE username ='$username' AND password = '$password' ");

// check the results


$num = mysql_num_rows($query);
if ( $num == 1) {
$output = Array();   // creates the array in which the out put will be saved

// retrieve all the data from query

	while ($list=mysql_fetch_assoc($query))
	{
	
	// puts it in the bellow array variable
	$output[] = $list;
	
	// encode with JSON the array and send it to the android app
	
	print json_encode($output);
	}
	mysql_close();
}
else {
	$falsedata = Array();
	print json_encode($falsedata);
	mysql_close();
}
?>