package com.android.demo.fb.uploader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.silencecork.demo.fb.api.FacebookApi;

public class Uploader extends Activity {
	
	private static final String TAG = "Uploader";
	private Bitmap b;
	private FacebookApi mFBApi;
	private AsyncTask<Void, Void, Boolean> task;
	private String dataPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.uploader);
		
		mFBApi = new FacebookApi(Uploader.this);
        if (!mFBApi.isLogin()) {
        	mFBApi.login();
        }
		
		Intent intent = getIntent();
		String action = intent.getAction();
		Uri photoUri;
		if (action!= null && action.equals(Intent.ACTION_SEND)) {
			photoUri = (Uri)intent.getExtras().getParcelable(Intent.EXTRA_STREAM);
		} else {
			photoUri = intent.getData();
		}
		Log.i(TAG, "photo Uri " + photoUri);
		ContentResolver resolver = getContentResolver();
		Cursor c = resolver.query(photoUri, new String[] {Images.ImageColumns.DATA}, null, null, null);
		if (c != null && c.moveToFirst()) {
			dataPath = c.getString(c.getColumnIndex(Images.ImageColumns.DATA));
			c.close();
			b = decodeFile(dataPath);
		}
		
		if (b == null) {
			finish();
			return;
		}
		
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		image.setImageBitmap(b);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (b != null) {
			b.recycle();
		}
		if (task != null) {
			task.cancel(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_logout) {
			if (mFBApi.logout()) {
				Toast.makeText(Uploader.this, R.string.logout_success, Toast.LENGTH_LONG).show();
				finish();
			}
		}else if (id == R.id.menu_share) {
			
			EditText input = (EditText) findViewById(R.id.desc);
			final String message = input.getText().toString();
			
			task = new AsyncTask<Void, Void, Boolean>() {
				ProgressDialog progress;
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					progress = new ProgressDialog(Uploader.this);
					progress.setMessage(getString(R.string.wait));
					progress.show();
				}
				
				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					progress.dismiss();
					if (result) {
						Toast.makeText(Uploader.this, R.string.result_success, Toast.LENGTH_LONG).show();
						finish();
					} else {
						Toast.makeText(Uploader.this, R.string.result_fail, Toast.LENGTH_LONG).show();
					}
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					String retUrl = mFBApi.uploadPhoto(dataPath, message);
					return (retUrl != null);
				}
				
			};
			task.execute();
		} 
		return true;
	}
	
	private Bitmap decodeFile(String dataPath) {
		if (TextUtils.isEmpty(dataPath)) {
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(dataPath, opts);
		Log.v(TAG, "bitmap width " + opts.outWidth + ", height " + opts.outHeight);
		int maxLength = 800;
		float scale;
		if (opts.outWidth > opts.outHeight) {
			scale = (float)opts.outWidth / (float)maxLength;
		} else {
			scale = (float)opts.outHeight / (float)maxLength;
		}
		Log.i(TAG, "sample size " + scale);
		opts.inSampleSize = (int) scale;
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		return BitmapFactory.decodeFile(dataPath, opts);
	}
}
