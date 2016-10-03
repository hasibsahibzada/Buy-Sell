
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

include "./dbconnect.php"; 



$name 		= $_POST["name"];
$lastname 	= $_POST["lastname"];
$age    	= $_POST["age"];
$email    	= $_POST["email"];
$pid    	= $_POST["pid"];


$search = mysql_query("SELECT * FROM profile WHERE username ='$username'");


$insert = "UPDATE profile SET Name ='$name',lastname ='$lastname',age ='$age',
			emailaddr= '$email' WHERE uid ='$pid'";


if (mysql_query($insert)) {
	print "success";
	
	} 
else {		
print mysql_error();

mysql_close();

}				
?>