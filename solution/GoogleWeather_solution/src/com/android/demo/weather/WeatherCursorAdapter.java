package com.android.demo.weather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherCursorAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private Context context;
	
	public WeatherCursorAdapter(Context c, Cursor cursor) {
		super(c, cursor);
		inflater = LayoutInflater.from(c);
		context = c;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		combineDataToView(v, c);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item, null);
		combineDataToView(v, c);
		return v;
	}
	
	private void combineDataToView(View v, Cursor c) {
		String condition = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_FORECAST_CONDITION));
		int dayOfWeek = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_FORECAST_DAYOFWEEK));
		String strDatOfWeek = getDayOfWeekString(dayOfWeek);
		int high = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_FORECAST_HIGH_TEMPERATURE));
		String strHight = context.getString(R.string.temperature, high);
		int low = c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_FORECAST_LOW_TEMPERATURE));
		String strLow = context.getString(R.string.temperature, low);
		
		TextView text = (TextView) v.findViewById(R.id.dayofweek);
		text.setText(strDatOfWeek);
		
		TextView tempature = (TextView) v.findViewById(R.id.tempature);
		tempature.setText(strHight + " / " + strLow);
		
		int resId = getItemIcon(condition);
		ImageView icon = (ImageView) v.findViewById(R.id.icon);
		icon.setImageResource(resId);
	}
	
	private String getDayOfWeekString(int dayOfWeek) {
		int index = dayOfWeek - 1;
		if (index < 0 || index > 6) {
			return "";
		}
		String[] strDayOfWeek = context.getResources().getStringArray(R.array.day_of_week);
		return strDayOfWeek[index];
	}
	
	private int getItemIcon(String condition) {
		if (condition.contains("sunny") || condition.contains("Sunny")) {
			return R.drawable.sunny;
		} else if (condition.contains("rain") || condition.contains("Rain")) {
			return R.drawable.rain;
		}  else if (condition.contains("storm") || condition.contains("Storm")) {
			return R.drawable.storm;
		} else {
			return R.drawable.cloud;
		}
	}

}
