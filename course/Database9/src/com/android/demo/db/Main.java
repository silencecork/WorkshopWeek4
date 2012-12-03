package com.android.demo.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Main extends Activity {
	
	private DatabaseHelper dbHelper;
	private Cursor c;
	long contextMenuDBRowId;
	private static final int MODIFY_DB_CODE = 100;
	// Code for this demo
	private MyCursorAdapter adapter;
	
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
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		Log.i("DBDemo", "onCreateContextMenu " + v);
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.clear();
		menu.add(0, 0, 0, R.string.menu_modify);
		menu.add(0, 1, 1, R.string.menu_delete);
		contextMenuDBRowId = (Long) v.getTag();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i("DBDemo", "onContextItemSelected");
		int id = item.getItemId();
		if (id == 0) {
			Intent intent = new Intent();
			intent.setClassName("com.android.demo.db", "com.android.demo.db.ModifyContent");
			intent.putExtra("id", contextMenuDBRowId);
			startActivityForResult(intent, MODIFY_DB_CODE);
		} else if (id == 1) {
			int count = dbHelper.onDelete(contextMenuDBRowId);
			Log.i("DBDemo", "delete result " + count);
			if (count > 0) {
				loadData();
			}
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, 0, 0, R.string.menu_delete_all);
		item.setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int count = dbHelper.onDelete();
		Log.i("DBDemo", "delete result " + count);
		if (count > 0) {
			loadData();
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MODIFY_DB_CODE && resultCode == RESULT_OK) {
			if (data != null) {
				long modifiedId = data.getLongExtra("modified_id", -1);
				Log.i("DBDemo", "modified id " + modifiedId);
			}
			loadData();
		}
	}
    
    private void loadData() {
    	// Code for this demo
    	ListView list = (ListView) findViewById(R.id.list);
    	if (c == null) {
    		c = dbHelper.onQuery();
    		adapter = new MyCursorAdapter(Main.this, c);
    		list.setAdapter(adapter);
    	} else {
    		c.requery();
    		if (adapter != null) {
    			adapter.notifyDataSetChanged();
    		}
    	}
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
    
}