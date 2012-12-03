package com.android.demo.fb.uploader;

import com.silencecork.demo.fb.api.FacebookApi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	
	private static final int SELECT_PHOTO = 10;
	private static final String TAG = "Main";
	private FacebookApi mFBApi;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button selectBtn = (Button) findViewById(R.id.btn_select);
        selectBtn.setOnClickListener(selectClick);
        mFBApi = new FacebookApi(Main.this);
        if (!mFBApi.isLogin()) {
        	mFBApi.login();
        }
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				Log.i(TAG, "uri u " + uri);
				if (uri != null) {
					Intent intent = new Intent();
					intent.setClassName("com.android.demo.fb.uploader", "com.android.demo.fb.uploader.Uploader");
					intent.setData(uri);
					startActivity(intent);
				}
			}
		}
	}
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.menu_logout);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mFBApi.logout()) {
			Toast.makeText(Main.this, R.string.logout_success, Toast.LENGTH_LONG).show();
		}
		return true;
	}

	private OnClickListener selectClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, SELECT_PHOTO);
		}
    	
    };
}