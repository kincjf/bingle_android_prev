package com.androidhuman.example.CameraPreview2;
/* 
 * Copyright (C) 2011 Tae-Ho, Kim.
 * This example contains some code from ApiDemos, The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.androidhuman.example.application.MyApp;
import com.androidhuman.example.utils.SocketUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";
    private MyApp mMyApp;
    
    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    
    Camera mCamera;
    int camCount;
    int currCamIdx = 0;
    
    Context mContext;

    private void init(Context context){
    	mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        camCount = Camera.getNumberOfCameras();
        
        CameraInfo info = new CameraInfo();
        for(int i=0; i<camCount; i++){
        	Camera.getCameraInfo(i, info);
        	if(info.facing == CameraInfo.CAMERA_FACING_BACK)
        		currCamIdx = i;
        }
        
        mContext = context;
        mMyApp = (MyApp)context.getApplicationContext();
    }
    
    public Preview(Context context) {
        super(context);
        init(context);
    }
    
    public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public void openCamera(){
		if(mCamera==null){
			mCamera = Camera.open(currCamIdx);
			preparePreviewSize();
		}
	}

	public void preparePreviewSize(){
		mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, 
        		getResources().getDisplayMetrics().widthPixels, 
        		getResources().getDisplayMetrics().heightPixels);
	}
	
	public void setCamera(Camera camera) {
        mCamera = camera;
        preparePreviewSize();
    }

    public void switchCamera() {
    	releaseCamera();
    	currCamIdx = (++currCamIdx)%camCount;
    	setCamera(Camera.open(currCamIdx));
    	try {
    		mCamera.setPreviewDisplay(mHolder);
    	} catch (IOException exception) {
    		Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
    	}
    	Camera.Parameters parameters = mCamera.getParameters();
    	parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
    	mCamera.setParameters(parameters);
    	mCamera.setDisplayOrientation(90);
    	
    	mCamera.startPreview();
    	requestLayout();
    }
    public void test(){
    	if(mCamera != null){
    		mCamera.stopPreview();
    	}
    }
    public void test2(){
    	if(mCamera != null){
    		mCamera.startPreview();
    	}
    }
    public void test3(){
    	if(mCamera != null){
    		mCamera.stopPreview();
    		mCamera.release();
    	}
    }
    
    public void releaseCamera(){
    	if(mCamera != null){
    		mCamera.stopPreview();
    		mCamera.release();
    		mCamera = null;
    	}
    }
    
    public interface testEventListener{
    	void OnTestevnet(String test);
    }
    
    private testEventListener mTestListener;
    public void setTestEvent(testEventListener listener){
    	mTestListener = listener;
    }
    
	AutoFocusCallback mAutoFocus = new AutoFocusCallback() {

 		public void onAutoFocus(boolean success, Camera camera) {

 		}

 	};

    
    public void takePicture(final String title, final String description){
    	mCamera.autoFocus(mAutoFocus);
    	mCamera.takePicture(new ShutterCallback() {
			
			@Override
			public void onShutter() {
				// TODO Auto-generated method stub
				
			}
		}, null, new PictureCallback(){

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length);	//이미지객체생성
				
				if(picture!=null){
                    File file=new File(SocketUtils.TEMP_FOLDER_PATH);
                    if(!file.isDirectory()){
                        file.mkdir();
                    }

                    file=new File(SocketUtils.TEMP_FOLDER_PATH,System.currentTimeMillis()+".jpg");
                    
                    // 이미지를 찍는 각도(가로, 세로)에 맞추어 사진을 회전시켜 기본 방향으로 재조정한다.
				    int rotation = mMyApp.getCurrentActivity().getWindowManager().getDefaultDisplay().getRotation();
				    int exifDegree =  getOrientation(rotation);
				    picture = rotate(picture, exifDegree);
                    try 
                    {
                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                        picture.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        
                        //Uri outUri = Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/Pictures/pastel");
                        Uri test = Uri.fromFile(file);
        		        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,test));
        		        
        				Toast.makeText(mContext, "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        				if(mTestListener != null)mTestListener.OnTestevnet("success");
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                    catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }

                }
				
                camera.startPreview();
                
				/*
				String outUriStr = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), picture, title, description);
				
				Uri outUri = Uri.parse(outUriStr);
		        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,outUri));
				Toast.makeText(mContext, "이미지가 저장되었습니다."+outUriStr, Toast.LENGTH_SHORT).show();
				Log.i("test","경로 : "+outUriStr);
				mCamera.startPreview();
				*/
			}
    		
    	});
    }
    public int getOrientation(int rotation)
    {
    	switch (rotation) {
        case Surface.ROTATION_0:
                return 90;
        case Surface.ROTATION_90:
                return 0;
        case Surface.ROTATION_270:
                return 180;
        }
    	return 0;
    }
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
      if(degrees != 0 && bitmap != null)
      {
        Matrix m = new Matrix();
        m.setRotate(degrees, (float) bitmap.getWidth() / 2, 
        (float) bitmap.getHeight() / 2);
        
        try
        {
          Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
          bitmap.getWidth(), bitmap.getHeight(), m, true);
          if(bitmap != converted)
          {
            bitmap.recycle();
            bitmap = converted;
          }
        }
        catch(OutOfMemoryError ex)
        {
          // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
        }
      }
      return bitmap;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
                //Toast.makeText(mContext, "onLayout()if", Toast.LENGTH_SHORT).show();
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                /*
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);*/
                child.layout(0, 0,
                        width, height);
            }
        }
    }
    
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        
        int targetHeight = h;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        

        return optimalSize;
    }
    
    /**
     * Set the clockwise rotation of preview display in degrees.
     * This affects the preview frames and the picture displayed after snapshot.
     * This method is useful for portrait mode applications.
     *  
     * @param activity
     * @param cameraId
     * @param camera
     * {@link http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)}
     */
    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        
        int degrees = 0;
        switch (rotation) {
        case Surface.ROTATION_0:
                degrees = 0;
                break;
        case Surface.ROTATION_90:
                degrees = 90;
                break;
        case Surface.ROTATION_180:
                degrees = 180;
                break;
        case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
                result = (info.orientation - degrees + 360) % 360;
        }
         
        camera.setDisplayOrientation(result);
    }
    
    //implements
    public void surfaceCreated(SurfaceHolder holder) {
        try {
        	//Toast.makeText(mContext, "surfaceCreated()", 0).show();
            if (mCamera != null) {
            	setCameraDisplayOrientation(mMyApp.getCurrentActivity(), this.currCamIdx, this.mCamera);
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
            mCamera.release();
            mCamera = null;
        }
    }
    //implements
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }
    //implements
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	if(mCamera != null){
    		//Toast.makeText(mContext, "surfaceChanged()", 0).show();
	        Camera.Parameters parameters = mCamera.getParameters();
	        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	        mCamera.setParameters(parameters);
        	
	        setCameraDisplayOrientation(mMyApp.getCurrentActivity(), this.currCamIdx, this.mCamera);

//	        mCamera.setDisplayOrientation(90);
	        //camera.setDisplayOrientation(90); // potrait 
//	        mCamera.setDisplayOrientation(180); // landscape
	        mCamera.startPreview();
    	}
    }

}