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
import android.widget.Button;    // for button objects
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	EditText tusername, tpassword;
	Button loginb,registerb;
	
	// Create HTTPCLient as the form container
	HttpClient httpclient;
	
	//Use HTTP Post method
	HttpPost httppost;
	
	// Create an array list for the input data to be sent
	ArrayList<NameValuePair> nameValuePairs;
	
	// Create a HTTP Response and HTTP Entity
	HttpResponse response;
	HttpEntity	entity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.first);   // sets the content of first from first.xml

	        login();    // calls the initialize function
	        register();  // call
			
}    
	
	      // our login function 
	      private  void login () {
	    	  
	    	// Gets all IDs   
	        tusername = (EditText) findViewById(R.id.firstname);
	        tpassword = (EditText) findViewById(R.id.lastname);
	        Button loginb = (Button) findViewById(R.id.logButton);
	        
	        
	        // Login Button Action Start
	        loginb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					  httpclient = new DefaultHttpClient();
			    	  httppost = new HttpPost("http://192.168.2.15/buysell/login.php");// Create new HTTP POST with URL of our .php file
			    	  
			    	  // Assign input text to strings
			    	  String  username = tusername.getText().toString();
			    	  String  password = tpassword.getText().toString();
			    	 
			    	  try {
			    		  	SocketAddress sockaddr = new InetSocketAddress("141.54.153.107",80);	    // Checks Network Connectivity Start
			    		    Socket sock = new Socket();			// Create an unbound socket

			    		    int timeoutMs = 2000;   			// 2 seconds
			    		    sock.connect(sockaddr, timeoutMs);
			    		    sock.close();
			    		  
			    		    nameValuePairs = new ArrayList<NameValuePair>();					// create new array list
			    		    nameValuePairs.add(new BasicNameValuePair("username", username));	// place the username and password to array.
			    		    nameValuePairs.add(new BasicNameValuePair("password", password));
			    		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 		// Add array list to http post
			    		  
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
			    				  JSONObject authentication;
			    				  String retuser=null,retpass=null,userid = null;
			    				  for(int i=0;i<jsonResponse.length();i++){
			    					  	authentication = jsonResponse.getJSONObject(i);
			    					  	retuser = authentication.getString("username");  // from SQL DB
			    					  	retpass = authentication.getString("password");  // From SQL DB
			    					  	userid = authentication.getString("uid");		 // From SQL DB
			    		            }
			    				  
			    				  // validate 
			    				  
			    				  if ( username.equals(retuser) && password.equals(retpass)) {
			    					  SharedPreferences usersession = getSharedPreferences("usersession", 0);// create a shared preference 
			    					  SharedPreferences.Editor spedit = usersession.edit();				  // Edit the shared preference
			    					  // puts the userid in globale share variable
			    					  spedit.putString("userid",userid );

			    					  // close the editor
			    					  spedit.commit();
			    					  
			    					  //Display a Toast saying login was successful
			    					  Toast.makeText(getBaseContext(),"login success", Toast.LENGTH_SHORT).show();
			    					  Intent intent = new Intent(Login.this,First.class); // 
			    					  startActivity(intent);   // starts the BuySell activity 
			    				  }
			    				  		else {
			    					  // Display Toast message about authentication invalid
			    				  	   Toast.makeText(getBaseContext(),"invalide data", Toast.LENGTH_SHORT).show();
			    				  }	  
			    			  }	    			  
			    		  }
			    		  
			    	  }catch(Exception e) {
			    		  e.printStackTrace();
			    		  // Display Toast message when connection error.
			    		  Toast.makeText(getBaseContext(), "Connection Error, Please check your Network!", Toast.LENGTH_SHORT).show();
			    	  }
			    }
			});
	        
	         // to change the strict mode 
			if (android.os.Build.VERSION.SDK_INT > 9) {
			      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			      StrictMode.setThreadPolicy(policy);
			    }
		  } 
	      
	      // our registration function
	  	private void register () {
			 Button registerb = (Button) findViewById(R.id.RegButton);
	    	 registerb.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent register = new Intent(Login.this,register.class);
					startActivity(register);
				}
			});
	    }

	      
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
	      
	      @Override
	      public boolean onOptionsItemSelected(MenuItem item){
	      	
	      	switch(item.getItemId()) {
	  		
	      			
	      	case R.id.login:
	      		Intent loginintent = new Intent(Login.this,Login.class); // 
	  			startActivity(loginintent);
	  			break;
	  			
	      	case R.id.register:
	      		Intent registerintent = new Intent(Login.this,register.class); // 
	  			startActivity(registerintent);
	  			break;
	  			
	      	case R.id.search_box:
	  			Intent searchintent = new Intent(Login.this,Search.class); // 
	  			startActivity(searchintent);
	  			break;
	      	case R.id.help:
				Intent helpintent = new Intent(Login.this,Help.class); // 
				startActivity(helpintent);
				break;	
				
	      	case R.id.sell:
				Intent sellintent = new Intent(Login.this,sell.class); // 
				startActivity(sellintent);
				break;
	  			
	      	case R.id.Main:
				Intent mainmenuintent = new Intent(Login.this,Products.class); // 
				startActivity(mainmenuintent);
				break;
				
	      	case R.id.profile:
				Intent profileintent = new Intent(Login.this,profile.class); // 
				startActivity(profileintent);
				break;
				
	      	case R.id.logout:
	  			
	  			// create a shared preference 
	  			SharedPreferences usersession = getSharedPreferences("usersession", 0);
	  			  
	  		    // Edit the shared preference
	  			SharedPreferences.Editor spedit = usersession.edit();
	  			    
	  			// Cleans the userid string to null
	  			spedit.putString("userid","" );

	  			  // Commits the changes and closes the editor
	  			  spedit.commit();
	  			  
	  			//Takes the user to the login screen and displays log out message  
	  			Intent logoutintent = new Intent(Login.this,Products.class); // 
	  			startActivity(logoutintent);   // starts the BuySell activity  
	  			Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
	  			
	  			break;
	      	}
	      	return true;
	      }
}
