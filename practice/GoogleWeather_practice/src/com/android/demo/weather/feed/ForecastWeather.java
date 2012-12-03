package com.android.demo.weather.feed;

public class ForecastWeather {
	public int dayOfWeek;
	public int highTempature;
	public int lowTempature;
	public String condition;
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("dayOfWeek: " + dayOfWeek);
		buffer.append(" high/low: " + highTempature + "/" + lowTempature);
		buffer.append(" condition: " + condition);
		return buffer.toString();
	}
	
}