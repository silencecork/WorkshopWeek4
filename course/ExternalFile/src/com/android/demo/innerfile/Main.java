package com.android.demo.innerfile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.android.demo.fileutils.FileUtils;

public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
    }

    @Override
	protected void onResume() {
		super.onResume();
		EditText input = (EditText) findViewById(R.id.editText1);
		String content = FileUtils.readFromExtrnal(Main.this, "my_text.txt");
		input.setText(content);
	}
    
	@Override
	protected void onPause() {
		super.onPause();
		EditText input = (EditText) findViewById(R.id.editText1);
		String content = input.getText().toString();
		boolean result = FileUtils.writeToExternal(Main.this, "my_text.txt", content);
		Log.i("saveFile", "save to file result " + result);
	}
}

