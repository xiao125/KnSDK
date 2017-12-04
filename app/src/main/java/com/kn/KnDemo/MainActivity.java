package com.kn.KnDemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.Data;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.GameUser;
import com.game.sdk.listener.LoginListener;
import com.game.sdk.listener.ReportListener;


public class MainActivity extends AppCompatActivity {

    private TextView te;
    private Button bt,jh,sb;


    private GameSDK knGameSDK = GameSDK.getInstance();


   // private KNSDKProxy knsdkProxy = new KNSDKProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        te = (TextView) findViewById(R.id.te);

        te.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(MainActivity.this, Gameactivity.class);
                startActivity(intent);*/

            }
        });

        init();

        inits();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //登录
                login();


            }
        });

        jh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //邀请码
              //  jh();


            }
        });

        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sb();
            }
        });
    }

    private void init(){

        bt = (Button) findViewById(R.id.dl);
        jh = (Button) findViewById(R.id.jh);
        sb = (Button) findViewById(R.id.sb);

       GameInfo info = new GameInfo();
       info.setAppKey("9LVcyGoNbY15ir82MvXuPqgxeOsmQ0Sa");
        info.setPlatform("android");
        info.setGameId("yxby");
        info.setChannel("tianyuyou");
        info.setOrientation(1);
        info.setAdChannel("28020109");
        info.setAdChannelTxt("0");

      /* GameInfo gameInfo =  Data.getInstance().getGameInfo();
        gameInfo.setAppKey("vmc9LUPQ2whK3VrRNHIfkp6dDXiWgayJ");*/


    //    Log.d("ttt","assets文件="+gameInfo.getAdChannel());

      /*  GameInfo info = new GameInfo();
        info.setAppKey("vmc9LUPQ2whK3VrRNHIfkp6dDXiWgayJ");
        info.setOrientation(0);*/

        //sdk初始化
        knGameSDK.initSDK(this ,info);
        //爱贝支付
        knGameSDK.initIappaySDK( this,SDKConfig.appid,SDKConfig.privateKey,SDKConfig.publicKey);

        //中间件初始化
      /*  KnGameInfo knGameInfo = new KnGameInfo("hhah","vmc9LUPQ2whK3VrRNHIfkp6dDXiWgayJ","yxby", KnConstants.PORTRAIT);

        knsdkProxy.init(this, knGameInfo, new InitListener() {
            @Override
            public void onSuccess(Object result) {

                Log.d("tt","初始化成功");
            }

            @Override
            public void onFail(Object result) {
                Log.d("tt","初始化失败");
            }
        });*/





    }


    private void login(){

        knGameSDK.login(this, new LoginListener() {
            @Override
            public void onSuccess(Object result) {
               Log.d("TTT","登录成功="+result.toString());

            }

            @Override
            public void onFail(Object result) {
                Log.d("TTT","登录失败="+result.toString());
            }
        });



    }


    private void sb(){



        GameUser gameUser = new GameUser();
        gameUser.setGid("dd");
        gameUser.setOpenid("fee13d740c9505553c045ba13126c9b8");
        gameUser.setServerId(10784);
        gameUser.setUserLevel(1);
        gameUser.setUid("842842895");
        gameUser.setExtraInfo("ddd");


        knGameSDK.reportGameRole(this, gameUser, new ReportListener() {
            @Override
            public void onSuccess(Object result) {

                Log.d("sss","上报成功="+result.toString());
            }

            @Override
            public void onFail(Object result) {

                Log.d("sss","上报失败="+result.toString());
            }
        });


    }


    private void inits() {

    }

}
