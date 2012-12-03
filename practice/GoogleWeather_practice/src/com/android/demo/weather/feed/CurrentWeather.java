package com.android.demo.weather.feed;

public class CurrentWeather {
	public String condition;
	public int tempature;
	public String humidity;
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("condition: " + condition);
		buffer.append(" tempature: " + tempature);
		buffer.append(" humidity: " + humidity);
		return buffer.toString();
	}
	
}
