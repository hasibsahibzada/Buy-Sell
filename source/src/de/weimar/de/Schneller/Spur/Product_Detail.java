package de.weimar.de.Schneller.Spur;

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

import de.weimar.de.Schneller.Spur.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Product_Detail extends Activity {

	private TextView p_name_detail;
	private TextView p_price_detail;
	private TextView p_description;
	private ImageView product_image;
	Button main_page,purchase;
	private int p_id;
	
	DBAdapter dbAdapter;// for accessing data adaptor class

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);   // sets the content of first from first.xm

        this.p_name_detail = (TextView) this.findViewById(R.id.P_name_detail);
        this.p_price_detail = (TextView) this.findViewById(R.id.PPrice_detail);
        this.p_description = (TextView) this.findViewById(R.id.p_description_view);
        this.product_image = (ImageView) this.findViewById(R.id.p_image_view_detail);
        
        dbAdapter = new DBAdapter(this);
    	dbAdapter.open();
        
        // get the id which was sent from detail page of the clicked item
        this.p_id = getIntent().getIntExtra("Pid", 0);

        try
        {
        	// internet connection check 	        		
   		 SocketAddress sockaddr = new InetSocketAddress("141.54.152.162",80);					        			   // Checks Network Connectivity Start
   		 Socket sock = new Socket();   // Create an unbound socket
   		 // This method will block no more than timeoutMs.
   		 // If the timeout occurs, SocketTimeoutException is thrown.
   		 int timeoutMs = 2000;   // 2 seconds
   		 sock.connect(sockaddr, timeoutMs);
   		 sock.close();
   		 
   		 // run the background process
     	new GetProductDetail().execute(new ApiConnector());

        	
        }catch(Exception  e){
        	
        	Log.d("mylog","i am in the cach");
        
        	loadcachedproducts(p_id);
        	
        	
        }
        
        	
   
        
       
        purchase = (Button) findViewById(R.id.purchase_item_btn);
        main_page = (Button) findViewById(R.id.Main_page_btn);
        
        // Purchase button click action
        purchase.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		
        		
        		String userid = null;

    			SharedPreferences usersession = getSharedPreferences("usersession", 0); 
    			userid = usersession.getString("userid",null);
    		
    			
    			
    			
    			if (userid == null) {    
    				 
    				Toast.makeText(getBaseContext(),"You are not logged in , Redirecting to login page", Toast.LENGTH_LONG).show();
    				Intent intent = new Intent(Product_Detail.this,Login.class); // 
    				
  				  	startActivity(intent); 

    			}else
    			{
    				Intent intent = new Intent(Product_Detail.this,Payment.class); // 
    				intent.putExtra("pid",getIntent().getIntExtra("Pid", 0) );   // gets the id and sends it to other page
    				intent.putExtra("pname",p_name_detail.getText());  //sends the name
    				intent.putExtra("pprice",p_price_detail.getText()); // send the price
    				startActivity(intent); 
    			}
    			
    			
        				}
        	
        });
        
        
        // main button action click.
        main_page.setOnClickListener(new OnClickListener() {
        	
        	@Override
			public void onClick(View v) {
        		
        		Intent intent = new Intent(Product_Detail.this,Products.class); // 
				startActivity(intent); 
        		
        	}
        	
        });
        
        
        
	}    

	private class GetProductDetail extends AsyncTask<ApiConnector,Long,JSONArray>
	    {
	        @Override
	        protected JSONArray doInBackground(ApiConnector... params) {

	            // it is executed on Background thread

	             return params[0].getproductdetail(p_id);
	        }
	        
	        @Override
	        protected void onPostExecute(JSONArray jsonArray) {
	        	
	        	try
	        	{
	        	

	        		JSONObject product = jsonArray.getJSONObject(0);
	        		p_name_detail.setText(product.getString("Pname"));
	        		p_price_detail.setText(product.getString("Price")); 
	        		p_description.setText(product.getString("Description"));
	        		Bitmap bmp;
	 	        	bmp = getBitmapFromURL(product.getString("image"));
	        		
	 	        	product_image.setImageBitmap(bmp);
		
	        		
	        	}catch(JSONException e) {
        		// load the product from cach
	        		e.printStackTrace();
	        	}
	        	
	        }
	        
	    }

	
	// on back action
    @Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			
			Intent mapintent = new Intent(Product_Detail.this,First.class); // 
			startActivity(mapintent); 
			
		}
	
	
 // load cashed products
 	 public void loadcachedproducts(int p_id){
 				// Access map
 					ArrayList<ArrayList<NameValuePair>> wordlist=dbAdapter.loadcachedproducts();	
 					for(int i=0;i<wordlist.size();i++)
 					{
 						
 						
 						ArrayList<NameValuePair> initialValues=wordlist.get(i);
 						Log.d("my",initialValues.get(0).getValue().toString());	
 						if (Integer.parseInt(initialValues.get(0).getValue().toString()) == p_id){
 						Log.d("my","i am found");	
 						// frame and download the image
 	 	        		    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
 	 	            		Bitmap bmp = Bitmap.createBitmap(8, 8, conf);
 	 	 	        		byte[] img=Base64.decode(initialValues.get(5).getValue().toString(),Base64.DEFAULT);
 	 	 	        		bmp = BitmapFactory.decodeByteArray(img , 0, img .length);
 	 	 	        		
 	 	 	        		product_image.setImageBitmap(bmp);	 	 	        		
 	 	 	        		p_name_detail.setText(initialValues.get(1).getValue().toString());
 	 		        		p_price_detail.setText(initialValues.get(3).getValue().toString()); 
 	 		        		p_description.setText(initialValues.get(2).getValue().toString());
 						}
			}
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

		@Override
	    public boolean onOptionsItemSelected(MenuItem item){
	    	
	    	switch(item.getItemId()) {
			
	    	case R.id.login:
	    		Intent loginintent = new Intent(Product_Detail.this,Login.class); // 
				startActivity(loginintent);
				break;
				
	    	case R.id.register:
	    		Intent registerintent = new Intent(Product_Detail.this,register.class); // 
				startActivity(registerintent);
				break;
				
	    	case R.id.search_box:
				Intent searchintent = new Intent(Product_Detail.this,Search.class); // 
				startActivity(searchintent);
				break;
				
	    	case R.id.profile:
				Intent profileintent = new Intent(Product_Detail.this,profile.class); // 
				startActivity(profileintent);
				break;
				
	    	case R.id.help:
				Intent helpintent = new Intent(Product_Detail.this,Help.class); // 
				startActivity(helpintent);
				break;
				
	    	case R.id.sell:
				Intent sellintent = new Intent(Product_Detail.this,sell.class); // 
				startActivity(sellintent);
				break;
				
	    	case R.id.Main:
				Intent mainmenuintent = new Intent(Product_Detail.this,Products.class); // 
				startActivity(mainmenuintent);
				break;
				
				
	    	case R.id.purchases_Menu:
				Intent purchaseintent = new Intent(Product_Detail.this,Purchases.class); // 
				startActivity(purchaseintent);
				break;
				
	    	case R.id.Request_Menu:
				Intent requestsintent = new Intent(Product_Detail.this,requests.class); // 
				startActivity(requestsintent);
				break;
				
	    	case R.id.logout:
				
				// create a shared preference 
				SharedPreferences usersession = getSharedPreferences("usersession", 0);
				  
			    // Edit the shared preference
				SharedPreferences.Editor spedit = usersession.edit();
				    
				// Cleans the userid string to null
				spedit.putString("userid",null);

				  // Commits the changes and closes the editor
				  spedit.commit();
				  
				//Takes the user to the login screen and displays log out message  
				Intent logoutintent = new Intent(Product_Detail.this,Products.class); // 
				startActivity(logoutintent);   // starts the BuySell activity  
				Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
				
				break;

				
	    	}
	    	
	    
	    	return true;
	    }
}
