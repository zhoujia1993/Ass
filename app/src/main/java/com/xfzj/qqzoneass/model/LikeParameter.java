package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/6/30.
 */
public class LikeParameter {
    public String unikey;
    public String curkey;
    public String abstime;
    public String fid;
    public String name;
    public String content;
    /**
     * 被赞的人的QQ号
     */
public String uin;
    public LikeParameter() {

    }

    public LikeParameter(String unikey, String curkey, String abstime, String fid, String name, String content,String uin) {
        this.unikey = unikey;
        this.curkey = curkey;
        this.abstime = abstime;
        this.fid = fid;
        this.name = name;
        this.content = content;
        this.uin = uin;
    }

    @Override
    public String toString() {
        return "LikeParameter{" +
                "unikey='" + unikey + '\'' +
                ", curkey='" + curkey + '\'' +
                ", abstime='" + abstime + '\'' +
                ", fid='" + fid + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", uin='" + uin + '\'' +
                '}';
    }
}
