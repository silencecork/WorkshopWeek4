package com.android.demo.datataipei;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "taipei_data";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "hotspot_table";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_LOCATION_TYPE = "loc_type";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_HOTSPOT_TYPE = "hotspot_type";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	
	public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY," 
			+ COLUMN_TITLE + " TEXT,"  
			+ COLUMN_LOCATION_TYPE + " TEXT," 
			+ COLUMN_ADDRESS + " TEXT,"
			+ COLUMN_HOTSPOT_TYPE + " TEXT,"
			+ COLUMN_LATITUDE + " DOUBLE,"
			+ COLUMN_LONGITUDE + " DOUBLE"
			+ ")";
			

	public MyDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public long onInsert(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(TABLE_NAME, null, values);
	}
	
	public Cursor onQuery() {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(TABLE_NAME, 
				new String[] {COLUMN_ID, COLUMN_TITLE, COLUMN_LOCATION_TYPE, 
				COLUMN_ADDRESS, COLUMN_HOTSPOT_TYPE, COLUMN_LATITUDE, COLUMN_LONGITUDE}, 
				null, null, null, null, COLUMN_ID + " ASC");
	}
	
	public Cursor onQuery(long id) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(TABLE_NAME, 
				new String[] {COLUMN_ID, COLUMN_TITLE, COLUMN_LOCATION_TYPE, 
				COLUMN_ADDRESS, COLUMN_HOTSPOT_TYPE, COLUMN_LATITUDE, COLUMN_LONGITUDE}, 
				COLUMN_ID + "=" + id, null, null, null, COLUMN_ID + " ASC");
	}
	
	public Cursor onQuery(String where) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(TABLE_NAME, 
				new String[] {COLUMN_ID, COLUMN_TITLE, COLUMN_LOCATION_TYPE, 
				COLUMN_ADDRESS, COLUMN_HOTSPOT_TYPE, COLUMN_LATITUDE, COLUMN_LONGITUDE}, 
				where, null, null, null, COLUMN_ID + " ASC");
	}
	
	public int onDelete() {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_NAME, null, null);
	}
	
}
