package com.game.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kngame_sdk_project.R;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.SDK;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;

import java.util.Timer;
import java.util.TimerTask;

import static com.alipay.sdk.app.statistic.c.G;
import static com.alipay.sdk.app.statistic.c.a;
import static com.alipay.sdk.app.statistic.c.b;
import static com.game.sdk.ResultCode.GET_USER_FAIL;

/**
 * 忘记密码选择找回密码方式
 * Created
 */

public class ForgotPasswordActivity extends Activity {

    private Activity activity;
    private EditText m_phone;
    private ImageView m_close;
    private Button m_zh_qd,m_phone_ks_code;
    private CheckBox m_cb_phone,m_cb_email;
    private TextView mphone,memail;
    private FrameLayout m_frameLayout;
    private Boolean iscb=true;
    private Timer m_timer = null ;
    private int      m_time  = 60 ;
    private Message m_msg = null ;
    private String newSdk="1";
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this ;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(GameSDK.getInstance().ismScreenSensor()){

        }else{
            setRequestedOrientation(GameSDK.getInstance().getmOrientation());
        }

        setContentView(R.layout.forgot_password_layout);

        initView();

        initLinerter();





/*
        Intent intent = getIntent();
        if(intent.hasExtra("userName")){
            m_userNames   = intent.getStringExtra("userName");
            userNameEt.setText(m_userNames);
        }*/

    }



    private void initLinerter() {

        m_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activity!=null){
                    activity.finish();
                    activity=null;
                }
            }
        });

        //手机号找回
        m_cb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    iscb=true;
                    mphone.setTextColor(getResources().getColor(R.color.Kn_Username));
                    memail.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                    m_cb_email.setChecked(false);
                    m_cb_phone.setChecked(true);



                }
            }
        });

        //邮箱找回
        m_cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){

                    iscb=false;
                    memail.setTextColor(getResources().getColor(R.color.Kn_Username));
                    mphone.setTextColor(getResources().getColor(R.color.kn_selecte_log));
                    m_cb_phone.setChecked(false);
                    m_cb_email.setChecked(true);
                }
            }
        });



        //确定
        m_zh_qd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //开始验证是否绑定手机号
                String phone= m_phone.getText().toString().trim();
                //查询账号是否绑定手机号
                HttpService.queryBindAccont(activity.getApplicationContext(), handler, phone);

            }
        });

        //查询是否注册了该账号
        m_phone_ks_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                checkAccountBindParams(activity,m_phone);


                }


        });



    }


    private void checkAccountBindParams(Activity context, EditText mUsername) {

        String username =  m_phone.getText().toString().trim(); //账号

        if (ismobile(activity, username)) return;

             /*   if (isphone()) return;*/


        if (!username.matches("^.{6,12}$")) {
            Util.ShowTips(activity, getResources().getString(R.string.tips_4) );
            return;
        }

        if (!username.matches("^[a-z|A-Z|0-9]{1,}$")) {
            Util.ShowTips(activity, getResources().getString(R.string.tips_3));
            return;
        }

               /* if (!userphone.matches("^[a-z|A-Z]{1}.{0,}$")) {
                    Util.ShowTips(context, getResources().getString(R.string.tips_1));
                    return;
                }
*/

        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
            return ;
        }

        LoadingDialog.show(activity, "正在验证手机账号中...", true);

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




    private boolean isphone() {
        String cell_num =  m_phone.getText().toString().trim(); //手机号
        if(TextUtils.isEmpty(cell_num)){
            Util.ShowTips(activity,getResources().getString(R.string.tips_58));
            return true;
        }
        if(!Util.isMobileNO(cell_num)){
            Util.ShowTips(activity,getResources().getString(R.string.tips_57));
            return true;
        }
        if(!Util.isNetWorkAvailable(getApplicationContext())){
            Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
            return true;
        }
        return false;
    }

    private void initView() {

        m_close = (ImageView) findViewById(R.id.select_forgot_close);
        m_phone = (EditText) findViewById(R.id.phone_ks_va); //手机号
        m_phone_ks_code = (Button) findViewById(R.id.phone_ks_code_va); //验证手机号是否绑定
        m_zh_qd= (Button) findViewById(R.id.zh_qd);
        m_cb_phone= (CheckBox) findViewById(R.id.zh_phone);
        m_cb_email= (CheckBox) findViewById(R.id.zh_email);
        mphone = (TextView) findViewById(R.id.tv);
        memail = (TextView) findViewById(R.id.tv_1);
        m_frameLayout= (FrameLayout) findViewById(R.id.zh_view); //显示找回密码view
    }



   private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            String msg_content = msg.obj.toString();
            int resultCode = msg.what;
            Intent intent = null;
            LoadingDialog.dismiss();
            switch (resultCode) {

                case ResultCode.SECURITY_FAIL:
                    Util.ShowTips(activity,msg_content);
                    break;

                case ResultCode.GET_USER_SUCCRESS: //账号存在



                    KnLog.log("账号已经被注册过了，返回的信息："+msg.obj.toString());
                    //显示view
                    m_frameLayout.setVisibility(View.VISIBLE);



                    break;

                case ResultCode.GET_USER_NoEXIStTENT: //账号没有被注册过

                    KnLog.log("账号没有被注册过，返回的信息："+msg.obj.toString());

                    Util.ShowTips(activity,"该没有被注册过,请重新输入！");


                    break;

                case GET_USER_FAIL: //查询出现错误

                    break;


                case ResultCode.QUERY_ACCOUNT_BIND_SUCCESS: //绑定了手机号

                  /*  intent = new Intent(m_activity.getApplicationContext(),AccountManagerCurrentActivity.class);
                    String mobile= msg.obj.toString() ;
                    intent.putExtra("userName",m_userNames);
                    intent.putExtra("mobile",mobile);*/

                    KnLog.log("账号绑定了手机"+msg.obj.toString());

                    String mobile= msg.obj.toString() ; //服务器返回的手机号
                        //手机验证码更改密码
                        Intent intent1 = new Intent(ForgotPasswordActivity.this,PasswordUpdateActivity.class);
                        intent1.putExtra("phone",mobile);
                         startActivity(intent1);

                        activity.finish();


                    break;
                case ResultCode.QUERY_ACCOUNT_BIND_FAIL: //没有绑定手机号

                    Util.ShowTips(activity,"尚未绑定手机号");

				break;

                default:
                    Util.ShowTips(activity,msg_content);
                    break;
            }
        }
    };



}
