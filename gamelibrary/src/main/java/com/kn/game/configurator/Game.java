package com.kn.game.configurator;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 2017/9/28 0028.
 */

public class Game {

    public static Configurator init(Context context) {
        Configurator.getInstance()
                .getLatteConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT, context.getApplicationContext());
        return Configurator.getInstance();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key); //获取 LATTE_CONFIGS数组中存储的状态
    }

    public static Context getApplicationContext() {
        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }

    public static Activity getActivity() {
        return getConfiguration(ConfigKeys.ACTIVITY);
    }

    public static Handler getHandler() {
        return getConfiguration(ConfigKeys.HANDLER);
    }

    public static void test(){
    }
}
