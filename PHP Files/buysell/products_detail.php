
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

// refers to dbconnect.php file where the connection is established.
include "./dbconnect.php";   

$pid = $_REQUEST['Pid'];

$result = mysql_query("SELECT * FROM product WHERE Pid = '$pid'") or die ("Error in query");

while($row = mysql_fetch_assoc($result))
  {
	$output[]=$row;
  }
print(json_encode($output));

?>