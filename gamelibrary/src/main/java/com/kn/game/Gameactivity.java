package com.kn.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kn.game.net.RestClient;
import com.kn.game.net.callback.IError;
import com.kn.game.net.callback.ISuccess;
import com.kn.game.util.DeviceUtil;
import com.kn.game.util.KnLog;
import com.kn.game.util.Util;


import java.util.WeakHashMap;

public class Gameactivity extends AppCompatActivity {

    private static final String PROXY_VERSION = "1.0.1" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameactivity);

       String imei = DeviceUtil.getDeviceId();//设备id
        KnLog.d("设备id="+imei);
        String proxy_version = PROXY_VERSION; //版本号
        String app_secret = "3d759cba73b253080543f8311b6030bf";
        WeakHashMap<String,Object> update_params1 = new WeakHashMap<>();
        update_params1.put("msi",imei);
        update_params1.put("proxyVersion",proxy_version);
        update_params1.put("app_secret","");





        RestClient.builder()
                .url("is_bind_username.php")
                .params("update_params1",update_params1)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {

                        KnLog.d(response.toString());
                       Toast.makeText(Gameactivity.this,""+response.toString(),Toast.LENGTH_LONG).show();
                    }

                })

                .error(new IError() {
            @Override
            public void onError(int code, String msg) {
                KnLog.d(msg);
                Toast.makeText(Gameactivity.this,""+msg,Toast.LENGTH_LONG).show();

            }
        })
                .build()
                .post();
    }
}
