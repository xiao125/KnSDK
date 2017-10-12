package com.game.sdk;

//import com.UCMobile.PayPlugin.PayInterface;
//import com.iapppay.mpay.ifmgr.IPayResultCallback;
//import com.iapppay.mpay.ifmgr.SDKApi;
//import com.iapppay.mpay.tools.PayRequest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.example.kngame_sdk_project.R;
import com.game.sdk.activity.AutoLoginActivity;
import com.game.sdk.activity.ChangePwdActivity;
import com.game.sdk.activity.FastLoginActivity;
import com.game.sdk.activity.ForgotPasswordActivity;
import com.game.sdk.activity.RegisterActivity;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.PayInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.listener.LoginListener;
import com.game.sdk.listener.PayListener;
import com.game.sdk.pay.PAY_API;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;
import com.game.sdk_project.SelecteLoginActivity;

public class GameSDK {

	public static GameSDK instance = null;

	private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; //横竖屏
	private boolean isInited = false;

	private Activity activity = null;

	private LoginListener mLoginListener = null;
	private PayListener mPayListener = null;

	private UserInfo userInfo = null;
	private GameInfo gameInfo = null;
	
	private boolean  mScreenSensor = false ;
	
	

	public boolean ismScreenSensor() {
		return mScreenSensor;
	}

	public void setmScreenSensor(boolean mScreenSensor) {
		this.mScreenSensor = mScreenSensor;
	}

	public static GameSDK getInstance() {
		if (instance == null) {
			instance = new GameSDK();
		}
		return instance;
	}

