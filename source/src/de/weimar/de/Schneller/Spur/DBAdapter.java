package de.weimar.de.Schneller.Spur;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;



public class DBAdapter 
{
	private static final String DATABASE_TABLE = "product";
	
	public static final String KEY_ID = "Pid";
	public static final String KEY_NAME = "Pname";
	public static final String KEY_PRICE = "Price";
	public static final String KEY_DESC = "Description";
	public static final String KEY_OWNID = "Owner_ID";
	public static final String KEY_LONGITUDE = "Longitude";
	public static final String KEY_LATITUDE = "Latitude";
	public static final String KEY_IMG = "image";
	public static final String KEY_STATUS = "status";
	SQLiteDatabase mDb;
	Context mCtx;
	DBHelper mDbHelper;
	
	public DBAdapter(Context context)
	{
		this.mCtx = context;
	}

	public DBAdapter open() throws SQLException
	{   
		mDbHelper = new DBHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		Log.d("my",mDb.getPath());
		return this;
	}
	
	public void close()
	{
		mDbHelper.close();
	}
	
	public long register(String name,String price,String desc,String ownid,byte[] imageInByte,double latitude , double longitude,int status)
	{
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PRICE, price);
		initialValues.put(KEY_DESC, desc);
		initialValues.put(KEY_OWNID, ownid);
		initialValues.put(KEY_IMG, imageInByte);
		initialValues.put(KEY_LONGITUDE, longitude);
		initialValues.put(KEY_LATITUDE, latitude);
		
		initialValues.put(KEY_STATUS, status);
		Log.d("my", "register");
		return mDb.insert(DATABASE_TABLE, null, initialValues);
		
	}
	
	public ArrayList<ArrayList<NameValuePair>> Login() throws SQLException 
    {	
		
		 ArrayList<ArrayList<NameValuePair>> wordList;
	     wordList = new ArrayList<ArrayList<NameValuePair>>();
	     String[] col={KEY_ID, KEY_NAME, KEY_PRICE, KEY_DESC, KEY_OWNID, KEY_IMG, KEY_LATITUDE, KEY_LONGITUDE };
	     Cursor c=mDb.query(DATABASE_TABLE, col, "status=0", null, null, null, null);
    
	  if (c.moveToFirst()) {
	    int index=0;
	    do {
        	ArrayList<NameValuePair> map =new ArrayList<NameValuePair>();
        	byte[] imageInByte=c.getBlob(c.getColumnIndex(KEY_IMG));
			String image_str = Base64.encodeToString(imageInByte,Base64.DEFAULT);
			
			map.add(new BasicNameValuePair(KEY_ID, c.getString(c.getColumnIndex(KEY_ID))));
            map.add(new BasicNameValuePair(KEY_NAME, c.getString(c.getColumnIndex(KEY_NAME))));
            map.add(new BasicNameValuePair(KEY_DESC, c.getString(c.getColumnIndex(KEY_DESC))));
            map.add(new BasicNameValuePair(KEY_PRICE, c.getString(c.getColumnIndex(KEY_PRICE))));
            map.add(new BasicNameValuePair(KEY_OWNID, c.getString(c.getColumnIndex(KEY_OWNID))));
            map.add(new BasicNameValuePair(KEY_LATITUDE, c.getString(c.getColumnIndex(KEY_LATITUDE))));
            map.add(new BasicNameValuePair(KEY_LONGITUDE, c.getString(c.getColumnIndex(KEY_LONGITUDE))));
            map.add(new BasicNameValuePair(KEY_IMG, image_str));
            wordList.add(index, map);
            index++;
        } while (c.moveToNext());
        return wordList;
      }
      return wordList;
    }
	
	
	
	public ArrayList<ArrayList<NameValuePair>> loadcachedproducts() throws SQLException 
    {	
		 ArrayList<ArrayList<NameValuePair>> wordList;
	     wordList = new ArrayList<ArrayList<NameValuePair>>();
    	 String[] col={KEY_ID, KEY_NAME, KEY_PRICE, KEY_DESC, KEY_OWNID, KEY_IMG, KEY_LATITUDE, KEY_LONGITUDE };
    	 Cursor c=mDb.query(DATABASE_TABLE, col, "status=1", null, null, null, null);
    	 Log.d("my","cash prucuct count-"+c.getCount()+"");
       if (c.moveToFirst()) {
    	int index=0;
    	do {
        	ArrayList<NameValuePair> map =new ArrayList<NameValuePair>();
        	byte[] imageInByte=c.getBlob(c.getColumnIndex(KEY_IMG));
			String image_str = Base64.encodeToString(imageInByte,Base64.DEFAULT);
			
			map.add(new BasicNameValuePair(KEY_ID, c.getString(c.getColumnIndex(KEY_ID))));
            map.add(new BasicNameValuePair(KEY_NAME, c.getString(c.getColumnIndex(KEY_NAME))));
            map.add(new BasicNameValuePair(KEY_DESC, c.getString(c.getColumnIndex(KEY_DESC))));
            map.add(new BasicNameValuePair(KEY_PRICE, c.getString(c.getColumnIndex(KEY_PRICE))));
            map.add(new BasicNameValuePair(KEY_OWNID, c.getString(c.getColumnIndex(KEY_OWNID))));
            map.add(new BasicNameValuePair(KEY_IMG, image_str));
            map.add(new BasicNameValuePair(KEY_LATITUDE, c.getString(c.getColumnIndex(KEY_LATITUDE))));
            map.add(new BasicNameValuePair(KEY_LONGITUDE, c.getString(c.getColumnIndex(KEY_LONGITUDE))));
            
            wordList.add(index, map);
            index++;
        } while (c.moveToNext());
        Log.d("my","return cash product-"+wordList.size());
        return wordList;
      }
      return wordList;
    }
	
	
	// count the number of new entries
	public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM "+DATABASE_TABLE+" WHERE status=0";
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        count = cursor.getCount();
        return count;
    }
	
	
	// count all entries of sql-light
	public int dbAllCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM "+DATABASE_TABLE+"";
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        count = cursor.getCount();
        return count;
    }
	
	// to get the id of the data to be synchronized
	public ArrayList<String> selectid() throws SQLException 
    {	
    	ArrayList<String> keyList;
	    keyList=new ArrayList<String>();
    		        
	String[] col={KEY_ID};
    Cursor c=mDb.query(DATABASE_TABLE, col, "status=1", null, null, null, null);
    Log.d("my","selected id-"+c.getCount()+"");
    if (c.moveToFirst()) {
    	int index=0;
    	do {
        	keyList.add(index,c.getString(c.getColumnIndex(KEY_ID)));
            index++;
        } while (c.moveToNext());
        Log.d("my","return selected id"+keyList.size());
        return keyList;
    }
    return keyList;
    }
	
	// delete data from sql-light which is already senchronized
	public void deleteRow(String key){
	int del=mDb.delete(DATABASE_TABLE, KEY_ID+"="+key+"", null);
	Log.d("my","delete called"+del);
	}
	
	// update status 
	public void updateRow(String key)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put("status", 1);
		mDb.update(DATABASE_TABLE, initialValues, KEY_ID+"="+key+"", null);
	}
}