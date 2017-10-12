package com.kn.KnDemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.game.sdk.GameSDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.listener.LoginListener;
import com.game.sdk.util.Util;
import com.kn.game.Gameactivity;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private TextView te;

    private GameSDK gameSDK = GameSDK.getInstance();

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

                login();
            }
        });

        init();


    }

    private void init(){


        GameInfo info = new GameInfo("sxs","ss", "dd", "dff","ddw", 0 ,"qqa");
        info.setGid("");

        gameSDK.initSDK(this ,info );

        // IAppPay.init(mActivity, IAppPay.LANDSCAPE,SDKConfig.appid); //需要渠道分包功能，请传入对应渠道标识ACID, 可以为空
            gameSDK.initIappaySDK( this, SDKConfig.appid ,SDKConfig.privateKey , SDKConfig.publicKey );

        }


        private void login(){

            gameSDK.login(this, new LoginListener() {

                @Override
                public void onSuccess(Object result) {



                    JSONObject obj = null;
                  //  final User user = new User();
                    String  invite  = "";
                    String  code    = "";
/*

                    try {
                        obj = new JSONObject(result.toString());

                      //  LogUtil.log("游戏登录成功返回的 ogj="+result.toString());

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
*/

                }

                @Override
                public void onFail(Object result) {
                 /*   KnLog.e("res+fal"+result.toString());
                    mLoginListener.onFail(result.toString());*/
                }
            });
        }


}
