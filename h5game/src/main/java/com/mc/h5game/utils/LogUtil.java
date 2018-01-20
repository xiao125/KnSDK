package com.mc.h5game.utils;

import android.util.Log;

public class LogUtil {

	public static boolean mLogEnable = true;
	
	public static void setLogEnable( boolean flag ){
		
		mLogEnable = flag ; 
		
	}

	public static void e(String message) {
		if(mLogEnable){
			Log.e("H5Log", message);
		}
		
	}

	public static void i(String message) {
		if (mLogEnable){
			Log.i("H5Log", message);
		}
	}

	public static void w(String message) {
		if (mLogEnable){
			Log.w("H5Log", message);
		}
	}

	public static void d(String message) {
		if (mLogEnable){
			Log.d("H5Log", message);
		}
	}
	
	public static void log( String message ){
		LogUtil.e(message);
	}

}
