package com.android.demo.weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.demo.weather.feed.CurrentWeather;
import com.android.demo.weather.feed.ForecastWeather;
import com.android.demo.weather.feed.WeatherFeed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends Activity {
	
	private static final String TAG = "Main";
	DatabaseHelper dbHelper;
	WeatherCursorAdapter adapter;
	Cursor forecastCursor;
	Cursor currWeatherCursor;
	String currentCity;
	AsyncTask<Void, Void, Void> task;
	MusicPlayer player;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        currentCity = getCurrentCityName();
        dbHelper = new DatabaseHelper(Main.this);
        player = new MusicPlayer(Main.this, R.raw.music);
        player.start();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
        String city = getCurrentCityName();
		if (!city.equals(currentCity)) {
			currentCity = city;
			dbHelper.delete(DatabaseHelper.TABLE_FORECAST);
			dbHelper.delete(DatabaseHelper.TABLE_CURR_WEATHER);
		}
		
		// Enter your code here
		
		getDataFromDB();
		if (forecastCursor == null || forecastCursor.getCount() <= 0
				|| currWeatherCursor == null
				|| currWeatherCursor.getCount() <= 0) {
			getDataFromInternetAndSaveToDB();
			getDataFromDB();
			if (forecastCursor == null
					|| forecastCursor.getCount() <= 0
					|| currWeatherCursor == null
					|| currWeatherCursor.getCount() <= 0) {
				Log.e(TAG, "There is no cursor");
			}
		}
		
		createCurrWeatherUI();
		ListView list = (ListView) findViewById(R.id.forecast_list);
		adapter = new WeatherCursorAdapter(Main.this, forecastCursor);
		list.setAdapter(adapter);
	}

	@Override
    public void onDestroy() {
    	super.onDestroy();
    	closeAllCursor();
    	if (dbHelper != null) {
    		dbHelper.close();
    	}
    	if (task != null) {
    		task.cancel(true);
    	}
    	if (player != null) {
    		player.stop();
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
			task = new AsyncTask<Void, Void, Void>() {
	        	
	        	ProgressDialog progress;
	        	
	        	@Override
				protected void onPreExecute() {
					super.onPreExecute();
					progress = new ProgressDialog(Main.this);
					progress.setMessage(getString(R.string.wait));
					progress.show();
				}
	        	
				@Override
				protected Void doInBackground(Void... arg0) {
			        getDataFromInternetAndSaveToDB();
			        getDataFromDB();
			        if (forecastCursor == null || forecastCursor.getCount() <= 0 || 
			            currWeatherCursor == null || currWeatherCursor.getCount() <= 0) {
			        	Log.e(TAG, "There is no cursor");
			        }
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					createCurrWeatherUI();
					ListView list = (ListView) findViewById(R.id.forecast_list);
			        adapter = new WeatherCursorAdapter(Main.this, forecastCursor);
			        list.setAdapter(adapter);
					progress.dismiss();
				}
	        	
	        };
	        task.execute();
		} else if (id == R.id.menu_setting) {
			Intent intent = new Intent();
			intent.setClassName("com.android.demo.weather", "com.android.demo.weather.Settings");
			startActivity(intent);
		}
		return true;
	}
	
	private void closeAllCursor() {
    	if (forecastCursor != null) {
    		forecastCursor.close();
    		forecastCursor = null;
    	}
    	if (currWeatherCursor != null) {
    		currWeatherCursor.close();
    		currWeatherCursor = null;
    	}
    }
	
    private void getDataFromDB() {
    	forecastCursor = dbHelper.query(DatabaseHelper.TABLE_FORECAST);
    	currWeatherCursor = dbHelper.query(DatabaseHelper.TABLE_CURR_WEATHER);
    }
    
    private void getDataFromInternetAndSaveToDB() {
    	Log.i("Weather", "get data from internet");
    	WeatherFeed feed = new WeatherFeed();
        boolean syncResult = feed.processUrl(currentCity);
        if (syncResult) {
        	CurrentWeather weather = feed.getCurrentWeather();
        	ForecastWeather[] forecasts = feed.getForecasts();
        	saveCurrentToDB(weather);
        	saveForecastToDB(forecasts);
        }
    }

	private void saveForecastToDB(ForecastWeather[] forecasts) {
		dbHelper.delete(DatabaseHelper.TABLE_FORECAST);
    	for (int i = 0; i < forecasts.length; i++) {
    		ContentValues values = new ContentValues();
    		ForecastWeather forecast = forecasts[i];
    		values.put(DatabaseHelper.COLUMN_FORECAST_CONDITION, forecast.condition);
    		values.put(DatabaseHelper.COLUMN_FORECAST_DAYOFWEEK, forecast.dayOfWeek);
    		values.put(DatabaseHelper.COLUMN_FORECAST_HIGH_TEMPERATURE, forecast.highTempature);
    		values.put(DatabaseHelper.COLUMN_FORECAST_LOW_TEMPERATURE, forecast.lowTempature);
    		dbHelper.insert(DatabaseHelper.TABLE_FORECAST, values);
    	}
    }
    
    private void saveCurrentToDB(CurrentWeather weather) {
    	dbHelper.delete(DatabaseHelper.TABLE_CURR_WEATHER);
    	ContentValues values = new ContentValues();
    	values.put(DatabaseHelper.COLUMN_CURR_CONDITION, weather.condition);
    	values.put(DatabaseHelper.COLUMN_CURR_TEMPERATURE, weather.tempature);
    	values.put(DatabaseHelper.COLUMN_CURR_HUMIDITY, weather.humidity);
    	dbHelper.insert(DatabaseHelper.TABLE_CURR_WEATHER, values);
    }
    
    private String getCurrentCityName() {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Main.this);
        String cityValue = pref.getString(getString(R.string.key_city), "0");
        int cityIndex = Integer.parseInt(cityValue);
        String[] cities = getResources().getStringArray(R.array.city_entries);
        return cities[cityIndex];
    }
    
    private void createCurrWeatherUI() {
    	Calendar calendar = Calendar.getInstance();
    	Date date = calendar.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("EEE, yyyy.MMMM.dd");
    	String time = format.format(date);
    	
    	TextView currDate = (TextView) findViewById(R.id.current_date);
    	currDate.setText(time);
    	
    	if (currWeatherCursor != null && currWeatherCursor.getCount() >= 1 && currWeatherCursor.moveToFirst()) {
    		TextView currTempature = (TextView) findViewById(R.id.current_tempature);
    		int tempature = currWeatherCursor.getInt(currWeatherCursor.getColumnIndex(DatabaseHelper.COLUMN_CURR_TEMPERATURE));
    		String strTempature = getString(R.string.temperature, tempature);
    		currTempature.setText(strTempature);
    		
    		String condition = currWeatherCursor.getString(currWeatherCursor.getColumnIndex(DatabaseHelper.COLUMN_CURR_CONDITION));
    		int weatherIconRes = getItemIcon(condition);
    		ImageView weatherImage = (ImageView) findViewById(R.id.current_weathers);
    		weatherImage.setImageResource(weatherIconRes);
    		
    		TextView currCity = (TextView) findViewById(R.id.current_city);
            String city = getCurrentCityName();
    		currCity.setText(city);
    	}
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