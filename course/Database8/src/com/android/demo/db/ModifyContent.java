package com.android.demo.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ModifyContent extends Activity {
	long id;
	private DatabaseHelper dbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify);
		dbHelper = new DatabaseHelper(ModifyContent.this);
		Intent intent = getIntent();
		id = intent.getLongExtra("id", -1);
		Cursor c = dbHelper.onQuery(id);
		if (c == null || c.getCount() <= 0) {
			finish();
			return;
		}
		c.moveToFirst();
		String content = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
		EditText input = (EditText) findViewById(R.id.editText1);
		input.setText(content);
		Button btnCancel = (Button) findViewById(R.id.button2);
		Button btnSave = (Button) findViewById(R.id.button1);
		btnCancel.setOnClickListener(cancelListener);
		btnSave.setOnClickListener(saveListener);
	}
	OnClickListener cancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED); // Code for this demo
			finish();
		}
	};
	OnClickListener saveListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			EditText input = (EditText) findViewById(R.id.editText1);
			String content = input.getText().toString();
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_CONTENT, content);
			int updateCount = dbHelper.onUpdate(id, values);
			Log.i("Modify", "updateCount " + updateCount);
			Intent intent = new Intent(); // Code for this demo
			intent.putExtra("modified_id", id); // Code for this demo
			setResult(RESULT_OK, intent); // Code for this demo
			finish();
		}
	};
}
