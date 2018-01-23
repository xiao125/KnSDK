package com.proxy.sdk.channel;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import com.mengyousdk.lib.LSListener;
import com.mengyousdk.lib.core.LSCore;
import com.mengyousdk.lib.model.pay.LSOrder;
import com.proxy.Listener;
import com.proxy.OpenSDK;
import com.proxy.Data;
import com.proxy.ResultCode;
import com.proxy.activity.ActivationCodeAc;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.Result;
import com.proxy.bean.User;
import com.proxy.listener.BaseListener;
import com.proxy.listener.InvitationListener;
import com.proxy.sdk.SdkProxy;
import com.proxy.service.HttpService;
import com.proxy.util.DeviceUtil;
import com.proxy.util.LoadingDialog;
import com.proxy.util.LogUtil;


public class SdkChannel extends SdkProxy {


	private static SdkChannel instance = null;
	protected Listener kNListener = Listener.getInstance();
	private static Activity mActivity = null;
	private final int EVENT_CREATE_ROLEINFO = 31;//创建角色事件ID
    private final int EVENT_ENTER_ROLEINFO = 32;//进入游戏事件ID
    private final int EVENT_UPDATE_ROLEINFO = 35;//角色升级事件ID

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
		mActivity = activity;		
		activationGame(activity);
		mInitListener = kNListener.getInitListener();
		LogUtil.log("开始初始化");
		
		init();
       
