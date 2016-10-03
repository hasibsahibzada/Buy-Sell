package de.weimar.de.Schneller.Spur;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import de.weimar.de.Schneller.Spur.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;    
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;




@SuppressLint("NewApi")
public class sell extends Activity implements OnMenuItemClickListener {
	//declaration
	private static String SERVERIP="192.168.2.15";
	private static int RESULT_LOAD_IMAGE = 1;
	private static int camera_token = 2;
	DBAdapter dbAdapter;
	EditText pname,pprice,pdescription;
	Button submit,cancel;;
	ImageView imageview1;
	byte[] imageInByte;
	TextView tv1,tv2;
	String userid = null;
	Button gallery,camera;
	File photoFile;
	Double Latitude=null,Longitude=null;
	private static int SQLiteSize=10;
	
	// Background process for gps
	AsyncTaskRunner gpsrunner = new AsyncTaskRunner();
	Boolean gps_status =false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sell);   // sets the content of first from first.xml	
	        
	        // do the background process
	        gpsrunner = new AsyncTaskRunner();
			gpsrunner.execute();
	        
	        initializeVar();				//initializa all declare variable
			getInternetPermission();		//Internet permission
	
			//submit button for register product
			submit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
								
					  String name 		= pname.getText().toString();
					  String price 		= pprice.getText().toString();
			    	  String desc 		= pdescription.getText().toString();
			            
		            if (gps_status ==true){
				    	try {
							long i = dbAdapter.register(name,price,desc,userid,imageInByte,Latitude,Longitude,0);
							if(i != -1)
								Toast.makeText(sell.this, "SQLite:successfully registered1",Toast.LENGTH_LONG).show();
			
						  } catch (SQLException e1) {
							    Toast.makeText(sell.this, "SQLite:Some problem occurred1",Toast.LENGTH_LONG).show();
		 				  }
				    	
				    	Syncronization();
				    	checkSizeTable();
				    	Intent sellintent = new Intent(sell.this,First.class); // go to map 
						startActivity(sellintent);
		            }
		            else {            	
						Toast.makeText(sell.this, "Wait till your location is loaded !! ",Toast.LENGTH_LONG).show();
		            }
				    
				}
			});
			
			//cancel button
	        cancel.setOnClickListener(new OnClickListener(){
	        	@Override
				public void onClick(View v) {
	        		Toast.makeText(getBaseContext(), "Operation Canceled", Toast.LENGTH_SHORT).show();
	        		Intent cancelintent = new Intent(sell.this,First.class); // 
	    			startActivity(cancelintent);
	        	}
	        	
	        });
	
	}

	
//Initialize declare variable
public void initializeVar()
{

	SharedPreferences usersession = getSharedPreferences("usersession", 0); 
	userid = usersession.getString("userid",null);
	pname  = (EditText) findViewById(R.id.pname_input);
	pprice = (EditText) findViewById(R.id.pprice_input);
	pdescription = (EditText) findViewById(R.id.pdescription_input);
	submit = (Button) findViewById(R.id.psubmit_btn);	
	cancel = (Button) findViewById(R.id.pcancel_btn);
	imageview1 = (ImageView)findViewById(R.id.imageView1);
	dbAdapter = new DBAdapter(this);
	dbAdapter.open();
}

//Internet permission
public void getInternetPermission()
{
if (android.os.Build.VERSION.SDK_INT > 9) {
	StrictMode.ThreadPolicy policy = 
		new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy);
}
}

//delete row if more than 10 Syncronized row is there
public void checkSizeTable()
{
int count=dbAdapter.dbAllCount();					//all entries in SQLite
if (count>SQLiteSize) 
{
	ArrayList<String> keyList=dbAdapter.selectid(); //syncronized entries in SQLite
	if (keyList.size()>=count-SQLiteSize)
	{
		for(int i=0;i<count-SQLiteSize;i++)  
		{
			dbAdapter.deleteRow(keyList.get(i));
		}
	}
	else
	{
		for(int i=0;i<keyList.size();i++)
		{
			dbAdapter.deleteRow(keyList.get(i));
		}
	}
}
}

