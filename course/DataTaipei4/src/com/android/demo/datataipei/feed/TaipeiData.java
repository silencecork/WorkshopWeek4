package com.android.demo.datataipei.feed;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TaipeiData {
	
	private static final String TAG = "TaipeiData";
	private static final String TAIPEI_DATA_URL = " http://taipeicityopendata.cloudapp.net/v1/TaipeiOGDI/wifi/?format=json";
	
	public TaipeiData() {
		
	}
	
	public HotSpot[] processData() {
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			URL url = new URL(TAIPEI_DATA_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60000);
			conn.connect();
			int code = conn.getResponseCode();
			Log.i(TAG, "get data result " + code);
			if (code == HttpURLConnection.HTTP_OK) {
				in = conn.getInputStream();
				
				String content = streamToString(in);
				JSONObject json = new JSONObject(content);
				JSONArray array = json.getJSONArray("d");
				int count = array.length();
				ArrayList<HotSpot> list = new ArrayList<HotSpot>();
				for (int i = 0; i < count; i++) {
					JSONObject obj = array.getJSONObject(i);
					String name = obj.getString("熱點名稱");
					String type = obj.getString("類別"); 
					String address = obj.getString("地址");
					String hotSpotType = obj.getString("熱點類別");
					String strLongitude = obj.getString("經度");
					String strLatitude = obj.getString("緯度");
					double longitude = Double.parseDouble(strLongitude);
					double latitude = Double.parseDouble(strLatitude);
					
					HotSpot hotSpot = new HotSpot();
					hotSpot.title = name;
					hotSpot.LocationType = type;
					hotSpot.address = address;
					hotSpot.hotspotType = hotSpotType;
					hotSpot.latitude = latitude;
					hotSpot.longitude = longitude;
					list.add(hotSpot);
				}
				if (list.size() > 0) {
					HotSpot hotspots[] = new HotSpot[list.size()];
					list.toArray(hotspots);
					return hotspots;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			close(in);
			disconnect(conn);
		}
		return null;
	}
	
	public void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect(HttpURLConnection conn) {
		if (conn != null) {
			conn.disconnect();
		}
	}
	
	@SuppressWarnings("unused")
	private boolean streamToFile(InputStream in, String fileName) {
		if (in == null || fileName == null) {
			return false;
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			byte[] buffer = new byte[1024];
			int nByte = 0;
			int len = 0;
			while ((nByte = in.read(buffer)) > 0) {
				out.write(buffer, 0, nByte);
				len += nByte;
			}
			return (len > 0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(out);
		}
		return false;
	}
	
	private String streamToString(InputStream in) {
		if (in == null) {
			return null;
		}
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			try {
				Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				int n;
			
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return writer.toString();
		} finally {
			close(writer);
		}
	}
	
}
