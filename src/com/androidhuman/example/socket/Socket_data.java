package com.androidhuman.example.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.androidhuman.example.CameraPreview2.CameraPreview;
import com.androidhuman.example.utils.SocketUtils;

public class Socket_data extends Thread {
	
	Socket socket;
	InputStream is;
	OutputStream os;
	
	FileInputStream fis;
	String filePath;
	long fileSize;
	
	FileOutputStream fos;
	
	Socket_command socket_command;
	
	
	public Socket_data(String filePath,Socket_command socket_command){
		this.filePath = filePath;
		this.socket_command = socket_command;
	}
	
	public void setFileSize(long fileSize){this.fileSize = fileSize;}
	
	String time;
	String FilePath;
	
	@Override
	public void run(){
		
		try {
			socket = new Socket(SocketUtils.SERVER_IP,SocketUtils.SERVER_PORT2);
			Log.i("test","소켓 데이터 연결 성공");
			is = socket.getInputStream();
			os = socket.getOutputStream();
			
			fis = new FileInputStream(filePath);
			byte[] buf = new byte[1024*4];
			int data =0;
			
			while(true){
				data = fis.read(buf);
				if(data == -1) break;
				os.write(buf,0,data);
				os.flush();
			}
			
			//-----------------------------------------------
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
			time = sdfNow.format(date);
			
			FilePath = SocketUtils.IMAGE_FILEPATH;
			if(fos==null)
				fos = new FileOutputStream(FilePath+time+".jpg");
			
			long readData=0;
			while(true){
				data = is.read(buf);
				fos.write(buf,0,data);
				fos.flush();
				readData +=data;
				Log.i("test","파일사이즈 :"+readData + "// 파일크기:"+fileSize);
				if(readData == fileSize)break;
			}
			
			
			fos.close();
			Log.i("test","파일송신완료");
			socket_command.writeSTAT(226);
			
		} catch (UnknownHostException e) {
			try {
				fos.close();
				File f = new File(FilePath+time+".jpg");
				f.delete();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} catch (IOException e) {
			try {
				fos.close();
				File f = new File(FilePath+time+".jpg");
				f.delete();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				is.close();
				os.close();
				socket.close();
				Thread.currentThread().interrupt();
				socket_command.closeAll();
				socket_command.interrupt();
				CameraPreview.main.notification.notification_suc();
				Log.i("test","소켓 종료");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
