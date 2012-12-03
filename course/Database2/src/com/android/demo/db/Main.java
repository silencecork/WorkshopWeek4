package com.android.demo.db;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity {
	
	private DatabaseHelper dbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button addBtn = (Button) findViewById(R.id.button1);
        addBtn.setOnClickListener(addClick);
        // Code for this demo
        dbHelper = new DatabaseHelper(Main.this);
    }
    
    private void addTextView(String content) {
    	LinearLayout root = (LinearLayout) findViewById(R.id.root);
		TextView text = new TextView(Main.this);
		text.setText(content);
		text.setTextSize(30);
		
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 
						72);
		
		root.addView(text, 0, params);
		
		View divider = new View(Main.this);
		LinearLayout.LayoutParams dividerParams = 
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, 1);
		divider.setBackgroundColor(Color.WHITE);
		root.addView(divider, 1, dividerParams);
    }

	private OnClickListener addClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			EditText input = (EditText) findViewById(R.id.editText1);
			String content = input.getText().toString();
			if (TextUtils.isEmpty(content)) {
				return;
			}
			// Code for this demo
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_CONTENT, content);
			values.put(DatabaseHelper.COLUMN_MODIFY, System.currentTimeMillis());
			long id = dbHelper.onInsert(values);
			Log.i("DBDemo", "insert id " + id);
			addTextView(content);
			input.setText("");
		}
    	
    };
    
}

