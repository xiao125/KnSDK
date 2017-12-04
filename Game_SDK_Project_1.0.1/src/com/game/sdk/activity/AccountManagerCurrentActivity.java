package com.game.sdk.activity;

import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 修改密码
 */
public class AccountManagerCurrentActivity extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private String  m_userNames = null ;
	private TextView m_textView = null ;
	private String  m_mobile = null ;

	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.account_manager_back){
			if(m_userNames==null){
				intent = new Intent(m_activity.getApplicationContext(),VisitManagerActivity.class);
			}else{
				intent = new Intent(m_activity.getApplicationContext(),AutoLoginActivity.class);
				intent.putExtra("userName",m_userNames);
			}
		}else if(id==R.id.password_update){

			intent = new Intent(m_activity.getApplicationContext(),PasswordUpdateActivity.class);
			intent.putExtra("userName", m_userNames );
			intent.putExtra("mobile"  , m_mobile );
		}else{
			
		}
		if( null == intent ){
			return ;
		}	
		if(null==m_activity){
			
		}else{
			
			m_activity.startActivity(intent);
			m_activity.finish();
			m_activity = null ;
			
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
		
		m_activity = this ; 
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
		
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_account_current_manager);
		
		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");
		if(intent.hasExtra("mobile")){
			m_mobile      = intent.getStringExtra("mobile");	
		}
		 		
		m_textView = (TextView)findViewById(R.id.text_current);
		if(m_mobile!=null){
			
			String text1= "已经成功绑定手机号码\n";
			String  text = text1+"\r\r\r\r\r\r"+m_mobile;
			SpannableString spanString = new SpannableString(text);      
			ForegroundColorSpan span = new ForegroundColorSpan(m_activity.getResources().getColor(R.color.text_color_hint));      
			spanString.setSpan(span,0,text1.length()-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			
			ForegroundColorSpan span1 = new ForegroundColorSpan(m_activity.getResources().getColor(R.color.text_color_loginmanager_tuhao));      
			spanString.setSpan(span1,text1.length(),text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			
			m_textView.setText( spanString );
			
		}
		
		findViewById(R.id.password_update).setOnClickListener(this);
		findViewById(R.id.account_manager_back).setOnClickListener(this);
		
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
