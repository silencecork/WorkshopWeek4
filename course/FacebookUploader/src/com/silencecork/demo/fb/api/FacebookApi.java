package com.silencecork.demo.fb.api;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class FacebookApi {
    
    private static final String TAG = "FacebookApi";
    private static final Uri GRAPH_BASE_URI = Uri.parse("https://graph.facebook.com/");
    private static final Uri FACEBOOK_URI = Uri.parse("https://www.facebook.com");
    private static final String ENCODE = "UTF-8";
    private Activity mContext;
    private FBLoginDialog mDialog;
    private OnAuthListener mListener = new OnAuthListener() {

		@Override
		public void onError(String username, String password, int errorCode) {
			Log.i(TAG, "login fail ");
		}

		@Override
		public void onSuccess(String userId, String username, String password,
				String token, long time) {
			Log.i(TAG, "login success " + token);
			SharedPreferences pref = mContext.getSharedPreferences("fb_info", Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = pref.edit();
			edit.putString("token", token);
			edit.commit();
		}
    	
    };
    
    public FacebookApi(Activity context) {
        mContext = context;
    }
    
    public boolean isLogin() {
    	SharedPreferences pref = mContext.getSharedPreferences("fb_info", Context.MODE_PRIVATE);
    	String token = pref.getString("token", null);
    	return !TextUtils.isEmpty(token);
    }
    
    String getToken() {
    	SharedPreferences pref = mContext.getSharedPreferences("fb_info", Context.MODE_PRIVATE);
    	String token = pref.getString("token", null);
    	return token;
    }
    
    public boolean logout() {
    	SharedPreferences pref = mContext.getSharedPreferences("fb_info", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = pref.edit();
		edit.remove("token");
		return edit.commit();
    }
    
    public void login() {
    	if (isLogin()) {
    		return;
    	}
//        clearCookies(mContext);
        doOAuthLogin();
    }
    
    private void doOAuthLogin() {
        Uri.Builder builder = FBLoginDialog.OAUTH_ENDPOINT.buildUpon();
        builder.appendQueryParameter("client_id", FBLoginDialog.APP_ID);
        builder.appendQueryParameter("scope", TextUtils.join(",", FBLoginDialog.PERMISSIONS));
        builder.appendQueryParameter("type", "user_agent");
        builder.appendQueryParameter("redirect_uri", FBLoginDialog.REDIRECT_URI);
        builder.appendQueryParameter("display", "touch");
        
        String authUrl = builder.build().toString();
        Log.i(TAG, "auth url " + authUrl);
        mDialog = new FBLoginDialog(mContext, authUrl, new OnAuthListener() {

            @Override
            public void onError(String username, String password, int errorCode) {
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.onError(username, password, errorCode);
                }
            }

            @Override
            public void onSuccess(String userId, String username, String password, String token, long time) {
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.onSuccess(userId, username, password, token, time);
                }
            }
            
        });
        
        mDialog.show();
    }
    
    @SuppressWarnings("unused")
	private void clearCookies(Context context) {
        // Edge case: an illegal state exception is thrown if an instance of 
        // CookieSyncManager has not be created.  CookieSyncManager is normally
        // created by a WebKit view, but this might happen if you start the 
        // app, restore saved state, and click logout before running a UI 
        // dialog in a WebView -- in which case the app crashes
        CookieSyncManager cookieSyncMngr = 
            CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    
    public String uploadPhoto(String path, String message) {
    	
    	if (!isLogin()) {
    		return null;
    	}
    	
    	String token = getToken();
    	
        if (TextUtils.isEmpty(token)) {
            throw new NullPointerException("Must provide token to perform upload operation");
        }
        if (TextUtils.isEmpty(path)) {
            throw new NullPointerException("upload photo can not be null");
        }
        File f = new File(path);
        if (!f.exists()) {
            Log.e(TAG, "file doesn't exist");
            throw new NullPointerException("file doesn't exist");
        }
        Log.d(TAG, "upload to facebook");
        
        String mimeType = "image/jpg";
        int index = path.lastIndexOf(".");
        if (index > 0) {
            String extension = path.substring(index, path.length());
            if ("png".equals(extension)) {
                mimeType = "image/png";
            } else if ("gif".equals(extension)) {
                mimeType = "image/gif";
            }
        }
        
        HttpURLConnection conn = null;
        try {
            
            Uri.Builder builder = GRAPH_BASE_URI.buildUpon();
            //if (albumId != null) {
            //    builder.appendEncodedPath(albumId);
            //} else {
                builder.appendEncodedPath("me");
            //}
            builder.appendEncodedPath("photos");
            builder.appendQueryParameter("access_token", token);
            builder.appendQueryParameter("format", "json");
            String url = builder.build().toString();
            Log.d(TAG, "url for upload " + url);
            
            FileInputStream in = null;
            DataOutputStream data = null;
            try {
                in = new FileInputStream(f);
                String boundary = Long.toHexString(System.currentTimeMillis());
                URL u = new URL(url);
                conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream out = conn.getOutputStream();
                data = new DataOutputStream(out);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("access_token", token));
                Log.v(TAG, "upload summary " + message);
                params.add(new BasicNameValuePair("message", message));
                ListIterator<NameValuePair> itr = params.listIterator();
                while (itr.hasNext()) {
                    data.write(("--" + boundary + "\r\n").getBytes(ENCODE));
                    NameValuePair pair = itr.next();
                    data.write(("Content-Disposition: form-data; name=\"" + pair.getName() + "\"\r\n\r\n").getBytes(ENCODE));
                    data.write(((pair.getValue() + "\r\n").getBytes(ENCODE)));
                }
                data.write(("--" + boundary + "\r\n").getBytes(ENCODE));
                data.write(("Content-Disposition: form-data; name=\"photo\"; filename=\"" + path + "\"\r\n").getBytes(ENCODE));
                data.write(("Content-Transfer-Encoding: binary\r\n").getBytes(ENCODE));
                data.write(("Content-Type: " + mimeType + "\r\n\r\n").getBytes(ENCODE));
                
                byte[] buf = new byte[512];
                int res = -1;
                float fileLength = f.length();
                
//              Log.d(TAG, "start upload data " + mDataPath + " available size " + totalSize);
                
                int notify = 0;
                float totalCount = 0;
                
                
                while ((res = in.read(buf)) != -1) {
                    data.write(buf);
                    totalCount += res;
                    int progress = (int)((totalCount / fileLength) * 100);
                    if (++notify % 10 == 0 || progress >= 100) {
//                      Log.v(TAG, "upload data " + mDataPath + ": " + progress);
                    }
                    
                }
                data.write(("\r\n--" + boundary + "--\r\n").getBytes(ENCODE));
                
                conn.connect();
                data.flush();
                
                int status = conn.getResponseCode();
                Log.v(TAG, "response code: " + status + " " + conn.getResponseMessage());
                if (status == HttpURLConnection.HTTP_OK) {
                    InputStream responseContent = conn.getInputStream();
                    if (responseContent != null) {
                        String strContent = Utils.streamToString(responseContent);
                        String id = Utils.getId(strContent);
                        Log.i(TAG, "response content " + strContent);
                        Uri.Builder resultUriBuilder = FACEBOOK_URI.buildUpon();
                        resultUriBuilder.appendPath(id);
                        String finalUrl = resultUriBuilder.build().toString();
                        Log.d(TAG, "end upload data " + path + ", final url " + finalUrl);
                        return finalUrl; 
                    }
                }
                
//              Log.v(TAG, "update row " + mId + " stat to STAT_COMPLETE result " + updated);
                
                return null;
            } finally {
                Utils.close(data);
                Utils.close(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "upload file occur exception", e);
        } finally {
            Utils.disconnect(conn);
        }
        return null;
    }
}
