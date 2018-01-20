package com.proxy.sdk.channel;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.WebView;
import com.game.sdk.GameSDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.PayInfo;
import com.game.sdk.listener.LoginListener;
import com.game.sdk.listener.PayListener;
import com.game.sdkproxy.R;
import com.proxy.Data;
import com.proxy.ResultCode;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.Result;
import com.proxy.bean.User;
import com.proxy.listener.BaseListener;
import com.proxy.sdk.SdkProxy;
import com.proxy.sdk.module.SdkConfigModule;
import com.proxy.service.HttpService;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;
import com.proxy.util.Util;


public class SdkChannel extends SdkProxy {
	
	private static SdkChannel instance = null;
	
	private Data knData = Data.getInstance();

	private static Activity  mActivity;
	

	private GameSDK gameSDK = GameSDK.getInstance();
	private boolean hasLogin = false;	// 登录成功后才可调用上传角色信息接口
	public static String paySign = null;
	public static String payKey = null;
	
	

	
	
	
	/**
	 * @return sdk渠道的版本号
	 */
	@Override
	public String getChannelVersion(){
		return null;
	}

	public static SdkChannel getInstance(){

		if(instance == null)
			instance = new SdkChannel();
		return instance;
	}

	

	@Override
	public void onCreate(Activity activity){
		super.onCreate(activity);		
		mActivity=activity;		
		activationGame(activity);


		//sdk初始化
		sdkinit();
	

		if(kNListener.getInitListener() != null){
			kNListener.getInitListener().onSuccess(null);
		}
	}
	

	
	
	//sdk初始化
	public void sdkinit(){


			LogUtil.log("点击初始化");
		   LogUtil.log("channel="+mGameInfo.getAdChannel());
			GameInfo info = new GameInfo(mGameInfo.getAppKey(), mGameInfo.getGameId(), mGameInfo.getChannel(), mGameInfo.getPlatform(), mGameInfo.getAdChannel(), mGameInfo.getScreenOrientation() , mGameInfo.getAdChannelTxt());
			info.setGid(mGameInfo.getGid());

			
			LogUtil.log("萌创sdk初始化="+mGameInfo.getAppKey()+ mGameInfo.getGameId()+mGameInfo.getChannel()
					+mGameInfo.getPlatform()+mGameInfo.getAdChannel()
					+mGameInfo.getScreenOrientation()+mGameInfo.getAdChannelTxt());
			
			gameSDK.initSDK(mActivity ,info );
			
			// IAppPay.init(mActivity, IAppPay.LANDSCAPE,SDKConfig.appid); //需要渠道分包功能，请传入对应渠道标识ACID, 可以为空
			
			if( Util.fileExits(mActivity ,"SDKFile/config.png")){
			
				gameSDK.initIappaySDK( mActivity,Util.getApiappId(mActivity) , Util.getApiprivateKey(mActivity) ,  Util.getApipublicKey(mActivity)  );			
			
			LogUtil.log("爱贝支付初始化成功1="+Util.getApiappId(mActivity) +" privateKey="+Util.getApiprivateKey(mActivity)+" publicKey="+Util.getApipublicKey(mActivity) );
			
			}else{
				
				gameSDK.initIappaySDK( mActivity, SDKConfig.appid ,SDKConfig.privateKey , SDKConfig.publicKey );			
				LogUtil.log("爱贝支付初始化成功2="+SDKConfig.appid +" privateKey="+SDKConfig.privateKey+" publicKey="+SDKConfig.publicKey);

			}
			
		}


	@Override
	public boolean canEnterGame() {
		return false;
	}

	/**
	 * 游戏进入游戏首页的时候调用
	 */
	@Override
	public void onEnterGame(Map<String, Object> data) {
		super.onEnterGame(data);
		//提交用户信息
		String serverid=String.valueOf(knData.getGameUser().getServerId());
		String servername = knData.getGameUser().getServerName();
		String level= String.valueOf(knData.getGameUser().getUserLevel());
		String username=knData.getGameUser().getUsername();
		String uid=knData.getGameUser().getUid();
		String roleCtime =knData.getGameUser().getRoleCTime();
		String vip=knData.getGameUser().getVipLevel();				 
						 
//		LogUtil.log("提交用户信息: 角色创建时间="+Integer.valueOf(roleCtime)+"角色id="+uid+"");
		
		
		
		if (knData.getGameUser().getUsername().equals("")||knData.getGameUser().getUsername()=="") {
			
			
		}else{
			
		
			
		}
		
		
	}

	

