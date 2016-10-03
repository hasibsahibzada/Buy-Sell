
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

include "./dbconnect.php"; 



$pname 		= 	$_POST["pname"];
$pprice 	= 	$_POST["pprice"];
$pdesc    	= 	$_POST["pdesc"];



$insert = "INSERT INTO product (Pname,Price,Description ) 
						VALUES ('$pname','$pprice','$pdesc')";
			

if (mysql_query($insert)) {
		
	print "success";
} 
else {
	print mysql_error();
} 
mysql_close();
			
?>