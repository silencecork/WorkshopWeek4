package com.android.demo.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity {
	
	private DatabaseHelper dbHelper;
	private Cursor c;
	// Code for this demo
	long contextMenuDBRowId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button addBtn = (Button) findViewById(R.id.button1);
        addBtn.setOnClickListener(addClick);
        
        dbHelper = new DatabaseHelper(Main.this);
        loadData();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
		if (c != null) {
			c.close();
		}
	}
    // Code for this demo
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Log.i("DBDemo", "onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.clear();
		menu.add(0, 0, 0, R.string.menu_modify);
		menu.add(0, 1, 1, R.string.menu_delete);
		contextMenuDBRowId = (Long) v.getTag();
	}
    // Code for this demo
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i("DBDemo", "onContextItemSelected");
		int id = item.getItemId();
		if (id == 0) {
			
		} else if (id == 1) {
			
		}
		return true;
	}
    
    private void loadData() {
    	LinearLayout root = (LinearLayout) findViewById(R.id.root);
    	root.removeAllViews();
    	c = dbHelper.onQuery();
    	if (c != null) {
    		while (c.moveToNext()) {
    			long id = c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_ID));
    			String content = c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CONTENT));
    			// Code for this demo
    			addTextView(content, id);
    		}
    	}
    }
    
    // Code for this demo
    private void addTextView(String content, long id) {
    	LinearLayout root = (LinearLayout) findViewById(R.id.root);
		TextView text = new TextView(Main.this);
		text.setText(content);
		text.setTextSize(30);
		text.setSingleLine(true);
		// Code for this demo
		text.setTag(id);
		text.setOnClickListener(modifyClick);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 72);
		root.addView(text, 0, params);
		
		View divider = new View(Main.this);
		LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1);
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
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_CONTENT, content);
			values.put(DatabaseHelper.COLUMN_MODIFY, System.currentTimeMillis());
			long id = dbHelper.onInsert(values);
			Log.i("DBDemo", "insert id " + id);
			loadData();
			input.setText("");
		}
    	
    };
    // Code for this demo
    private OnClickListener modifyClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Main.this.registerForContextMenu(v);
			Main.this.openContextMenu(v);
		}
    	
    };
    
}