	@Override
	public void onGameLevelChanged(int newlevel) {
		super.onGameLevelChanged(newlevel);
	}

	@Override
	public void onResume() {
		
		super.onResume();		
		
	
		
	}

	@Override
	public void onPause() {
		
		
		super.onPause();
		
	   LogUtil.log("onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		LogUtil.log("onStop");
		
	}

	@Override
	public void onRestart() {
		
		super.onRestart();
		
		
		LogUtil.log("onRestart");
		
		
		
		
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();	

		LogUtil.log("onDestroy");
		

	}
					
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}
	
	
	@Override
	protected void onNewIntent(Intent newIntent) {
		// TODO Auto-generated method stub
		super.onNewIntent(newIntent);
	}

	
	@Override
    protected void onWindowFocusChanged(boolean hasFocus) {
    	// TODO Auto-generated method stub
    	super.onWindowFocusChanged(hasFocus);
    	
    	
		}    	
	
	
	
	//是否调用第三方退出
	public boolean hasThirdPartyExit(){
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	
	@Override
	public void login(Activity activity, Map<String, Object> params) {
		super.login(activity , params);

		String gameId = Data.getInstance().getGameInfo().getGameId();

		gameSDK.login(activity, new LoginListener() {

			@Override
			public void onSuccess(Object result) {


				JSONObject obj = null;
				final User user = new User();
				String  invite  = "";
				String  code    = "";

				try {
					obj = new JSONObject(result.toString());

					LogUtil.log("游戏登录成功返回的 ogj="+result.toString());

					user.setOpenId( obj.getString("open_id") );
					user.setSid( obj.getString("sid") );
					user.setSign( obj.getString("sign") );
					user.setIsIncompany( Integer.parseInt(obj.getString("iscompany")) );
					user.setLogin(true);
					user.setExtenInfo( obj.getString("extra_info") );
					invite = obj.getString("invite");

					JSONObject  inviteObj = new JSONObject(invite.toString());
					code = inviteObj.getString("code");
					//Log.e("code="+code);
					Data.getInstance().setUser(user);
					Data.getInstance().setInviteCode(code);
					mLoginListener.onSuccess(user);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFail(Object result) {
				LogUtil.log("res+fal"+result.toString());
				mLoginListener.onFail(result.toString());
			}
		});
		
	}
	

	
	
	

	@Override
	public void pay( final Activity activity , final KnPayInfo knPayInfo) {
		super.pay(activity,knPayInfo);

		
		final PayInfo payInfo = new PayInfo();		
		LoadingDialog.show(activity, "正在申请订单", false);
		LogUtil.log("订单申请");
		HttpService.applyOrder(activity, knPayInfo, new BaseListener() {
			
			@Override
			public void onSuccess(Object result) {
				LogUtil.log("订单申请成功");
				LoadingDialog.dismiss();
				JSONObject obj = null;
				try {
					JSONObject obj1 = new JSONObject(result.toString());
					final String order = obj1.getString("order_no");
					double price = knPayInfo.getPrice();
					final int  priceInt=Integer.parseInt(new java.text.DecimalFormat("0").format(price/100));
					final String num = Integer.toString(priceInt);
					final String gameId = Data.getInstance().getGameInfo().getGameId();
					LogUtil.log("order = "+order +"  num = "+num +"  gameId= "+gameId);
					
					//根据返回isGamePay字段判断微信或爱贝
					if (Integer.parseInt(obj1.getString("isGamePay")) == 1) {
		
						
					}else{

					try {
						LogUtil.log("--------------支付信息 obj = "+ result.toString());

						obj = new JSONObject(result.toString());

						payInfo.setExorderno(obj.getString("order_no"));
						// payInfo.setNotifyurl(PayConfig.notifyurl);
						payInfo.setAppid(SDKConfig.appid);

						payInfo.setWaresid(1);
						payInfo.setPrice((knPayInfo.getPrice()));
						payInfo.setProductName(knPayInfo.getProductName());
						// payInfo.setCpprivateinfo(knPayInfo.getCpPrivateInfo());
						payInfo.setUid(mGameUser.getUid());
						payInfo.setServerId(mGameUser.getServerId());
						LogUtil.log("--------------支付信息 obj " + " order_no = "
								+ obj.getString("order_no") + "  appId= "
								+ SdkConfigModule.getApiappId(activity)
								+ " Price= " + knPayInfo.getPrice()
								+ "  ProductName=" + knPayInfo.getProductName()
								+ " Uid =" + mGameUser.getUid() + "  ServerId="
								+ mGameUser.getServerId());
					} catch (JSONException e) {
						e.printStackTrace();
					}
						

						
			     	LogUtil.log(",开始爱贝支付,payInfo="+payInfo.getExorderno()+ payInfo.getPrice()+payInfo.getUid()+payInfo.getProductName());
					GameSDK.getInstance().pay(activity, payInfo,
							new PayListener() {

								@Override
								public void onSuccess(Object result) {
									
									LogUtil.log("sdk爱贝支付成功,result="+result);
									mPayListener.onSuccess(result);
									if (null == result) {
									} else {
										LogUtil.log("result:" + result.toString());
									}
								}

								@Override
								public void onFail(Object result) {
									
									LogUtil.log("sdk爱贝支付失败,result="+result);
									mPayListener.onFail(result);
									if (null == result) {
									} else {
										LogUtil.log("result:" + result.toString());
									}
								}
							});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFail(Object result) {
				LoadingDialog.dismiss();
				
				LogUtil.log("订单申请失败");
				mPayListener.onFail(new Result(ResultCode.APPLY_ORDER_FAIL, "申请订单失败"));
			}
		});
	}	

	
	@Override
	protected void switchAccount() {
		// TODO Auto-generated method stub
		super.switchAccount();
		
		//注销账号
		LogUtil.log("切换游戏账号");

		 gameSDK.login(mActivity, new LoginListener() {
				
				@Override
				public void onSuccess(Object result) {
					
				
					
					JSONObject obj = null;
					final User user = new User();
					String  invite  = "";
					String  code    = "";
					
					try {
						obj = new JSONObject(result.toString());
						
						LogUtil.log("游戏登录成功返回的 ogj="+result.toString());
						
						user.setOpenId( obj.getString("open_id") );
						user.setSid( obj.getString("sid") );
						user.setSign( obj.getString("sign") );
						user.setIsIncompany( Integer.parseInt(obj.getString("iscompany")) );
						user.setLogin(true);								
						user.setExtenInfo( obj.getString("extra_info") );									
						invite = obj.getString("invite");
						
						JSONObject  inviteObj = new JSONObject(invite.toString());
						code = inviteObj.getString("code");
						//Log.e("code="+code);
						Data.getInstance().setUser(user);
						Data.getInstance().setInviteCode(code);
						mLoginListener.onSuccess(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				@Override
				public void onFail(Object result) {
					LogUtil.e("res+fal"+result.toString());
					mLoginListener.onFail(result.toString());
				}
			});
			
		
		
	}
	
	
		@Override
		protected void onThirdPartyExit() {
			// TODO Auto-generated method stub
			super.onThirdPartyExit();
			
			LogUtil.log("退出游戏");


			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setTitle(R.string.game_app_name );
			builder.setMessage(R.string.exit_game);
			builder.setPositiveButton(R.string.exit_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					System.exit(0);
				}
			});
			builder.setNegativeButton(R.string.exit_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();


		}

		@Override
		protected void logout() {
			// TODO Auto-generated method stub
			super.logout();
			
			LogUtil.log("调用sdk注销登录");
				
		}
		
		public void pushActivation(  final Activity activity , final Map<String, Object>  data  ){
			
			HttpService.doPushActivation(activity, data,  new BaseListener() {
				
				@Override
				public void onSuccess(Object result) {
				
					activity.finish();
					
				}
				
				@Override
				public void onFail(Object result) {
					

					activity.finish();
					
				}
			} );
	 	
		}
		
		
		//上报
		public void pushData( final Activity activity , Map<String,Object> data ){
			
			HttpService.doPushData(activity, data, new BaseListener() {
				
				@Override
				public void onSuccess(Object result) {
					
					
					
				}
				
				@Override
				public void onFail(Object result) {
					
					
					
				}
			} );
			
		}






		
		public void activationGame( Activity activity  ){
			
			//游戏激活
			Map<String, Object>  data = new HashMap<String, Object>();
			
			data.put("game_id",mGameInfo.getGameId());
			data.put("ip",DeviceUtil.getMacAddress());
			data.put("app_key",mGameInfo.getAppKey());
			String mis =DeviceUtil.getDeviceId(); //IMEI码
			data.put("imei",mis);
			data.put("platform",mGameInfo.getPlatform());

			data.put("ad_channel",mGameInfo.getAdChannel());
			data.put("phone_type",DeviceUtil.getPhoneType());
			
			data.put("channel",mGameInfo.getChannel());
			
			pushData(activity,data);
			
		}








}





