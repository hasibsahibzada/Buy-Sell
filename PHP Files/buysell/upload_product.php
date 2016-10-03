<?php

include "./dbconnect.php";   

 $date = new DateTime();
 $filename=$date->format('Ymd-His');
 $filename="productpictures/".$filename."img.jpg";
 
  $image = "http://141.54.152.162/buysell/".$filename;
  $pname=$_POST['Pname'];
  $price=$_POST['Price'];
  $desc=$_POST['Description'];
  $ownId=$_POST['Owner_ID'];
  $img=$_POST['image'];
  $longitude=$_POST['Longitude'];
  $latitude=$_POST['Latitude'];
  
  $binary=base64_decode($img);
  header('Content-Type: bitmap; charset=utf-8');
  $file = fopen($filename, 'wb');
  fwrite($file, $binary);
  fclose($file);

  $sql = "INSERT INTO product(Pname,Price,Description,Owner_ID,image,Latitude,Longitude) VALUES('$pname','$price','$desc','$ownId','$image','$latitude','$longitude')";
  mysql_query($sql);
  echo "Image upload complete!!";
?>
 