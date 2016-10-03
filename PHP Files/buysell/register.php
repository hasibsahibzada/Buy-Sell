
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

include "./dbconnect.php"; 



$name 		= $_POST["name"];
$lastname 	= $_POST["lastname"];
$age    	= $_POST["age"];
$username 	= $_POST["username"];
$password 	= $_POST["password"];
$gender 	= $_POST["gender"];

$search = mysql_query("SELECT * FROM profile WHERE username ='$username'");


$insert = "INSERT INTO profile (Name,lastName,age,Gender,username,password ) 
						VALUES ('$name','$lastname','$age','$gender','$username','$password')";
			

$num = mysql_num_rows($search);
if ( $num == 1) {
	//print "the username exists";
	print $username;
	mysql_close();
}

else {
		if (mysql_query($insert)) {
		print "success";
		} 
		else {
		
		print mysql_error();
	} 
mysql_close();

}				
?>