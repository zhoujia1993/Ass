package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/7/1.
 */
public class ShowContent {
    public String name;
    public String content;
    public Type type;
    public String qq;

    public ShowContent(String name, String content, Type type, String qq) {
        this.name = name;
        this.content = content;
        this.type = type;
        this.qq = qq;
    }

    public ShowContent() {
    }

    @Override
    public String toString() {

        return "ShowContent{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +", qq="+qq+
                '}';
    }
}
