package com.xfzj.qqzoneass.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by zj on 2015/8/29.
 */
public class FeedBack extends BmobObject {
    public static final String QQNUM="qqNum";
    public static final String TYPE="type";
    public static final String CONTENT="content";
    
    public String qqNum;
    public String type;
    public String content;
    public String sdk;
    public String model;

    public FeedBack(String qqNum, String type, String content, String sdk, String model) {
        this.qqNum = qqNum;
        this.type = type;
        this.content = content;
        this.sdk = sdk;
        this.model = model;
    }
}
