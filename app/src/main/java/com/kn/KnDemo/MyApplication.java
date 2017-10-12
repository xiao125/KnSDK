package com.kn.KnDemo;

import android.app.Activity;
import android.app.Application;

import com.kn.game.configurator.Game;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class MyApplication extends Application {
    private Activity activity;
    @Override
    public void onCreate() {
        super.onCreate();
        Game.init(this).withApiHost("http://oms.szkuniu.com/api/")
                .withLoaderDelayed(0) //设置延时时间
                .withActivity(activity) //设置全局avtivity
                .withAppId("")
                .withAppKey("")
                .configure();
    }
}
