
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

include "./dbconnect.php"; 



$p_id_string 		= $_POST["pid"];     // product id
$p_id 				= (int)$p_id_string;
$p_name 			= $_POST["pname"];   // product name
$userid 			= $_POST["userid"];  // purchaser ID
$status 			= "pending";		// default is pending
$date = date('Y-m-d H:i:s'); 


// get product owner
$getownerid = mysql_query("SELECT Owner_ID from product WHERE Pid = '$p_id'");
									
$getrowowner = mysql_fetch_array($getownerid);
$Owner_ID = $getrowowner["Owner_ID"];


// get owner name
$getownername = mysql_query("SELECT Name from profile WHERE uid = '$Owner_ID'");									
$getrowpname = mysql_fetch_array($getownername);
$Owner_name = $getrowpname["Name"];



$insert = mysql_query("INSERT INTO purchases (P_id,Pname,Owner_ID,Owner_name,R_User_ID,status)
									VALUES ('$p_id','$p_name','$Owner_ID','$Owner_name','$userid','$status')");


if (mysql_query($insert)) {
		print "success";
		} 
	else {
		print mysql_error();
	} 
mysql_close();
			
?>