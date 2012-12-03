package com.android.demo.fileutils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FileUtils {

	public static String readFromInternal(Context context, String fileName) {
		if (context == null || TextUtils.isEmpty(fileName)) {
			return null;
		}
		String content = null;
		try {
			FileInputStream in = context.openFileInput(fileName);
			content = readFromFile(in);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		return content;
	}

	public static boolean writeToInternal(Context context, String fileName,
			String content, int permission) {
		if (context == null || TextUtils.isEmpty(fileName)) {
			return false;
		}
		if (permission < 0 || permission > 2) {
			permission = Context.MODE_PRIVATE;
		}
		try {
			FileOutputStream out = context.openFileOutput(fileName, permission);
			return writeToFile(out, content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public static String readFromExtrnal(Context context, String fileName) {
		if (!checkExternalStorage()) {
			return null;
		}
		File external = Environment.getExternalStorageDirectory();
		String filePath = external.getAbsolutePath() + "/" + fileName;
		Log.i("readFromExtrnal", "filePath " + filePath);
		if (context == null || TextUtils.isEmpty(fileName)) {
			return null;
		}
		
		String content = null;
		try {
			FileInputStream in = new FileInputStream(filePath);
			content = readFromFile(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		
		return content;
	}
	
	public static boolean writeToExternal(Context context, String fileName,
			String content) {
		if (!checkExternalStorage()) {
			return false;
		}
		File external = Environment.getExternalStorageDirectory();
		String filePath = external.getAbsolutePath() + "/" + fileName;
		Log.i("writeToExternal", "filePath " + filePath);
		if (context == null || TextUtils.isEmpty(fileName)) {
			return false;
		}
		
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			return writeToFile(out, content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		boolean ret;
		if ((ret = Environment.MEDIA_MOUNTED.equals(state))) {
			Log.w("checkExternalStorage", "You don't have memory card to store file");
		}
		return ret;
	}
	
	private static String readFromFile(FileInputStream in) {
		ByteArrayOutputStream out = null;
		String content = null;
		try {
			out = new ByteArrayOutputStream();
			int nByte = 0;
			byte[] buffer = new byte[1024];
			while ((nByte = in.read(buffer)) > 0) {
				out.write(buffer, 0, nByte);
			}
			content = new String(out.toByteArray());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(in);
			close(out);
		}
		return content;
	}
	
	private static boolean writeToFile(FileOutputStream out, String content) {
		try {
			out.write(content.getBytes());
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(out);
		}
		return false;
	}

	private static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}