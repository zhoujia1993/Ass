package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/6/29.
 */
public class UserInfo {
    public String number;
    public String password;
    public String cookie;
    public String gtk;
    public String sid;
    public String p_skey;
    public String pt4_token;
    public UserInfo() {

    }

    public UserInfo(String number, String password, String cookie, String gtk, String sid,String p_skey,String pt4_token ) {
        this.number = number;
        this.password = password;
        this.cookie = cookie;
        this.gtk = gtk;
        this.sid = sid;
        this.p_skey=p_skey;
        this.pt4_token = pt4_token;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "number='" + number + '\'' +
                ", password='" + password + '\'' +
                ", cookie='" + cookie + '\'' +
                ", gtk='" + gtk + '\'' +",sid="+sid+",p_skey="+p_skey+" pt4_token="+pt4_token+
                '}';
    }
}
