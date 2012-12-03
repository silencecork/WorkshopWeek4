package com.silencecork.demo.fb.api;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;

public class Utils {
    
    public static String getId(String content) {
        if (content == null)
            return null;
        try {
            JSONObject object = new JSONObject(content);
            String id = object.getString("id");
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String streamToString(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            int size = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
            if (size <= 0) {
                return null;
            }
            return out.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static void streamToFile(InputStream in, String loc) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(loc);
            byte[] buffer = new byte[1024];
            int len;
            int size = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
            if (size <= 0) {
                return;
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(out);
        }
    }
    
    public static void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public static long parseAtomTimestamp(String timestamp) {
        int index = timestamp.indexOf("+");
        if (index != -1) {
            timestamp = timestamp.substring(0, index);
            timestamp += ".000Z";
            Time time = new Time();
            time.parse3339(timestamp);
            return time.toMillis(true);
        }
        return 0L;
    }
}
