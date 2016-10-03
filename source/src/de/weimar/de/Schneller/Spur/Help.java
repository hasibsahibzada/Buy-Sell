package de.weimar.de.Schneller.Spur;

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class Help extends Activity {
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.help);   // sets the content of first from first.xm
			
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
    		Intent loginintent = new Intent(Help.this,Login.class); // 
			startActivity(loginintent);
			break;
			
    	case R.id.register:
    		Intent registerintent = new Intent(Help.this,register.class); // 
			startActivity(registerintent);
			break;
			
    	case R.id.search_box:
			Intent searchintent = new Intent(Help.this,Search.class); // 
			startActivity(searchintent);
			break;
			
    	case R.id.profile:
			Intent profileintent = new Intent(Help.this,profile.class); // 
			startActivity(profileintent);
			break;
    	case R.id.help:
			Intent helpintent = new Intent(Help.this,Help.class); // 
			startActivity(helpintent);
			break;
			
    	case R.id.sell:
			Intent sellintent = new Intent(Help.this,sell.class); // 
			startActivity(sellintent);
			break;
			
			
    	case R.id.Main:
			Intent mainmenuintent = new Intent(Help.this,Products.class); // 
			startActivity(mainmenuintent);
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
			Intent logoutintent = new Intent(Help.this,Products.class); // 
			startActivity(logoutintent);   // starts the BuySell activity  
			Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
			
			break;

			
    	}
    	
    
    	return true;
    }
    
     
}
		
			
			
		
	
	
	 
