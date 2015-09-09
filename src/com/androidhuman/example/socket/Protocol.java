package com.androidhuman.example.socket;


import java.nio.ByteBuffer;

import com.androidhuman.example.utils.SocketUtils;

/**
 * 
 * @author Hyun
 * 서버와의 통신을 위한 프로토콜에 대한 함수 정의를 위한 클래스.
 * 
 * stat,stor,type 3가지 종류를 사용한다.
 */
public class Protocol {
	
	private ByteBuffer buffer;
	private int BUFFER_SIZE = SocketUtils.BODY_SIZE+SocketUtils.HEADER_SIZE;	// 1024*4 + 14s

	public Protocol(){
		//ByteBuffer buffer = ByteBuffer.allocate(capacity);
		
	}
	
	public byte[] getSTOR(){
		String STOR = "STOR";
		if(buffer!=null)
			buffer.clear();	
		buffer = ByteBuffer.allocate(14+0);	
		
		buffer.put(STOR.getBytes());	// 4byte?
		buffer.putInt(0); // 4byte
		buffer.put((byte)0x00);	//1byte
		buffer.put((byte)0x01); //1byte
		buffer.putInt(0); //4byte
		//buffer.put(body);	//BodyLength
		
		buffer.flip();
		
		return buffer.array();
	}
	// byte[] result = getType();
	
	
	public byte[] getTYPE(long fileSize){
		String TYPE = "TYPE";
		if(buffer !=null)
			buffer.clear();	
		buffer = ByteBuffer.allocate(14+8);	
		
		buffer.put(TYPE.getBytes());	// 4byte?
		buffer.putInt(8); // 4byte
		buffer.put((byte)0x00);	//1byte	        0x01 = yes / 0x00 = no
		buffer.put((byte)0x01); //1byte
		buffer.putInt(0); //4byte
		buffer.putLong(fileSize);	//BodyLength
		
		buffer.flip();
		
		return buffer.array();
	}
	
	public byte[] getSTAT(int ResponseCode){
		String STAT = "STAT";
		
		buffer.clear();
		buffer = ByteBuffer.allocate(14+4);
		buffer.put(STAT.getBytes());	// 4byte?
		buffer.putInt(4); // 4byte
		buffer.put((byte)0x00);	//1byte	        0x01 = yes / 0x00 = no
		buffer.put((byte)0x01); //1byte
		buffer.putInt(0); //2byte
		buffer.putInt(ResponseCode);	//BodyLength		150:�غ� 200:���ϼ۽ſϷ� 220:���ϼ۽ſϷ� �� ����
		
		buffer.flip();
		
		return buffer.array();
	}
	
	
	
}
