
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);

// refers to dbconnect.php file where the connection is established.
include "./dbconnect.php";   

$key = $_REQUEST['item'];


$result = mysql_query("SELECT * FROM product WHERE Pname LIKE '%$key%' OR Description LIKE '%$key%'" );

while($row = mysql_fetch_assoc($result))
  {
	$output[]=$row;
  }
print(json_encode($output));

?>