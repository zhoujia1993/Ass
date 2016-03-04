package com.xfzj.qqzoneass.activity;

import android.app.Application;
import android.os.Build;

import com.umeng.socialize.sso.UMQQSsoHandler;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.utils.Funcation;

import java.util.HashSet;
import java.util.Set;

import cn.bmob.v3.Bmob;

/**
 * Created by zj on 2015/7/6.
 */
public class MyApp extends Application {
    public long availablrTime = 0;
    public String objectId;
    public int adCount;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "886caa356980e1925e8da55cfc1f3462");

        Funcation.init(this, Config.getUserInfo(getApplicationContext()).number);

        

        boolean isFirstUse = Config.isFirstUse(getApplicationContext());
        if (isFirstUse) {
            Config.registerApp(getApplicationContext());
            Set<String> sets = new HashSet<>();
            sets.add("不显示机型");
            sets.add(Build.MODEL);
            sets.add("iPhone 6 Plus");
            sets.add("iPhone 6 ");
            sets.add("iPhone 5");
            sets.add("iPhone 5s");
            sets.add("iPhone 5c");
            sets.add("iPad Air 2");
            sets.add("iPad Air");
            sets.add("iPad mini 3");
            sets.add("iPad mini 2");
            Config.savePhoneTYpe(getApplicationContext(), sets);

        }


    }
}
