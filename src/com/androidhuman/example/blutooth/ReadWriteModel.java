package com.androidhuman.example.blutooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.androidhuman.example.CameraPreview2.CameraPreview;
import com.androidhuman.example.utils.SocketUtils;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.util.Log;

/**
 * 
 * @author Hyun
 * 블루투스 소켓통신이 연결되면 데이터 input/output을 위한 스트림을 관리하는 클래스.
 */
public class ReadWriteModel extends Thread{

	public static InputStream in;
	public static OutputStream out;
	private String sendNumber="sfsfefwewefwefwefv\r\n";
	private CameraPreview mContext;
	private BluetoothSocket socket;
	
	public ReadWriteModel(CameraPreview context,BluetoothSocket socket){
		mContext = context;
		try{
			this.socket = socket;
			in=socket.getInputStream();
			out = socket.getOutputStream();
			Log.i("test","스트림 생성");
		}catch(IOException e1){
			Log.i("test","스트림 오류");
			e1.printStackTrace();
		}
	}
	
	public void write(byte[] buf){
		try{
			out.write(buf);
		}catch(IOException e){e.printStackTrace();}
	}
	
	public void run(){
		Log.i("test","스트림 스레드 시작");
		byte[] buf=null;
		String rcvNum = null;
		int tmpBuf =0;
		
		try{
			write(sendNumber.getBytes("UTF-8"));
		}
		catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		
		while(socket.isConnected()){
			try{
				buf = new byte[1024];
				Log.i("test","메시지 응답 대기중......");
				tmpBuf = in.read(buf);
				
				if(tmpBuf != 0){
					rcvNum = new String(buf,0,1);
					Log.i("test","메시지 받았다 :" + rcvNum);
					if(rcvNum.equals("C")){	//촬영
						mContext.takePicture();
					}else if(rcvNum.equals("E")){	//촬영종료
						Log.i("test","촬영끝");
						mContext.notification.notification_start();
						CameraPreview.doZipAndSocket();	//사진 압축 및 서버 소켓통신 시작
					}
				}
				
			}catch(IOException e){
				e.printStackTrace();
				Log.i("test","블루투스 소켓 오류");
				mContext.handler.sendEmptyMessage(SocketUtils.BLUETOOTH_CONNECTION_CLOSE);
			}
		}
		
	}
}
