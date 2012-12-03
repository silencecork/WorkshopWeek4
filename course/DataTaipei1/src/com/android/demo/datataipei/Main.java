package com.android.demo.datataipei;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.demo.datataipei.feed.HotSpot;
import com.android.demo.datataipei.feed.TaipeiData;

public class Main extends Activity {
	
	private static final String TAG = "Main";
	MyDatabaseHelper dbHelper;
	MyCursorAdapter adapter;
	Cursor c;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        dbHelper = new MyDatabaseHelper(Main.this);
        loadData();
        if (c != null) {
    		adapter = new MyCursorAdapter(Main.this, c);
    	}
		ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClick);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if (c != null) {
    		c.close();
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_refresh) {
			refresh();
		}
		return true;
	}

	private boolean loadData() {
    	if (c == null) {
    		c = dbHelper.onQuery();
    	}
    	if (c == null || c.getCount() <= 0) {
    		HotSpot[] spots = loadFromInternet();
    		saveToDB(spots);
    		c = dbHelper.onQuery();
    	} 
    	return (c != null);
    }
    
    private HotSpot[] loadFromInternet() {
    	Log.i(TAG, "load data from internet");
    	TaipeiData data = new TaipeiData();
		HotSpot[] hotSpots = data.processData();
		return hotSpots;
    }
    
    private void saveToDB(HotSpot[] hotSpots) {
    	Log.i(TAG, "save to database");
    	int count = hotSpots.length;
    	for (int i = 0; i < count; i++) {
    		HotSpot spot = hotSpots[i];
    		ContentValues values = new ContentValues();
    		values.put(MyDatabaseHelper.COLUMN_TITLE, spot.title);
    		values.put(MyDatabaseHelper.COLUMN_LOCATION_TYPE, spot.LocationType);
    		values.put(MyDatabaseHelper.COLUMN_ADDRESS, spot.address);
    		values.put(MyDatabaseHelper.COLUMN_HOTSPOT_TYPE, spot.hotspotType);
    		values.put(MyDatabaseHelper.COLUMN_LATITUDE, spot.latitude);
    		values.put(MyDatabaseHelper.COLUMN_LONGITUDE, spot.longitude);
    		dbHelper.onInsert(values);
    	}
    }
    
    private void refresh() {
    	HotSpot[] spots = loadFromInternet();
    	if (spots != null && spots.length > 0) {
    		dbHelper.onDelete();
    		saveToDB(spots);
    	}
    	if (adapter != null) {
    		adapter.notifyDataSetChanged();
    	}
    }
    
    private OnItemClickListener onItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			ListView list = (ListView) parent;
			Cursor c = (Cursor) list.getItemAtPosition(pos);
			String title = c.getString(c.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
			String address = c.getString(c.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
			final double lat = c.getDouble(c.getColumnIndex(MyDatabaseHelper.COLUMN_LATITUDE));
			final double lng = c.getDouble(c.getColumnIndex(MyDatabaseHelper.COLUMN_LONGITUDE));
			
			AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
			builder.setTitle(R.string.details);
			builder.setMessage(title + "/n" + address);
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
					Uri data = Uri.parse("google.streetview:cbll=" + lat + "," + lng + "&cbp=1,0,,0,1.0&mz=19");
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