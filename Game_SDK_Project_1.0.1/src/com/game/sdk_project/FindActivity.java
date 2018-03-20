package com.game.sdk_project;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.game.sdkproxy.R;
import com.game.sdk.activity.AccountForgetActivity;
import com.game.sdk.activity.FirstLoginActivity;
import com.game.sdk.activity.PassWordForgetActivity;

/**
 * 找回密码
 */
public class FindActivity extends Activity {
private ImageButton find_password,find_account;
private Activity activity;
private ImageView image_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mc_activity_find);
		activity=this;
		find_password=(ImageButton) findViewById(R.id.find_password);
		find_account=(ImageButton) findViewById(R.id.find_account);
		image_back=(ImageView) findViewById(R.id.image_back);

		find_password.setOnClickListener(new OnClickListener() { //找回密码
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(FindActivity.this, PassWordForgetActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});
		
		find_account.setOnClickListener(new OnClickListener() { //找回账号
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(FindActivity.this, AccountForgetActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});
		
		image_back.setOnClickListener(new OnClickListener() { //返回账号登陆
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(activity, FirstLoginActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});
	}

	
}
