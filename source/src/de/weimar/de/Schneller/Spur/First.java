package de.weimar.de.Schneller.Spur;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class First extends Activity{
	AsyncTaskRunner runner = new AsyncTaskRunner();// background process
	String gps_add = null;// GPS stuffs
	int flag_count=0;
	Integer loadtime = 10;
	DBAdapter dbAdapter;// for accessing data adaptor class
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.map);   // sets the content of first from first.xml
	    	// for network strict mode of android
    		if (android.os.Build.VERSION.SDK_INT > 9) {
    			StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
    			StrictMode.setThreadPolicy(policy);
    		}
        	// synchronization
	        dbAdapter = new DBAdapter(this);
	    	dbAdapter.open();
	}   
	
	// load location function
	public void load_current_location(){
         GoogleMap mapnew = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
         Double Clatitude= null,Cmagnitude=null;
				    		  // gps related things
				    		    LocationManager mlocManager=null; 
				    		    LocationListener mlocListener; 
				    		    mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
				    		    mlocListener = new MyLocationListener(); 
				    		    mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);							
				  
								if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
																		
					            	 if(MyLocationListener.latitude>0) {	
					            		 
					            		 Clatitude = MyLocationListener.latitude;
					            		 Cmagnitude = MyLocationListener.longitude;
					            		 
					            		 // sets the address on the gps_add textview.
					            		 
							              gps_add = Clatitude+"-"+Cmagnitude;  // must be sent to the server
							              loadtime = 1*60*1000; 
		              
									      // Get a handle to the Map Fragment
							              LatLng mycurrentlocation = new LatLng(MyLocationListener.latitude,MyLocationListener.longitude);
							       
							              // put current location pin
							              mapnew.setMyLocationEnabled(true);
							              
							              // zoom the camera 
							              // note: (2.0-21.0) values from zoom out to zoom in 
							              mapnew.moveCamera(CameraUpdateFactory.newLatLngZoom(mycurrentlocation, 11.0F));

							              
							          	// Draws a Circle 
						            	  	  mapnew.addCircle(new CircleOptions()
							        	     .center(new LatLng(Clatitude, Cmagnitude))
							        	     .radius(10000)     // in meters
							        	     .strokeColor(Color.RED)
							        	     .fillColor(Color.TRANSPARENT));
							              
							        		 
							        		 try {
							        			 SocketAddress sockaddr = new InetSocketAddress("192.168.2.15",80);					        			   // Checks Network Connectivity Start
									    		 Socket sock = new Socket();   // Create an unbound socket
									    		 // This method will block no more than timeoutMs.
									    		 // If the timeout occurs, SocketTimeoutException is thrown.
									    		 int timeoutMs = 2000;   // 2 seconds
									    		 sock.connect(sockaddr, timeoutMs);
									    		 sock.close();
									    		 // Checks Network Connectivity End
									    		 	new GetAllProductList().execute(new ApiConnector());// runs the class to load the products
									     			flag_count=0;//for offline data to reload
							        		 }catch(Exception e){
							        			 Toast.makeText(getBaseContext(), "Server Connection problem... .", Toast.LENGTH_LONG).show();  
							        			 // load the cached database
							 	            	if(flag_count==0)
							 	            		loadcachedproducts();
							 	            	flag_count++;
							        		}
							         	}
						            else  
						             {  
						            	// Toast.makeText(getBaseContext(), "Searching for location ...", Toast.LENGTH_SHORT).show();  
						             }  
						          } 
								
								else {  
						              Toast.makeText(getBaseContext(), "GPS is turned Off, Please switch it On !", Toast.LENGTH_LONG).show(); 
						          }  	  
	 					}

	 
	 // download iamges
	 public Bitmap getBitmapFromURL(String imageUrl) {
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	 
	 // Get Product class
	 private class GetAllProductList extends AsyncTask<ApiConnector,Long,JSONArray>
	    {
       @Override
	        protected JSONArray doInBackground(ApiConnector... params) {
    	   		// it is executed on Background thread
    	   		return params[0].GetAllProducts(gps_add);
	        }

	        @Override
	        protected void onPostExecute(JSONArray jsonArray) {
	        	GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();// Access map
	        	//	Toast.makeText(getBaseContext(),"google maped accessed" , Toast.LENGTH_LONG).show(); 
	        	// put the products on each marker
	        	try {       		
	        		// Delete the cach of the phone
 	        		resizeDB();
	        	    int cnt=0;
 	        		for(int i=0;i<jsonArray.length();i++){
 	        			JSONObject product = jsonArray.getJSONObject(i);	// create the product object and access it
	 	        		LatLng productposition = new LatLng(product.getDouble("Latitude"),product.getDouble("Longitude"));// Access the product position and store it to the variable
	 	        		Bitmap.Config conf = Bitmap.Config.ARGB_8888;// frame and download the image
	 	        		Bitmap bmp = Bitmap.createBitmap(8, 8, conf);
		 	        	bmp = getBitmapFromURL(product.getString("image"));
 	        	 		Bitmap bhalfsize=Bitmap.createScaledBitmap(bmp, 80,80 , false);
	 	        		// create a marker for the product
	 	        		map.addMarker(new MarkerOptions()
	        	          .title(product.getString("Pname"))
	        	          .snippet(product.getString("Pid"))
	        	          .position(productposition)       // product position set on map
	        	          .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize)));
	        		
	 	        		
	 	        		cnt =dbAdapter.dbAllCount();
	 	        		//adding Product in SQLITE
	 	        		if (i<20-cnt){
	 	        		ByteArrayOutputStream stream = new ByteArrayOutputStream();
	 	        		bhalfsize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	 	        		byte[] imageInByte = stream.toByteArray();
	 	        		dbAdapter.register(product.getString("Pname"),product.getString("Price"),product.getString("Description"),product.getString("Owner_ID"),imageInByte,product.getDouble("Latitude"),product.getDouble("Longitude"),1);
	 	        		}
	 	        		
 		            }
		 
	        		 map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
        		        @Override
	        		        public void onInfoWindowClick(Marker marker) {
	        		        	// cancel the background task
	        		        	runner.cancel(true);
	        		        	// send Product ID.
	                			Intent show_product_detail = new Intent(getApplicationContext(),Product_Detail.class);
	                			show_product_detail.putExtra("Pid", Integer.parseInt(marker.getSnippet()));	            
	                			startActivity(show_product_detail);
	                		}
	        		    });
   		 
	        		}catch(JSONException e) {
	        			e.printStackTrace();
			       //  Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show(); 
	        		}
        	 }
	    }

	 
	 // load cashed products
	 public void loadcachedproducts(){
				// Access map
		  		  GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					ArrayList<ArrayList<NameValuePair>> wordlist=dbAdapter.loadcachedproducts();	
					for(int i=0;i<wordlist.size();i++)
					{
						ArrayList<NameValuePair> initialValues=wordlist.get(i);
						Bitmap.Config conf = Bitmap.Config.ARGB_8888;				// frame and download the image
	            		Bitmap bmp = Bitmap.createBitmap(8, 8, conf);
	 	        		byte[] img=Base64.decode(initialValues.get(5).getValue().toString(),Base64.DEFAULT);
	 	        		bmp = BitmapFactory.decodeByteArray(img , 0, img .length);
	 	        		Bitmap bhalfsize=Bitmap.createScaledBitmap(bmp, 80,80 , false);

	 	        		// create the gps location 
	 	        		LatLng productposition = new LatLng(Double.parseDouble(initialValues.get(6).getValue().toString()),Double.parseDouble(initialValues.get(7).getValue().toString()));
 		  
	 	        		// create a marker for the product
	 	        		map.addMarker(new MarkerOptions()
	 	        		.title(initialValues.get(1).getValue())
	 	        		.snippet(initialValues.get(0).getValue())
	 	        		.position(productposition)       // product position set on map
	 	        		.icon(BitmapDescriptorFactory.fromBitmap(bhalfsize)));
	 	
					}
	
					 map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	        		        @Override
		        		        public void onInfoWindowClick(Marker marker) {
		        		        	// cancel the background task
		        		        	runner.cancel(true);
		        		        	
		        		        	// send Product ID.
		                			Intent show_product_detail = new Intent(getApplicationContext(),Product_Detail.class);
		                			show_product_detail.putExtra("Pid", Integer.parseInt(marker.getSnippet()));	            
		                			startActivity(show_product_detail);
		                		}
		        		    });
	 
	 }
	 
	 // Background tasks
	 private class AsyncTaskRunner extends AsyncTask<String, String, String > {

		 @Override
		  protected String doInBackground(String... params) {  
		 	try{
		 		
		 		Thread.sleep(loadtime); //1000 is to change the milisecond to second
		 	}catch(InterruptedException e){
		 		e.printStackTrace();
		 	}
	   	    return null;
		  }
		  
		 @Override
		  protected void onPostExecute(final String map) {
		   // execution of result of Long time consuming operation
			 runOnUiThread(new Runnable() {
				    public void run() {
				    	load_current_location();  
				    }
				});
			  
			// run the same type task again. 
				  runner = new AsyncTaskRunner();
		 		  runner.execute();
		  }
	
		  @Override
		  protected void onPreExecute() {}

		  protected void onProgressUpdate(String... text) {}
	}
	 
	
	 // on resume action
	  @Override
		protected void onResume() {
			super.onResume();
			 loadtime = 10;
			 runner = new AsyncTaskRunner();
	 		 runner.execute();
			
		}
	 
	 
     // my option menu
     @Override
 	public boolean onCreateOptionsMenu (Menu menu){
 		MenuInflater inflater = getMenuInflater();
 		// Get user session
 		String userid = null;

 		SharedPreferences usersession = getSharedPreferences("usersession", 0); 
 		userid = usersession.getString("userid",null);
 		
 		//if guest
 		
 		if (userid == null) {    
 			inflater.inflate(R.menu.public_menu, menu);
 			return true;
 		}else   // if logged in user
 		{
 			inflater.inflate(R.menu.user_menu, menu);
 			return true;
 		}
 	}
	 
	 // option menu
     @Override
      public boolean onOptionsItemSelected(MenuItem item){
      	
      	switch(item.getItemId()) {
  		
      	case R.id.login:
      	// cancel the background task
        	runner.cancel(true);
      		Intent loginintent = new Intent(First.this,Login.class); // 
  			startActivity(loginintent);
  			break;
  			
  			
      	case R.id.register:
      	// cancel the background task
        	runner.cancel(true);
      		Intent registerintent = new Intent(First.this,register.class); // 
  			startActivity(registerintent);
  			break;
  			
      	case R.id.search_box:
      	// cancel the background task
        	runner.cancel(true);
  			Intent searchintent = new Intent(First.this,Search.class); // 
  			startActivity(searchintent);
  			break;
      	case R.id.help:
      	// cancel the background task
        	runner.cancel(true);
        	loadtime=10;
        	break;	
			
      	case R.id.sell:
      	// cancel the background task
        	runner.cancel(true);
			Intent sellintent = new Intent(First.this,sell.class); // 
			startActivity(sellintent);
			break;
  			
      	case R.id.Main:
      	// cancel the background task
        	runner.cancel(true);
			Intent mainmenuintent = new Intent(First.this,Products.class); // 
			startActivity(mainmenuintent);
			break;
			
      	case R.id.profile:
      	// cancel the background task
        	runner.cancel(true);
			Intent profileintent = new Intent(First.this,profile.class); // 
			startActivity(profileintent);
			break;
			
      	case R.id.purchases_Menu:
          	// cancel the background task
            	runner.cancel(true);
    			Intent purchases_Menuintent = new Intent(First.this,Purchases.class); // 
    			startActivity(purchases_Menuintent);
    			break;
			
      	case R.id.Request_Menu:
          	// cancel the background task
            	runner.cancel(true);
    			Intent Request_Menu_intent = new Intent(First.this,requests.class); // 
    			startActivity(Request_Menu_intent);
    			break;
			
			
      	case R.id.logout:
      
      	// cancel the background task
        	runner.cancel(true);	
  			// create a shared preference 
  			SharedPreferences usersession = getSharedPreferences("usersession", 0);
  			  
  		    // Edit the shared preference
  			SharedPreferences.Editor spedit = usersession.edit();
  			    
  			// Cleans the userid string to null
  			spedit.putString("userid","" );

  			  // Commits the changes and closes the editor
  			  spedit.commit();
  			  
  			//Takes the user to the login screen and displays log out message  
  			Intent logoutintent = new Intent(First.this,Products.class); // 
  			startActivity(logoutintent);   // starts the BuySell activity  
  			Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
  			
  			break;

  			
      	}
      	
      
      	return true;
      }
	 
	// resize the database
	public void resizeDB()
	{
		int count=dbAdapter.dbAllCount();
		if (count>10) 
		{
			ArrayList<String> keyList=dbAdapter.selectid();
			for(int i=0;i<keyList.size();i++)
			{
				Log.d("my","key deleted"+keyList.get(i));
				dbAdapter.deleteRow(keyList.get(i));
			}
		}
	}
	
}