//syncronization
public void Syncronization() {

	int i;
	try {
		int count=dbAdapter.dbSyncCount();
		if (count>0) 
		{
			ArrayList<ArrayList<NameValuePair>> wordlist=dbAdapter.Login();			
			for(i=0;i<count;i++)
			{
			ArrayList<NameValuePair> initialValues=wordlist.get(i);
			connectionCheck(SERVERIP);//check connection 
			uploadOnServer(initialValues);		// upload the data on server
			String userid=initialValues.get(0).getValue();
			dbAdapter.updateRow(userid);
			}
			Toast.makeText(sell.this,"All Syncronised",Toast.LENGTH_LONG).show();
		} else 
		{
			Toast.makeText(sell.this,"Already syncronised",Toast.LENGTH_LONG).show();
		}
		
	} catch (Exception e) {
		Toast.makeText(sell.this, "hello Internet problem occurred"+e,Toast.LENGTH_LONG).show();
	}
	checkSizeTable(); 		//resize SQLite table if some entries are already syncronized.
}



// load current location
public void load_current_location(){

			    		  // gps related things
			    		    LocationManager mlocManager=null; 
			    		    LocationListener mlocListener; 
			    		    mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
			    		    mlocListener = new MyLocationListener(); 
			    		    mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);							
			  
							if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
																	
				            	 if(MyLocationListener.latitude>0) {	

				            		 // Set the values to global values of gps 
				            		 Latitude = MyLocationListener.latitude;
				            		 Longitude = MyLocationListener.longitude;

						             // set the status to true to stop the location searching								  
				            		 gps_status= true;
								  }
					            else  
					             {  
					            	 Toast.makeText(getBaseContext(), "Searching for location ...", Toast.LENGTH_LONG).show();  
					              }  
					          } 
							
							else {  
					              Toast.makeText(getBaseContext(), "GPS is turned Off, Please switch it On !", Toast.LENGTH_LONG).show(); 
					          }  	  
					}


// Asynch task runner class

// Background tasks
private class AsyncTaskRunner extends AsyncTask<String, String, String > {

	

	  @Override
	  protected String doInBackground(String... params) {  

		  // wait for a couple of seconds	  
		  try{	 		
		 		Thread.sleep(10*1000); //1000 is to change the milisecond to second
		 	}catch(InterruptedException e){
		 		e.printStackTrace();
		 	}
		  
		   return null;
	  }
	  @Override
	  protected void onPostExecute(final String map) {
	
		// run the same type task again. 
		  runOnUiThread(new Runnable() {
			    public void run() {
			    	load_current_location();  
			    }
			});
		  
		 // re-run if gps_status is null
		  if (gps_status == false){
		  gpsrunner = new AsyncTaskRunner();
		  gpsrunner.execute();
		  }
	  }

	
	  @Override
	  protected void onPreExecute() {}

	 
	  protected void onProgressUpdate(String... text) {}
}

//Checking Network Connectivity with timeout			
public void connectionCheck(String ipaddress) throws Exception
{
SocketAddress sockaddr = new InetSocketAddress(ipaddress,80);
Socket sock = new Socket();
int timeoutMs = 2000;   // 2 seconds
sock.connect(sockaddr, timeoutMs);
sock.close();
}

//upload Data on Server
public void uploadOnServer(ArrayList<NameValuePair> nameValuePairs) throws Exception
{
HttpClient httpclient = new DefaultHttpClient();
HttpPost httppost = new HttpPost("http://"+SERVERIP+"/buysell/upload_product.php");
httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
HttpResponse response = httpclient.execute(httppost);
String the_string_response = convertResponseToString(response);
Toast.makeText(sell.this, "MySQL:" + the_string_response, Toast.LENGTH_LONG).show();
}

