package de.weimar.de.Schneller.Spur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class profile extends Activity {

	EditText Name,lastname,age,emailadd;
	TextView genderview,username;
	Button save;
	

	// 1: Create HTTPCLient as the form container
	HttpClient httpclient;
	
	// 2: Use HTTP Post method
	HttpPost httppost;
	
	// 3: Create an array list for the input data to be sent
	ArrayList<NameValuePair> nameValuePairs;
	
	// 4: Create a HTTP Response and HTTP Entity
	HttpResponse response;
	HttpEntity	entity;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		// Array to be sent to Server script file for profile query
		ArrayList<NameValuePair> useridarray;
	
		
		// Profile view data
		final EditText Name 		= (EditText) findViewById(R.id.profile_name_view);
		final EditText lastname 	= (EditText) findViewById(R.id.profile_lname_view);
		final EditText age 			= (EditText) findViewById(R.id.Profile_age_view);
		final EditText emailadd 	= (EditText) findViewById(R.id.Profile_emailaddr_view);
		TextView username 			= (TextView) findViewById(R.id.Profile_uname_View);
		TextView genderview 		= (TextView) findViewById(R.id.Profile_gender_view);
		Button save 				= (Button) findViewById(R.id.Profile_Save_View);
	    
		final String userid;  // for user id 
		
		// Gets the userid
		SharedPreferences usersession = getSharedPreferences("usersession", 0); 
		userid = usersession.getString("userid","Session value not fetched");
		
		
		
		// create new default HTTPClient
		 httpclient = new DefaultHttpClient();
   	  
		// Create new HTTP POST with URL of our .php file
		 httppost = new HttpPost("http://141.54.152.162/buysell/profile.php");
		 
   	  try {
   		  
   		// for network strict mode of android
  		if (android.os.Build.VERSION.SDK_INT > 9) {
  			StrictMode.ThreadPolicy policy = 
  				new StrictMode.ThreadPolicy.Builder().permitAll().build();
  			StrictMode.setThreadPolicy(policy);
  		}
   		  
   		  
   		    // Checks Network Connectivity Start
   		    SocketAddress sockaddr = new InetSocketAddress("141.54.152.162",80);
   		    // Create an unbound socket
   		    Socket sock = new Socket();

   		    // This method will block no more than timeoutMs.
   		    // If the timeout occurs, SocketTimeoutException is thrown.
   		    int timeoutMs = 2000;   // 2 seconds
   		    sock.connect(sockaddr, timeoutMs);
   		
   		    sock.close();
   		    // Checks Network Connectivity End
   		  
   		  
   		    // set data for Sending
   		    useridarray = new ArrayList<NameValuePair>();
   		  
   		    // place the username and password to array.
   		    useridarray.add(new BasicNameValuePair("userid", userid));
   		 
   		    // Add array list to http post
   		    httppost.setEntity(new UrlEncodedFormEntity(useridarray));
   		  
   		    // Assign executed form container to response 
   		  
   		    response = httpclient.execute(httppost);
   		  
   		    // Check status code, need to check status code 200
   		  if (response.getStatusLine().getStatusCode() == 200) {
   			  // assign response entity to http entity
   			  
   			  entity = response.getEntity();
   			  
   			  // check if entity is not null
   			  if (entity !=null) {
   				  
   				  // create new import stream with received data assigned.
   				  InputStream instream = entity.getContent();
   				  
   				  //create new JSON object , assign converted data as parameter.
   				  JSONArray jsonResponse = new JSONArray(convertStreamToString(instream));
   				  JSONObject profiledetail;
   				  String retuser=null,retname,retlastname,retemail,retage,gender;
   				  for(int i=0;i<jsonResponse.length();i++){
   					  profiledetail = jsonResponse.getJSONObject(i);
   					  	retname 	= profiledetail.getString("Name");  // From SQL DB
   					  	retlastname = profiledetail.getString("lastname");	// From SQL DB
   					  	retage 		= profiledetail.getString("age");	// From SQL DB
   					  	retemail 	= profiledetail.getString("emailaddr");  // from SQL DB
   					  	retuser 	= profiledetail.getString("username");  // from SQL DB
   					    gender		= profiledetail.getString("Gender");// from SQL DB
   					  	
   					  	Name.setText(retname);
   					  	lastname.setText(retlastname);
   					  	age.setText(retage);
   					  	emailadd.setText(retemail);
   					  	username.setText(retuser); 	
   					  	genderview.setText(gender); 	
   		            }
   				  
   				  }
   				  		else {
   					  // Display Toast message about authentication invalid
   				  			Toast.makeText(getBaseContext(),"No Profile found", Toast.LENGTH_SHORT).show();
   				  }	  
   			  }	    			  
   		  
   	  }catch(Exception e) {
   		  e.printStackTrace();
   		  // Display Toast message when connection error.
   		  Toast.makeText(getBaseContext(), "Connection Error, Please check your Network!", Toast.LENGTH_SHORT).show();
   	  } 
	
	
   	save.setOnClickListener(new OnClickListener() {
   	 
   		@Override
   		public void onClick(View v) {
			
   			
   		// for network strict mode of android
    		if (android.os.Build.VERSION.SDK_INT > 9) { 
    			StrictMode.ThreadPolicy policy = 
    				new StrictMode.ThreadPolicy.Builder().permitAll().build();
    			StrictMode.setThreadPolicy(policy);
    		}
			
			
			   // create new default HTTPClient
	    	  httpclient = new DefaultHttpClient();
	 
	    	  // Create new HTTP POST with URL of our .php file
	    	  httppost = new HttpPost("http://141.54.152.162:8888/buysell/update.php");
			
	    	  
	    	  //the values of the above variables are converted to strings.
	    	  String dbname = Name.getText().toString();
			  String dblastname = lastname.getText().toString();
			  String dbage		= age.getText().toString();                         
			  String dbemailadd = emailadd.getText().toString();
			  
			  
			  // End
	    	  
	  try {
	    		  
	    		  // Checks Network Connectivity Start
	    		    SocketAddress sockaddr = new InetSocketAddress("141.54.152.162", 8888);
	    		    // Create an unbound socket
	    		    Socket sock = new Socket();

	    		    // This method will block no more than timeoutMs.
	    		    // If the timeout occurs, SocketTimeoutException is thrown.
	    		    int timeoutMs = 2000;   // 2 seconds
	    		    sock.connect(sockaddr, timeoutMs);
	    		    sock.close();
	    		  // Checks Network Connectivity End
	    		 // Create array to place the above variable for sending to server scripting file.
	    			  final ArrayList<NameValuePair> registerformdata;
	    			  registerformdata = new ArrayList<NameValuePair>();
	    			  
	    			  // Fills the Array with the registration form data.
	    			  registerformdata.add(new BasicNameValuePair("name",dbname));
	    			  registerformdata.add(new BasicNameValuePair("lastname", dblastname));
	    			  registerformdata.add(new BasicNameValuePair("age", dbage));
	    			  registerformdata.add(new BasicNameValuePair("email", dbemailadd));
	    			  registerformdata.add(new BasicNameValuePair("pid", userid));
	    		
	    		    
	    		  // Add array list to http post
	    		  httppost.setEntity(new UrlEncodedFormEntity(registerformdata));
	    		  
	    		  // Assign executed form container to response 		    		  
	    		  response = httpclient.execute(httppost);
	    		  
	    		  // Check status code, need to check status code 200
	    		  if (response.getStatusLine().getStatusCode() == 200) {
	    			  // assign response entity to http entity
	    			  
	    			  entity = response.getEntity();		    			  
	    			  // check if entity is not null
	    			  if (entity !=null) {
	  
	    				  //Display a Toast sent by server
    					 Toast.makeText(getBaseContext(),"Profile Updated", Toast.LENGTH_LONG).show();
    					 Intent intent = new Intent(profile.this,profile.class); // 
    					 startActivity(intent);   // starts the BuySell activity 
    				
	    		       }
	    	
	    			  }	   
	    		  
	    	  }catch(Exception e) {
	    		  e.printStackTrace();
	    		  // Display Toast message when connection error.
	    		  Toast.makeText(getBaseContext(), "Connection Error, Please check your Network!", Toast.LENGTH_SHORT).show();
	    	  }
	    	  
   			
   		}
   		
   	});
   	
	}
	
	
	
	// our main menue for profile activity
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			
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
	
	// for selection of an item in the menue
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// if logout option is selected	
		case R.id.logout:
			
			// create a shared preference 
			SharedPreferences usersession = getSharedPreferences("usersession", 0);
			  
		    // Edit the shared preference
			SharedPreferences.Editor spedit = usersession.edit();
			    
			// Cleans the userid string to null
			spedit.putString("userid",null );

			  // Commits the changes and closes the editor
			  spedit.commit();
			  
			//Takes the user to the login screen and displays log out message  
			Intent intent = new Intent(profile.this,Login.class); // 
			startActivity(intent);   // starts the BuySell activity  
			Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
			
			break;
		
		case R.id.register:
    		Intent registerintent = new Intent(profile.this,register.class); // 
			startActivity(registerintent);
			break;
			
    	case R.id.search_box:
			Intent searchintent = new Intent(profile.this,Search.class); // 
			startActivity(searchintent);
			break;
			
    	case R.id.profile:
			Intent profileintent = new Intent(profile.this,profile.class); // 
			startActivity(profileintent);
			break;
	
    	case R.id.help:
			Intent helpintent = new Intent(profile.this,Help.class); // 
			startActivity(helpintent);
			break;
			
    	case R.id.sell:
			Intent sellintent = new Intent(profile.this,sell.class); // 
			startActivity(sellintent);
			break;
			
    	case R.id.Main:
			Intent mainmenuintent = new Intent(profile.this,Products.class); // 
			startActivity(mainmenuintent);
			break;
			
    	case R.id.purchases_Menu:
			Intent purchaseintent = new Intent(profile.this,Purchases.class); // 
			startActivity(purchaseintent);
			break;
			
    	case R.id.Request_Menu:
			Intent requestsintent = new Intent(profile.this,requests.class); // 
			startActivity(requestsintent);
			break;
			
		}
		return true;
	}
	
	
	
	// Converts the stream of php response to string
	  private static String convertStreamToString(InputStream is) {
          /*
           * To convert the InputStream to String we use the BufferedReader.readLine()
           * method. We iterate until the BufferedReader return null which means
           * there's no more data to read. Each line will appended to a StringBuilder
           * and returned as String.
           */
          BufferedReader reader = new BufferedReader(new InputStreamReader(is));
          StringBuilder sb = new StringBuilder();
   
          String line = null;
          try {
              while ((line = reader.readLine()) != null) {
                  sb.append(line + "\n");
              }
          } catch (IOException e) {
              e.printStackTrace();
          } finally {
              try {
                  is.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
          return sb.toString();
      }

      
	
}
