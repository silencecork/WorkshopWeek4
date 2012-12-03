package com.android.demo.datataipei;

import com.android.demo.datataipei.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Search extends Activity {
	
	private MyDatabaseHelper dbHelper;
	private MyCursorAdapter adapter;
	private Cursor c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.search);
		
		dbHelper = new MyDatabaseHelper(Search.this);
		
		Button searchBtn = (Button) findViewById(R.id.btn_search);
		searchBtn.setOnClickListener(searchClick);
		
		ListView list = (ListView) findViewById(R.id.search_result);
		list.setOnItemClickListener(onItemClick);
		list.setFastScrollEnabled(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (c != null) {
			c.close();
		}
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
	
	private OnClickListener searchClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			EditText input = (EditText) findViewById(R.id.input_search);
			String searchText = input.getText().toString();
			if (c != null) {
				c.close();
			}
			c = dbHelper.onQuery(MyDatabaseHelper.COLUMN_ADDRESS + " like" + "'%" + searchText + "%'");
			adapter = new MyCursorAdapter(Search.this, c);
			ListView list = (ListView) findViewById(R.id.search_result);
			list.setAdapter(adapter);
		}
		
	};
	
	private OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			ListView list = (ListView) parent;
			Cursor c = (Cursor) list.getItemAtPosition(pos);
			String title = c.getString(c.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
			String address = c.getString(c.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
			final double lat = c.getDouble(c.getColumnIndex(MyDatabaseHelper.COLUMN_LATITUDE));
			final double lng = c.getDouble(c.getColumnIndex(MyDatabaseHelper.COLUMN_LONGITUDE));
			
			AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
			builder.setTitle(R.string.details);
			builder.setMessage(title + "\n" + address);
			builder.setPositiveButton(R.string.btn_open_map, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri data = Uri.parse("geo:" + lat + "," + lng);
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(data);
					startActivity(intent);
				}
			});
			builder.setNegativeButton(R.string.btn_open_street, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri data = Uri.parse("google.streetview:cbll=" + lat + "," + lng + "&cbp=0,0,,0,1.0&mz=19");
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(data);
					startActivity(intent);
				}
			});
			builder.show();
		}
		
	};

}
