package de.weimar.de.Schneller.Spur;

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
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class register extends Activity {
	Button registerb;
	EditText tName,tlastname,tage,tusername,tpassword,tpassword2;
	RadioGroup gendergroup;
	RadioButton maleradio,femaleradio;
	  // HTTP variables
	// 1: Create HTTPCLient as the form container
	HttpClient httpclient;
	// 2: Use HTTP Post method
	HttpPost httppost;
	// 3: Create an array list for the input data to be sent
	ArrayList<NameValuePair> nameValuePairs;
	// 4: Create a HTTP Response and HTTP Entity
	HttpResponse response;
	HttpEntity	entity;
	//End
	
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.register);   // sets the content of register from register.xml
	        registeruser();
}
	  // Register function bellow
	  private void registeruser() {
		  //gets IDs.
		   tName      = (EditText) findViewById(R.id.firstname);
		   tlastname  = (EditText) findViewById(R.id.lastname);
		   tusername  = (EditText) findViewById(R.id.username);
		   tpassword  = (EditText) findViewById(R.id.password);
		   tpassword2 = (EditText) findViewById(R.id.password2);
		   tage 	  = (EditText) findViewById(R.id.age);
		   maleradio = (RadioButton)findViewById(R.id.male_radio);
		   femaleradio = (RadioButton)findViewById(R.id.female_radio);
		   gendergroup = (RadioGroup)findViewById(R.id.gendergroup);
		   registerb  = (Button) findViewById(R.id.registerb);
		  // End
	
		  registerb.setOnClickListener(new OnClickListener() {
	     
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
		    	  httppost = new HttpPost("http://141.54.152.162/buysell/register.php");
		    	  //the values of the above variables are converted to strings.
				  String gender="";
		    	  String name 		= tName.getText().toString();
				  String lastname 	= tlastname.getText().toString();
				  String age		= tage.getText().toString();                         
				  String username 	= tusername.getText().toString();
				  String password 	= tpassword.getText().toString();
				  String password2 	= tpassword2.getText().toString();
				  
				  if (femaleradio.isChecked()){
					  gender = "female";  
				  }
				  else if(maleradio.isChecked()){
					  gender = "male";
				  }
				  
				  // End
		    	  
		    	  if (!name.equals("")  && !lastname.equals("")  
		    			  && !age.equals("") && !username.equals("") 
		    			  && !age.equals("") && !username.equals("") 
		    			  && !password.equals("") && !password2.equals("")
		    			  && password.equals(password2) && !gender.equals("") ){
		    		  try {
		    		  
		    		  // Checks Network Connectivity Start
		    		    SocketAddress sockaddr = new InetSocketAddress("141.54.152.162", 80);
		    		    // Create an unbound socket
		    		    Socket sock = new Socket();
		    		    int timeoutMs = 2000;   // 2 seconds
		    		    sock.connect(sockaddr, timeoutMs);
		    		    sock.close();
		    		 // Create array to place the above variable for sending to server scripting file.
		    			  final ArrayList<NameValuePair> registerformdata;
		    			  registerformdata = new ArrayList<NameValuePair>();
		    			  
		    			  // Fills the Array with the registration form data.
		    			  registerformdata.add(new BasicNameValuePair("name", name));
		    			  registerformdata.add(new BasicNameValuePair("lastname", lastname));
		    			  registerformdata.add(new BasicNameValuePair("username", username));
		    			  registerformdata.add(new BasicNameValuePair("password", password2));
		    			  registerformdata.add(new BasicNameValuePair("age", age));
		    			  registerformdata.add(new BasicNameValuePair("gender", gender));
		    		    
		    		    
		    		  // Add array list to http post
		    		  httppost.setEntity(new UrlEncodedFormEntity(registerformdata));
		    		  response = httpclient.execute(httppost);
		    		  // Check status code, need to check status code 200
		    		  if (response.getStatusLine().getStatusCode() == 200) {
		    			  // assign response entity to http entity
		    			  entity = response.getEntity();		    			  
		    			  // check if entity is not null
		    			  if (entity !=null) {
		    				  
		    				  Toast.makeText(getBaseContext(),"You are successfully registered", Toast.LENGTH_LONG).show();//Display a Toast sent by server
	    					  Intent intent = new Intent(register.this,Login.class); // 
	    					  startActivity(intent);   // starts the BuySell activity 
	    				  }
		    	
		    		 }	   
		    		}catch(Exception e) {
		    		  e.printStackTrace();
		    		  // Display Toast message when connection error.
		    		  Toast.makeText(getBaseContext(), "Connection Error, Please check your Network!", Toast.LENGTH_SHORT).show();
		    	  }
		    	  }
		    	  else{
		    		  Toast.makeText(getBaseContext(), "Fill all Fields, Or Retype Password !! ", Toast.LENGTH_LONG).show();
		    	  }
		    }
		});
		  
		  
	  }
	  
	  // for picture selection
	  public void showPopup(View v) {
			    PopupMenu popup = new PopupMenu(this, v);
			    MenuInflater inflater = popup.getMenuInflater();
			    inflater.inflate(R.menu.picmenue, popup.getMenu());
			    popup.show();
	  }
	  
	  public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {

			// if logout option is selected	
			case R.id.pcamera:
				  
				//Takes the user to the login screen and displays log out message  
				Intent intent = new Intent(register.this,Login.class); // 
				startActivity(intent);   // starts the BuySell activity  
				Toast.makeText(getBaseContext(),"Camera selected", Toast.LENGTH_SHORT).show();
				return true;
				
			case R.id.pgallery:
				Toast.makeText(getBaseContext(),"Gallery", Toast.LENGTH_SHORT).show();
				return true;
				
			default:
				return false;
				
			}
		
		}
	  
	  
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
	    		Intent loginintent = new Intent(register.this,Login.class); // 
				startActivity(loginintent);
				break;
				
	    	case R.id.register:
	    		Intent registerintent = new Intent(register.this,register.class); // 
				startActivity(registerintent);
				break;
				
	    	case R.id.search_box:
				Intent searchintent = new Intent(register.this,Search.class); // 
				startActivity(searchintent);
				break;
				
	    	case R.id.profile:
				Intent profileintent = new Intent(register.this,profile.class); // 
				startActivity(profileintent);
				break;
				
	    	case R.id.help:
				Intent helpintent = new Intent(register.this,Help.class); // 
				startActivity(helpintent);
				break;
				
	    	case R.id.sell:
				Intent sellintent = new Intent(register.this,sell.class); // 
				startActivity(sellintent);
				break;
				
	    	case R.id.Main:
				Intent mainmenuintent = new Intent(register.this,Products.class); // 
				startActivity(mainmenuintent);
				break;
				
	    	case R.id.purchases_Menu:
				Intent purchaseintent = new Intent(register.this,Purchases.class); // 
				startActivity(purchaseintent);
				break;
				
	    	case R.id.Request_Menu:
				Intent requestsintent = new Intent(register.this,requests.class); // 
				startActivity(requestsintent);
				break;
				
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
				Intent logoutintent = new Intent(register.this,Products.class); // 
				startActivity(logoutintent);   // starts the BuySell activity  
				Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
				
				break;

				
	    	}
	    	
	    
	    	return true;
	    } 
}