	/**
	 * @param activity
	 * @param
	 * *  0为横屏 ， 1为竖屏
	 */
	public void initSDK(Activity activity, GameInfo gameInfo) {
		this.activity = activity;
		this.isInited = true;
		setmOrientation(gameInfo.getOrientation());
		setGameInfo(gameInfo);
		SDK.changeConfig(gameInfo.getAdChannelTxt());
		KnLog.setLogEnable(false);
		
		
		//	读取activity中manifest.xml中某个键值对是否支持横竖屏切换
		ApplicationInfo ai;
		String adChannel = null ;
		try {
			ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			if(null==bundle){
				KnLog.e("bundle is null");
			}else{
				if(bundle.containsKey("ScreenSendor")){
					mScreenSensor = true ;
				}else{
					
				}
			}
		    
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param activity
	 * @param appid
	 *            爱贝appid
	 * @param appkey
	 *            爱贝appkey
	 */
	public void initIappaySDK(Activity activity,String appid, String appkey , String publicKey) {
		PAY_API.getInstance().init(activity, getmOrientation(), appid , appkey  , publicKey);
	}

	/**
	 * 跳转到登陆页面
	 * 
	 * @param
	 * @param
	 */
	
	public void login( Activity activity ){
		
		KnLog.log(" login ccc");
		
		if (!isInited()) {
			Util.ShowTips(activity, activity.getResources().getString(R.string.tips_16) );
			return;
		}

		String[] usernames = DBHelper.getInstance().findAllUserName();
		Intent intent = null;
		//	数据库中获取用户数据量
		if (usernames.length == 0 ) {
			intent = new Intent(activity.getApplicationContext(), SelecteLoginActivity.class);
			intent.putExtra("selectLogin", "selectLogin");
		} else {
			String  lastUserName = usernames[0];
			KnLog.log("lastUserName:"+lastUserName);
			intent = new Intent(activity.getApplicationContext(), AutoLoginActivity.class);
			KnLog.log("lastUserName="+lastUserName);
			intent.putExtra("userName",lastUserName);
		}
		
		if( null == intent ){
			return ;
		}
		
		activity.startActivity(intent);
		activity.finish();
		activity = null ;
		
	}
	
	public void login(Activity activity, LoginListener listener) {
		
		KnLog.log(" login cccd");
		
		if( listener==null ){
			KnLog.e("请先设置登录监听");
			Util.ShowTips(activity, "请先设置登录监听" );
		}
			
		setmLoginListener(listener);
		
		if (!isInited()) {
			Util.ShowTips(activity, activity.getResources().getString(R.string.tips_16) );
			return;
		}

		String[] usernames = DBHelper.getInstance().findAllUserName();
		Intent intent = null;
		//	数据库中获取用户数据量
		if (usernames.length == 0 ) {
			intent = new Intent(activity.getApplicationContext(), SelecteLoginActivity.class);
			intent.putExtra("selectLogin", "selectLogin");
			
		} else {
			String  lastUserName = usernames[0];
			KnLog.log("lastUserName:"+lastUserName);
			intent = new Intent(activity.getApplicationContext(), AutoLoginActivity.class);
			KnLog.log("lastUserName="+lastUserName);
			intent.putExtra("userName",lastUserName);
		}
		
		if( null == intent ){
			return ;
		}
		
		activity.startActivity(intent);
		
	}

	/**
	 * 
	 * @param
	 */

	public void changePwd(Activity activity, String username, boolean hasResult) {
		
		if (hasResult) 
		{
			Intent intent = new Intent(activity.getApplicationContext(), ChangePwdActivity.class);
			intent.putExtra("username", username);
			activity.startActivityForResult(intent, SDK.REQUESTCODE_CHANGEPWD);
		}
		else
		{
			Intent intent = new Intent(activity.getApplicationContext(), ChangePwdActivity.class);
			intent.putExtra("username", username);
			activity.startActivity(intent);
		}
		
	}

	// 跳转到注册页面
	public void register(Activity activity, boolean hasResult) {

		if (hasResult)
		{
			Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
			activity.startActivityForResult(intent, SDK.REQUESTCODE_REG);
			activity.finish();
		}
		else
		{
			Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
			activity.startActivity(intent);
		}
	}


	// 跳转到快速注册页面
	public void KsRegister(Activity activity, boolean hasResult) {

		if (hasResult)
		{
			Intent intent = new Intent(activity.getApplicationContext(),FastLoginActivity.class);
			activity.startActivityForResult(intent, SDK.REQUESTCODE_REG);
			activity.finish();
		}
		else
		{
			Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
			activity.startActivity(intent);
		}
	}

	// 跳转到修改密码
	public void Update_password(Activity activity, boolean hasResult) {

		if (hasResult)
		{

			Intent intent = new Intent(activity.getApplicationContext(),ForgotPasswordActivity.class);
			activity.startActivityForResult(intent, SDK.UPDATE_PASSWORD);
			activity.finish();

		/*	Intent intent = new Intent(activity.getApplicationContext(),PasswordUpdateActivity.class);
			activity.startActivityForResult(intent, SDK.UPDATE_PASSWORD);
			activity.finish();*/
		}
		else
		{
			Intent intent = new Intent(activity.getApplicationContext(), RegisterActivity.class);
			activity.startActivity(intent);
		}
	}



	/**
	 * 
	 * @param activity
	 * @param
	 * @param
	 */
	public void pay(final Activity activity, final PayInfo payInfo,
			final PayListener payListener) {
		
		if (userInfo == null || !userInfo.isLogin()) {
			Util.ShowTips(activity,  activity.getResources().getString(R.string.tips_17) );
			return;
		}
		
		setmPayListener(payListener);
		PAY_API.getInstance().pay(activity, payInfo, payListener);
		
	}

	public int getmOrientation() {
		return mOrientation;
	}

	private void setmOrientation(int mOrientation) {
		this.mOrientation = mOrientation;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public LoginListener getmLoginListener() {
		return mLoginListener;
	}

	private void setmLoginListener(LoginListener mLoginListener) {
		this.mLoginListener = mLoginListener;
	}

	public PayListener getmPayListener() {
		return mPayListener;
	}

	public void setmPayListener(PayListener mPayListener) {
		this.mPayListener = mPayListener;
	}

	public boolean isInited() {
		return isInited;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public void setGameInfo(GameInfo gameInfo) {
		this.gameInfo = gameInfo;
	}

}
