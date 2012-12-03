package com.android.demo.preference;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;

public class Main extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = getSharedPreferences("my_test", MODE_PRIVATE);
		String content = prefs.getString("input_text", "");
		boolean isToggle = prefs.getBoolean("is_toggle", false);
		boolean isChecked = prefs.getBoolean("is_checked", false);
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		CheckBox box = (CheckBox) findViewById(R.id.checkBox1);
		EditText input = (EditText) findViewById(R.id.editText1);
		toggle.setChecked(isToggle);
		box.setChecked(isChecked);
		input.setText(content);
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = getSharedPreferences("my_test", MODE_PRIVATE);
		Editor edit = prefs.edit();
		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		boolean isToggle = toggle.isChecked();
		edit.putBoolean("is_toggle", isToggle);
        CheckBox box = (CheckBox) findViewById(R.id.checkBox1);
        boolean isChecked = box.isChecked();
        edit.putBoolean("is_checked", isChecked);
        EditText input = (EditText) findViewById(R.id.editText1);
        String content = input.getText().toString();
        edit.putString("input_text", content);
        edit.commit();
	}
}

