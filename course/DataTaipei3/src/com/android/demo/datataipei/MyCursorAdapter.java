package com.android.demo.datataipei;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {
	LayoutInflater inflater;
	public MyCursorAdapter(Context context, Cursor c) {
		super(context, c);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		setViewInfo(v, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item, null);
		setViewInfo(v, cursor);
		return v;
	}
	
	private void setViewInfo(View v, Cursor cursor) {
		ImageView iconImage = (ImageView) v.findViewById(R.id.icon);
		TextView titleText = (TextView) v.findViewById(R.id.title);
		TextView addressText = (TextView) v.findViewById(R.id.address);
		String title = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_TITLE));
		String address = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_ADDRESS));
		String hotSpotType = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.COLUMN_HOTSPOT_TYPE));
		if (hotSpotType.contains("室外公共場所")) {
			iconImage.setImageResource(R.drawable.wifi2);
		} else {
			iconImage.setImageResource(R.drawable.wifi1);
		}
		titleText.setText(title);
		addressText.setText(address);
		
	}

}

