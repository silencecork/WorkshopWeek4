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
	

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// enter your code here
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// enter your code here
	}
	
	public long insert(String tableName, ContentValues values) {
		// enter your code here
		return 0;
	}
	
	public Cursor query(String tableName) {
		// enter your code here
		return null;
	}
	
	public int delete(String tableName) {
		// enter your code here
		return 0;
	}
	
}
