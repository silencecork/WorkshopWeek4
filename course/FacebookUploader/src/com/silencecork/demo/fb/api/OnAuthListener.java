package com.silencecork.demo.fb.api;
public interface OnAuthListener {
		public void onError(String username, String password, int errorCode);
		public void onSuccess(String userId, String username, String password, String token, long time);
	}