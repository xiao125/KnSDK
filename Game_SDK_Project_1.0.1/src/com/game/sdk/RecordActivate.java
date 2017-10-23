package com.game.sdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.game.sdk.service.HttpService;
import com.game.sdk.util.KnLog;

/**
 * 上报数据接口
 */

public class RecordActivate {


    private static RecordActivate inst=null;

    public static RecordActivate getInstance(){
        if (inst==null){
            inst = new RecordActivate();
        }
        return inst;
    }



    public void init( final Activity activity){
        HttpService.recordActivate(activity,handler);


    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){

                case ResultCode.SUCCESS:
                    KnLog.log("上报数据成功"+msg.obj.toString());

                    break;

                case ResultCode.FAIL:
                    KnLog.log("上报数据失败"+msg.obj.toString());
                    break;
            }
        }
    };

}
