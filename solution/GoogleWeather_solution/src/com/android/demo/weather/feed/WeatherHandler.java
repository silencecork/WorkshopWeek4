package com.android.demo.weather.feed;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class WeatherHandler extends DefaultHandler {
	
	private boolean inCurrentCondition;
	private boolean inForecast;
	private CurrentWeather currWeather;
	private ForecastWeather forecastWeather;
	private ArrayList<ForecastWeather> list = new ArrayList<ForecastWeather>();

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if ("current_conditions".equals(localName)) {
			inCurrentCondition = false;
		}
		if ("forecast_conditions".equals(localName)) {
			inForecast = false;
			list.add(forecastWeather);
			forecastWeather = null;
		}
	}


	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if ("current_conditions".equals(localName)) {
			Log.i("", "enter current condition");
			inCurrentCondition = true;
			currWeather = new CurrentWeather();
		}
		if ("forecast_conditions".equals(localName)) {
			inForecast = true;
			forecastWeather = new ForecastWeather();
		}
		if ("condition".equals(localName)) {
			if (inCurrentCondition) {
				String currCondition = attributes.getValue("data");
				currWeather.condition = currCondition;
			} else if (inForecast) {
				String forecastCondition = attributes.getValue("data");
				forecastWeather.condition = forecastCondition;
			}
		}
		if ("temp_f".equals(localName)) {
			if (inCurrentCondition) {
				String strTempature = attributes.getValue("data");
				int tempature = Integer.parseInt(strTempature);
				currWeather.tempature = FahrenheitTocentigrade(tempature);
			}
		}
		if ("humidity".equals(localName)) {
			if (inCurrentCondition) {
				String strHumidity = attributes.getValue("data");
				int index = strHumidity.indexOf(": ");
				String strPartialHumidity = (index != -1) ? strHumidity.substring(index + 2) : null;
				currWeather.humidity = strPartialHumidity;
			}
		}
		if ("day_of_week".equals(localName)) {
			String dayOfWeek = attributes.getValue("data");
			forecastWeather.dayOfWeek = dayOfWeek(dayOfWeek);
		}
		if ("low".equals(localName)) {
			String strTempature = attributes.getValue("data");
			int tempature = Integer.parseInt(strTempature);
			forecastWeather.lowTempature = FahrenheitTocentigrade(tempature);
		}
		if ("high".equals(localName)) {
			String strTempature = attributes.getValue("data");
			int tempature = Integer.parseInt(strTempature);
			forecastWeather.highTempature = FahrenheitTocentigrade(tempature);
		}
	}
	
	public void processFeed(String strUrl) {
        try {
        	URL url = new URL(strUrl);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
            xr.parse(new InputSource(url.openStream()));
               
        } catch (IOException e) {
            Log.e("", e.toString());
        } catch (SAXException e) {
            Log.e("", e.toString());
        } catch (ParserConfigurationException e) {
            Log.e("", e.toString());
        }
	}
	
	public int dayOfWeek(String strDay) {
		if ("Mon".equalsIgnoreCase(strDay)) {
			return 1;
		} else if ("Tue".equalsIgnoreCase(strDay)) {
			return 2;
		} else if ("Wed".equalsIgnoreCase(strDay)) {
			return 3;
		} else if ("Thu".equalsIgnoreCase(strDay)) {
			return 4;
		} else if ("Fri".equalsIgnoreCase(strDay)) {
			return 5;
		} else if ("Sat".equalsIgnoreCase(strDay)) {
			return 6;
		} else if ("Sun".equalsIgnoreCase(strDay)) {
			return 7;
		} else {
			return -1;
		}
	}
	
	public int FahrenheitTocentigrade(int fahrenheit) {
		return (fahrenheit - 32) * 5 / 9;
	}
	
	public CurrentWeather getCurrentWeather() {
		return currWeather;
	}
	
	public ForecastWeather[] getForecastWeathers() {
		if (list == null || list.size() <= 0) {
			return null;
		}
		ForecastWeather[] ret = new ForecastWeather[list.size()];
		list.toArray(ret);
		
		return ret;
	}
	
}
