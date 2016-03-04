package com.xfzj.qqzoneass.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by zj on 2015/8/23.
 */
public class Member extends BmobObject {
    public static final String QQNUMBER = "qqNum";
    public static final String AVAILABLETIME = "availableTime";
    public static final String ADCOUNT = "adCount";
    /**
     * QQQ号
     */
    public String qqNum;
    /**
     * 过期时间
     */
    public String availableTime;
    /**
     * 广告累计下载个数
     */
    public Integer adCount;

    public Member(String qqNum, String availableTime, Integer adCount) {
        this.qqNum = qqNum;
        this.availableTime = availableTime;
        this.adCount = adCount;
    }

    public Member() {
        
    }
    
    
}
