
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

// refers to dbconnect.php file where the connection is established.
include "./dbconnect.php";   

$userid 	= $_GET["userid"]; 
$result = mysql_query("SELECT * FROM purchases WHERE R_User_ID = '$userid'");


while($row = mysql_fetch_assoc($result))
  {
	$output[]=$row;
  }
print(json_encode($output));

?>