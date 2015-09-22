package com.androidhuman.example.Notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.androidhuman.example.CameraPreview2.R;

public class GalleryActivity extends Activity {

	
	private static final int PICK_FROM_GALLERY = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
//		Intent intent = new Intent();
//		
//		intent.setType("image/*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
////		Log.i("yoon", "income gallery Class");
//		try{
//			
//			startActivityForResult(intent.createChooser(intent, "Complete"),PICK_FROM_GALLERY);
//		} catch (ActivityNotFoundException e){
//			
//		}
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 2);
		// notification 매니저 생성
		Log.i("yoon","start img view");
		NotificationManager nm = 
				(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		// 등록된 notification 을 제거 한다.
		nm.cancel(1234);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
	        if (data == null) {
	            //Display an error
	            return;
	        }
	        
	        Uri selectedImage = data.getData();
	        String[] filePathColumn = { MediaStore.Images.Media.DATA };

	        Cursor cursor = getContentResolver().query(selectedImage,
	                filePathColumn, null, null, null);
	        cursor.moveToFirst();

	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        String picturePath = cursor.getString(columnIndex);
	        cursor.close();

	        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
//	        image.setImageBitmap(bitmap);

	        if (bitmap != null) {
	            ImageView rotate = (ImageView) findViewById(R.id.img_result);
	            rotate.setImageBitmap(bitmap);

	        }
	        //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
	    }
	}
}
