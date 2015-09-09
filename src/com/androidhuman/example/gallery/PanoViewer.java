package com.androidhuman.example.gallery;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.panorama.Panorama;
import com.google.android.gms.panorama.PanoramaApi.PanoramaResult;


/**
 * 
 * @author sumin
 * 디바이스 내에 있는 파노라마 사진을 불러와서 뷰어로 보여줌
 * 
 * 
 *https://github.com/JcDenton86/cordova-plugin-panoramaviewer/blob/master/src/gr/jcdenton/photosphere/PanoramaViewer.java * 1. ���� ��� �ҷ��ͼ� ���� �����
 */
public class PanoViewer extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	 public static final String TAG = "exam";
	 public String filepath = null;
	
	    private GoogleApiClient mClient;
	   
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("test", "panoviewer");
		Intent intent = getIntent();
		filepath = intent.getExtras().getString("path");
		
		mClient = new GoogleApiClient.Builder(this, this, this)
                .addApi(Panorama.API)
                .build();
		
	 }
	 
	 @Override
	    public void onStart() {
	        super.onStart();
	        mClient.connect();
	    }
	 
	 @Override
	    public void onConnected(Bundle connectionHint) {
		 Log.i(TAG, filepath);
		        Panorama.PanoramaApi.loadPanoramaInfo(mClient, getUriFromPath(filepath)).setResultCallback(
	                new ResultCallback<PanoramaResult>() {
	            @Override
	            public void onResult(PanoramaResult result) {
	                if (result.getStatus().isSuccess()) {
	                    Intent viewerIntent = result.getViewerIntent();
	                    Log.i(TAG, "�ѳ�?2");
	                    Log.i(TAG, "found viewerIntent: " + viewerIntent);
	                    if (viewerIntent != null) {
	                        startActivity(viewerIntent);
	                        Log.i(TAG, "�ѳ�?2");
	                    }
	                } else {
	                    Log.e(TAG, "error: " + result);
	                }
	            }
	        });
	    }
	 
	 
	 @Override
	    public void onConnectionSuspended(int cause) {
	        Log.i(TAG, "connection suspended: " + cause);
	    }

	    @Override
	    public void onConnectionFailed(ConnectionResult status) {
	        Log.e(TAG, "connection failed: " + status);
	        // TODO fill in
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        mClient.disconnect();
	    }
	    /**
	     * 
	     * @param path
	     * uri를 filepath로 변환 시켜준다.
	     * @return
	     */
	    public Uri getUriFromPath(String path){
		    
		    Uri fileUri = Uri.parse(path);
		    String filePath = fileUri.getPath();
		    Cursor c = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + filePath + "'", null, null );
		    c.moveToNext();
		    int id = c.getInt( c.getColumnIndex( "_id" ));
		    Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );

		    return uri;
	    }
}
	

