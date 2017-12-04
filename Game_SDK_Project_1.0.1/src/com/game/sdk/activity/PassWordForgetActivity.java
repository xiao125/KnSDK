package com.game.sdk.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;
import com.game.sdk.util.Md5Util;
import com.game.sdk_project.FindActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 手机验证码更改账号密码
 */
public class PassWordForgetActivity extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private EditText m_securityCode  = null ;
	private EditText m_cellNum  = null ;
	private EditText m_passWord  = null ;
	private TextView   m_get_security_codeBtn = null ;
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
    private String newSdk="1";
	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.password_get__back){ //返回
			intent = new Intent(m_activity.getApplicationContext(),FindActivity.class);
		}else if(id==R.id.password_get_submit){ //提交新密码
			String cell_num      = m_cellNum.getText().toString().trim(); //手机号
			String security_code = m_securityCode.getText().toString().trim(); //验证码
			String newPwd        = m_passWord.getText().toString().trim();	 //密码
			if(TextUtils.isEmpty(security_code)){
				Util.ShowTips(m_activity,"验证码不能为空");
				return ;
			}
			if(TextUtils.isEmpty(newPwd)){
				Util.ShowTips(m_activity.getApplicationContext(), getResources().getString(R.string.tips_8) , Toast.LENGTH_SHORT);
				return ;
			}
			if (!newPwd.matches("^.{6,16}$")) {
				Util.ShowTips(m_activity,  getResources().getString(R.string.tips_5) );
				return;
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}
			String newPassword = Md5Util.getMd5(newPwd);
			if(null==m_activity){
				
			}else{
				LoadingDialog.show(m_activity, "请求中...", true);
				String newSdk="1";

				//发送更改新密码请求
				HttpService.passwordNewSubmit(m_activity, handler, cell_num , security_code, newPassword,newSdk );
				
			}
			
		}else if(id==R.id.get_security_code){ //获取验证码
			String cell_Num = m_cellNum.getText().toString().trim();
			if(TextUtils.isEmpty(cell_Num)){
				Util.ShowTips(m_activity,getResources().getString(R.string.tips_58));
				return ;
			}
			if(!Util.isMobileNO(cell_Num)){
				Util.ShowTips(m_activity,getResources().getString(R.string.tips_57));
			}
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}
			if(null==m_activity){
				
			}else{

				//

				LoadingDialog.show(m_activity, "获取验证码中...", true);
				HttpService.getSecCode(m_activity, handler,cell_Num,newSdk);
				
			}
			
		}
//		else if(id==R.id.forget_account){
//			intent = new Intent(m_activity.getApplicationContext(),AccountForgetActivity.class);
//		}
		if(null==intent){
			
			return ;
			
		}else{
		
			if(null==m_activity){
				
			}else{
				
				m_activity.startActivity(intent);
				m_activity.finish();
				m_activity = null ;
				
			}
			
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
			setRequestedOrientation(GameSDK.getInstance().getmOrientation()); //横竖屏
		}
		
		setContentView(R.layout.mc_password_forget);
		
		m_get_security_codeBtn = (TextView) findViewById(R.id.get_security_code);
		findViewById(R.id.password_get__back).setOnClickListener(this);
		findViewById(R.id.password_get_submit).setOnClickListener(this);
		m_get_security_codeBtn.setOnClickListener(this);
	//	findViewById(R.id.forget_account).setOnClickListener(this);
		
		
		m_securityCode  = (EditText)findViewById(R.id.security_code__et) ; //验证码
		m_cellNum       = (EditText)findViewById(R.id.cell_numner__et) ; //手机号
		m_passWord      = (EditText)findViewById(R.id.new_password__et) ;
	
		
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

	//更改密码请求后的回调
	private Handler  handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String msg_content = msg.obj.toString();
			int resultCode = msg.what;
			Intent intent = null;
			LoadingDialog.dismiss();
			Log.e("sadaaaawdawf", msg_content+"////"+resultCode);
			switch (resultCode) {
			case ResultCode.PASSWORD_NEW_SUCCESS:
				 KnLog.log("修改密码成功++");
				 try {
					 KnLog.log("msg_content:"+msg_content);
					JSONObject json = new JSONObject( msg_content );
					 KnLog.log("json:"+json.toString());
					 String reason = json.getString("reason");
					 String user_name = json.getString("user_name");
					 KnLog.log("reason:"+reason);
					 KnLog.log("user_name:"+user_name);
					 Util.ShowTips(m_activity,reason);
					 intent = new Intent(m_activity.getApplicationContext(), AutoLoginActivity.class); //跳转到免输入密码登录界面
					 intent.putExtra("userName", user_name);
					 if(null==intent){

					 }else{
						 if(null==m_activity){

						 }else{

							 m_activity.startActivity(intent);
							 m_activity.finish();
							 m_activity = null ;

						 }
					 }
					 break;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			case ResultCode.PASSWORD_NEW_FAIL:
				 Util.ShowTips(m_activity,msg_content);
				 break;
			case ResultCode.SECURITY_SUCCESS:
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
				 break;
			case ResultCode.SECURITY_FAIL:
				 Util.ShowTips(m_activity,msg_content);
				 break;
			default:
				Util.ShowTips(m_activity,msg_content);
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
				m_get_security_codeBtn.setText(R.string.tips_48);
			}
			else if(10000==msg.what){
				String text ="已发送"+"("+m_time+")";
				m_get_security_codeBtn.setText(text);
			}	
		}
		
	};
	
}
