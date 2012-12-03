package com.android.demo.db;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {

	public MyCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		long id = c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_ID));
		String content = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
		TextView text = (TextView) v;
		text.setText(content);
		text.setTextSize(30);
		text.setSingleLine(true);
		text.setTag(id);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		TextView text = new TextView(context);
		ListView.LayoutParams params = 
				new ListView.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 
						72);
		text.setLayoutParams(params);
		long id = c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_ID));
		String content = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
		text.setText(content);
		text.setTextSize(30);
		text.setSingleLine(true);
		text.setTag(id);
		return text;
	}

}
