package com.game.sdk.activity;

import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdk.util.Md5Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

/**
 * 账号注册
 */
public class RegisterActivity extends Activity implements OnClickListener {
	
	private Activity activity = null ;
	public  static   String    m_userName ;
	public  static   String    m_passWord ;
	public  EditText           userNameEt ;
	public  EditText           passWordEt ;
    public EditText confirmPassword;
	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.regist_now){ //注册
			Util.hideEditTextWindow(this, passWordEt);
			checkRegisterParams(activity,userNameEt,passWordEt);
		}else if(id==R.id.register_back){
			Util.hideEditTextWindow(this, passWordEt);
			intent = new Intent(activity.getApplicationContext(),FirstLoginActivity.class);
			activity.startActivity(intent);
			activity.finish();
			activity = null ;
			
		}else{
			
		}
		if(intent!=null){
			
			if(null==activity){
				
			}else{
				
				activity.startActivity(intent);
				activity.finish();
				activity = null ;
				
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
		
		activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.register);
		
		userNameEt = (EditText)findViewById(R.id.account__et)  ; //用户名
		passWordEt = (EditText)findViewById(R.id.password__et) ; //密码
		confirmPassword=(EditText) findViewById(R.id.confirm_password); //确定密码
		
		userNameEt.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				userNameEt.setCursorVisible(true);
			}
		} );
		
		userNameEt.setOnEditorActionListener( new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					userNameEt.clearFocus();
					passWordEt.requestFocus();
					userNameEt.setCursorVisible(false);
				}
				return false;
			}
		} );
		
		passWordEt.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				passWordEt.setCursorVisible(true);
			}
		} );
		
		passWordEt.setOnEditorActionListener( new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v , int actionId , KeyEvent event ) {
				// TODO Auto-generated method stub
				if(EditorInfo.IME_ACTION_DONE==actionId){
					passWordEt.clearFocus();
					userNameEt.clearFocus();
					passWordEt.requestFocus();
					passWordEt.setCursorVisible(false);
					Util.hideEditTextWindow(activity, passWordEt);
					Util.hideEditTextWindow(activity, userNameEt);
				}
				return false;
			}
		} );
		confirmPassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passWordEt.setCursorVisible(true);
			}
		});
		
		findViewById(R.id.regist_now).setOnClickListener(this);
		findViewById(R.id.register_back).setOnClickListener(this);
		
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
	
	private void checkRegisterParams(Context context, EditText userNameEt, EditText passWordEt) {
		String username = userNameEt.getText().toString();
		if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_2) );
			return;
		}
		String password = passWordEt.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_8) );
			return;
		}
		String confirm_password=confirmPassword.getText().toString();
		if (TextUtils.isEmpty(confirm_password)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_64) );
			return;
		}
		
		if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_1));
			return;
		}

		if (!username.matches("^[a-z|A-Z|0-9]{1,}$")) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
			return;
		}

		if (!username.matches("^.{6,12}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_4) );
			return;
		}

		if (!password.matches("^.{6,16}$")) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_5) );
			return;
		}
		if (!confirm_password.matches("^.{6,16}$")) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_5) );
			return;
		}
		if (!confirm_password.equals(password)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_65) );
			return;
		}
		
		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}
		password = Md5Util.getMd5(password);
		m_userName = username ;
		m_passWord = password ;
		LoadingDialog.show(activity, "注册中...",true);
		HttpService.doRegister(getApplicationContext(), handler, username, password);
		
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
			
			case ResultCode.SUCCESS:
				setResult(Activity.RESULT_OK);
				DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
				Util.ShowTips(RegisterActivity.this, getResources().getString(R.string.tips_15) );
				//RegisterActivity.this.finish();
				GameSDK.instance.login(RegisterActivity.this);
				//	执行自动登录
				break;
			case ResultCode.FAIL:
				if(msg.obj!=null)
					Util.ShowTips(RegisterActivity.this,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
				break;
			default:
				break;
			}
		}
	};
	
}
