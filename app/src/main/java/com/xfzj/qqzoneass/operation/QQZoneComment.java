package com.xfzj.qqzoneass.operation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.xfzj.qqzoneass.model.CommentParameter;
import com.xfzj.qqzoneass.model.Type;
import com.xfzj.qqzoneass.utils.Code;
import com.xfzj.qqzoneass.utils.Constants;
import com.xfzj.qqzoneass.utils.ParseParameter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj on 2015/7/4.
 */
public class QQZoneComment {
    private Context context;
    private String qqNUm;
    private String gtk;
    private String content;
    private CommentParameter commentParameter;
    private String url = "http://user.qzone.qq.com/q/taotao/cgi-bin/emotion_cgi_re_feeds?g_tk=";
    private static final String TAG = "aaa";

    public QQZoneComment(Context context, String qqNUm, String gtk, String result, String content) {
        this.content = content;
        this.context = context;
        this.qqNUm = qqNUm;
        this.gtk = gtk;
        url += gtk;
        commentParameter = ParseParameter.startParseCommPara(result);
        commentParameter.g_tk = gtk;
        commentParameter.uin = qqNUm;
        commentParameter.content = content;
    }

    public void startComment(SuccessCallBack successCallBack, FailCallBack failCallBack) {
//        Log.i(TAG, "开始评论");
        //判断是否满足点赞的要求
        if (isCommForbid(context, commentParameter)) {
            return;
        }


        MyThread myThread = new MyThread(commentParameter);
        myThread.like(successCallBack, failCallBack);

    }

    /**
     * 判断p是否是需要屏蔽
     *
     * @param context
     * @param p
     * @return 需要屏蔽，返回true
     */
    private boolean isCommForbid(Context context, CommentParameter p) {
        if (null == p) {
//            Log.i("aaa", "p为空");
            return true;

        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //被禁止的QQ号
        String strNums = sp.getString("etCommNumForbid", "");
        //对QQ号进行逐个解析，判断p是否满足要求
        if (!TextUtils.isEmpty(strNums)) {
            String[] nums = strNums.split(",");
//            Log.i("aaa", "nums=" + nums.length + "  =" + nums[0]);
            for (int i = 0; i < nums.length; i++) {
//                Log.i("aaa", "nums=" + nums[i]);
                if (p.hostUin.equals(nums[i])) {
                    return true;
                }
            }
        }
//        Log.i("aaa", "QQ号没被屏蔽");
        //被禁止的内容
        String strCon = sp.getString("etCommConForbid", "");
        if (null != strCon && !"".equals(strCon)) {
//            return false;
//        }
            //对内容进行诸葛解析，判断p是否满足要求
            String[] cons = strCon.split("/");

            for (int i = 0; i < cons.length; i++) {
//                Log.i("aaa", "cons==" + cons[i]);
                if (p.SSContent.contains(cons[i]) || cons[i].contains(p.SSContent)) {
                    return true;
                }
            }
        }
//        Log.i("aaa", "内容没被屏蔽");

        //之前是否评论过，并且是否设置了不允许重复评论
        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = s.getBoolean("cbCommRepeat", false);
        boolean bb = false;
        if (!b) {
            //之前评论过的QQ号集合
            String[] commNum = p.commNum;
            if (null != commNum) {
                for (int i = 0; i < commNum.length; i++) {
                    if (p.uin.equals(commNum[i])) {
                        bb = true;
                        break;
                    }
                }
//                Log.i("aaa", "bb=" + bb);
                return bb;
            }
        }
        return false;


    }

    private class MyThread {
        private CommentParameter p;

        public MyThread(CommentParameter p) {
            this.p = p;

        }


        public void like(SuccessCallBack successCallBack,
                         FailCallBack failCallBack) {
            try {
                HttpPost post = new HttpPost(url);
                post.setEntity(setParams(p));
                String cookie = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
                if (null != cookie && !"".equals(cookie)) {
                    post.setHeader("Cookie", cookie);
//                    Log.i(TAG, "cookie:" + cookie);
                }
                HttpResponse response = new DefaultHttpClient().execute(post);
                String result = EntityUtils.toString(response.getEntity());
                if (response.getStatusLine().getStatusCode() == 200) {
//                    Log.i(TAG, "result=" + result);
                    if (result.contains("\"code\":0")) {
                        if (successCallBack != null) {
//                            Log.i(TAG, "成功了");
                            successCallBack.onSucess(p.name, p.SSContent, Type.Comment, p.hostUin);

                        }
                    } else {
                        if (failCallBack != null) {
//                            Log.i(TAG, "失败了");
                            failCallBack.onFail(Code.Comment_Fail);
                        }
                    }
                } else {
                    if (failCallBack != null) {
//                        Log.i(TAG, "失败了");
                        failCallBack.onFail(Code.Comment_Fail);
                    }
                }
                // System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                if (failCallBack != null) {
//                    Log.i(TAG, "失败了");
                    failCallBack.onFail(Code.Comment_Fail);
                }
            }
        }

        private UrlEncodedFormEntity setParams(CommentParameter p)
                throws UnsupportedEncodingException {
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("g_tk", gtk));
            list.add(new BasicNameValuePair("uin", p.uin));
            list.add(new BasicNameValuePair("topicId", p.topicId));
            list.add(new BasicNameValuePair("source", p.source));
            list.add(new BasicNameValuePair("richval", p.richval));
            list.add(new BasicNameValuePair("richtype", p.richtype));
            list.add(new BasicNameValuePair("ref", p.ref));
            list.add(new BasicNameValuePair("platformid", p.platformid));
            list.add(new BasicNameValuePair("plat", p.plat));
            list.add(new BasicNameValuePair("outCharset", p.outCharset));
            list.add(new BasicNameValuePair("isSignIn", p.isSignIn));
            list.add(new BasicNameValuePair("inCharset", p.inCharset));
            list.add(new BasicNameValuePair("hostUin", p.hostUin));
            list.add(new BasicNameValuePair("format", p.format));
            list.add(new BasicNameValuePair("feedsType", p.feedsType));
            list.add(new BasicNameValuePair("content", p.content));
//            Log.i("aaa", p.toString());
            return new UrlEncodedFormEntity(list, "utf-8");
        }
    }


    public interface SuccessCallBack {
        void onSucess(Object... result);
    }

    public interface FailCallBack {
        void onFail(int errorcode);
    }
}
