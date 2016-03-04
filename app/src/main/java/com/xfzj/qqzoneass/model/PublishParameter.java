package com.xfzj.qqzoneass.model;

import java.util.Random;

/**
 * Created by zj on 2015/7/1.
 */
public class PublishParameter {
    /**
     * 不解释，必传
     */
    public String g_tk;
    /**
     * 说说的内容
     */
    public String content;
    /**
     * 是否同步到微博，默认为0,保留参数
     */
    public String issyncweibo = "0";
    /**
     * 位置信息，纬度
     */
    public String lat;
    /**
     * 位置信息，经度
     */
    public String lon;
    /**
     * 不清楚，默认为空，保留参数
     */
    public String lbsid = "";
    /**
     * 事件类型，默认为“publish_shuoshuo”，保留参数
     */
    public String opr_type = "publish_shuoshuo";
    /**
     * 发布说说的QQ号
     */
    public String res_uin;
    /**
     * 保证说说不被认为是同一条的必要条件，是随机值
     */
    public String sid;
    /**
     * 不清楚，默认为空，保留参数
     */
    public String richval = "";
    /**
     * 小尾巴，显示机型
     */
    public String source_name;
    /**
     * 不清楚，默认为“2”，保留参数
     */
    public String is_winphone = "2";
    /**
     * 封装格式，默认是“json”，保留参数
     */
    public String format = "json";

    public PublishParameter() {
    }


    public PublishParameter(String g_tk, String content, String lon, String lat, String res_uin, String source_name) {
        this.g_tk = g_tk;
        this.content = content;
        this.lon = lon;
        this.lat = lat;
        this.res_uin = res_uin;
        this.source_name = source_name;

    }

    /**
     * 生成24位的随机码
     *
     * @return
     */
    private String getRandSid() {
//Acs0c8j8n-zaoRdiW48dYGdI
//CP3aEQJ4l-clBzRNp5V8djJV
        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < 10; i++) {
            switch (new Random().nextInt(3)) {
                // 随机码0是数字
                case 0:
                    sb.append((char) (new Random().nextInt(10) + 48));
                    break;
                // 随机码1是大写字母
                case 1:
                    sb.append((char) (new Random().nextInt(26) + 65));
                    break;
                // 随机码2是小写字母
                case 2:
                    sb.append((char) (new Random().nextInt(26) + 97));
                    break;
            }
        }
        return sb.toString();

    }

    @Override
    public String toString() {
        return "PublishParameter{" +
                "g_tk='" + g_tk + '\'' +
                ", content='" + content + '\'' +
                ", issyncweibo='" + issyncweibo + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", lbsid='" + lbsid + '\'' +
                ", opr_type='" + opr_type + '\'' +
                ", res_uin='" + res_uin + '\'' +
                ", sid='" + sid + '\'' +
                ", richval='" + richval + '\'' +
                ", source_name='" + source_name + '\'' +
                ", is_winphone='" + is_winphone + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
