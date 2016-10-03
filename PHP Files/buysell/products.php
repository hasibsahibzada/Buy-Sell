
<?php
error_reporting( E_All ^ E_NOTICE ^ E_WARNING);
// refers to dbconnect.php file where the connection is established.
include "./dbconnect.php";   

// haversine class to measure the distance between two gps locations.
class POI {
    private $latitude;
    private $longitude;
	function __construct($latitude, $longitude) {
        $this->latitude = deg2rad($latitude);
        $this->longitude = deg2rad($longitude);
    }
     function getLatitude(){

      return $this->latitude;
      
     }
	 
	 function getLongitude() { 
	 
	 return $this->longitude;
	 
	 }
     
     function getDistanceInMetersTo(POI $other) {
        $radiusOfEarth = 6371000;// Earth's radius in meters.
        $diffLatitude = $other->getLatitude() - $this->latitude;
        $diffLongitude = $other->getLongitude() - $this->longitude;
        $a = sin($diffLatitude / 2) * sin($diffLatitude / 2) +
            cos($this->latitude) * cos($other->getLatitude()) *
            sin($diffLongitude / 2) * sin($diffLongitude / 2);
        $c = 2 * asin(sqrt($a));
        $distance = $radiusOfEarth * $c;
        return $distance;
    }
}


// fitches the gps location from Mobile
$GPS_Add  = $_Get['current_gps'];

// splits the gps add to latitude and longitude
$GPS_Add_Split = explode("-", $GPS_Add);

$latitude  = doubleval($GPS_Add_Split[0]);		// the latitude value is taken and changed to double at the same time
$Longitude = doubleval($GPS_Add_Split[1]);		// the longitude value is taken and changed to double at the same time



// Current User possition
$user_location = new POI($latitude,$Longitude);


// array used to store the query.

$output =  array();


// query the products from the database
$result = mysql_query("SELECT * FROM product");

while($row = mysql_fetch_assoc($result))
  {
  
// Product gps location 
 $latTo = doubleval($row['Latitude']);  		// the latitude value is taken and changed to double at the same time
 $lognTo = doubleval($row['Longitude']);  	// the longitude value is taken and changed to double at the same time

	//Product location sent to function
 $poi = new POI($latTo, $lognTo);
 
	 $distance = $user_location->getDistanceInMetersTo($poi);   // finds the distance between the ponts , (user, product)  in meter
 
   if ($distance <8000000)    // if the product is between 0 and 2km
	  {
  //put that data to the array if in range (0,2) km
  $output[]=$row;
  
   	   }
  
}
  
  
 print(json_encode($output));

?>