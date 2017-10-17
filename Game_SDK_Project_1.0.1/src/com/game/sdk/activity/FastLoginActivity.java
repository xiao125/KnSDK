package com.game.sdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 快速注册
 */
public class FastLoginActivity extends Activity {


    private Activity m_activity = null ;
    private ImageView re_back,re_close,select_close,m_phone_ks_close;
    private Button user_register,phone_register,phone_ks_code,kn_user_zc;
    private LinearLayout user_layout,phone_layout;
    private CheckBox cb;
    private TextView mc;
    private EditText ks_user,kn_password,phone_ks_register,phone_ks_register_code,phone_ks_register_password;
    private boolean isVISIBLE=false;
    private String newSdk="1";
    public  static   String    m_userName ;
    public  static   String    m_passWord ;

    public static String m_userPhone;
    public static String m_phone_pw;

    public String m_phone;
    public String m_pw;

    public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 倒计时秒数
     */
    private int mCount;


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

        setContentView(R.layout.fast_login);

        initView();

        generateMixString(5);

        initLinster();



    }

    private void initLinster() {



        KSUser();



        re_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KnLog.d("快速注册里面的返回按钮");
              /*  Intent intent=new Intent(FastLoginActivity.this, SelecteLoginActivity.class);
                startActivity(intent);*/

              if (m_activity!=null){
                  m_activity.finish();
                  m_activity=null;
              }

            }
        });

        re_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_activity!=null){
                    m_activity.finish();
                    m_activity=null;
                }
            }
        });

        phone_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_register.setBackgroundColor(getResources().getColor(R.color.kn_text));
                phone_register.setBackgroundColor(getResources().getColor(R.color.Kn_Username));
                user_layout.setVisibility(View.INVISIBLE); //隐藏
                phone_layout.setVisibility(View.VISIBLE);//显示

                isVISIBLE=true;

                KnLog.log("手机注册。。。。。，isVISIBLE="+isVISIBLE);
            }



        });


        user_register.setOnClickListener(new View.OnClickListener() { //用户名注册
            @Override
            public void onClick(View view) {

                //随机参数一组数字



                user_register.setBackgroundColor(getResources().getColor(R.color.Kn_Username));
                phone_register.setBackgroundColor(getResources().getColor(R.color.kn_text));
                user_layout.setVisibility(View.VISIBLE);//显示
                phone_layout.setVisibility(View.INVISIBLE); //隐藏
                isVISIBLE=false;
                KnLog.log("用户名注册。。。。。，isVISIBLE="+isVISIBLE);

            }
        });






        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    //选择了
                    kn_user_zc.setEnabled(true);
                    kn_user_zc.setBackgroundColor(getResources().getColor(R.color.Kn_Username));
                }else {


                    kn_user_zc.setEnabled(false);
                    kn_user_zc.setBackgroundColor(getResources().getColor(R.color.kn_text));

                }
            }
        });


        mc.setOnClickListener(new View.OnClickListener() { //弹出dialog
            @Override
            public void onClick(View view) {


            }
        });


        kn_user_zc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (user_layout.getVisibility()==View.VISIBLE){
                    KnLog.log("开始用户名注册");
                    //用户名注册
                    UserRegister();

                }else if ( phone_layout.getVisibility()==View.VISIBLE){

                    KnLog.log("开始手机注册");
                    //手机注册
                    MobileRegister();

                }



            }
        });







        //验证码倒计时
        phone_ks_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cell_Num = phone_ks_register.getText().toString().trim();

                if (isPhone(cell_Num)){
                    return;
                }

                countdownTimer();
                sendcod();

            }


        });


        select_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ks_user.setText("");
            }
        });

        m_phone_ks_close.setOnClickListener(new View.OnClickListener() { //清除验证码
            @Override
            public void onClick(View view) {
                phone_ks_register_code.setText("");
            }
        });



    }


    //随机生成一组字符串
    public void  generateMixString(int length)

    {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < length; i++)

        {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));

        }
        String username ="mc"+sb.toString();
        ks_user.setText(username); //默认填写用户名


    }


    //判断手机号是否正确
    private boolean isPhone(String phone) {
        if(TextUtils.isEmpty(phone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_58));

            return true;
        }
        if(!Util.isMobileNO(phone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_57));
            return true;
        }
        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());

            return true;
        }
        return false;
    }

    //输入用户名与密码监听
    private void KSUser(){

        ks_user.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v ) {
                // TODO Auto-generated method stub
                ks_user.setCursorVisible(true);
            }
        } );

        ks_user.setOnEditorActionListener( new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v , int actionId, KeyEvent event ) {
                // TODO Auto-generated method stub
                if(EditorInfo.IME_ACTION_DONE==actionId){ // 按下完成按钮
                    ks_user .clearFocus(); //清除光标，也就是失去焦点
                    kn_password.requestFocus();
                    ks_user.setCursorVisible(false); //让EditText不出现光标
                }
                return false;
            }
        } );

        kn_password.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                kn_password.setCursorVisible(true);
            }
        } );



        kn_password.setOnEditorActionListener( new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v , int actionId , KeyEvent event ) {
                // TODO Auto-generated method stub
                if(EditorInfo.IME_ACTION_DONE==actionId){
                    kn_password.clearFocus();
                    ks_user.clearFocus();
                    kn_password.requestFocus();
                    kn_password.setCursorVisible(false);
                    Util.hideEditTextWindow(m_activity, kn_password);
                    Util.hideEditTextWindow(m_activity, ks_user);
                }
                return false;
            }
        } );


    }




    private void initView() {
        re_back = (ImageView) findViewById(R.id.image_back);//返回
        re_close = (ImageView) findViewById(R.id.select_login_close); //关闭
        user_register = (Button) findViewById(R.id.kn_selecte_user_register); //用户名注册
        ks_user= (EditText) findViewById(R.id.ks_user); //用户名
        kn_password = (EditText) findViewById(R.id.kn_password); //用户名密码
        select_close = (ImageView) findViewById(R.id.select_close); //清除账号


        phone_register = (Button) findViewById(R.id.kn_selecte_phone_register);//手机注册
        phone_ks_register= (EditText) findViewById(R.id.phone_ks_register); //手机号
        phone_ks_register_code= (EditText) findViewById(R.id.phone_ks_register_code); //获取到的验证码
        phone_ks_register_password= (EditText) findViewById(R.id.phone_ks_register_password);//输入的密码
        phone_ks_code = (Button) findViewById(R.id.phone_ks_code); //验证码
        m_phone_ks_close = (ImageView) findViewById(R.id.phone_ks_close); //清除验证码


        user_layout = (LinearLayout) findViewById(R.id.user_register_layout); //用户名注册view
        phone_layout = (LinearLayout) findViewById(R.id.phone_register_layout); //手机号注册view

        cb= (CheckBox) findViewById(R.id.zc_cb);
        mc = (TextView) findViewById(R.id.game_sy); //协议
        kn_user_zc = (Button) findViewById(R.id.kn_user_zc); //注册按钮




    }

    /**
     * 发送验证码倒计时
     */
    private void countdownTimer(){

        phone_ks_code.setEnabled(false);
        mCount = 60;
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCount--;
                        phone_ks_code.setText(String.valueOf(mCount)+"秒");
                        phone_ks_code.setBackgroundColor(getResources().getColor(R.color.kn_text));

                        if (mCount<=0){
                            phone_ks_code.setText("重新发送");
                            phone_ks_code.setEnabled(true);
                            timer.cancel();
                        }
                    }
                });
            }
        };

        timer.schedule(task,1000,1000);
    }


    /**
     * 发送验证码
     */

    private void sendcod(){

        String cell_Num = phone_ks_register.getText().toString().trim();
        if (isPhone(cell_Num)){
            return;
        }

        LoadingDialog.show(m_activity, "获取验证码中...", true);
        HttpService.getSecCode(m_activity, handler,cell_Num,newSdk);

    }


    //判断手机号是否正确
    private  void isphone( String cell_Num){


        if(TextUtils.isEmpty(cell_Num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_58));

            return ;
        }
        if(!Util.isMobileNO(cell_Num)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_57));
            return ;
        }
        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());

            return ;
        }


    }



    //用户名与密码，注册
    private void UserRegister(){

        Util.hideEditTextWindow(this,kn_password);
        checkRegisterParams(m_activity,ks_user,kn_password);

    }

    //手机号注册
    private void MobileRegister(){

        Util.hideEditTextWindow(this,phone_ks_register_password);

        checkRegisterParams(m_activity,phone_ks_register,phone_ks_register_password,phone_ks_register_code);



    }


    //判断手机号，验证码，密码
    private void checkRegisterParams(Context context, EditText phone, EditText passWordEt,EditText code) {
        String userphone = phone.getText().toString().trim(); //手机号
        String security_code = code.getText().toString().trim();//验证码
        String password = passWordEt.getText().toString().trim();//密码

        if(TextUtils.isEmpty(security_code)){
            Util.ShowTips(m_activity,"验证码不能为空");
            return ;
        }

        if(TextUtils.isEmpty(userphone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_58));

            return ;
        }
        if(!Util.isMobileNO(userphone)){
            Util.ShowTips(m_activity,getResources().getString(R.string.tips_57));
            return ;
        }

        if (TextUtils.isEmpty(password)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_8) );
            return;
        }
       /* String confirm_password=confirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirm_password)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_64) );
            return;
        }*/

      /*  if (!userphone.matches("^[a-z|A-Z]{1}.{0,}$")) {
            Util.ShowTips(context, getResources().getString(R.string.tips_1));
            return;
        }*/

       /* if (!userphone.matches("^[a-z|A-Z|0-9]{1,}$")) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
            return;
        }*/

       if (!userphone.matches("^.{6,12}$")) {
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
       // String pw = Md5Util.getMd5(password);
        m_phone = userphone ;
        m_pw = password ;

        KnLog.log("手机注册的用户名："+userphone+"  密码："+password);

        LoadingDialog.show(m_activity, "注册中...",true);

        //手机注册
        HttpService.doMobileRegister(getApplicationContext(), handler, userphone,security_code, password);

    }


    private void checkRegisterParams(Context context, EditText userNameEt, EditText passWordEt) {
        String username = userNameEt.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_2) );
            return;
        }
        String password = passWordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_8) );
            return;
        }
       /* String confirm_password=confirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirm_password)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_64) );
            return;
        }*/

        if (!username.matches("^[a-z|A-Z]{1}.{0,}$")) {
            Util.ShowTips(context, getResources().getString(R.string.tips_1));
            return;
        }


        if (!username.matches("^[A-Za-z0-9_-]+$")) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
            return;
        }

       /* if (!username.matches("^[a-z|A-Z|0-9]{1,}$")) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_3) );
            return;
        }*/

        if (!username.matches("^.{6,16}$")) {
            Util.ShowTips(context, getResources().getString(R.string.tips_4) );
            return;
        }

        if (!password.matches("^.{6,16}$")) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_5) );
            return;
        }

       /* if (!confirm_password.equals(password)) {
            Util.ShowTips(context,  getResources().getString(R.string.tips_65) );
            return;
        }*/

        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
            return ;
        }
     //  String  pw = Md5Util.getMd5(password);
        m_userName = username ;
        m_passWord = password ;

        KnLog.log("用户名注册的密码="+password);

        LoadingDialog.show(m_activity, "注册中...",true);

        HttpService.doRegister(getApplicationContext(), handler, username, password);

    }



    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String msg_content = msg.obj.toString();
            int resultCode = msg.what;
            Intent intent = null;

            LoadingDialog.dismiss();
            Log.e("sadaaaawdawf", msg_content+"////"+resultCode);
            switch (resultCode) {
                case ResultCode.PASSWORD_NEW_SUCCESS:
                    KnLog.log("修改密码成功++");
                    try {
                        KnLog.log("msg_content:"+msg_content);
                        JSONObject json = new JSONObject( msg_content );
                        KnLog.log("json:"+json.toString());
                        String reason = json.getString("reason");
                        String user_name = json.getString("user_name");
                        KnLog.log("reason:"+reason);
                        KnLog.log("user_name:"+user_name);
                        Util.ShowTips(m_activity,reason);
                        intent = new Intent(m_activity.getApplicationContext(), AutoLoginActivity.class); //跳转到免输入密码登录界面
                        intent.putExtra("userName", user_name);
                        if(null==intent){

                        }else{
                            if(null==m_activity){

                            }else{

                                m_activity.startActivity(intent);
                                m_activity.finish();
                                m_activity = null ;

                            }
                        }
                        break;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                case ResultCode.PASSWORD_NEW_FAIL:
                    Util.ShowTips(m_activity,msg_content);
                    break;
                case ResultCode.SECURITY_SUCCESS: //验证码获取成功
                   /* m_get_security_codeBtn.setClickable(false);
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
                    },1000,1000);*/


                    break;
                case ResultCode.SECURITY_FAIL:
                    Util.ShowTips(m_activity,msg_content);
                    break;

                case ResultCode.SUCCESS: //注册成功
                    setResult(Activity.RESULT_OK);
                    DBHelper.getInstance().insertOrUpdateUser( m_userName , m_passWord );
                    Util.ShowTips(FastLoginActivity.this, getResources().getString(R.string.tips_15) );
                    //RegisterActivity.this.finish();

                 /*   m_activity.finish();
                    m_activity=null;*/

                   GameSDK.instance.login(FastLoginActivity.this); //跳转到免密码登录
                    //	执行自动登录
                    break;
                case ResultCode.FAIL: ////注册失败
                    if(msg.obj!=null)
                        Util.ShowTips(FastLoginActivity.this,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
                    break;

                case ResultCode.MOBILE_REG_SUCCRESS: //手机注册成功

                    setResult(Activity.RESULT_OK);

                    KnLog.log("手机注册成功1");

                    //添加手机账号
                    DBHelper.getInstance().insertOrUpdateUser( m_phone ,m_pw );
                    Util.ShowTips(FastLoginActivity.this, getResources().getString(R.string.tips_15) );
                    //RegisterActivity.this.finish();

                 /*   m_activity.finish();
                    m_activity=null;*/

                    GameSDK.instance.login(FastLoginActivity.this); //跳转到免密码登录


                    break;

                case ResultCode.MOBILE_REG_FAIL: //手机注册失败

                    if(msg.obj!=null)
                        Util.ShowTips(FastLoginActivity.this,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
                    break;



                default:
                    Util.ShowTips(m_activity,msg_content);
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
