package com.game.sdk.util;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Util {
	
	private static long  mTime = System.currentTimeMillis() ;
	
	public static boolean  findNameInSet( final String[] set ,final String name ){
		boolean  flag = false ;
		if(0==set.length){
			
		}else{
			for(int i=0;i!=set.length;++i){
				String str = set[i];
				if(str.equals(name)){
					flag = true ;
				}
			}
		}
		return   flag ;
	}

	public static String getCurTimestamp() {
		String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
		return timeStamp;
	}

	public static void ShowTips(Context context, String strText) {
		if( TextUtils.isEmpty(strText) )
			return ;
		
		if(null==context){
			return ;
		}
		
		ShowTips(context, strText, Toast.LENGTH_LONG);
		
	}

	public static void ShowTips(Context context, String strText, int time) {
		
		if( TextUtils.isEmpty(strText) )
			return ;
		
		long nowTime = System.currentTimeMillis();
		if(nowTime - mTime<=1000*time){
			return ;
		}
		mTime = nowTime;
		Toast toast = Toast.makeText(context, strText, time);
		toast.show();
	}

	public static void hideEditTextWindow(Activity activity, EditText editText) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public static String stringToUTF8(String str) {

		String result = null;

		try {
			result = new String(str.getBytes("utf-8"), "utf-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	
	public static String mapToString(Map<String, String> map)
	  {
	    if ((map == null) || (map.isEmpty()))
	    {
	      return null;
	    }

	    String string = "";

	    int index = 0;
	    for (String key : map.keySet())
	    {
	      string = string + key + "=" +  (String)map.get(key);
	      if (index != map.size() - 1)
	      {
	        string = string + "&";
	      }
	      ++index;
	    }

	    return string;
	  }
	
	public static String getJsonStringByName(String json , String name){
		
		try {
			JSONObject obj = new JSONObject(json);
			
			String retStr = obj.getString(name);
			
			if(retStr == null){
				return "";
			}
			return obj.getString(name);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getAppInfo(Activity activity) {
		JSONObject appInfo = new JSONObject();

		try {
			String pkName = activity.getPackageName();
			String versionName = activity.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			int versionCode = activity.getPackageManager().getPackageInfo(
					pkName, 0).versionCode;

			appInfo.put("packageName", pkName);
			appInfo.put("versionName", versionName);
			appInfo.put("versionCode", versionCode);

			// return pkName + "   " + versionName + "  " + versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appInfo.toString();
	}
	
	public static Map<String, String> getSign( Map<String, String> update_params , String app_secret ){
		
		String sign = "";
		
		Map<String, String> update_paramsTmp = new TreeMap<String, String>( new Comparator<String>() {

			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		for(Map.Entry<String, String> entry:update_params.entrySet()){    
			update_paramsTmp.put(entry.getKey(), entry.getValue());
		}   
		
		int    len = update_paramsTmp.size();
		int    count = 0;
		for(Map.Entry<String, String> entry:update_paramsTmp.entrySet()){
			
			count++;
			if(count==len){
				sign = sign+entry.getKey()+"="+entry.getValue();
			}else{
				sign = sign+entry.getKey()+"="+entry.getValue()+"&";
			}
			
		} 
		sign = sign+"&app_secret="+app_secret;
		KnLog.log("sign:"+sign);
		update_params.put("sign",Md5Util.getMd5(sign).toLowerCase());
		return 		update_params;
	}
	
	public static boolean isMobileNO(String mobiles){  
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}  
	
	public static  boolean isNetWorkAvailable( Context  context ) {
		 KnLog.log("是否网络连接++");  
		try { 
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	        if (connectivity != null) { 
	        	NetworkInfo info = connectivity.getActiveNetworkInfo();
	            if (info != null&& info.isConnected()) {
	            	if (info.getState() == NetworkInfo.State.CONNECTED) {
	            		KnLog.log("网络连接");
	                    return true; 
	                } 
	            } 
	        } 
	    } catch (Exception e) {   
	    	e.printStackTrace();
	    }
		KnLog.log("网络非连接");
	    return false; 
	}
	
	
}