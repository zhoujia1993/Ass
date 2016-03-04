package com.xfzj.qqzoneass.utils;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zj on 2015/8/23.
 */
public class NetTimeUtils {

    public static long getNetTime() {
        try {
            URL url = new URL("http://www.baidu.com");
            //生成连接对象
            URLConnection uc = url.openConnection();
            //发出连接
            uc.connect();
            return uc.getDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
