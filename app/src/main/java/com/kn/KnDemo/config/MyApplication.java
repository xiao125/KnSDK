package com.kn.KnDemo.config;

import android.app.Activity;
import android.app.Application;

import com.kn.game.configurator.Game;

/**
 *
 */

public class MyApplication extends Application {
    private Activity mactivity;
    @Override
    public void onCreate() {
        super.onCreate();
        Game.init(this).withApiHost("http://oms.u7game.cn/api/")
                .withLoaderDelayed(0) //设置延时时间
                .withActivity(mactivity) //设置全局avtivity
                .withAppId("")
                .withAppKey("")
                .configure();
    }


}
