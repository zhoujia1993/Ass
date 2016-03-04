package com.xfzj.qqzoneass.model;

import java.util.Arrays;

/**
 * Created by zj on 2015/7/2.
 */
public class CommentParameter {
    /**
     * 不解释，必传
     */
    public String g_tk;
    /**
     * 说说的ID
     */
    public String topicId;
    /**
     * 反馈类型，默认为”100“，保留参数
     */
    public String feedsType = "100";
    /**
     * 编码格式，默认为”utf-8",保留参数
     */
    public String inCharset = "utf-8";
    /**
     * 编码格式，默认为”utf-8",保留参数
     */
    public String outCharset = "utf-8";
    /**
     * 平台类型，默认为“qzone”，保留参数
     */
    public String plat = "qzone";
    /**
     * 来源，默认为“ic”，保留参数
     */
    public String source = "ic";
    /**
     *被评论的QQ号
     */
    public String hostUin;
    /**
     * 不清楚，默认为空，保留参数
     */
    public String isSignIn = "";
    /**
     * 平台形式id，默认为“52”，保留参数
     */
    public String platformid = "52";
    /**
     * 主人的QQ号
     */
    public String uin;
    /**
     * 格式，默认为“fs”，保留参数
     */
    public String format = "fs";
    /**
     * 指向类型，默认为“feeds”，保留参数
     */
    public String ref = "feeds";
    /**
     * 评论内容
     */
    public String content;
    /**
     * 不清楚，默认为空，保留参数
     */
    public String richval = "";
    /**
     * 不清楚，默认为空，保留参数
     */
    public String richtype = "";
    /**
     * QQ昵称
     */

    public String name = "";
    /**
     * 说说内容
     */
    public String SSContent = "";
    
    public String[]  commNum;

    /**
     * 
     * @param g_tk
     * @param topicId
     * @param hostUin
     * @param uin
     * @param content
     * @param name
     * @param SSContent
     */
    public CommentParameter(String g_tk, String topicId, String hostUin, String uin, String content, String name, String SSContent,String[] commNum) {
        this.g_tk = g_tk;
        this.topicId = topicId;
        this.hostUin = hostUin;
        this.uin = uin;
        this.content = content;
        this.name = name;
        this.SSContent = SSContent;
        this.commNum = commNum;
    }

    public CommentParameter() {
    }

    @Override
    public String toString() {
        return "CommentParameter{" +
                "g_tk='" + g_tk + '\'' +
                ", topicId='" + topicId + '\'' +
                ", feedsType='" + feedsType + '\'' +
                ", inCharset='" + inCharset + '\'' +
                ", outCharset='" + outCharset + '\'' +
                ", plat='" + plat + '\'' +
                ", source='" + source + '\'' +
                ", hostUin='" + hostUin + '\'' +
                ", isSignIn='" + isSignIn + '\'' +
                ", platformid='" + platformid + '\'' +
                ", uin='" + uin + '\'' +
                ", format='" + format + '\'' +
                ", ref='" + ref + '\'' +
                ", content='" + content + '\'' +
                ", richval='" + richval + '\'' +
                ", richtype='" + richtype + '\'' +
                ", name='" + name + '\'' +
                ", SSContent='" + SSContent + '\'' +
                ", commNum=" + Arrays.toString(commNum) +
                '}';
    }
}