		if(kNListener.getInitListener() != null){
			kNListener.getInitListener().onSuccess(null);
		}
		
	}
	
	//sdk初始化
	private void init(){
		
		 //添加全局回调事件
        LSCore.getInstance().addCallback(new LSListener() {
            @Override
            public void init(int code, String msg) {
                if (LSListener.CODE_INIT_SUCCESS == code) {
					LogUtil.log("sdk初始化成功:"+msg);
					if(msg!=null){

						//初始化回调
						mInitListener.onSuccess(msg);
					}

                
                } else {
					mInitListener.onFail(msg);
					LogUtil.log("sdk初始化失败:"+msg);
                	
                }
            }
            @Override
            public void login(int code, String msg) {
                // 收到登录成功回调后，CP可进行后续游戏逻辑处理
                if (LSListener.CODE_LOGIN_SUCCSSE == code) {
                   LogUtil.log("登录完成之后的token:"+msg);
					kngamelog(msg);
                   
                } else if(LSListener.CODE_CANCEL_LOGIN == code){
                	LogUtil.log("sdk取消登录");
                	
                	
                    
                } else{
                	LogUtil.log("sdk登录失败");
                    
                	
                	
                }
            }
            @Override
            public void logout() {
                //在这个回调里清空帐号信息，需要回到游戏的登录界面重新调用登录接口
            	LogUtil.log("注销成功");
            	
            	kNListener.getLogoutListener().onSuccess(1);
            	
            	
            }
            @Override
            public void paySuccess(int code,String msg) {
                if(code==0){
                	LogUtil.log("充值成功"+msg);
                }else{
                  
                }
            }
            @Override
            public void payCancel() {
            	LogUtil.log("取消充值");
                
            }
            @Override
            public void exit() {
                System.exit(0);
                //真实退出游戏
                mActivity.finish();
            }
        });

        //初始化 - *注意* 需先添加全局回调事件后，再调用init

			LSCore.getInstance().init(mActivity);


		
		
	}
	
	

	

	//游戏登录	    
   private void kngamelog(String token){
	   
	   
	   
	   JSONObject  sendJson=new JSONObject();
		try {
			sendJson.put("token",token);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LogUtil.log("sendJson="+sendJson.toString());
		HttpService.doLogin(mActivity,sendJson.toString(),new BaseListener() {
			@Override
			public void onSuccess(Object result) {
				 
				LogUtil.log("游戏方登录成功.....");
				
				JSONObject obj = null;
				final User user = new User();
   				String  invite  = "";
   				String  code    = "";
   				try {
       					obj = new JSONObject(result.toString());
       					user.setOpenId( obj.getString("open_id") );
       					user.setSid( obj.getString("sid") );
       					user.setSign( obj.getString("sign") );
       					user.setIsIncompany( Integer.parseInt(obj.getString("iscompany")) );
       					user.setLogin(true);
       					Data.getInstance().setUser(user);
       					
       					invite = obj.getString("invite");
       					JSONObject  inviteObj = new JSONObject(invite.toString());
       					code = inviteObj.getString("code");
       					LogUtil.log("code="+code);		
   				}catch(Exception e){
   					e.printStackTrace();
   				}
   				if(TextUtils.isEmpty(code)){
						
   					LogUtil.log("code=null");
						mLoginListener.onSuccess(user);
						
					}else{
						
						LogUtil.log("code not null");
						
						if(code.equals("100")){
							
							OpenSDK.getInstance().invitation( mActivity , new InvitationListener() {
								
								@Override
								public void onSuccess(Object result) {
									// TODO Auto-generated method stub
									mLoginListener.onSuccess(user);
									
								}
								
								@Override
								public void onFail(Object result) {
									// TODO Auto-generated method stub
									kNListener.getLogoutListener().onSuccess(1);
								}
							} );
							
						}else{
							
							mLoginListener.onSuccess(user);
							
						}
					}
//   				mLoginListener.onSuccess(user);
			}
			@Override
			public void onFail(Object result) {
				
				LogUtil.log("游戏登录失败......");
				mLoginListener.onFail(result.toString());
			}
		});
	
		
	
	
	   
   }
	
	
   
	

	@Override
	public void onEnterGame(Map<String, Object> data) {
		super.onEnterGame(data);
		
		
		//提交用户信息
		String serverid=String.valueOf(knData.getGameUser().getServerId());
		String servername = knData.getGameUser().getServerName();
		String level= String.valueOf(knData.getGameUser().getUserLevel());
		String username=knData.getGameUser().getUsername();
		String uid=knData.getGameUser().getUid();
		String roleId = knData.getGameUser().getRoleId();
		String roleCtime =knData.getGameUser().getRoleCTime();
		String vip=knData.getGameUser().getVipLevel();	
		String gameId=knData.getGameInfo().getGameId();	//游戏id
						
		String sceneid=knData.getGameUser().getScene_id();
		String balance=knData.getGameUser().getBalance();
		
		
		
		LogUtil.log("提交用户信息: 角色创建时间="+Integer.valueOf(roleCtime)+"角色id="+uid+" 游戏id="+gameId+"  balance="+balance+"  sceneid="+sceneid);
			
					
		if(knData.getGameUser().getScene_id().equals("1")){ //进入游戏

			
			 LSCore.getInstance().reportData(initTestGameData(EVENT_ENTER_ROLEINFO));
				        
				  
			}else if(knData.getGameUser().getScene_id().equals("2")){//创建角色
						
				 LSCore.getInstance().reportData(initTestGameData(EVENT_CREATE_ROLEINFO));

				
				
						
			}else if(knData.getGameUser().getScene_id().equals("4")){//提升等级
					
				 LSCore.getInstance().reportData(initTestGameData(EVENT_UPDATE_ROLEINFO));

			}						
	}
	
	
	 /**
     * 事件接口信息
     * @param eventId 事件id
     * @return
     */
    private JSONObject initTestGameData(int eventId){

    	//提交用户信息
    			String serverid=String.valueOf(knData.getGameUser().getServerId());
    			String servername = knData.getGameUser().getServerName();
    			String level= String.valueOf(knData.getGameUser().getUserLevel());
    			String username=knData.getGameUser().getUsername();
    			String roleId = knData.getGameUser().getRoleId();
    			String roleCtime =knData.getGameUser().getRoleCTime();
    			String vip=knData.getGameUser().getVipLevel();	
    			String gameId=knData.getGameInfo().getGameId();	//游戏id
    							
    	
    	
        JSONObject evenData = null;

        try{
            evenData = new JSONObject();
            evenData.put("eid",eventId); //上报时机
            evenData.put("dsid",serverid); //服务器 ID
            evenData.put("dsname",servername); //服务器名称
            evenData.put("drid",roleId); //角色 ID
            evenData.put("drname",username); //角色名称
            evenData.put("drlevel",level); //角色等级
            evenData.put("drbalance","88"); //角色游戏内余额
            evenData.put("drvip",vip); //角色游戏内 VIP 等级
            evenData.put("dcountry","武当派"); //帮派
            evenData.put("dparty","公会"); //公会
            evenData.put("dext",new JSONObject()); //此参数为 Json 形式，可以自己定义参数
        }catch (Exception e){
            e.printStackTrace();
        }

        return evenData;
    }
	

 

	@Override
	public void onGameLevelChanged(int newlevel) {
		super.onGameLevelChanged(newlevel);
	}

	@Override
	public void onResume() {
		super.onResume();
        LSCore.getInstance().onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
        LSCore.getInstance().onPause();


	}

	@Override
	public void onStop() {
		super.onStop();
        LSCore.getInstance().onStop();

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
        LSCore.getInstance().onStart();

	}
	
	@Override
	public void onRestart() {
		super.onRestart();
        LSCore.getInstance().onRestart();


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        LSCore.getInstance().onDestroy();

	}
	
	
	
	
     @Override
    protected void onWindowFocusChanged(boolean hasFocus) {
    	// TODO Auto-generated method stub
    	super.onWindowFocusChanged(hasFocus);
    	
    	
    }
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        LSCore.getInstance().onActivityResult(requestCode, resultCode, data);


	}
	
	@Override
	public void login(Activity activity, Map<String, Object> params) {
		super.login(activity , params);
		
	
		LSCore.getInstance().login(mActivity);
	 
			
		
	}
	
	
	
	
	@Override
	public void pay( final Activity activity , final KnPayInfo knPayInfo) {
		super.pay(activity,knPayInfo);
		
		LoadingDialog.show(activity, "正在申请订单", false);
		LogUtil.log("订单申请");
        HttpService.applyOrder(activity, knPayInfo, new BaseListener() {
			
			@Override
			public void onSuccess(Object result) {
				LogUtil.log("申请订单成功了.....");
				LoadingDialog.dismiss();
				JSONObject obj = null;
				String     order="";
				try {		
						obj = new JSONObject(result.toString());
						order=obj.getString("order_no");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				double price=knPayInfo.getPrice();
				int    priceInt=Integer.parseInt(new java.text.DecimalFormat("0").format(price))/100;	
				LogUtil.log(obj.toString());
				LogUtil.log("order:"+order);
			    
				String serverid = Integer.toString(knData.getGameUser().getServerId());
				String servername = knData.getGameUser().getServerName();
				String username = knData.getGameUser().getUsername();
				String uid = knData.getGameUser().getUid();
				String vip = knData.getGameUser().getVipLevel();
				int userLevel = knData.getGameUser().getUserLevel();
				String gameId=knData.getGameInfo().getGameId();	//游戏id
				String roleId = knData.getGameUser().getRoleId();

				
			    String serverName=Data.getInstance().getGameUser().getServerName();//区服名字
			    int sid=Data.getInstance().getGameUser().getServerId();//区服ID
			    String productId = knPayInfo.getProductId(); // 订单id
				String productName = knPayInfo.getProductName();// 订单名称
			    
			    
				LogUtil.log("serverName: "+serverName+"sid: "+sid+"priceInt: "+priceInt+"order: "+order);
				
				  LSOrder lsorder = new LSOrder();
	                //*必填* cp的订单ID，供对账用，需要替换为真实值
				  lsorder.setDoid(order);
	                //*必填* 商品名称，如钻石、元宝
				  lsorder.setDunit("元宝");
	                //*必填* 商品比率 例如：1：10  1块钱可以购买  1*10的元宝
				  lsorder.setDradio(10);
	                //*必填* 商品实际支付的总价格，单位为 "分"
				  lsorder.setDmoney(priceInt*100);
	                //*必填* 服务器ID,需要替换为真实值
				  lsorder.setDsid(serverid);
	                //*必填* 服务器名称,需要替换为真实值
				  lsorder.setDsname(servername);
	                //*必填* 角色ID，需要替换为真实值
				  lsorder.setDrid(roleId);
	                //*必填* 角色名，需要替换为真实值
				  lsorder.setDrname(username);
	                //*必填* 角色等级，需要替换为真实值
				  lsorder.setDrLevel(userLevel);
	                //cp 透传字段，充值完成后完整回调给CP设置的发货地址，详见文档
				  lsorder.setDext("充值");
	                LSCore.getInstance().pay(mActivity,lsorder);
				
				
				
						
			}
			
			@Override
			public void onFail(Object result) {
			//	KnLoadingDialog.dismiss();
				LogUtil.log("申请订单失败了.....");
				mPayListener.onFail(new Result(ResultCode.APPLY_ORDER_FAIL, "申请订单失败"));
			}
		});
	}
	
	
	
	
	
	@Override
	protected void switchAccount() {
		// TODO Auto-generated method stub
		super.switchAccount();
		
		 //切换账号会退出登陆，请在登出监听中接收切换退出结果
		 
		 LSCore.getInstance().logout(mActivity);
		
		
        
	}
	
	
	@Override
	protected boolean hasThirdPartyExit() {
		// TODO Auto-generated method stub
		return true;
		
	}
	
	@Override
	protected void onThirdPartyExit() {
		// TODO Auto-generated method stub
		super.onThirdPartyExit();
		
		 LSCore.getInstance().exit(mActivity);
		
	}
	
	
	
	public void pushActivation(  final Activity activity , final Map<String, Object>  data  ){
		
		HttpService.doPushActivation(activity, data,  new BaseListener() {
			
			@Override
			public void onSuccess(Object result) {
			
				//Log.e("success:"+result.toString());
				
				activity.finish();
				
			}
			
			@Override
			public void onFail(Object result) {
				
				//Log.e("faild:"+result.toString());
				
				activity.finish();
				
			}
		} );
	 	
		
	}
	
	
	
	public void activation( final Activity activity ) {
		
		Intent  intent = new Intent();
		intent.setClass(activity,ActivationCodeAc.class);
		activity.startActivity(intent);
	
	}
	
	public void pushData( final Activity activity , Map<String,Object> data ){
		
		
		HttpService.doPushData(activity, data, new BaseListener() {
			
			@Override
			public void onSuccess(Object result) {
				
				//Log.e("result:"+result.toString());
				
			}
			
			@Override
			public void onFail(Object result) {
				
				//Log.e("result:"+result.toString());
				
			}
		} );
		
	}
	
	public void activationGame( Activity activity  ){
		
		//		游戏激活
		Map<String, Object>  data = new HashMap<String, Object>();
		//Log.e("初始化"+mGameInfo.getGameId()) ;
		data.put("game_id",mGameInfo.getGameId());
		data.put("ip",DeviceUtil.getMacAddress());
		data.put("app_key",mGameInfo.getAppKey());
		data.put("imei",DeviceUtil.getDeviceId());
		data.put("platform",mGameInfo.getPlatform());
		//Log.e("初始化"+mGameInfo.getAdChannel()) ;
		data.put("ad_channel",mGameInfo.getAdChannel());
		data.put("phone_type",DeviceUtil.getPhoneType());
		//Log.e("初始化"+mGameInfo.getChannel()) ;
		data.put("channel",mGameInfo.getChannel());
		
		pushData(activity,data);
		
	}
	
	
	
}
