package com.game.sdk.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.sdkproxy.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 游客登录，可以选择绑定密码或切换账号
 */
public class VisitManagerActivity extends Activity implements OnClickListener {
	
	private Timer  m_timer = null ;
	private int    m_time  = 5 ;
	private Message  m_msg = null ;
	private Button  m_btn = null ;
	private Activity m_activity = null ;
	
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
	
    	setContentView(R.layout.mc_visit_manager);
		
		m_activity = this ;
		m_btn = (Button)findViewById(R.id.login_game_bt);
		

		m_timer = new Timer();
		m_timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				m_msg = new Message();
				if(0==m_time){
					m_msg.what = 10001;
					m_timer.cancel();
				}else{
					m_time -- ;
					m_msg.what = 10000;
				}
				m_handler.sendMessage(m_msg);
			}
		},1000,1000);
		
		findViewById(R.id.account_change).setOnClickListener(this);
		findViewById(R.id.account_manager).setOnClickListener(this);
		findViewById(R.id.login_game_bt).setOnClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(R.id.account_manager==id){
			intent = new Intent(m_activity.getApplicationContext(),AccounterBindActivity.class);
		}
		else if(R.id.account_change==id){
			intent = new Intent(m_activity.getApplicationContext(),FirstLoginActivity.class);
		}else if(id==R.id.login_game_bt){
			//	直接登录
			KnLog.log("游客直接登录++");
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			if(	null == DeviceUtil.getDeviceId()){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_59).toString());
				return ;
			}
			KnLog.log("游客登录开始");
			LoadingDialog.show(m_activity, "请求中...", true);
			HttpService.visitorReg(m_activity,handler);
			
		}else{
			
		}
		
		if(null==intent){
		}else{
			if(m_handler==null){		
			}else{
				
				if(m_timer==null){
					
				}else{
					m_timer.cancel();
				}
				m_handler.removeMessages(10001);
				m_handler.removeMessages(10000);
			}
			if(null==m_activity){
			}else{
				m_activity.startActivity(intent);
				m_activity.finish();
				m_activity = null ;	
			}	
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
			case ResultCode.VISITOR_LOGIN_SUCCESS:
				KnLog.log("游客登录成功");
				if(msg.obj!=null){
					KnLog.log("游客登录成功结果:"+msg.obj.toString());
					if(GameSDK.getInstance().getmLoginListener()!=null){
						KnLog.log("getmLoginListener().onSuccess++");
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
						if(m_activity!=null){
							m_activity.finish();
							m_activity = null ;
						}
					}else{
						
					}
				}
				break;
			case ResultCode.VISITOR_LOGIN_FAIL:
				KnLog.log("游客登录失败");
				if(msg.obj!=null)
				{
					KnLog.log("游客登录失败结果:"+msg.obj.toString());
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						Util.ShowTips(m_activity,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
						m_activity.finish();
					}else{
						
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	private Handler m_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(10000==msg.what){
				String text =" 登 录 游 戏 "+"("+m_time+")";
				m_btn.setText(text);
			}
			else if(10001==msg.what){	
				if(!Util.isNetWorkAvailable(getApplicationContext())){
					Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
					return ;
				}
				if(	null == DeviceUtil.getDeviceId()){
					Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_59).toString());
					return ;
				}
				KnLog.log("游客登录开始");
				LoadingDialog.show(m_activity, "请求中...", true);
				HttpService.visitorReg(m_activity,handler);
			}
			
		}
		
	};
	
	
}
