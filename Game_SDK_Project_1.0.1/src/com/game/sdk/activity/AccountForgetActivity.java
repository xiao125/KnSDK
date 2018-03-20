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
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdk_project.FindActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 获取手机验证码
 */
public class AccountForgetActivity extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private EditText m_securityCode  = null ;
	private EditText m_phoneNumber  = null ;
	private TextView   m_get_security_codeBtn = null ;
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
	private View     m_View = null ;
	private String newSdk="1";

	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.password_get__back){
			intent = new Intent(m_activity.getApplicationContext(),FindActivity.class);
		}else if(id==R.id.password_get_submit){
			String cell_num      = m_phoneNumber.getText().toString().trim();
			String security_code = m_securityCode.getText().toString().trim();
			if(TextUtils.isEmpty(security_code)){
				Util.ShowTips(m_activity,"验证码不能为空");
				return ;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			LoadingDialog.show(m_activity, "提交中...",true);
			HttpService.getAccountSubmit(m_activity, handler, cell_num , security_code);
		}else if(id==R.id.get_security_code){
			String cell_num = m_phoneNumber.getText().toString().trim();
			if(TextUtils.isEmpty(cell_num)){
				Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_58));
				return ;
			}
			if(!Util.isMobileNO(cell_num)){
				Util.ShowTips(m_activity,getResources().getString(R.string.mc_tips_57));
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			LoadingDialog.show(m_activity, "获取验证码中...",true);
			HttpService.getSecCode(m_activity, handler,cell_num,newSdk);
			
		}
//		else if(id==R.id.forget_password_bt){
//			intent = new Intent(m_activity.getApplicationContext(),PassWordForgetActivity.class);
//		}
		if(intent!=null){
			if(null==m_activity){
				
			}else{
				m_activity.startActivity(intent);
				m_activity.finish();
				m_activity = null ;
			}
		}else{
			return ;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_account_forget);
		
		m_get_security_codeBtn = (TextView) findViewById(R.id.get_security_code);
		
		findViewById(R.id.password_get__back).setOnClickListener(this);
		findViewById(R.id.password_get_submit).setOnClickListener(this);
		findViewById(R.id.get_security_code).setOnClickListener(this);
//		findViewById(R.id.forget_password_bt).setOnClickListener(this);
		
		m_securityCode  = (EditText)findViewById(R.id.security_code__et) ;
		m_phoneNumber   = (EditText)findViewById(R.id.cell_numner__et) ;
		 
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
	
	private Handler  handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String msg_content = msg.obj.toString();
			int resultCode = msg.what;
			Intent intent = null;
			LoadingDialog.dismiss();
			switch (resultCode) {
				case ResultCode.ACCOUNT_GET_SUCCESS:

					if (msg.obj != null) {
						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onSuccess(msg.obj.toString());


							String reason = "";
							try {
								JSONObject json = new JSONObject(msg_content);
								final String username = json.getString("username");
								reason = json.getString("reason");

								LayoutInflater inflater = m_activity.getLayoutInflater();
								m_View = inflater.inflate(R.layout.mc_tip, null);
								FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
								layoutParams1.gravity = Gravity.CENTER;
								m_activity.addContentView(m_View, layoutParams1);

								View backGround = (View) m_View.findViewById(R.id.background);
								backGround.getBackground().setAlpha(255);

								TextView tView = (TextView) m_View.findViewById(R.id.text_current);
								Button btnSubmit = (Button) m_View.findViewById(R.id.submit);

								String str1 = "您的账户为:" + username;
								String str2 = "请记住您的账号";
								int len1 = str1.length();
								int len2 = str2.length();
								int mid = (len1 - len2) / 2;
								String str3 = "\n\n\r\r";
								for (int i = 0; i != mid - 1; ++i) {
									str3 = str3 + "\r\r";
								}
								tView.setText(str1 + str3 + str2);

								btnSubmit.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										Intent intent = null;
										intent = new Intent(m_activity.getApplicationContext(), FirstLoginActivity.class);
										intent.putExtra("userName", username);
										if (null == m_activity) {

										} else {

											m_activity.startActivity(intent);
											m_activity.finish();
											m_activity = null;

										}
									}
								});

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}


					break;
				case ResultCode.ACCOUNT_GET_FAIL:
					if (msg.obj != null) {
						if (GameSDK.getInstance().getmLoginListener() != null) {
							GameSDK.getInstance().getmLoginListener().onFail(msg.obj.toString());

							Util.ShowTips(m_activity,msg_content);
						}
					}


				 break;
			case ResultCode.SECURITY_SUCCESS:

				if (msg.obj != null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onSuccess(msg.obj.toString());

						m_get_security_codeBtn.setClickable(false);
						m_timer = new Timer();
						m_time = 60 ;
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
					}
				}


				 break;
			case ResultCode.SECURITY_FAIL:

				if (msg.obj != null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onFail(msg.obj.toString());

						if(null==m_activity){

						}else{
							Util.ShowTips(m_activity,msg_content);
						}
					}
				}


				 break;
			default:
				 if(null==m_activity){
					 
				 }else{
					 Util.ShowTips(m_activity,msg_content); 
				 }
				break;
			}
		}
		
	};
	
	private Handler m_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(10001==msg.what){
				m_get_security_codeBtn.setClickable(true);
				m_get_security_codeBtn.setText(R.string.mc_tips_48);
			}
			else if(10000==msg.what){
				String text ="已发送"+"("+m_time+")";
				m_get_security_codeBtn.setText(text);
			}	
		}
		
	};
	
	void testFunc( final String username ){
		
		 LayoutInflater inflater = m_activity.getLayoutInflater();
		 m_View = inflater.inflate(R.layout.mc_tip, null);
		 FrameLayout.LayoutParams  layoutParams1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
	  	 layoutParams1.gravity = Gravity.CENTER;
		 m_activity.addContentView(m_View,layoutParams1);
		 
		 View backGround = (View)m_View.findViewById(R.id.background);
		 backGround.getBackground().setAlpha(255);
		 
		 TextView   tView     = (TextView)m_View.findViewById(R.id.text_current);
		 Button     btnSubmit = (Button)m_View.findViewById(R.id.submit);
		 
		 String str1 = "您的账户为:"+username ;
		 String str2 = "请记住您的账号" ;
		 int    len1 = str1.length();
		 int    len2 = str2.length();
		 int    mid  = (len1-len2)/2;
		 String str3 = "\n\n\r\r";
		 for(int i=0;i!=mid-1;++i){
			 str3=str3+"\r\r";
		 }
		 tView.setText(str1+str3+str2);
		
	}
	
	

}
