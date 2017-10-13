package com.game.sdk.service;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.game.sdk.GameSDK;
import com.game.sdk.SDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.PayInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.listener.BaseListener;
import com.game.sdk.task.BindMobileAsyncTask;
import com.game.sdk.task.CommonAsyncTask;
import com.game.sdk.task.GetAccontMobileAsyncTask;
import com.game.sdk.task.GetSecurityCodeAsyncTask;
import com.game.sdk.task.GetUserNameAsyncTask;
import com.game.sdk.task.LoginAsyncTask;
import com.game.sdk.task.MobileRegisterAsyncTask;
import com.game.sdk.task.PassWordNewBindMobileAsyncTask;
import com.game.sdk.task.QueryAccountBindAsyncTask;
import com.game.sdk.task.QueryMsiBindAsyncTask;
import com.game.sdk.task.RegisterAsyncTask;
import com.game.sdk.task.VisitorAccountBindAsyncTask;
import com.game.sdk.task.VisitorAsyncTask;
import com.game.sdk.task.VisitorBindMobileAsyncTask;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Md5Util;
import com.game.sdk.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpService {
	
	private static final String PROXY_VERSION = "1.0.1" ;

	//查询是否绑定账号
	public static void queryBindMsi( Context applicationContext, Handler handler){
		
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION;
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		update_params.put("msi",imei);
		
		Log.e("msi", imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret ); //默认签名规则
		
		new QueryMsiBindAsyncTask(applicationContext, handler, SDK.QUERY_MSI_BIND)
				.execute(new Map[] { update_params1, null, null });
		
	}

	//查询账号是否绑定手机号
	public static void queryBindAccont( Context applicationContext, Handler handler, String user_Name ){
		
		try {

			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION;
			String gameName = gameInfo.getGameId() ; 
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("user_name",user_Name);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new QueryAccountBindAsyncTask(applicationContext, handler, SDK.QUERY_ACCOUNT_BIND)
			.execute(new Map[] { update_params1 , null, null });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	//验证账号是否存在
	public static void getUsername( Context applicationContext, Handler handler, String user_Name ){

		try {


			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();

			HashMap<String , String> params = getCommonParams();

			String game_id = gameInfo.getGameId();
			String platform = gameInfo.getPlatform();
			String channel = gameInfo.getChannel();

			JSONObject obj = new JSONObject();
			obj.put("user_name", user_Name);
			String content = obj.toString();
			params.put("content", content);

			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey())); //这个接口验签必须是md5


			new GetUserNameAsyncTask(applicationContext, handler, SDK.GET_USER_NAME)
					.execute(new Map[] { params , null, null });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//游客绑定账号
	public static void visitorBindAccount( Context applicationContext, Handler handler,
			String username, String password ){
		
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		JSONObject content = new JSONObject();
		try {
			content.put("user_name",username);
			content.put("passwd",password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		update_params.put("content", content.toString());
		
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		
		new VisitorAccountBindAsyncTask(applicationContext, handler, SDK.VISITOR_ACCOUNT_BIND)
				.execute(new Map[] { update_params1, null, null });
		
		
	}

	//游客登录
	public static void visitorReg( Context applicationContext, Handler handler ){
		
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		
		new VisitorAsyncTask(applicationContext, handler, SDK.VISITOR_REG)
				.execute(new Map[] { update_params1, null, null });
		
	}

	//获取验证码请求
	public static void getSecCode( Context applicationContext, Handler handler, String mobile,String newSdk ){
		try {
			
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			HashMap<String , String> params = new HashMap<String, String>();
			
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ; 
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
		//	update_params.put("newSdk", newSdk);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new GetSecurityCodeAsyncTask(applicationContext, handler, SDK.GET_RESURITY_CODE_URL)
					.execute(new Map[] { update_params1, null, null });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	//绑定手机请求
	public static void bindMobile( Context applicationContext, Handler handler, String mobile , String security_code , String user_Name ){
		
		try {

			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ;
			String imei = DeviceUtil.getDeviceId();
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("msi",imei);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new BindMobileAsyncTask(applicationContext, handler, SDK.BIND_MOBILE_URL)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//游客绑定手机
	public static void visitorbindMobile( Context applicationContext, Handler handler, String mobile , String security_code , String user_Name,String user_Password ){

		try {

			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ;
			String imei = DeviceUtil.getDeviceId();
			String channel = gameInfo.getChannel();
			String ad_channel = gameInfo.getAdChannel();
			String platform = gameInfo.getPlatform();

			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );

			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("passwd",user_Password);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("msi",imei);
			update_params.put("game_id",gameName);
			update_params.put("channel",channel);
			update_params.put("platform",platform);
			update_params.put("ad_channel",ad_channel);

			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );

			new VisitorBindMobileAsyncTask(applicationContext, handler, SDK.VISITOR_BIND_MOBILE)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	
	public static void getAccountSubmit( Context applicationContext, Handler handler, String mobile , String security_code ){
	
		try {
			
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = Util.getJsonStringByName(appInfo, "versionCode") ;
//			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ; 
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new GetAccontMobileAsyncTask(applicationContext, handler, SDK.GET_ACCOUNT_URL)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//更加手机验证码修改密码
	public static void passwordNewSubmit( Context applicationContext, Handler handler, String mobile , String security_code , String new_password,String newSdk ){
		
		try {

			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ; 
			
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("new_pwd",new_password);
			content.put("rand_code",security_code);
			update_params.put("newSdk", newSdk);//区分sdk
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new PassWordNewBindMobileAsyncTask(applicationContext, handler, SDK.UPDATE_PASSWORD_URL)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

   //账号登录请求
	public static void doLogin(Context applicationContext, Handler handler,
			String username, String password) {

		try {
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();

			HashMap<String , String> params = getCommonParams();

			String game_id = gameInfo.getGameId();
			String platform = gameInfo.getPlatform();
			String channel = gameInfo.getChannel();
			
			JSONObject content = new JSONObject();
			content.put("user_name", username);
			content.put("passwd", password);

			params.put("content", content.toString());
			
			params.put(
					"sign",
					Md5Util.getMd5(game_id + channel
							+ platform + content.toString()
							+ gameInfo.getAppKey()));
			
			KnLog.log(" login push the data "+gameInfo.getAppKey());

			new LoginAsyncTask(applicationContext, handler, SDK.LOGIN_URL)
					.execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//根据用户名与密码来注册账号
	public static void doRegister(Context applicationContext, Handler handler,
			String username, String password) {
		
		try {
			HashMap<String,String> params = getCommonParams();
			
			JSONObject obj = new JSONObject();
			obj.put("user_name", username);
			obj.put("passwd", password);
			
			String content = obj.toString();
			
			params.put("content", content);
			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));

			new RegisterAsyncTask(applicationContext, handler, SDK.REG_URL)
					.execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//手机号注册账号
	public static void doMobileRegister(Context applicationContext, Handler handler,
								  String mobile,String code, String password) {

		try {

			HashMap<String,String> params = getCommonParams();

			JSONObject obj = new JSONObject();
			obj.put("mobile",mobile);
			obj.put("passwd",password);
			obj.put("rand_code",code);
			String content = obj.toString();
			params.put("content",content);

			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));

			new MobileRegisterAsyncTask(applicationContext, handler, SDK.REG_MOBILE)
					.execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	
	public static void chanagePwd(Activity activity,
			String username, String oldpassword , String newpassword , BaseListener listener) {
		try {
			HashMap<String,String> params = getCommonParams();
			
			JSONObject obj = new JSONObject();
			obj.put("user_name", username);
			obj.put("passwd", oldpassword);
			obj.put("new_pwd", newpassword);

			String content = obj.toString();

			params.put("content", content);
			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));
			
			KnLog.log(params.toString());

			new CommonAsyncTask(activity , SDK.CHANGE_PWD_URL, listener)
			.execute(new Map[] { params, null, null });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void applyOrder( Activity activity ,PayInfo payInfo , BaseListener listener) {
		try {

			UserInfo userInfo = GameSDK.getInstance().getUserInfo();
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();

			Map<String,String> params = getCommonParams();
			
			String uid = payInfo.getUid();
			int server_id = payInfo.getServerId();
			
			String open_id = userInfo.getOpenId();

			String game_id = gameInfo.getGameId();
			String platform = gameInfo.getPlatform();
			String channel = gameInfo.getChannel();

			params.put("platform", platform);
			params.put("extra_info", payInfo.getCpprivateinfo());

			params.put(
					"sign",
					Md5Util.getMd5(game_id + channel + platform + uid + open_id
							+ server_id + gameInfo.getAppKey()));
			
			new CommonAsyncTask(activity , SDK.APPLY_ORDER_URL, listener)
					.execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static HashMap<String, String> getCommonParams(){
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		UserInfo userInfo = GameSDK.getInstance().getUserInfo();
		GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
		
		String open_id="",game_id="",channel="",ad_channel="",msi="",platform="";
		String uid="",server_id="";
		
		if(userInfo!= null){
			open_id = userInfo.getOpenId();
			uid = userInfo.getUid();
			server_id = String.valueOf(userInfo.getServerId());
		}
		
		if(gameInfo!=null){
			platform = gameInfo.getPlatform();
			game_id =  gameInfo.getGameId();
			channel = gameInfo.getChannel();
			ad_channel = gameInfo.getAdChannel();
		}
		msi = DeviceUtil.getDeviceId();
		
		params.put("game_id", game_id);
		params.put("channel", channel);
		params.put("ad_channel", ad_channel);
		params.put("uid", String.valueOf(uid));
		params.put("open_id", open_id);
		params.put("server_id", String.valueOf(server_id));
		params.put("mac", DeviceUtil.getMacAddress());
		params.put("platform", platform);
		params.put("phoneType", DeviceUtil.getPhoneType());
		params.put("netType", DeviceUtil.getNetWorkType());
		
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
		params.put("packageName", Util.getJsonStringByName(appInfo, "packageName") );
		params.put("versionName", Util.getJsonStringByName(appInfo, "versionName") );
		params.put("versionCode", Util.getJsonStringByName(appInfo, "versionCode") );
		
		
		params.put("msi", msi );
		KnLog.log(" Login params :"+params.toString());
		
		return params;
		
	}

}