//start conv to string
public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{
  
String res = "";
StringBuffer buffer = new StringBuffer();
InputStream inputStream = response.getEntity().getContent();
int contentLength = (int) response.getEntity().getContentLength(); 
if (contentLength < 0){
}
else{
       byte[] data = new byte[512];
       int len = 0;
       try
       {
           while (-1 != (len = inputStream.read(data)) )
           {
               buffer.append(new String(data, 0, len)); //converting to string and appending  to string buffer
           }
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
       try
       {
           inputStream.close(); // closing the stream…..
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
       res = buffer.toString();     // converting string buffer to string…..

       Toast.makeText(this, "Result : " + res, Toast.LENGTH_LONG).show();
       //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
}
return res;
}
//end conversation to string

//for getting image
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
 
if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
	Uri selectedImage = data.getData();
    String[] filePathColumn = { MediaStore.Images.Media.DATA };
    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
    cursor.moveToFirst();
    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    String picturePath = cursor.getString(columnIndex);
    cursor.close();
    Bitmap image=BitmapFactory.decodeFile(picturePath);
    imageview1.setImageBitmap(image);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	imageInByte = stream.toByteArray();
    	         
}
else if(requestCode == camera_token && resultCode == RESULT_OK) 
{
	Bundle extras = data.getExtras();
    Bitmap imageBitmap = (Bitmap) extras.get("data");
    imageview1.setImageBitmap(imageBitmap);
	
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
  	imageInByte = stream.toByteArray();

}
}


@Override
protected void onPostResume() {
	super.onPostResume();
}

@Override
protected void onPause() {
	super.onPause();
}



//popup men
public void showMenu(View v) {
    PopupMenu popup = new PopupMenu(this, v);
    // This activity implements OnMenuItemClickListener
    popup.setOnMenuItemClickListener(this);
    popup.inflate(R.menu.picmenue);
    popup.show();
}

public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.pcamera:
			Toast.makeText(sell.this, "Camera is clicked !!",Toast.LENGTH_LONG).show();
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		        startActivityForResult(takePictureIntent, camera_token);
		    }
            return true;

        case R.id.pgallery:
        	Intent i = new Intent( Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);				
			return true;
       
        default:
            return false;
    }
}


// my option menu
@Override
public boolean onCreateOptionsMenu (Menu menu){
	MenuInflater inflater = getMenuInflater();
	String userid = null;		// Get user session
	SharedPreferences usersession = getSharedPreferences("usersession", 0); 
	userid = usersession.getString("userid",null);
	if (userid == null) {		//if guest    
		inflater.inflate(R.menu.public_menu, menu);
		return true;

	}else   					// if logged in user
	{
		inflater.inflate(R.menu.user_menu, menu);
		return true;
	}
}

@Override
	public boolean onOptionsItemSelected(MenuItem item){
	switch(item.getItemId()) {
	
	case R.id.login:
		Intent loginintent = new Intent(sell.this,Login.class); // 
		startActivity(loginintent);
		break;
		
	case R.id.register:
		Intent registerintent = new Intent(sell.this,register.class); // 
		startActivity(registerintent);
		break;
		
	case R.id.search_box:
		Intent searchintent = new Intent(sell.this,Search.class); // 
		startActivity(searchintent);
		break;
		
	case R.id.profile:
		Intent profileintent = new Intent(sell.this,profile.class); // 
		startActivity(profileintent);
		break;
		
	case R.id.help:
		Intent helpintent = new Intent(sell.this,Help.class); // 
		startActivity(helpintent);
		break;
		
	case R.id.sell:
		Intent sellintent = new Intent(sell.this,sell.class); // 
		startActivity(sellintent);
		break;
		
	case R.id.Main:
		Intent mainmenuintent = new Intent(sell.this,Products.class); // 
		startActivity(mainmenuintent);
		break;
		

	case R.id.purchases_Menu:
		Intent purchaseintent = new Intent(sell.this,Purchases.class); // 
		startActivity(purchaseintent);
		break;
		
	case R.id.Request_Menu:
		Intent requestsintent = new Intent(sell.this,requests.class); // 
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
		Intent logoutintent = new Intent(sell.this,Products.class); // 
		startActivity(logoutintent);   // starts the BuySell activity  
		Toast.makeText(getBaseContext(),"You are logged Out", Toast.LENGTH_SHORT).show();
		break;
	}
	return true;
}
	
}

