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

import android.os.Environment;
import android.util.Log;

import com.androidhuman.example.utils.SocketUtils;


public class Socket_command extends Thread {
	
	private Socket socket;
	private int BUFFER_SIZE = SocketUtils.BODY_SIZE+SocketUtils.HEADER_SIZE;	//4kb
	
	private InputStream is;
	private OutputStream os;
	private FileInputStream fis;
	private File file;
	private FileOutputStream fos;
	private String filePath ="";
	private String outFilePath = "";
	private long fileSize;
	private Protocol protocol;
	private Socket_data socket_data;
	
	
	public Socket_command(String filepath){
		this.filePath = filepath;
		
	}
	
	public void writeSTAT(int responsecode){
		try {
			os.write(protocol.getSTAT(responsecode));
			os.flush();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	//파일송신완료 & 연결종료
	}
	
	public void closeAll(){
		try {
			os.close();
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		try {
			socket = new Socket(SocketUtils.SERVER_IP,SocketUtils.SERVER_PORT);
			Log.i("test","소켓 커멘드 연결 성공");
			is = socket.getInputStream();
			os = socket.getOutputStream();
			
			protocol = new Protocol();
			file = new File(filePath);
			fileSize = file.length();
			
			os.write(protocol.getTYPE(fileSize));
			
			int i=0;
			byte[] buf,bodybuf;
			
			while(true){
				buf=null;
				buf = new byte[14];
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i=is.read(buf,0,14);
				int bodyLength = byte2int(buf,4);
				String type = new String(buf,0,4);
				
				Log.i("test","================================");
				Log.i("test","패킷 크기 : "+i);
				Log.i("test","RequestName : "+new String(buf,0,4));
				Log.i("test","BodyLength : "+byte2int(buf,4));
				Log.i("test","Fragmented : "+ Integer.toOctalString(buf[8]));
				Log.i("test","LastMsg : "+ Integer.toOctalString(buf[9]));
				Log.i("test","Seqeunce : "+byte2int(buf,10));
				
				bodybuf = new byte[bodyLength];
				i=is.read(bodybuf,0,bodyLength);
				
				if(type.equals("TYPE")){	//Type
					Log.i("test","body : "+byte2long(bodybuf,0));
					socket_data.setFileSize(byte2long(bodybuf,0));
					os.write(protocol.getSTAT(150)); //파일 송신 준비
					os.flush();
				}
				else if(type.equals("STAT")){	//STAT
					Log.i("test","body : "+byte2int(bodybuf,0));
					
					switch(byte2int(bodybuf,0)){
					case 150:	//파일 송신 준비
					//	writeSTOR();
						os.write(protocol.getSTOR());
						os.flush();
						socket_data = new Socket_data(this.filePath,this);
						socket_data.start();
						break;
					case 200:	//파일 송신 완료
						Log.i("test","파일 송신완료");
						break;
					}
					
				}else{	//STOR
					Log.i("test","body : "+byte2int(bodybuf,0));
					/*
					Date date = new Date(System.currentTimeMillis());
					SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmmss");
					String time = sdfNow.format(date);
					
					String FilePath = Environment.getExternalStorageDirectory()+"/Pictures/pastel/";
					if(fos==null)
					fos = new FileOutputStream(FilePath+time+".jpg");
					
					fos.write(buf,14,buf.length-14);
					fos.flush();
					
					String LastMsg=Integer.toOctalString(buf[9]);
					if(Integer.parseInt(LastMsg) == 1){
						os.write(protocol.getSTAT(220));	//파일송신완료 & 연결종료
						Log.i("test","파일송신완료 & 연결종료");
						fos.close();
						break;
					}*/
				}
				
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.i("test",e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("test",e.getMessage());
		}finally{
			Log.i("test","소켓 문제로 종료");
			Thread.currentThread().interrupt();
		}
	}
	
	
//	public static void main(String arg[]){
//		new Client();
//	}
	
	public int byte2int(byte[] src,int offset){
		int s1=src[offset] & 0xFF;	//16진수 0xFF= 이진수 11111111 십진수 255 &= AND연산자
		int s2=src[offset+1] & 0xFF;
		int s3=src[offset+2] & 0xFF;
		int s4=src[offset+3] & 0xFF;
		/*
		Log.i("========================");
		Log.i(Integer.toBinaryString(s1));
		Log.i(Integer.toBinaryString(s2));
		Log.i(Integer.toBinaryString(s3));
		Log.i(Integer.toBinaryString(s4));
		Log.i("========================");
		*/
		return ((s1 << 24)+(s2 <<16)+(s3 << 8) + (s4 << 0)); // <<왼쪽 시프트 연산자 = s1을 왼쪽으로 숫자만큼이동 int형이기때문에 32자리다.
	}
	public int byte2long(byte[] src,int offset){
		int s1=src[offset] & 0xFF;	// 16진수 0xFF= 이진수 11111111 십진수 255 &= AND연산자
		int s2=src[offset+1] & 0xFF;
		int s3=src[offset+2] & 0xFF;
		int s4=src[offset+3] & 0xFF;
		int s5=src[offset+4] & 0xFF;
		int s6=src[offset+5] & 0xFF;
		int s7=src[offset+6] & 0xFF;
		int s8=src[offset+7] & 0xFF;
		/*
		Log.i("========================");
		Log.i(Integer.toBinaryString(s1));
		Log.i(Integer.toBinaryString(s2));
		Log.i(Integer.toBinaryString(s3));
		Log.i(Integer.toBinaryString(s4));
		Log.i("========================");
		*/
		return ((s1 << 56)+(s2 <<48)+(s3 << 40) + (s4 << 32) + (s5 << 24)+(s6 <<16)+(s7 << 8) + (s8 << 0));
	}

}
