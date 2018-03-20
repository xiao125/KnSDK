package com.game.sdk.task;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.game.sdkproxy.R;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.bean.Result;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.HttpUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;

public class VisitorAccountBindAsyncTask extends AsyncTask<Map<String, String>, Void, Void>  {
	
	private Context context = null;
	private String loginUrl = null;
	private Handler handler = null;
	
	public VisitorAccountBindAsyncTask(Context context, Handler handler,String loginUrl) {
		this.loginUrl = loginUrl;
		this.handler  = handler;
		this.context = context;
	}

	@Override
	protected Void doInBackground(Map<String, String>[] params) {
		Message msg = handler.obtainMessage();
		KnLog.log("loginUrl : " + this.loginUrl+  "LoginAsyncTask params = " + params[0] );
		
		for (int i = 0; i < 3; i++) {
			
			try {
				 String result = HttpUtil.doHttpPost(params[0], this.loginUrl);				 
				 if(result == null){
					 Thread.sleep(500L);
					 if ( i==2 ) {
						 msg.what = ResultCode.FAIL;
						 msg.obj = new Result(ResultCode.NET_DISCONNET,  context.getResources().getString(R.string.mc_tips_34).toString() ).toString() ;
						 break;
					}
				 }
				 else{
					msg.obj = result;
					JSONObject obj = new JSONObject(result);
					int resultCode = obj.getInt("code");
					switch (resultCode) {
					case ResultCode.SUCCESS:
						 msg.what = ResultCode.VISITOR_BIND_SUCCESS;
						break;
					default:
						msg.what = ResultCode.VISITOR_BIND_FAIL;
						break;
					}
					
					break;
				 }
				 
			} catch (Exception e) {
				e.printStackTrace();
				if(i==2){
					msg.what = ResultCode.UNKNOW;
					msg.obj = new Result(ResultCode.NET_DISCONNET, "catch the exception").toString() ;
				}
			}
		
		}
		handler.sendMessage(msg);
		return null;
	}
}
