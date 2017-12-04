package com.game.sdk.activity;

import java.util.Timer;
import java.util.TimerTask;
import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 手机号注册账号
 */
public class BindCellActivity  extends Activity implements OnClickListener {
	
	private Activity m_activity = null ;
	private EditText m_cellNum_et = null ;
	private EditText m_security_code__et = null ;
	private ImageView m_phone_ks_close,m_select_login_close,m_get_security_back;
	private String  m_userNames = null ;
	private Button   m_get_security_codeBtn,m_get_security_submit;
	private Timer    m_timer = null ;
	private int      m_time  = 60 ;
	private Message  m_msg = null ;
    private String newSdk="1";

	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		Intent intent = null;
		if(id==R.id.get_security_back){ //返回
				if(null==m_activity){
					
				}else{
					
					m_activity.finish();
					m_activity = null ;
					
				}
		}else if(id==R.id.get_security_code){ //验证码
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			if (isphone(cell_num)) return;
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}
			if(null==m_activity){
				
			}else{
				LoadingDialog.show(m_activity, "获取验证码中...", true);
				HttpService.getSecCode(m_activity, handler,cell_num,newSdk);
				
			}

		}else if(id==R.id.get_security_submit){ //获取到验证码，下一步
			// 成功
			String cell_num = m_cellNum_et.getText().toString().trim(); //手机号
			String security_code = m_security_code__et.getText().toString().trim(); //验证码

			if (isphone(cell_num)) return;

			if(TextUtils.isEmpty(security_code)){
				Util.ShowTips(m_activity,"验证码不能为空");
				return ;
			}

			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}
			if(null==m_activity){
				
			}else{
				LoadingDialog.show(m_activity, "绑定中...", true);

				HttpService.bindMobile(m_activity.getApplicationContext(), handler, cell_num , security_code,m_userNames);
				
			}
			
		}else if (id==R.id.phone_ks_close){ //清除验证码

			m_security_code__et.setText("");

		}else if (id == R.id.select_login_close){ //关闭

			if(null==m_activity){

			}else{

				m_activity.finish();
				m_activity = null ;

			}
		}
	}

	private boolean isphone(String cell_num) {
		if(TextUtils.isEmpty(cell_num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_58));
			return true;
        }
		if(!Util.isMobileNO(cell_num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_57));
			return true;
        }
		return false;
	}

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
		
		m_activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_bind_cell);

		initView();

		
		//m_get_security_codeBtn = (TextView) findViewById(R.id.get_security_code);
		

		Intent intent = getIntent();
		m_userNames   = intent.getStringExtra("userName");
		
	}

	private void initView() {

		m_cellNum_et = (EditText)findViewById(R.id.cellnumber__et); //手机号
		m_security_code__et = (EditText)findViewById(R.id.security_code__et); //请输入验证码
		m_phone_ks_close = (ImageView) findViewById(R.id.phone_ks_close); //清除验证码
		m_select_login_close = (ImageView) findViewById(R.id.select_login_close); //关闭
		m_get_security_codeBtn= (Button) findViewById(R.id.get_security_code); //验证码
		m_get_security_back= (ImageView) findViewById(R.id.get_security_back); //返回

		m_get_security_submit = (Button) findViewById(R.id.get_security_submit); //确定

		m_get_security_back.setOnClickListener(this);
		m_get_security_codeBtn.setOnClickListener(this);
		m_get_security_submit.setOnClickListener(this);
		m_phone_ks_close.setOnClickListener(this);
		m_select_login_close.setOnClickListener(this);



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
			case ResultCode.BIND_SUCCESS: //绑定手机号成功

				if(msg.obj!=null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onSuccess(msg.obj.toString());

						Util.ShowTips(m_activity,msg_content);
				       /* intent = new Intent(m_activity.getApplicationContext(), AutoLoginActivity.class);
				        intent.putExtra("userName",m_userNames);*/

						if (m_activity!=null){
							m_activity.finish();
							m_activity = null ;
						}
					}
				}




				 break;
			case ResultCode.BIND_FAIL:

				if(msg.obj!=null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onFail(msg.obj.toString());

						Util.ShowTips(m_activity,msg_content);
					}
				}


				 break;
			case ResultCode.SECURITY_SUCCESS: //验证码获取成功

				KnLog.log("收到验证码了");

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
				String text =+m_time+"秒";

				m_get_security_codeBtn.setText(text);
			}	
		}
		
	};
	
}
