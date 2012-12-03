package com.android.demo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private String dbCreateSQL;
	private static final String DBNAME = "my_db";
	private static final int DBVERSION = 1;
	private static final String TABLENAME = "my_table";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_MODIFY = "modifiy_time";

	public DatabaseHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
		dbCreateSQL = "CREATE TABLE " + TABLENAME + "("
		+ COLUMN_ID + " INTEGER PRIMARY KEY," 
		+ COLUMN_CONTENT + " TEXT,"
		+ COLUMN_MODIFY + " INTEGER"
		+ ")";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("DatabaseHelper", "onCreate");
		db.execSQL(dbCreateSQL);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DatabaseHelper", "onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
		onCreate(db);
	}

	public long onInsert(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		long id = db.insert(TABLENAME, null, values);
		return id;
	}
	
	public Cursor onQuery() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TABLENAME, new String[] {COLUMN_ID, COLUMN_CONTENT, COLUMN_MODIFY}, 
				null, null, null, null, COLUMN_MODIFY + " DESC");
		return c;
	}
	
	public int onDelete(long id) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLENAME, COLUMN_ID + "=" + id, null);
	}
	
	public int onDelete() {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLENAME, null, null);
	}
	
	public Cursor onQuery(long id) {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TABLENAME, new String[] {COLUMN_ID, COLUMN_CONTENT, COLUMN_MODIFY}, 
				COLUMN_ID + "=" + id, null, null, null, COLUMN_MODIFY + " DESC");
		return c;
	}
	
	public int onUpdate(long id, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		int updateCount = db.update(TABLENAME, values, COLUMN_ID + "=" + id, null);
		return updateCount;
	}
}

