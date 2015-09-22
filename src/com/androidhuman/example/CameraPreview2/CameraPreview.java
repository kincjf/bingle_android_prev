package com.androidhuman.example.CameraPreview2;

import java.io.File;

import com.androidhuman.example.Notification.Notifications;
import com.androidhuman.example.application.MyApp;
import com.androidhuman.example.blutooth.BluetoothClientThread;
import com.androidhuman.example.blutooth.DeviceListActivity;
import com.androidhuman.example.gallery.GalleryActivity;
import com.androidhuman.example.socket.Socket_command;
import com.androidhuman.example.utils.CustomIO;
import com.androidhuman.example.utils.SocketUtils;
import com.androidhuman.example.utils.ZipUtils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * 
 * @author Hyun
 *
 * App의 메인화면이자 카메라를 실행시키는 클래스
 * Preview.java를 통하여 카메라 객체를 컨트롤 하게된다.
 * 블루투스 및 서버와의 소켓통신 객체가 존재함.
 */
public class CameraPreview extends Activity {
	

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode==66){
			Log.i("test","keyup"+event.getKeyCode());
			mCaptureButton.requestFocus();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == event.KEYCODE_VOLUME_UP){
			Log.i("test","onKeyDown - Key_Volume_up");
			return true;
		}else if(keyCode == 66){
			Log.i("test","onKeyDown - Key_Enter");
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	public Handler handler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			switch(msg.what){
			case SocketUtils.BLUETOOTH_CONNECTION:
				btn2.setBackgroundColor(Color.BLUE);
				break;
			case SocketUtils.BLUETOOTH_CONNECTION_CLOSE:
				btn2.setBackgroundColor(Color.RED);
				break;
			}
			
			return false;
		}
	});

	private Preview mPreview;
	private Button mCaptureButton;

	// ------------------------------------------------
	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
	protected MyApp mMyApp;
	
	BluetoothAdapter mBTAdapter;
	BluetoothClientThread btclientThread = null;
	Context mContext;
	Button btn2,btntest,img_btn;
	BluetoothDevice device;
	MotionEvent motion;
	static public CameraPreview main;
	public Notifications notification = null;
	
	public void takePicture(){
		mPreview.takePicture("","");
	}
	
	public Button getBtn2(){
		return btn2;
	}
	
	BroadcastReceiver bluetoothReceiver =  new BroadcastReceiver(){
	    public void onReceive(Context context, Intent intent) {
	         String action = intent.getAction();
	        //연결된 장치를 intent를 통하여 가져온다.
	        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	         
	         //장치가 연결이 되었으면
	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	             Log.d("test", device.getName().toString() +" Device Is Connected!");
	        //장치의 연결이 끊기면 
	        }else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){ 
	             Log.d("test", device.getName().toString() +" Device Is DISConnected!");
	             
	        }
	    }           
	};
	
	/**
	 * 사진촬영을 끝으로 파일경로에 있는 사진을 압축하려 소켓객체 쓰레드를 생성 및 실행.
	 */
	public static void doZipAndSocket(){
		String filePath = SocketUtils.TEMP_FOLDER_PATH;
		
		try {
			ZipUtils.zip(filePath, filePath+"test.zip");
			File file = new File(filePath);
			CustomIO.deleteDirectory(file);		// 임시 파일 저장 경로 삭제
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Socket_command(filePath+"test.zip").start();
		Log.i("test","btn click");
	}
	
	// ------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i("test", "onCreate()");
		
		main = this;		// 잠재적으로 memory leak 문제가 발생할 수 있음
		mMyApp = (MyApp)this.getApplicationContext();
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mBTAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(notification == null)
			notification = new Notifications(main);
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(bluetoothReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(bluetoothReceiver, filter);

		if (mBTAdapter == null) {
			// device dos not support Bluetooth
		}

		if (!mBTAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableBtIntent);
		}

		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setFocusable(true);
		btn2.setBackgroundColor(Color.RED);
		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						DeviceListActivity.class);
				startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
			}
		});
		
		btntest = (Button)findViewById(R.id.test);
		btntest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btclientThread.write("Frn");
//				if(notification == null)
//					notification = new Notifications(main);
//				notification.notification_start();;
			}
		});
		
		img_btn = (Button)findViewById(R.id.image_btn);
		img_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent intent = new Intent(CameraPreview.this,GalleryActivity.class);
					startActivity(intent);
			}
		});

		// --------------------------------------------------------
		mPreview = (Preview) findViewById(R.id.preview_layer);
		mCaptureButton = (Button) findViewById(R.id.capture_button);
		mCaptureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(btclientThread != null){
					if(btclientThread.isConnected())
						btclientThread.write("Srn");
				}else{
					Toast.makeText(getApplicationContext(), "블루투스 연결해 새꺄", 0).show();
				}
				
				mPreview.takePicture("My Photo","Photo taken by sample application");
			}
		});
		
		mPreview.setTestEvent(new Preview.testEventListener() {
			
			@Override
			public void OnTestevnet(String test) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				Log.i("test","F보낸다");
				btclientThread.write("Frn");
			}
		});
		
	}


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Attempt to connect to the device
				if (address != null)
					// BluetoothDevice device =
					// mBTAdapter.getRemoteDevice(address);
					
					btclientThread = new BluetoothClientThread(this, address,
							mBTAdapter);
				btclientThread.start();
			}
			break;
		case 0:
			break;
		} // End of switch(requestCode)
	}

	@Override
	protected void onResume() {
		super.onResume();
	    mMyApp.setCurrentActivity(this);
		mPreview.openCamera();
		Log.i("test", "onResume()");
	}

	@Override
	protected void onPause() {
		clearReferences();
		super.onPause();
		// mPreview.releaseCamera();
		Log.i("test", "onPause()");
	}
	
	@Override
	protected void onDestroy() {
		clearReferences();
		super.onDestroy();
	}
	
	private void clearReferences(){
        Activity currActivity = mMyApp.getCurrentActivity();
        if (currActivity.equals(this))
        	mMyApp.setCurrentActivity(null);
	}
	
	private long backKeyPressedTime = 0;
	private Toast toast;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
			backKeyPressedTime = System.currentTimeMillis();
			toast = Toast.makeText(getApplicationContext(), "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
			this.finish();
			mPreview.releaseCamera();
			toast.cancel();
		}
		//super.onBackPressed();
	}

	private static final int MENU_SWITCH_CAM = 0;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SWITCH_CAM, Menu.NONE, "회전");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SWITCH_CAM:
			mPreview.switchCamera();
			return true;
		}
		return false;
	}

	public void onStop() {
		super.onStop();
		Log.i("test", "onStop()");
	}
}
