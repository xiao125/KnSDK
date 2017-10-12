package com.game.sdk.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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

/**
 * 已有账号登录
 */
public class FirstLoginActivity extends Activity implements OnClickListener {

	public static String m_userName;
	public static String m_pad;
	private EditText userNameEt;
	private EditText passWordEt; 
	private Activity activity ; 
	private static String   m_password ;
	private ImageView image_back;
	private String  m_userNames = null ;
	private TextView findPassword,register;
	private TextView update_Password,new_account_bt;
	private Button login_bt;


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
	
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.first_login_new);

		initView();

		initLinerter();



        

		
		Intent intent = getIntent();
		if(intent.hasExtra("userName")){
			m_userNames   = intent.getStringExtra("userName");
			userNameEt.setText(m_userNames);
		}
		if(intent.hasExtra("password")){
			m_pad   = intent.getStringExtra("password");
			passWordEt.setText(m_pad);
		}
			
	}

	private void initLinerter() {

		//返回
		image_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent intent=new Intent(activity,SelecteLoginActivity.class);
				if(null==activity){

				}else{

					activity.startActivity(intent);
					activity.finish();
					activity = null ;

				}*/

				if(null==activity){

				}else {


					activity.finish();
					activity = null;
				}

			}
		});

		//全屏，进入输入用户名
		userNameEt.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v ) {
				// TODO Auto-generated method stub
				userNameEt.setCursorVisible(true);
			}
		} );

		//输入完毕，下一步，进入输入密码
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

		//输入密码
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


	}

	private void initView() {

		userNameEt = (EditText) findViewById(R.id.account__et); //用户名或者手机号
		passWordEt = (EditText) findViewById(R.id.password__et); //密码
		image_back=(ImageView) findViewById(R.id.first_login_back); //返回
		update_Password= (TextView) findViewById(R.id.update_Password); //忘记密码
		login_bt = (Button) findViewById(R.id.login_bt);
		new_account_bt = (TextView) findViewById(R.id.new_account_bt);

		login_bt.setOnClickListener(this);
		new_account_bt.setOnClickListener(this);
		update_Password.setOnClickListener(this);

//		findViewById(R.id.new_account_bt).setOnClickListener(this);
//		findViewById(R.id.login_visitor_bt).setOnClickListener(this);
//		findViewById(R.id.forget_password).setOnClickListener(this);


	}

	@Override
	protected void onResume() {
		super.onResume();
		// 0为横屏 ， 1为竖屏
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		if (id == R.id.login_bt) { //登录
			KnLog.log("login");

			Util.hideEditTextWindow(this, passWordEt);
			Util.hideEditTextWindow(this, userNameEt);
			checkAccountBindParams(activity, userNameEt, passWordEt);




			/*Util.hideEditTextWindow(this, passWordEt);
			checkLoginParams(FirstLoginActivity.this, userNameEt, passWordEt);*/
			KnLog.log("login End");
		} 
		else if (id == R.id.new_account_bt) { //快速注册
			// 隐藏虚拟键盘
			KnLog.log("new_account_bt");
			Util.hideEditTextWindow(this, passWordEt);
			//GameSDK.getInstance().register(FirstLoginActivity.this , true); //跳转到注册界面

			GameSDK.getInstance().KsRegister(FirstLoginActivity.this,true); //跳转到快速注册界面

		}else if (id == R.id.update_Password){ //忘记密码

			GameSDK.getInstance().Update_password(FirstLoginActivity.this,true); //修改密码
			activity.finish();

		}




//		else if (id == R.id.login_visitor_bt) {
//			/**
//			 * 	先请求服务器获取用户名和密码，如果已经绑定过了返回用户名和密码进行登录;如果没有绑定就进行游客登录
//			 */
//			KnLog.log("login_visitor_bt");
//			LoadingDialog.show(activity, "请求中...", true);
//			HttpService.queryBindMsi(activity, handler);
//			
//		} 
//		else if (id == R.id.forget_password) {
//			KnLog.log("forget_password");
//			intent = new Intent(activity.getApplicationContext(),FindActivity.class);
//			if( null == intent ){
//				return ;
//			}	
//			if(null==activity){
//				
//			}else{
//				
//				activity.startActivity(intent);
//				activity.finish();
//				activity = null ;
//				
//			}
//
//		}
		else{
			KnLog.log("forget_password null ");
			return ;
		}
		
	}


	private void checkAccountBindParams(Activity context, EditText mUsername, EditText mPassword) {

		String username = mUsername.getText().toString();
		String password = mPassword.getText().toString();


		if(TextUtils.isEmpty(username)){
			Util.ShowTips(activity,getResources().getString(R.string.tips_58));
			return ;
		}



		//KnLog.log("判断是否手机号："+ismobile(context, username));

		if (ismobile(context, username)) return;



		if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_2) );
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context,  getResources().getString(R.string.tips_8) );
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


		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}
		LoadingDialog.show(context, "绑定中...",true);


		//查询账号是否存在
		HttpService.getUsername(activity.getApplicationContext(), handler,username);


	}


	private boolean ismobile(Activity context, String username) {
		if(!Util.isMobileNO(username)) { //如果不是手机号
			//Util.ShowTips(m_activity, getResources().getString(R.string.tips_57)); //如果不是手机号

			if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
				Util.ShowTips(context, getResources().getString(R.string.tips_1));
				return true;
			}

			if (!username.matches("^[a-z|A-Z|0-9]{1,}$")) {
				Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
				return true;
			}

			if (!username.matches("^.{6,12}$")) {
				Util.ShowTips(context, getResources().getString(R.string.tips_4) );
				return true;
			}



		}
		return false;
	}




	/**
	 * 手机登录中
	 * @param context
	 * @param mUsername
	 * @param mPassword
	 */
	private void checkPhoneLoginParams(Context context, EditText mUsername, EditText mPassword) {
		String username = mUsername.getText().toString();
		if(TextUtils.isEmpty(username)){
			Util.ShowTips(context,getResources().getString(R.string.tips_58));
			return;
		}
		if(!Util.isMobileNO(username)){
			Util.ShowTips(context,getResources().getString(R.string.tips_57));
			return ;
		}
		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());

			return ;
		}

		String password = mPassword.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_8) );
			return;
		}

		password = Md5Util.getMd5(password);
		m_password = password ;
		m_userName = username ;
		LoadingDialog.show(activity, "手机登录中...",true);

		HttpService.doLogin(getApplicationContext(), handler, username, password);

	}



	private void checkLoginParams(Context context, EditText mUsername, EditText mPassword) {
		String username = mUsername.getText().toString().trim();
		String password = mPassword.getText().toString().trim();

		if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_2) );
			return;
		}

		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_8) );
			return;
		}
		
		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}

		String pw = Md5Util.getMd5(password);
		m_userName = username ;
		m_password = pw;
		KnLog.log("输入账号登录username="+username+" password="+pw);

		LoadingDialog.show(activity, "登录中...",true);

		HttpService.doLogin(getApplicationContext(), handler, username, pw);
		
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
			case ResultCode.SUCCESS:
				KnLog.log("登录成功++");
				DBHelper.getInstance().insertOrUpdateUser( m_userName , m_password );
				if(msg.obj!=null){
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
						//查询账号是否绑定手机号
					//	HttpService.queryBindAccont(activity.getApplicationContext(), handler, m_userName);

						activity.finish();
						activity=null;

					}else{
//						KnLog.log("请先设置登录回调");
					}
				}
				
				break;
			case ResultCode.FAIL:
				KnLog.log("登录失败++");				
				if(msg.obj!=null)
				{
					KnLog.log("登录失败01++");
					if(GameSDK.getInstance().getmLoginListener()!=null){
						KnLog.log("登录失败01++ reason:"+msg.obj.toString());
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						Util.ShowTips(activity,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
						KnLog.log("登录失败02++");
					}else{
//						KnLog.e("请先设置登录回调");
					}
				}
				KnLog.log("登录失败++End");
				break;
			case ResultCode.QUERY_MSI_BIND_SUCCESS:
				
				KnLog.log("MIS已经绑定过了++");
				
				String  msg_content = msg.obj.toString();
				JSONObject json;
				try {
					json = new JSONObject(msg_content);
					String username = json.getString("username");
					String[] usernames = DBHelper.getInstance().findAllUserName();
					
					for(int i=0;i!=usernames.length;++i){
						KnLog.log("username:"+usernames[i]);
					}
					
					boolean  flag = Util.findNameInSet(usernames, username);
					
					if(true==flag){
						 String password = DBHelper.getInstance().findPwdByUsername(username);
						 DBHelper.getInstance().insertOrUpdateUser( username , password );
					}else{
						KnLog.log("说明这个用户名是没有缓存数据的");
					}
					//String password = DBHelper.getInstance().findPwdByUsername(username);
					//DBHelper.getInstance().insertOrUpdateUser( username , password );
					Intent intent = null;
					if(null==activity){
						
					}else{
						intent = new Intent(activity.getApplicationContext(), AutoLoginActivity.class);
						intent.putExtra("userName", username);
						intent.putExtra("dbHas", flag);
						intent.putExtra("youke", true);
						if(null==intent){
							
						}else{
							
							activity.startActivity(intent);
							activity.finish();
							activity = null ;
							
						}
					}					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case ResultCode.QUERY_MSI_BIND_FAIL: //登录成功，开启倒计时完成详细信息界面
				KnLog.log("MIS没有绑定过了++");
				if(null==activity){
					
				}else{
					Intent intent = new Intent(activity.getApplicationContext(),VisitManagerActivity.class);
					if( null == intent ){
						return ;
					}	
					if(null==activity){
						
					}else{
						
						activity.startActivity(intent);
						activity.finish();
						activity = null ;
						
					}
					
				}
				
				break;
			case ResultCode.QUERY_ACCOUNT_BIND_SUCCESS: //返回账号绑定的手机号
				
							String mobile= msg.obj.toString() ; //服务器返回的手机号

				KnLog.log("返回账号绑定的手机号:"+mobile);


							if(null==activity){
							
							}else{
								
					
								activity.finish();
								activity = null ;
								
							}
							break;
			case ResultCode.QUERY_ACCOUNT_BIND_FAIL: //没有绑定手机号
							if(null==activity){
								
							}else{

								KnLog.log("返回账号绑定的手机号:没有绑定手机");
								//提示绑定手机弹窗
								LayoutInflater inflater = LayoutInflater.from(activity);
								View v = inflater.inflate(R.layout.bind_mobile_dialog, null);
								LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
								final AlertDialog dia=new AlertDialog.Builder(activity).create();
								Button bind=(Button) v.findViewById(R.id.visit_bind_account); //立即注册
							    Button cont=(Button) v.findViewById(R.id.visit_continue); //重新输入]
								TextView name = (TextView) v.findViewById(R.id.username); //提示

								String user= name.getText().toString(); //占位符
								String et=userNameEt.getText().toString().trim(); //账号
								name.setText(user.replace("1",et)); //替换

							   // bind.setText("绑定手机");
							    dia.show();
							    dia.setContentView(v);
							    bind.setOnClickListener(new OnClickListener() { //立即注册

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dia.dismiss();
										DBHelper.getInstance().insertOrUpdateUser( m_userName , m_password );
										Intent intent=new Intent(activity, BindCellActivity.class);
												intent.putExtra("userName", m_userName);
												startActivity(intent);
												if(null==activity){

												}else{
													activity.finish();
													activity = null ;
												}

									}
								});
				                cont.setOnClickListener(new OnClickListener() { //重新输入

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										dia.dismiss();
										DBHelper.getInstance().insertOrUpdateUser( m_userName , m_password );
										if(null==activity){

												}else{


											      dia.dismiss();

													/*activity.finish();
													activity = null ;*/
												}


									}
								});
								if(null==activity){
									
								}else{
									/*activity.finish();
									activity = null ;*/
								}
							}
               break;

				case ResultCode.GET_USER_SUCCRESS: //账号已经被注册过了


					KnLog.log("账号已经被注册过了，返回的信息："+msg.obj.toString());

					//开始登录
					Util.hideEditTextWindow(FirstLoginActivity.this, passWordEt);
			        checkLoginParams(FirstLoginActivity.this, userNameEt, passWordEt);

					break;

				case ResultCode.GET_USER_NoEXIStTENT: //账号没有被注册过

					KnLog.log("账号没有被注册过，返回的信息："+msg.obj.toString());

					//提示绑定手机弹窗
					LayoutInflater inflater = LayoutInflater.from(activity);
					View v = inflater.inflate(R.layout.bind_mobile_dialog, null);
					LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
					final AlertDialog dia=new AlertDialog.Builder(activity).create();
					Button bind=(Button) v.findViewById(R.id.visit_bind_account); //立即注册
					Button cont=(Button) v.findViewById(R.id.visit_continue); //重新输入]
					TextView name = (TextView) v.findViewById(R.id.username); //提示

					String user= name.getText().toString(); //占位符
					String et=userNameEt.getText().toString().trim(); //账号
					name.setText(user.replace("1",et)); //替换

					// bind.setText("绑定手机");
					dia.show();
					dia.setContentView(v);
					bind.setOnClickListener(new OnClickListener() { //立即注册

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dia.dismiss();
							DBHelper.getInstance().insertOrUpdateUser( m_userName , m_password );
							Intent intent=new Intent(activity, BindCellActivity.class);
							intent.putExtra("userName", m_userName);
							startActivity(intent);
							if(null==activity){

							}else{
								activity.finish();
								activity = null ;
							}

						}
					});
					cont.setOnClickListener(new OnClickListener() { //重新输入

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dia.dismiss();
							DBHelper.getInstance().insertOrUpdateUser( m_userName , m_password );
							if(null==activity){

							}else{


								dia.dismiss();


							}


						}
					});


					break;

				default:
				break;
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	

}
