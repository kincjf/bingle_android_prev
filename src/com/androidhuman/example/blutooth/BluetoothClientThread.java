package com.androidhuman.example.blutooth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.security.auth.callback.Callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.androidhuman.example.CameraPreview2.CameraPreview;
import com.androidhuman.example.utils.SocketUtils;

/**
 * 
 * @author Hyun
 * 블루투스 통신을 연결하는 소켓을 관리하는 클레스
 * 블루투스의 UUID를 통하여 해당 기기와 연결하여 스트림방식으로 데이터를 주고받게 된다.
 */
public class BluetoothClientThread extends Thread {
	
	private final BluetoothSocket clientSocket;
	private final BluetoothDevice mDevice;
	private CameraPreview mContext;
	ReadWriteModel rw;
	
	//UUID생성
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final UUID TECHBOOSTER_BTSAMPLE_UUID2 = UUID.fromString("00001124-0000-1000-8000-00805F9B34FB");
	static BluetoothAdapter myClientAdapter;
	public String myNumber;
	
	//소켓준비
	public BluetoothClientThread(CameraPreview context,String address, BluetoothAdapter btAdapter){
		
		mContext = context;
		BluetoothSocket tmpSock = null;
		mDevice = btAdapter.getRemoteDevice(address);
		
		myClientAdapter = btAdapter;
		//00001101-0000-1000-8000-00805F9B34FB
		try{
			tmpSock = mDevice.createRfcommSocketToServiceRecord(TECHBOOSTER_BTSAMPLE_UUID);
		}catch(IOException e){
			Toast.makeText(context, "소켓연결 오류", 0).show();
			Log.i("test","UUID연결 오류");
		}
		clientSocket = tmpSock;
	}
	
	public boolean isConnected(){
		if(clientSocket.isConnected()) return true;
		else return false;
	}
	
	public void run(){
		if(myClientAdapter.isDiscovering()){
			myClientAdapter.cancelDiscovery();
		}
		try{
			clientSocket.connect();
			
			Log.i("test","연결 성공");
			mContext.handler.sendEmptyMessage(SocketUtils.BLUETOOTH_CONNECTION);
		}catch(IOException e){
			Log.i("test","연결실패");
			mContext.handler.sendEmptyMessage(SocketUtils.BLUETOOTH_CONNECTION_CLOSE);
			return;
		}
		if(clientSocket.isConnected()){
			rw = new ReadWriteModel(mContext,clientSocket);
			rw.start();
		}
	}
	
	public void write(String msg){
		
		try {
			if(clientSocket.isConnected()){
				//mContext.setTextView(msg);
				rw.write(msg.getBytes("UTF-8"));
			}else{
				Toast.makeText(mContext, "연결해 새꺄", 0).show();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	//종료
	public void cancel(){
		try{
			clientSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
