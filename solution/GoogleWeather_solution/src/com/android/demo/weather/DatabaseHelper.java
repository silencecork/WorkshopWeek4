package com.android.demo.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "weather";
	private static final int DB_VERSION = 1;
	
	public static final String TABLE_CURR_WEATHER = "curr_weather";
	public static final String TABLE_FORECAST = "forecast_weather";
	
	public static final String COLUMN_CURR_ID = "_id";
	public static final String COLUMN_CURR_CONDITION = "condition";
	public static final String COLUMN_CURR_TEMPERATURE = "temperature";
	public static final String COLUMN_CURR_HUMIDITY = "humidity";
	
	public static final String COLUMN_FORECAST_ID = "_id";
	public static final String COLUMN_FORECAST_DAYOFWEEK = "day_of_week";
	public static final String COLUMN_FORECAST_HIGH_TEMPERATURE = "high";
	public static final String COLUMN_FORECAST_LOW_TEMPERATURE = "low";
	public static final String COLUMN_FORECAST_CONDITION = "condition";
	
	private static final String SQL_CREATE_TABLE_CURR = "CREATE TABLE " + TABLE_CURR_WEATHER + "("
			+ COLUMN_CURR_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_CURR_CONDITION + " TEXT,"
			+ COLUMN_CURR_TEMPERATURE + " INTEGER,"
			+ COLUMN_CURR_HUMIDITY + " INTEGER"
			+ ")";
	
	private static final String SQL_CREATE_TABLE_FORECAST = "CREATE TABLE " + TABLE_FORECAST + "("
			+ COLUMN_FORECAST_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_FORECAST_DAYOFWEEK + " INTEGER,"
			+ COLUMN_FORECAST_HIGH_TEMPERATURE + " INTEGER,"
			+ COLUMN_FORECAST_LOW_TEMPERATURE + " INTEGER,"
			+ COLUMN_FORECAST_CONDITION + " TEXT"
			+ ")";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE_CURR);
		db.execSQL(SQL_CREATE_TABLE_FORECAST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURR_WEATHER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
		onCreate(db);
	}
	
	public long insert(String tableName, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		return db.insert(tableName, null, values);
	}
	
	public Cursor query(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(tableName, null, null, null, null, null, null);
	}
	
	public int delete(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(tableName, null, null);
	}
	
}
