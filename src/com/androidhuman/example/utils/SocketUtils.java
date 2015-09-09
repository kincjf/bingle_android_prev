package com.androidhuman.example.utils;

import android.os.Environment;

public class SocketUtils {

	static final public String SERVER_IP = "192.168.0.14";
	static final public int SERVER_PORT = 2020;
	static final public int SERVER_PORT2 = 2021;
	
	static final public int HEADER_SIZE = 14;
	static final public int BODY_SIZE = 1024*4; 
	
	static final int TYPE = 0;
	static final int STAT = 1;
	static final int STOR = 2;
	static final int EXIT = 3;
	
	static final public int BLUETOOTH_CONNECTION = 1;
	static final public int BLUETOOTH_CONNECTION_CLOSE = 0;
	
	static final public String FOLDER_PATH = Environment.getExternalStorageDirectory()+"/Pictures/pastels"; 
	static final public String IMAGE_FILEPATH= Environment.getExternalStorageDirectory()+"/Pictures/pastel/";
}
