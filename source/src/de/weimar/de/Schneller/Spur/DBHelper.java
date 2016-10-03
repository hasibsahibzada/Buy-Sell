package de.weimar.de.Schneller.Spur;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
	// Database and the product table
	private static final String DATABASE_NAME = "buysell";
	private static final int DATABASE_VERSION = 5;
	private static final String DATABASE_TABLE1 = "product";
	private static final String DATABASE_CREATE1 = "create table "+DATABASE_TABLE1+"("
	+ "Pid integer primary key autoincrement, "
	+ "Pname text not null, "
	+ "Description varchar(50), "
	+ "Price integer not null, "
	+ "Owner_ID text, "
	+ "image blob,"
	+ "Latitude Double,"
	+ "Longitude Double,"
	+ "status integer);";
	
	
	// Profile table
	private static final String DATABASE_TABLE2 = "profile";
	
	private static final String DATABASE_CREATE2 = "create table "+DATABASE_TABLE2+"("
			+ "Uid integer, "
			+ "Name text not null, "
			+ "lastname text, "
			+ "age integer , "
			+ "Gender text, "
			+ "emailaddr text,"
			+ "username text,"
			+ "password text);";     // uses if the user is online or offline

	
	public DBHelper(Context context) {
		// create our database
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try
		{
		db.execSQL(DATABASE_CREATE1);   // create the Product table
		db.execSQL(DATABASE_CREATE2);  // create the Profile table
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS members");
		onCreate(db);
	}

}
