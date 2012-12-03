package com.silencecork.demo.fb.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class FacebookWebClient extends WebViewClient {

	private static final String TAG = "FacebookWebClient";
	private OnAuthListener mListener;
	private OnLoadListener mOnLoadListener;
	private Context mContext;
	public static final String REDIRECT_URI = "fbconnect://success";
	public static final String CANCEL_URI = "fbconnect:cancel";

	public FacebookWebClient(Context context, OnAuthListener listener,
			OnLoadListener onLoadListener) {
		mListener = listener;
		mOnLoadListener = onLoadListener;
		mContext = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.d(TAG, "shouldOverrideUrlLoading " + url);
		if (url.startsWith(REDIRECT_URI)) {
			Bundle values = parseResult(url);
			String token = values.getString("access_token");
			@SuppressWarnings("unused")
			String expireIn = values.getString("expires_in");
			// saveToken(mContext, token, expireIn);
			// fbconnect://success/?error_reason=user_denied
			String error = values.getString("error_reason");
			if (error == null) {
				view.setVisibility(View.GONE);
				if (mListener != null) {
					mListener.onSuccess(null, null, null, token, 0L);
				}
			} else {
				Log.e(TAG, "error " + error);
				Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
				if (mListener != null) {
					// TODO return error
					mListener.onError(null, null, -1);
				}
			}
			hideWaitDialog();
			return true;
		} else if (url.startsWith(CANCEL_URI)) {
			if (mListener != null) {
				// return correct error
				mListener.onError(null, null, -1);
			}
			return true;
		} else if (url.contains("touch")) {
			return false;
		}
		return true;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		Log.d(TAG, "onReceivedError " + failingUrl + " errCode " + errorCode
				+ ", desc " + description);
		if (mListener != null) {
			// return correct error
			mListener.onError(null, null, -1);
		}
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		Log.d(TAG, "onPageStarted " + url);
		super.onPageStarted(view, url, favicon);
		showWaitDialog();
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		Log.d(TAG, "onPageFinished " + url);
		super.onPageFinished(view, url);
		hideWaitDialog();
	}

	private void showWaitDialog() {
		// mProgressDialog.show();
		if (mOnLoadListener != null) {
			mOnLoadListener.onShowDialog();
		}
	}

	private void hideWaitDialog() {
		// mProgressDialog.dismiss();
		if (mOnLoadListener != null) {
			mOnLoadListener.onDismissDialog();
		}
	}

	private Bundle parseResult(String url) {
		Bundle ret = new Bundle();
		Uri uri = Uri.parse(url);
		String fragment = uri.getFragment();
		if (fragment != null) {
			String[] splitStr = fragment.split("&");
			for (String s : splitStr) {
				String[] keyValPair = s.split("=");
				Log.d(TAG, "key " + keyValPair[0] + ", value " + keyValPair[1]);
				ret.putString(keyValPair[0], keyValPair[1]);
			}
		}
		String queries = uri.getQuery();
		if (queries != null) {
			String[] params = queries.split("&");
			for (String s : params) {
				String[] keyValPair = s.split("=");
				Log.d(TAG, "key " + keyValPair[0] + ", value " + keyValPair[1]);
				ret.putString(keyValPair[0], keyValPair[1]);
			}
		}
		return ret;
	}
}