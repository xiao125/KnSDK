package com.game.sdk_project;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kngame_sdk_project.R;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.activity.AccountManagerActivity;
import com.game.sdk.activity.AccounterBindActivity;
import com.game.sdk.activity.AutoLoginActivity;
import com.game.sdk.activity.FastLoginActivity;
import com.game.sdk.activity.FirstLoginActivity;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class SelecteLoginActivity extends Activity {
private ImageButton visit_login,account_login,ks_login;
private ImageView imageView;
private Activity activity;
private String username;
private boolean isFirstLogin=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selecte_login);
		
		activity=this;
		visit_login=(ImageButton) findViewById(R.id.select_login_visit_login); //游客登录
		account_login=(ImageButton) findViewById(R.id.select_login_account_login); //已有账号
		imageView=(ImageView) findViewById(R.id.select_log_close); //返回
		ks_login = (ImageButton) findViewById(R.id.select_login_ks_login); //快速登录
		String[] usernames = DBHelper.getInstance().findAllUserName(); //数据库查询是否有注册过的账号
		if (usernames.length == 0) {
			/*imageView.setBackgroundResource(R.drawable.auto_login_cancel);*/
			isFirstLogin=true;
		}
		
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isFirstLogin==true) {
					if(activity!=null){
						activity.finish();
						activity = null ;
					}
				}
				else{
					Intent intent=new Intent(activity, AutoLoginActivity.class); //账号登录
					startActivity(intent);
					activity.finish();
					activity=null;
				}
			}
		});

		//游客登录
		visit_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KnLog.log("login_visitor_bt");
				/**
				 * 	此时先查询是否绑定了账号
				 */
				HttpService.queryBindMsi(activity, handler);
				
			}
		});

		//已有账号登录
		account_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SelecteLoginActivity.this, FirstLoginActivity.class);
				startActivity(intent);
				if (activity!=null) {
					activity.finish();
				}
			}
		});

		//快速登录
		ks_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SelecteLoginActivity.this, FastLoginActivity.class);
				startActivity(intent);
				if (activity!=null) {
					activity.finish();
				}
			}
		});


		if (activity==null){
			activity.finish();
			activity=null;
		}

	}


	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case ResultCode.QUERY_MSI_BIND_SUCCESS:
				
				KnLog.log("MIS已经绑定过了++");
				LoadingDialog.show(activity, "正在登陆....", true);
				String  msg_content = msg.obj.toString();
				JSONObject json;
				try {
					json = new JSONObject(msg_content);
					username = json.getString("username");
					String[] usernames = DBHelper.getInstance().findAllUserName();
					
					for(int i=0;i!=usernames.length;++i){
						KnLog.log("username:"+usernames[i]);
					}
					
					boolean  flag = Util.findNameInSet(usernames, username);
					
					if(true==flag){
						 String password = DBHelper.getInstance().findPwdByUsername(username);
						 DBHelper.getInstance().insertOrUpdateUser( username , password );
						//登录
						 HttpService.doLogin(getApplicationContext(), mHandler, username,password);
					}else{
						KnLog.log("说明这个用户名是没有缓存数据的");

						//游客登录MIS绑定
						HttpService.visitorReg(activity,mHandler);


					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case ResultCode.QUERY_MSI_BIND_FAIL:
				KnLog.log("MIS没有绑定过了++");
				//弹出对话框，选择是否绑定手机
				LayoutInflater inflater = LayoutInflater.from(activity);
				View v = inflater.inflate(R.layout.visit_manager_dialog, null);
				LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
				final AlertDialog dia=new AlertDialog.Builder(activity).create();
				
			    dia.show();
			    dia.setContentView(v);
			    Button bind=(Button) v.findViewById(R.id.visit_bind_account_zh); //绑定萌创账号
			    Button cont=(Button) v.findViewById(R.id.bind_mis_visit_continue); //游客登录
				ImageView image_back = (ImageView) v.findViewById(R.id.image_back); //返回
				ImageView select_log_close = (ImageView) v.findViewById(R.id.select_log_close); //关闭

				image_back.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(activity!=null){
							activity.finish();
							activity=null;
						}

					}
				});

				select_log_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(activity!=null){
							activity.finish();
							activity=null;
						}
					}
				});



				//升级成账号登录
			    bind.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dia.dismiss();
						Intent intent=new Intent(activity, AccounterBindActivity.class);
						startActivity(intent);
						activity.finish();
						activity=null;
					}
				});


				//使用游客登录
                cont.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dia.dismiss();
						if(!Util.isNetWorkAvailable(getApplicationContext())){
							Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
							return ;
						}
						if(	null == DeviceUtil.getDeviceId()){
							Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_59).toString());
							return ;
						}
						KnLog.log("游客登录开始");
						//游客登录并绑定手机mis
						HttpService.visitorReg(activity,mHandler);
					}
				});
				break;
			default:
				break;
			}
		}
	};


	//游客登录回调事件
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoadingDialog.dismiss();
			switch (msg.what) {
			case ResultCode.SUCCESS:
				if(msg.obj!=null){
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );

						//游客模式询问用户是否注册账号
						Intent intent = new Intent(SelecteLoginActivity.this,AccountManagerActivity.class);
						startActivity(intent);

						if(null==activity){
							
						}else{
							Toast.makeText(activity, "亲爱的"+username+"玩家，登陆成功", Toast.LENGTH_SHORT).show();
							activity.finish();
							activity = null ;
							
						}
						
					}else{

					}
				}
				break;
			case ResultCode.FAIL:
				if(msg.obj!=null)
				{
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );	
						if(null==activity){
							
						}else{
							
							activity.finish();
							activity = null ;
							
						}
					}else{
//						KnLog.e("请先设置登录回调");
					}
				}
				break;
			case ResultCode.VISITOR_LOGIN_SUCCESS: //
				KnLog.log("游客登录成功");
				if(msg.obj!=null){
					KnLog.log("游客登录成功结果:"+msg.obj.toString());
					if(GameSDK.getInstance().getmLoginListener()!=null){
						KnLog.log("getmLoginListener().onSuccess++");
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
						if(activity!=null){
							activity.finish();
							activity = null ;
						}
					}else{
						
					}
				}
				break;
			case ResultCode.VISITOR_LOGIN_FAIL:
				KnLog.log("游客登录失败");
				if(msg.obj!=null)
				{
					KnLog.log("游客登录失败结果:"+msg.obj.toString());
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						Util.ShowTips(activity,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
						activity.finish();
					}else{
						
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	
}
