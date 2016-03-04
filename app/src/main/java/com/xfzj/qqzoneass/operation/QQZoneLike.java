package com.xfzj.qqzoneass.operation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.xfzj.qqzoneass.model.LikeParameter;
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

@SuppressWarnings("deprecation")
public class QQZoneLike {
    private String appid = "311";
    private String qzreferrer = "http://m.qzone.qq.com/infocenter?g_f=";
    private String url = "http://wap.m.qzone.com/praise/like?g_tk=" +
            "";
    private String cookie;
    private LikeParameter p;
    private static final String TAG = "aaa";
    private Context context;
    private String gtk;

    /**
     * 构造方法，在这里解析一个人说说详细数据
     *
     * @param qqNum
     * @param context
     * @param qqNum
     * @param result
     */
    public QQZoneLike(Context context, String qqNum, String gtk, String result) {
        try {
            this.context = context;
            qzreferrer += qqNum;
            url += gtk;
            this.gtk = gtk;

            p = ParseParameter.startParseLikePara(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLike(SuccessCallBack successCallBack,
                          FailCallBack failCallBack) {

//			System.out.println(p.toString());
        //Log.i(TAG, "开始点赞");
        //判断是否满足点赞的要求
        if (isLikeForbid(context, p)) {
            return;
        }
        MyThread myThread = new MyThread(p, this.cookie);
        myThread.like(successCallBack, failCallBack);

    }

    /**
     * 判断p是否是需要屏蔽
     *
     * @param context
     * @param p
     * @return 需要屏蔽，返回true
     */
    private boolean isLikeForbid(Context context, LikeParameter p) {
        if (null == p) {
            return true;

        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //被禁止的QQ号
        String strNums = sp.getString("etLikeNUmForbid", "");
        //对QQ号进行逐个解析，判断p是否满足要求
        if (!TextUtils.isEmpty(strNums)) {
            String[] nums = strNums.split(",");
            for (int i = 0; i < nums.length; i++) {
                if (p.uin.equals(nums[i])) {
                    return true;
                }
            }
        }
        //被禁止的内容
        String strCon = sp.getString("etLikeConForbid", "");
        if (null == strCon || "".equals(strCon)) {
            return false;
        }
        //对内容进行诸葛解析，判断p是否满足要求
        String[] cons = strCon.split("/");
        for (int i = 0; i < cons.length; i++) {
            if (p.content.contains(cons[i]) || cons[i].contains(p.content)) {
                return true;
            }
        }


        return false;


    }

    private class MyThread {
        private LikeParameter p;
        private String cookie;

        public MyThread(LikeParameter p, String cookie) {
            this.p = p;
            this.cookie = cookie;
        }


        public void like(SuccessCallBack successCallBack,
                         FailCallBack failCallBack) {
            try {
                HttpPost post = new HttpPost(url);
                post.setEntity(setParams(p));
                String cookie = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE).getString(Constants.COOKIE, "");
                if (null != cookie && !"".equals(cookie)) {
                    post.setHeader("Cookie", cookie);
                    Log.i("bbb", "cookie:" + cookie);
                }
                HttpResponse response = new DefaultHttpClient().execute(post);
                String result = EntityUtils.toString(response.getEntity());
                Log.i("bbb", result);
                if (response.getStatusLine().getStatusCode() == 200) {
                    if (result.contains("\"code\":0")) {
                        if (successCallBack != null) {
                            Log.i(TAG, "成功了");
                            successCallBack.onSucess(p.name, p.content, Type.LIKE, p.uin);

                        } else {
                            if (failCallBack != null) {
                                Log.i(TAG, "失败了");
                                failCallBack.onFail(Code.Fail);
                            }
                        }
                    }
                } else {
                    if (failCallBack != null) {
                        //Log.i(TAG, "失败了");
                        failCallBack.onFail(Code.Fail);
                    }
                }
                // System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private UrlEncodedFormEntity setParams(LikeParameter p)
                throws UnsupportedEncodingException {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("qzreferrer", qzreferrer));
            list.add(new BasicNameValuePair("g_tk", gtk));
            list.add(new BasicNameValuePair("opr_type", "like"));
            list.add(new BasicNameValuePair("action", "0"));
            list.add(new BasicNameValuePair("res_uin", p.uin));
            list.add(new BasicNameValuePair("res_type", appid));
            list.add(new BasicNameValuePair("uin_key", p.unikey));
            list.add(new BasicNameValuePair("cur_key", p.curkey));
            list.add(new BasicNameValuePair("format", "json"));

            Log.i("bbb", qzreferrer);

            return new UrlEncodedFormEntity(list);
        }
    }

    public interface SuccessCallBack {
        void onSucess(Object... result);
    }

    public interface FailCallBack {
        void onFail(int errorcode);
    }
}
