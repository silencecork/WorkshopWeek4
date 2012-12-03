package com.silencecork.demo.fb.api;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class FBLoginDialog extends Dialog implements OnLoadListener {
	
	private RelativeLayout mRoot;
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private WebView mWebView;
	public static final String REDIRECT_URI = "fbconnect://success";
	public static final String APP_ID = FacebookParams.APP_ID;
	public static final String[] PERMISSIONS = FacebookParams.PERMISSIONS;
	
	public static final Uri OAUTH_ENDPOINT = Uri.parse("https://graph.facebook.com/oauth/authorize");
    public static final String CANCEL_URI = "fbconnect:cancel";
    static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    
    private String mAuthURL;
    private OnAuthListener mListener;
    
	public FBLoginDialog(Context context, String url, OnAuthListener listener) {
		super(context);
		mContext = context;
		mAuthURL = url;
		mListener = listener;
	}
	
	public FBLoginDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	protected FBLoginDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoot = new RelativeLayout(mContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgressDialog.setMessage(FacebookParams.ASYNC_WAIT_MESSAGE);
		init(mContext, mAuthURL, mListener);
		setContentView(mRoot, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, 
				FrameLayout.LayoutParams.WRAP_CONTENT));
    }

	private void init(Context context, String authUrl, OnAuthListener listener) {
		mWebView = new WebView(context);
		mWebView.setVisibility(View.VISIBLE);
		mWebView.setWebViewClient(new FacebookWebClient(mContext, listener, this));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(authUrl);
		mWebView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 
                LayoutParams.FILL_PARENT));
		mRoot.addView(mWebView);
	}

	@Override
	public void onShowDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.show();
		}
	}

	@Override
	public void onDismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
	
	
}
