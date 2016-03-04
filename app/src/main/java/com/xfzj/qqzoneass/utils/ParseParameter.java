package com.xfzj.qqzoneass.utils;

import android.util.Log;

import com.xfzj.qqzoneass.model.CommentParameter;
import com.xfzj.qqzoneass.model.LikeParameter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseParameter {
    /**
     * 解析点赞需要的各个参数
     *
     * @param result
     * @return
     */
    public static LikeParameter startParseLikePara(String result) {
      
        result = result.replaceAll("x22", "\"");
        result = result.replaceAll("x3C", "<");
     
        Document doc = Jsoup.parse(result);
//        Log.i("bbb", doc.toString());
        //解析data_unikey和data-curkey
        Element btn = doc.getElementsByAttributeValue("class",
                "item qz_like_btn_v3").get(0);
//        Log.i("bbb", btn.toString());
                //解析data-abstime
        Element name = doc.getElementsByAttributeValue("name", "feed_data")
                .get(0);
        //解析昵称
        Element nick = doc.getElementsByAttributeValue("data-clicklog", "nick")
                .get(0);
        //解析说说内容
        Element content = doc.getElementsByAttributeValue("class", "f-info").get(0);

        Element topic = doc.getElementsByAttributeValue("name", "feed_data").get(0);
        LikeParameter p = new LikeParameter();
        String unikey = btn.attr("data-unikey");
        String curkey = btn.attr("data-curkey");
        String fid = unikey.substring(unikey.lastIndexOf("/") + 1);
        p.unikey = unikey;
        p.curkey = curkey;
        p.fid = fid;
        p.abstime = name.attr("data-abstime");
        p.name = nick.text();
        p.content = content.text();
        p.uin = topic.attr("data-uin");
        Log.i("bbb", p.toString());
        return p;
    }

    /**
     * 解析评论需要的各个参数
     *
     * @param result
     * @return
     */
    public static CommentParameter startParseCommPara(String result) {
        CommentParameter p = new CommentParameter();
        try {
            result = result.replaceAll("x22", "\"");
            result = result.replaceAll("x3C", "<");
            Document doc = Jsoup.parse(result);


            //解析昵称
            Element nick = doc.getElementsByAttributeValue("data-clicklog", "nick").get(0);
            //解析说说内容
            Element content = doc.getElementsByAttributeValue("class", "f-info").get(0);
            Element topic = doc.getElementsByAttributeValue("name", "feed_data").get(0);
            //解析之前评论的QQ号
            Elements eles = doc.getElementsByAttributeValue("class", "comments-item bor3");
            String[] commNum = new String[eles.size()];
            for (int i = 0; i < eles.size(); i++) {
                commNum[i] = eles.get(i).attr("data-uin");
            }


            String topicId = topic.attr("data-topicid");

            String uin = topic.attr("data-uin");
            p.topicId = topicId;
            p.hostUin = uin;
            p.name = nick.text();
            p.SSContent = content.text();
            p.commNum = commNum;
        } catch (Exception e) {
            e.printStackTrace();
//            Log.i("aaa", "错误的代码=" + result);
        }
        return p;
    }

}
