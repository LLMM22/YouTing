package com.example.testvolley;

import android.app.NotificationManager;


public interface MainActivityCallBack {

	public void setProgress();
	public void setPage();
	public void initCallBack();
	public void setUserInfo();
	public void setNotificationIntent();
	public NotificationManager showCustomView();
}
