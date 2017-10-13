package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Md5Util;
import com.game.sdk.util.Util;
import com.game.sdk_project.SelecteLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.kngame_sdk_project.R.id.password_update_back;
import static com.example.kngame_sdk_project.R.id.update_code;

/**
 * 通过手机验证码重新修改密码
 */
public class PasswordUpdateActivity extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private EditText m_code = null ;
	private EditText password_new = null ;
	private EditText password_new_ng = null ;
	private ImageView m_password_update_back,m_select_login_close;
	private Button m_password_update_submit,m_update_code;
	private String   va_phone = null ;
	private String newSdk="1";
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
	private String  qdPwd;
	private String newpassword; //输入的密码进行md5加密存入本地数据库


	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id== R.id.password_update_back){ //返回
			
			/*if(null==m_mobile){
				
				intent = new Intent(m_activity.getApplicationContext(),AccountManagerActivity.class);
				intent.putExtra("userName",userName);
				
			}else{
				
				intent = new Intent(m_activity.getApplicationContext(),AccountManagerCurrentActivity.class);
				intent.putExtra("userName",userName);
				intent.putExtra("mobile"  , m_mobile );
				
			}*/

			if (m_activity!=null){

				Intent intent1 = new Intent(PasswordUpdateActivity.this,ForgotPasswordActivity.class);
				startActivity(intent1);
				m_activity.finish();
			}


		}else if(id==R.id.password_update_submit){ //确定提交

			String cell_num = va_phone; //手机号
			String security_code = m_code.getText().toString().trim(); //验证码
			String newPwd  = password_new.getText().toString().trim(); //新密码
			qdPwd =password_new_ng.getText().toString().trim(); //确定密码
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

			if (!newPwd.equals(qdPwd)) {
				Util.ShowTips(m_activity,getResources().getString(R.string.tips_65) );
				return;
			}

			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}

			newpassword = newPwd;

			KnLog.log("开始更改新密码请求");
			LoadingDialog.show(m_activity, "请求中...", true);

				//发送更改新密码请求
			HttpService.passwordNewSubmit(m_activity, handler, cell_num , security_code, newpassword,newSdk );










			/*//
			
			String oldPwd = password_old.getText().toString().trim(); //验证码
			String newPwd = password_new.getText().toString().trim();
			String newPwdng = password_new_ng.getText().toString().trim();

			if(TextUtils.isEmpty(oldPwd)){
				Util.ShowTips(m_activity,"旧密码不能为空");
				return ;
			}
			
			if(TextUtils.isEmpty(newPwd)){
				Util.ShowTips(m_activity,"新密码不能为空");
				return ;
			}
			
			if(TextUtils.isEmpty(newPwdng)){
				Util.ShowTips(m_activity,"新密码不能为空");
				return ;
			}
			
			if(!newPwd.equals(newPwdng)){
				Util.ShowTips(m_activity,"两次输入新密码不一致");
				return ;
			}
			
			if (!oldPwd.matches("^.{6,16}$")||!newPwd.matches("^.{6,16}$")||!newPwdng.matches("^.{6,16}$")) {
				Util.ShowTips(m_activity,  getResources().getString(R.string.tips_5) );
				return;
			}
			
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}

			String oldPwd1 = Md5Util.getMd5(oldPwd);
			final String newPwd1 = Md5Util.getMd5(newPwd);
			LoadingDialog.show(m_activity, "请求中...", true);
			HttpService.chanagePwd(m_activity,userName, oldPwd1 , newPwd1 , new BaseListener() {
				@Override
				public void onSuccess(Object result) {
					LoadingDialog.dismiss();
					Util.ShowTips(m_activity, Util.getJsonStringByName( result.toString() , "reason" ));
					DBHelper.getInstance().insertOrUpdateUser( userName , newPwd1 );
					setResult(Activity.RESULT_OK);
					if(null==m_activity){

					}else{

						m_activity.finish();
						m_activity = null ;

					}

				}
				@Override
				public void onFail(Object result) {
					LoadingDialog.dismiss();
					Util.ShowTips(m_activity,  Util.getJsonStringByName( result.toString() , "reason" ) );
				}
			});
			return ;*/


		}else if(id==R.id.update_code){ //验证码


			String cell_Num = va_phone; //手机号
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
			
		}else if (id==R.id.passwd_select_login_close){

			if (m_activity!=null){
				m_activity.finish();
				m_activity=null;
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
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.password_update);


		initView();


		Intent intent = getIntent();
		va_phone = intent.getStringExtra("phone");

		
	}



	private void initView() {

		m_code = (EditText) findViewById(R.id.phone_code); //验证码
		password_new = (EditText) findViewById(R.id.password_new); //新密码
		password_new_ng = (EditText) findViewById(R.id.password_new_ng); //确认密码

		m_password_update_back = (ImageView) findViewById(R.id.password_update_back);//返回
		m_select_login_close= (ImageView) findViewById(R.id.passwd_select_login_close);//关闭
		m_password_update_submit= (Button) findViewById(R.id.password_update_submit); //确定
		m_update_code= (Button) findViewById(R.id.update_code); //验证码

		m_code.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_password_update_back.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);
		m_update_code.setOnClickListener(this);
		m_password_update_submit.setOnClickListener(this);


		//全屏，进入输入用户名
		m_code.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				m_code.setCursorVisible(true);
			}
		} );

		//输入完毕，下一步，进入输入密码
		m_code.setOnEditorActionListener( new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					m_code.clearFocus();
					password_new.requestFocus();
					m_code.setCursorVisible(false);
				}
				return false;
			}
		} );


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
	private Handler handler = new Handler(){

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
						DBHelper.getInstance().insertOrUpdateUser(user_name ,newpassword ); //账号密码保存本地数据库

						Intent intent1=new Intent(PasswordUpdateActivity.this, FirstLoginActivity.class);
						intent1.putExtra("userName", user_name);
						intent1.putExtra("password",qdPwd);
						startActivity(intent1);
						m_activity.finish();
						m_activity=null;

						/*intent = new Intent(m_activity.getApplicationContext(), AutoLoginActivity.class); //跳转到免输入密码登录界面
						intent.putExtra("userName", user_name);*/
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
				case ResultCode.SECURITY_SUCCESS: //发送验证码成功
					m_update_code.setClickable(false);
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
				m_update_code.setClickable(true);
				m_update_code.setText(R.string.tips_48);
			}
			else if(10000==msg.what){
				String text =m_time+"秒";
				m_update_code.setBackgroundResource(R.color.kn_text);
				m_update_code.setText(text);
			}
		}

	};


}
