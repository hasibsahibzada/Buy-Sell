package de.weimar.de.Schneller.Spur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Purchases extends Activity {

	private ListView getallpurchases;
	private JSONArray jsonarray;
	
	
	String userid;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase);   // sets the content of first from first.xml
        SharedPreferences usersession = getSharedPreferences("usersession", 0);
    	userid = usersession.getString("userid",null);   // user ID saved
    	this.getallpurchases = (ListView) this.findViewById(R.id.all_purchases);
        new GetAllPurchase().execute(new ApiConnector());
        // for click of each item
        this.getallpurchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		try
        		{
        			// Get the item clicked
        			JSONObject productClicked = jsonarray.getJSONObject(position);
        			
        			// send Product ID.
        			Intent show_product_detail = new Intent(getApplicationContext(),Product_Detail.class);
        			show_product_detail.putExtra("Pid", productClicked.getInt("P_Id"));
    
        			startActivity(show_product_detail);	
        			
        		}
        		catch(JSONException e)
        		{
        			e.printStackTrace();
        		}
        	}
        });
        
}    
	    private class GetAllPurchase extends AsyncTask<ApiConnector,Long,JSONArray>
	    {
	        @Override
	        protected JSONArray doInBackground(ApiConnector... params) {

	            // it is executed on Background thread

	             return params[0].GetAllPurchases(userid);
	        }

	        @Override
	        protected void onPostExecute(JSONArray jsonArray) {

	        	setListAdapter(jsonArray);
	        	
	        }
	    }
	    public void setListAdapter (JSONArray jsonArray) {
	    	this.jsonarray = jsonArray;
	    	this.getallpurchases.setAdapter(new Get_All_Purchase_list_View(jsonArray,this));
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
    		Intent loginintent = new Intent(Purchases.this,Login.class); // 
			startActivity(loginintent);
			break;
			
    	case R.id.register:
    		Intent registerintent = new Intent(Purchases.this,register.class); // 
			startActivity(registerintent);
			break;
			
    	case R.id.search_box:
			Intent searchintent = new Intent(Purchases.this,Search.class); // 
			startActivity(searchintent);
			break;
    	case R.id.help:
			Intent helpintent = new Intent(Purchases.this,Help.class); // 
			startActivity(helpintent);
			break;	
			
    	case R.id.sell:
			Intent sellintent = new Intent(Purchases.this,sell.class); // 
			startActivity(sellintent);
			break;
			
    	case R.id.Main:
			Intent mainmenuintent = new Intent(Purchases.this,Products.class); // 
			startActivity(mainmenuintent);
			break;
			
    	case R.id.profile:
			Intent profileintent = new Intent(Purchases.this,profile.class); // 
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
			Intent logoutintent = new Intent(Purchases.this,Products.class); // 
			startActivity(logoutintent);   // starts the BuySell activity  
			Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
			break;
	}
    return true;
    }
}
