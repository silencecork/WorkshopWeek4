package com.android.demo.weather.feed;

import android.net.Uri;
import android.util.Log;

public class WeatherFeed {

	private static final String WEATHER_FEED = "http://www.google.com/ig/api";
	private WeatherHandler handler;
	private ForecastWeather[] weathers;
	CurrentWeather currWeather;
	public WeatherFeed() {
		handler = new WeatherHandler();
	}
	
	public boolean processUrl(String city) {
		Uri u = Uri.parse(WEATHER_FEED);
		Uri.Builder builder = u.buildUpon();
		builder.appendQueryParameter("hl", "us");
		builder.appendQueryParameter("weather", city);
		String url = builder.build().toString();
		Log.v("", "run url " + url);
		handler.processFeed(url);
		currWeather = handler.getCurrentWeather();
		Log.i("", "current weather " + currWeather);
		weathers = handler.getForecastWeathers();
		return (currWeather != null && weathers != null);
	}
	
	public ForecastWeather[] getForecasts() {
		return weathers;
	}
	
	public CurrentWeather getCurrentWeather() {
		return currWeather;
	}
	
}
