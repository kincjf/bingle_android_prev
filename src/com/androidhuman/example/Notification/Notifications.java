package com.androidhuman.example.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidhuman.example.CameraPreview2.CameraPreview;
import com.androidhuman.example.CameraPreview2.R;

public class Notifications {

	CameraPreview main;

	public Notifications(CameraPreview main) {
		this.main = main;
	}
	
	public NotificationManager nm;

	public void notification_start() {
		nm = (NotificationManager) main
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final Notification.Builder mBuilder = new Notification.Builder(this.main);
		mBuilder.setSmallIcon(R.drawable.icon);

		mBuilder.setTicker("사진변환을 시작 합니다.");
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setNumber(10);
		mBuilder.setContentTitle("사진변환 중입니다.....");
		mBuilder.setContentText("ProgressBar Message");

		//mBuilder.setProgress(0, 0, true);
		mBuilder.setContentText("wait please....");
		mBuilder.setOngoing(true);

		nm.notify(666, mBuilder.getNotification());
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				for (int i = 1; i <= 100; i++) {
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//
//					mBuilder.setProgress(100, i, false);
//					mBuilder.setContentText("사진 변환 : " + i + "%");
//					nm.notify(666, mBuilder.getNotification());
//
//					if (i >= 100) {
//						nm.cancel(666);
//						NotificationS();
//					}
//				}
//			}
//		}).start();
	}

	public void notification_suc() {
		nm.cancel(666);
		nm = (NotificationManager) main
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(main, GalleryActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this.main, 0, intent,
				0);

		Notification.Builder builder = new Notification.Builder(
				main.getApplicationContext())
				.setContentTitle("사진 변환 완료")
				.setContentText("사진을 보시려면 눌러주세요")
				.setTicker("사진 변환이 완료 되었습니다.")
				.setSmallIcon(R.drawable.icon)
				.setAutoCancel(true)
				.setWhen(System.currentTimeMillis())
				.setDefaults(
						Notification.DEFAULT_SOUND
								| Notification.DEFAULT_VIBRATE
								| Notification.DEFAULT_LIGHTS).setNumber(13)
				.setContentIntent(pIntent);

		Notification n = builder.getNotification();
		nm.notify(1234, n);
	}

}
