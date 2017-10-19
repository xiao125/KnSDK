package com.game.sdk.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;
import com.game.sdk.util.Md5Util;
import com.game.sdk_project.SelecteLoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import static com.game.sdk.ResultCode.GET_USER_FAIL;

/**
 * 游客升级成萌创账号
 */
public class AccounterBindActivity extends Activity implements OnClickListener {

	private Activity m_activity = null ;
	private String   m_username = null ;
	private String   m_password = null ;
	private EditText userNameEt;
	private EditText passWordEt;
	private EditText updatePassword;
	private CheckBox m_zc_cb;
	private Button m_bind_bt;

	public  static   String    m_userName ;
	public  static   String    m_passWord ;

	@Override
	public void onClick(View v ) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.account_bind_back){
			Intent intent = null;

			if(null==m_activity){

			}else{

				intent = new Intent(m_activity.getApplicationContext(),SelecteLoginActivity.class);
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

		}else if(id==R.id.account_bind_bt){ //注册账号
			KnLog.log("绑定+++");
			Util.hideEditTextWindow(this, passWordEt);
			Util.hideEditTextWindow(this, userNameEt);
			checkAccountBindParams(m_activity, userNameEt, passWordEt);
			KnLog.log("绑定+++END");
		}else{

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

		setContentView(R.layout.visitor_account_bind);


		initView();


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
					Util.hideEditTextWindow(m_activity, passWordEt);
					Util.hideEditTextWindow(m_activity, userNameEt);
				}
				return false;
			}
		} );


		//协议
		m_zc_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					//选择了
					m_bind_bt.setEnabled(true);
					m_bind_bt.setBackgroundColor(getResources().getColor(R.color.Kn_Username));
				}else {


					m_bind_bt.setEnabled(false);
					m_bind_bt.setBackgroundColor(getResources().getColor(R.color.kn_text));

				}
			}
		});





	}

	private void initView() {

		userNameEt = (EditText) findViewById(R.id.account__et);
		passWordEt = (EditText) findViewById(R.id.password__et);
		m_zc_cb = (CheckBox) findViewById(R.id.zc_cb);
		m_bind_bt= (Button) findViewById(R.id.account_bind_bt);// 确定
		findViewById(R.id.account_bind_back).setOnClickListener(this);
		m_bind_bt.setOnClickListener(this);
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

	private void checkAccountBindParams(Activity context, EditText mUsername, EditText mPassword) {

		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();


		if(TextUtils.isEmpty(username)){
			Util.ShowTips(m_activity,getResources().getString(R.string.tips_100));
			return ;
		}


		KnLog.log("判断是否手机号2："+ismobile(context, username));

		if (ismobile(context, username)) return;


		if(Util.isMobileNO(username)) {

			Util.ShowTips(context,  getResources().getString(R.string.tips_58) );
		}

			if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_2) );
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_8) );
			return;
		}

		if (!username.matches("^.{6,25}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_4) );
			return;
		}

		/*if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
			Util.ShowTips(context, getResources().getString(R.string.tips_1));
			return;
		}*/

		if (!username.matches("^[A-Za-z0-9_-]+$")) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
			return;
		}


		if (!password.matches("^.{6,20}$")) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_5) );
			return;
		}
		password = Md5Util.getMd5(password);
		m_username = username ;
		m_password = password ;



		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}
		LoadingDialog.show(context, "绑定中...",true);


		//查询账号是否存在
		HttpService.getUsername(m_activity.getApplicationContext(), handler,username);



		//游客绑定账号
		//HttpService.visitorBindAccount(getApplicationContext(), handler, username, password);
	}

	private boolean ismobile(Activity context, String username) {
		if(!Util.isMobileNO(username)) { //如果不是手机号
			//Util.ShowTips(m_activity, getResources().getString(R.string.tips_57)); //如果不是手机号

			if(TextUtils.isEmpty(username)){
				Util.ShowTips(m_activity,getResources().getString(R.string.tips_100));
				return true;
			}

			if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
				Util.ShowTips(context, getResources().getString(R.string.tips_1));
				return true;
			}

			if (!username.matches("^[A-Za-z0-9_-]+$")) {
				Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
				return true;
			}

			if (!username.matches("^.{6,25}$")) {
				Util.ShowTips(context, getResources().getString(R.string.tips_4) );
				return true;
			}




		}
		return false;
	}



	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
				/*case ResultCode.SUCCESS: //注册账号成功

					//保存用户名与密码
					DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
					Util.ShowTips(AccounterBindActivity.this, getResources().getString(R.string.tips_15) );


					GameSDK.instance.login(AccounterBindActivity.this); //跳转到免密码登录

					break;

				case ResultCode.FAIL: ////注册失败
					if(msg.obj!=null)
						Util.ShowTips(AccounterBindActivity.this,Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
					break;*/

				case ResultCode.GET_USER_SUCCRESS: //账号已经被注册过了

					Util.ShowTips(m_activity,"账号已经被注册了，请重新输入！");

					KnLog.log("账号已经被注册过了，返回的信息："+msg.obj.toString());

					break;

				case ResultCode.GET_USER_NoEXIStTENT: //账号没有被注册过


					KnLog.log("账号没有被注册过，返回的信息："+msg.obj.toString());

					String username= userNameEt.getText().toString().trim(); //账号
					String  password=passWordEt.getText().toString().trim(); //密码
					String ps = Md5Util.getMd5(password);

					KnLog.log("判断是否是手机："+ismobile(m_activity, username));

					if (Util.isMobileNO(username)){ //

						KnLog.log("是手机号："+username+" 密码="+ps);

						//跳转发送验证码注册
						Intent intent1 =new Intent(AccounterBindActivity.this,TourtistRegActivity.class);
						intent1.putExtra("phone",username);
						intent1.putExtra("password",ps);

						startActivity(intent1);

						KnLog.log("跳转页面：");




					}else {


						KnLog.log("不是手机号：");

						String pd = Md5Util.getMd5(password);
						m_userName = username ;
						m_passWord = pd ;
						LoadingDialog.show(m_activity, "绑定中...",true);

						//游客绑定账号
						HttpService.visitorBindAccount(getApplicationContext(), handler, username, pd );


						//直接用户名与密码，注册账号
					//	HttpService.doRegister(getApplicationContext(), handler, username, pd);


					}




					break;

				case ResultCode.GET_USER_FAIL: //查询账号返回错误

					Util.ShowTips(m_activity,"网络错误！");

					break;

				case ResultCode.VISITOR_BIND_SUCCESS: //游客绑定账号成功

					String result=	msg.obj.toString();
					KnLog.log("游客绑定账号成功:"+result);

					//保存用户名与密码
					DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
					Util.ShowTips(AccounterBindActivity.this, getResources().getString(R.string.tips_15) );

					GameSDK.instance.login(AccounterBindActivity.this); //跳转到免密码登录

					break;

				case ResultCode.VISITOR_BIND_FAIL: //游客绑定账号失败

				    String result1=	msg.obj.toString();
					KnLog.log("游客绑定账号失败:"+result1);

					Util.ShowTips(AccounterBindActivity.this,"绑定账号失败:"+result1 );
					break;

				default:
					break;
			}
		}
	};

}
