package com.androidhuman.example.Notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

public class GalleryActivity extends Activity {

	
	private static final int PICK_FROM_GALLERY = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent();
		
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		try{
			
			startActivityForResult(intent.createChooser(intent, "Complete"),PICK_FROM_GALLERY);
		} catch (ActivityNotFoundException e){
			
		}
		// notification 매니저 생성
		NotificationManager nm = 
				(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		// 등록된 notification 을 제거 한다.
		nm.cancel(1234);
	}
}
