package com.androidhuman.example.application;

import android.app.Activity;
import android.app.Application;


/**
 * 
 * @author Seonho Kim
 * 전역변수 관리를 위한 Pool
 */
public class MyApp extends Application {
	public void onCreate() {
		super.onCreate();
	}

	private Activity mCurrentActivity = null;

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public void setCurrentActivity(Activity mCurrentActivity) {
		this.mCurrentActivity = mCurrentActivity;
	}
}
