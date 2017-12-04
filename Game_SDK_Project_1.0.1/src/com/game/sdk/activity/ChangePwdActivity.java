package com.game.sdk.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.listener.BaseListener;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdk.util.Md5Util;

/**
 * 根据账号与旧密码，重新修改密码
 */
public class ChangePwdActivity extends Activity implements OnClickListener {

	public static String userName;
	private EditText userNameEt;
	private EditText olderPasswordEt;
	private EditText newPasswordEt;
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
		
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_changepwd);
		
		userNameEt = (EditText) findViewById(R.id.cpac_input_acc);
		olderPasswordEt = (EditText) findViewById(R.id.cpac_older_input_pwd);
		newPasswordEt = (EditText) findViewById(R.id.cpac_new_input_pwd);
		
		findViewById(R.id.cpac_confirm_btn).setOnClickListener(this);
		findViewById(R.id.cpac_back_btn).setOnClickListener(this);
		
		
		
		userNameEt.setText( getIntent().getExtras().getString("username") );
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.cpac_confirm_btn) {
			Util.hideEditTextWindow(this, olderPasswordEt);
			Util.hideEditTextWindow(this, newPasswordEt);
			checkChangePwdParams(ChangePwdActivity.this ,userNameEt , olderPasswordEt , newPasswordEt);
		} 
		else if (id == R.id.cpac_back_btn) {
			Util.hideEditTextWindow(this, olderPasswordEt);
			Util.hideEditTextWindow(this, newPasswordEt);
			finish();
		} 
		else {
			
		}
	}

	private void checkChangePwdParams(Activity context, EditText userNameEt, EditText olderPasswordEt , EditText newPasswordEt) {
		
		final String username = userNameEt.getText().toString();
		
		if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_2) , Toast.LENGTH_SHORT);
			return;
		}
		
		String oldPassword = olderPasswordEt.getText().toString();
		String newPassword = newPasswordEt.getText().toString();
		 
		if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) ) {
			Util.ShowTips(context, getResources().getString(R.string.tips_7), Toast.LENGTH_SHORT);
			return;
		}
		if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_1));
			return;
		}
		if (!username.matches("^[a-z|A-Z|0-9]{1,}$")) {
			Util.ShowTips(context,getResources().getString(R.string.tips_3));
			return;
		}
		if (!username.matches("^.{4,16}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_4));
			return;
		}
		if (!oldPassword.matches("^.{6,16}$") || !newPassword.matches("^.{6,16}$") ) {
			Util.ShowTips(context, getResources().getString(R.string.tips_5));
			return;
		}
		
		oldPassword = Md5Util.getMd5(oldPassword);
		final String newpwd= Md5Util.getMd5(newPassword);
		
		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}
		LoadingDialog.show(context, "请求中...", true);
		HttpService.chanagePwd(this, username, oldPassword ,newpwd , new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				LoadingDialog.dismiss();
				Util.ShowTips(ChangePwdActivity.this, Util.getJsonStringByName( result.toString() , "reason" ));
				DBHelper.getInstance().insertOrUpdateUser( username , newpwd );
				setResult(Activity.RESULT_OK);
				ChangePwdActivity.this.finish();
			}
			@Override
			public void onFail(Object result) {
				LoadingDialog.dismiss();
				Util.ShowTips(ChangePwdActivity.this,  Util.getJsonStringByName( result.toString() , "reason" ) );
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
