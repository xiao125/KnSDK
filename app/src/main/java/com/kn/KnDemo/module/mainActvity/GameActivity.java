package com.kn.KnDemo.module.mainActvity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.kn.KnDemo.R;
import com.kn.KnDemo.URL.URLConfig;
import com.kn.game.Loader.LatteLoader;
import com.kn.game.configurator.Game;
import com.kn.game.net.RestClient;
import com.kn.game.net.callback.IError;
import com.kn.game.net.callback.IFailure;
import com.kn.game.net.callback.ISuccess;
import com.proxy.Constants;
import com.proxy.Data;
import com.proxy.OpenSDK;
import com.proxy.bean.GameInfo;
import com.proxy.bean.KnPayInfo;
import com.proxy.bean.User;
import com.proxy.listener.InitListener;
import com.proxy.listener.LoginListener;
import com.proxy.listener.LogoutListener;
import com.proxy.listener.PayListener;
import com.proxy.listener.RoleReportListener;
import com.proxy.util.LogUtil;
import com.proxy.util.Md5Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class GameActivity extends Activity{

    private WebView mweview;
    private LinearLayout webViewLayout;
    OpenSDK m_proxy = OpenSDK.getInstance();
    private String m_appKey = "uVkyGhiKWm7T2B43n5rEafHleXwPzjRU";
    private String m_gameId = "rxcqh5";
    private String m_gameName = "rxcqh5";
    private int m_screenOrientation = 1;

    private GameInfo m_gameInfo = new GameInfo(m_gameName, m_appKey, m_gameId, m_screenOrientation);
    public static String HtmlUrl;
    private String roleDate;
    private String roledata;
    public static final int REQUEST_READ_PHONE_STATE=101;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webViewLayout = (LinearLayout) findViewById(R.id.wb);
        mweview = new WebView(this);
        webViewLayout.addView(mweview);

        //6.0 动态权限（getDeviceId()获取手机串码）
        permissions();

        sdkProxyinit();
        webviewinit();

    }

    private void permissions(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) { //检查是否有权限

                LogUtil.d( "permission denied to SEND_SMS - requesting it");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);

            }else {

                LogUtil.d( "不需要申请权限");
            }

        }

    }



    //初始化中间件读取assets资源文件
    private void sdkProxyinit(){

        //初始化读取adChannel文件中的数据
        //Game.getApplicationContext() 全局上下文 ，在Application配置
        Data.getInstance().setApplicationContex(Game.getApplicationContext());
        Data.getInstance().setGameInfo(m_gameInfo);

    }

    //webview
    private void webviewinit() {

        getHtmlUrl();

    }


    //SDK服务器获取H5游戏地址
    private void getHtmlUrl() {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        String game_id = m_gameInfo.getGameId();
        String appkey = m_gameInfo.getAppKey();
        String channel =m_gameInfo.getChannel();
        String platform  = m_gameInfo.getPlatform();

        //网络请求
        RestClient.builder()
                .url(URLConfig.URL.GETHTMLURL)
                .loader(GameActivity.this)
                .params("game_id",game_id)
                .params("app_key", appkey)
                .params("platform",platform)
                .params("channel", channel)
                .params("time", date)
                . params("sign", Md5Util.getMd5(game_id+channel+platform+date+appkey))
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            HtmlUrl = jsonObject.getString("loginUrl");

                            LogUtil.log("获取h5地址=" + HtmlUrl);

                            //主线程显示WebView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showHtml(HtmlUrl);
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                })

                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        LogUtil.log("服务返回失败");

                    }
                })

                .failure(new IFailure() { //
                    @Override
                    public void onFailure() {

                        LogUtil.log("网络出问题");
                    }
                })
                .build()
                .post();






    }


    //显示HTML页面
    private void showHtml(String htmlUrl) {


        WebSettings webSettings = mweview.getSettings();
        webSettings.setJavaScriptEnabled(true); //在WebView中启用JavaScript
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 设置允许JS弹窗
        //设置自适应屏幕，两者合用（下面这两个方法合用）
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用缓存
        // mweview.setWebViewClient(new WebViewClient()); //不启动浏览器加载html
        // mweview.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //Http与Https混合内容
            //两者都可以
            webSettings.setMixedContentMode(webSettings.getMixedContentMode());

        }

        //webview调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mweview.setWebViewClient(new WebViewClient() {

            //不启动浏览器加载html
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mweview.loadUrl(url);
                return true;
            }

            //onPageStarted会在WebView开始加载网页时调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.log("WebView开始加载网页时调用");
                LatteLoader.showLoading(GameActivity.this);
               // LoadingDialog.show(GameActivity.this, "拼命加载游戏中....", false);
                mweview.addJavascriptInterface(new InitGame(), "MCBridge");
            }

            //加载结束时调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                LogUtil.log("WebView加载结束时调用");
               // LoadingDialog.dismiss();
                LatteLoader.stopLoading();

            }


            //该方法传回了错误码，根据错误类型可以进行不同的错误分类处理
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

                LogUtil.log("错误码：" + errorCode);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }
        });


        mweview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) { //页面加载进度

                LogUtil.log("加载进度=" + newProgress);

                super.onProgressChanged(view, newProgress);
            }
        });

        mweview.loadUrl(htmlUrl); //加载h5页面


    }



    public class InitGame {


        @JavascriptInterface
        public void activate() {

            LogUtil.log("点击初始化按钮");

            m_proxy.isSupportNew(true);
            m_proxy.doDebug(true);
            //中间件

            m_proxy.init(GameActivity.this, m_gameInfo, new InitListener() {

                @Override
                public void onSuccess(Object arg0) {
                    // TODO Auto-generated method stub
                    LogUtil.log("游戏初始化成功=");

                    activateCallback();

                }

                @Override
                public void onFail(Object arg0) {
                    // TODO Auto-generated method stub
                    LogUtil.log("游戏初始化失败");
                }
            });

            //登录回调
            m_proxy.setLogoinListener(new LoginListener() {

                @Override
                public void onSuccess(User user) {

                    loginCallback(user);


                }

                @Override
                public void onFail(String result) {
                    LogUtil.log("登录失败:" + result);
                }
            });

            //上报数据回调
            m_proxy.setRoleReportListener(new RoleReportListener() {
                @Override
                public void onSuccess(Object result) {

                    roleDate = result.toString();
                    int i = 1;
                    LogUtil.log((++i) + "上报数据成功返回参数=" + roleDate.toString());

                    //  roleData(roleDate);


                }

                @Override
                public void onFail(Object result) {

                    LogUtil.log("上报数据失败返回参数=" + result.toString());
                }
            });

            //支付回调
            m_proxy.setPayListener(new PayListener() {

                @Override
                public void onSuccess(Object result) {
                    LogUtil.log("setPayListener 支付回调成功=" + result.toString());


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("reason", "支付成功");
                        jsonObject.put("code", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //调用js支付回调
                    mweview.loadUrl("javascript:payCallback('" + jsonObject.toString() + "')");

                }

                @Override
                public void onFail(Object result) {
                    LogUtil.log("setPayListener 支付回调失败:" + result);
                }
            });


            //登出回调
            m_proxy.setLogoutListener(new LogoutListener() {

                @Override
                public void onSuccess(Object result) {
                    LogUtil.log("游戏登出成功:" + result);
                    //游戏账号注销，返回到登录界面


                }
                @Override
                public void onFail(Object result) {
                    LogUtil.log("游戏登出失败:" + result);
                }
            });


        }


        @JavascriptInterface
        public void login() {

            LogUtil.log("点击登录");

            m_proxy.login(GameActivity.this);




        }


        /**
         * 支付
         * @param payContent
         */
        @JavascriptInterface
        public void pay(String payContent) {
            try {
                JSONObject jsonObject = new JSONObject(payContent.toString());

                String OrderNo = jsonObject.getString("extra_info"); //游戏订单
                String Price = jsonObject.getString("price"); //商品价格

                LogUtil.log("支付参数=" + jsonObject + " 商品价格=" + Price + " 游戏订单=" + OrderNo);

                String CoinName = jsonObject.getString("coinName");
                String CoinRate = jsonObject.getString("coinRate");
                String ProductId = jsonObject.getString("productId");

                final KnPayInfo payInfo = new KnPayInfo();
                payInfo.setProductName("钻石");                //商品名称
                payInfo.setCoinName(CoinName);                        //货币名称	如:元宝
                payInfo.setCoinRate(Integer.valueOf(CoinRate));        //游戏货币的比率	如:1元=10元宝 就传10
                payInfo.setPrice(Double.valueOf(Price) * 100);                //商品价格  分
                payInfo.setProductId(ProductId);                    //商品Id,没有可以填null
                payInfo.setOrderNo(OrderNo);  //订单号
                m_proxy.pay(GameActivity.this, payInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        /**
         * js传递过来的参数={"senceType":"1","userId":"1","serverId":"3","userLv":"4",
         * "serverName":"战","roleName":"xiao","vipLevel":"0","roleCTime":"roleCTime"}
         * @param roleReportContent
         */

        @JavascriptInterface
        public void roleReport(String roleReportContent) {

            LogUtil.log("js传递过来的参数=" + roleReportContent);
            roledata = roleReportContent;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(roledata.toString());
                        LogUtil.log("js传递过来的参数=" + jsonObject.toString());

                        String userId = jsonObject.getString("userId");//用户id
                        String serverId = jsonObject.getString("serverId"); //服务器Id
                        String gameLv = jsonObject.getString("lv"); //游戏等级

                        String serverName = jsonObject.getString("serverName"); //玩家所在服区名称
                        String roleName = jsonObject.getString("roleName"); //游戏角色名称
                        String roleCTime = jsonObject.getString("roleCTime"); //游戏角色创建时间（时间戳）
                        String vipLevel = jsonObject.getString("vipLevel"); //玩家VIP等级
                        String user_sex = jsonObject.getString("user_sex"); //玩家性别
                        String user_age = jsonObject.getString("user_age"); //玩家年龄
                        String factionName = jsonObject.getString("factionName"); //用户所在帮派名称
                        String senceType = jsonObject.getString("senceType"); ///场景ID(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
                        String diamondLeft = jsonObject.getString("diamondLeft"); //玩家货币余额
                        String extraInfo = jsonObject.getString("extraInfo"); //玩家信息拓展字段


                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put(Constants.USER_ID, userId);            //游戏玩家ID
                        data.put(Constants.SERVER_ID, serverId);        //游戏玩家所在的服务器ID
                        data.put(Constants.USER_LEVEL, gameLv);        //游戏玩家等级
                        data.put(Constants.ROLE_ID, userId);            //角色ID


                        //  int senceType =1; //场景ID
                        // String  extraInfo = "";      			//玩家信息拓展字段
                        // String vipLevell ="0";          				//玩家VIP等级
                        // String factionName="";     					//用户所在帮派名称
                        //场景ID;//(值为1则是进入游戏场景，值为2则是创建角色场景，值为4则是提升等级场景)
                        //  String  diamondLeft = diamondLeft;        			//玩家货币余额
                        data.put(Constants.EXPEND_INFO, extraInfo);    //扩展字段
                        data.put(Constants.SERVER_NAME, serverName);    //所在服务器名称
                        data.put(Constants.ROLE_NAME, roleName);//角色名称
                        data.put(Constants.VIP_LEVEL, vipLevel);        //VIP等级
                        data.put(Constants.FACTION_NAME, factionName);//帮派名称
                        data.put(Constants.SCENE_ID, senceType);        //场景ID
                        data.put(Constants.ROLE_CREATE_TIME, roleCTime);//角色创建时间
                        data.put(Constants.BALANCE, diamondLeft);        //剩余货币
                        data.put(Constants.IS_NEW_ROLE, Integer.valueOf(senceType) == 2 ? true : false);    //是否是新角色
                        data.put(Constants.USER_ACCOUT_TYPE, "1");        //玩家账号类型账号类型，0:未知用于来源1:游戏自身注册用户2:新浪微博用户3:QQ用户4:腾讯微博用户5:91用户(String)
                        data.put(Constants.USER_SEX, user_sex);                //玩家性别，0:未知性别1:男性2:女性；(String)
                        data.put(Constants.USER_AGE, user_age);            //玩家年龄；(String)

                        m_proxy.onEnterGame(data);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });


        }


        //退出

        @JavascriptInterface
        public void Logout() {

            m_proxy.onThirdPartyExit();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("reason", "退出");
                jsonObject.put("code", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //调用js回调
            mweview.loadUrl("javascript:logoutCallback('" + jsonObject.toString() + "')");

        }


    }


    //调用js初始化成功回调方法
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void activateCallback() {

        final int version = Build.VERSION.SDK_INT;
        if (version < 18) {

            //调用js初始化回调
            mweview.loadUrl("javascript:activateCallback('" + getJson().toString() + "')");

        } else { // 该方法在 Android 4.4 版本才可使用，


            //调用js初始化回调
            mweview.evaluateJavascript("javascript:activateCallback('" + getJson().toString() + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    //js返回的结果
                }
            });

        }

    }


    //调用js中登录回调方法
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void loginCallback(User user) {

        final int version = Build.VERSION.SDK_INT;
        if (version < 18) {

            //调用js初始化回调
            mweview.loadUrl("javascript:loginCallback('" + getJson(user) + "')");

        } else { // 该方法在 Android 4.4 版本才可使用，

            //调用js初始化回调
            mweview.evaluateJavascript("javascript:loginCallback('" + getJson(user) + "')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    //js返回的结果
                }
            });

        }
    }

    //sdk登录成功返回的对象数据
    private JSONObject getJson(User user) {


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("open_id", user.getOpenId());
            jsonObject.put("sid", user.getSid());
            LogUtil.log("登录成功,user=" + jsonObject.toString() + " open_id:" + user.getOpenId() + ",sid:" + user.getSid());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    //初始化成功返回的对象数据
    private JSONObject getJson() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reason", "初始化成功");
            jsonObject.put("code", 0);

            LogUtil.log("sdk初始化成功返回数据=" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    //调用js中的上报回调方法
    private void roleData(String data) {

        final int version = Build.VERSION.SDK_INT;
        if (version < 18) {

            mweview.loadUrl("javascript:roleReportCallback('" + data + "')");


        } else { // 该方法在 Android 4.4 版本才可使用，
            //开线程进行js方法调用
            mweview.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {

                    mweview.evaluateJavascript("javascript:roleReportCallback('" + roleDate + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            //js返回的结果
                            LogUtil.log("js返回的结果:" + s);
                        }
                    });
                }
            });

        }


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setTitle("游戏");
        builder.setMessage("真的忍心退出游戏么？");
        builder.setPositiveButton("忍痛退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        builder.setNegativeButton("手误点错", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

        return true;

    }

    @Override
    protected void onPause() {
        mweview.onPause();
        mweview.pauseTimers();//调用pauseTimers()全局停止Js
        super.onPause();

    }

    @Override
    protected void onResume() {
        mweview.onResume();
        mweview.resumeTimers(); //调用onResume()恢复js
        super.onResume();
        m_proxy.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_proxy.onStop();

    }

    @Override
    protected void onDestroy() {
        if (mweview != null) {
            mweview.clearHistory();
            ((ViewGroup) mweview.getParent()).removeView(mweview);
            mweview.destroy();
            mweview = null;
        }
        super.onDestroy();
        m_proxy.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    LogUtil.log("开启权限");
                }
                break;
            default:
                break;
        }
    }


}
