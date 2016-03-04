package com.xfzj.qqzoneass.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.UserInfo;
import com.xfzj.qqzoneass.net.Result_Get_Net;
import com.xfzj.qqzoneass.operation.QQZoneComment;
import com.xfzj.qqzoneass.operation.QQZoneLike;
import com.xfzj.qqzoneass.utils.Code;
import com.xfzj.qqzoneass.utils.UrlUtils;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class LikeService extends Service {

    private MyBinder myBinder = new MyBinder();
    private String qqNum, cookie, gtk;
    private Timer LikeTimer, CommTimer;
    private boolean isLike, isComment;
    private SuccessCallBack successCallBack;
    private FailCallBack failCallBack;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //如果开启点赞或者评论，则开始获取说说详情
                    if (isLike) {
                        Log.i("aaa", "liketimer");
                        loadShuoShuo(qqNum, gtk, successCallBack, failCallBack);
                    }
                    break;
                case 1:
                    //如果开启点赞或者评论，则开始获取说说详情
                    if (isComment) {
//                        Log.i("aaa", "commtimer");
                        loadShuoShuo(qqNum, gtk, successCallBack, failCallBack);
                    }
                    break;
                case 2:
                    //如果开启点赞或者评论，则开始获取说说详情
                    if (isLike || isComment) {
//                        Log.i("aaa", "时间相同");
                        loadShuoShuo(qqNum, gtk, successCallBack, failCallBack);
                    }
                    break;

            }
        }
    };

    public LikeService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    public class MyBinder extends Binder {
        public LikeService getService() {
            return LikeService.this;
        }
    }

    /**
     * 开始加载说说的准备工作，设定计时器，循环加载
     *
     * @param successCallBack
     * @param failCallBack
     */
    public void startLoadShuoShuo(final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        this.successCallBack = successCallBack;
        this.failCallBack = failCallBack;
        UserInfo userInfo = Config.getUserInfo(getApplicationContext());
        qqNum = userInfo.number;
//        cookie = userInfo.cookie;
        gtk = userInfo.gtk;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int likeRefreshTime = Integer.valueOf(sp.getString("etLikeRefreshTime", "5"));
        int commRefreshTime = Integer.valueOf(sp.getString("etCommRefreshTime", "5"));
//        Log.i("aaa", "likeRefreshTime=" + likeRefreshTime + " commRefreshTime" + commRefreshTime);

        if (LikeTimer != null) {
            LikeTimer.cancel();
            LikeTimer = null;
        }
        if (CommTimer != null) {
            CommTimer.cancel();
            CommTimer = null;
        }
        //如果点赞和评论的刷新时间是一致的，则只开启一个liketimer
//        if (likeRefreshTime == commRefreshTime) {
//            LikeTimer = new Timer();
//            LikeTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    Message msg = handler.obtainMessage(2);
//                    handler.sendMessage(msg);
//
//                }
//            }, 0, likeRefreshTime * 1000*60);
//            return;
//        }
        LikeTimer = new Timer();
        LikeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage(0);
                handler.sendMessage(msg);

            }
        }, 0, likeRefreshTime * 1000*60);

        CommTimer = new Timer();
        CommTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage(1);
                handler.sendMessage(msg);

            }
        }, 0, commRefreshTime * 1000*60);
    }

    /**
     * 加载说说
     *
     * @param qqNum
     * @param gtk
     * @param successCallBack
     * @param failCallBack
     */
    private void loadShuoShuo(String qqNum, String gtk, final SuccessCallBack successCallBack, final FailCallBack failCallBack) {
        String url = UrlUtils.LOAD_SHUOSHUO1_URL + qqNum + UrlUtils.LOAD_SHUOSHUO2_URL + gtk;
        Log.i("aaa", "url"+url);

        new Result_Get_Net(getApplicationContext(), url, new Result_Get_Net.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.i("aaa", result);
                    String str = result;
                    str = str.substring(str.indexOf("(") + 1);
                    str = str.substring(0, str.length() - 2);
                    JSONObject object = new JSONObject(str);
                    int code = object.getInt("code");
                    //code==0说明获取数据成功，否则失败，提示用户需要重新登陆来获取数据
                    if (code == 0) {
                        //开始解析说说的数据
                        startParseData(result, successCallBack, failCallBack);

                    } else if (code == -3000) {
                        closeTimer();
                        if (failCallBack != null) {
                            failCallBack.onFail(Code.LOGIN_Fail);
                        }


                    } else {
                        closeTimer();
                        if (failCallBack != null) {
                            failCallBack.onFail(Code.Fail);
                        }

                    }


                } catch (Exception e) {
                    closeTimer();
                    if (failCallBack != null) {
                        failCallBack.onFail(Code.PARSE_DATA_ERROR);
                    }
                }


            }

            @Override
            public void onSuccess(HttpResponse response) {


            }
        }, new Result_Get_Net.FailCallback() {
            @Override
            public void onFail() {
                closeTimer();
                if (failCallBack != null) {
                    failCallBack.onFail(Code.Fail);
                }
            }
        });


    }

    private void closeTimer() {
        if (LikeTimer != null) {
            LikeTimer.cancel();
            LikeTimer = null;
        }
        if (CommTimer != null) {
            CommTimer.cancel();
            CommTimer = null;
        }
    }

    /**
     * 开始解析说说的数据,并在这里开始点赞
     *
     * @param result          获得的数据
     * @param successCallBack
     * @param failCallBack
     */
    private void startParseData(String result, SuccessCallBack successCallBack, FailCallBack failCallBack) {


        try {
            String strData = result.substring(result.indexOf("data:["),
                    result.length() - 6);

            strData = "{" + strData.substring(0, strData.lastIndexOf(","))
                    + "]}";
            JSONObject object = new JSONObject(strData);
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                String content = array.getJSONObject(i).getString(
                        "html");
                //开启线程，解析每一个人的说说的数据，从而进行点赞
                MyThread myThread = new MyThread(cookie, content,
                        successCallBack, failCallBack);
                myThread.start();

            }


        } catch (JSONException e) {
            e.printStackTrace();
            if (failCallBack != null) {
                failCallBack.onFail(Code.PARSE_DATA_ERROR);
            }
        }


    }

    /**
     * 点赞钮按下去，相应的布尔值会改变，从而开始相应的操作
     *
     * @param isLike
     */
    public void isLikeStart(boolean isLike) {
        this.isLike = isLike;

    }

    /**
     * 点赞钮按下去，相应的布尔值会改变，从而开始相应的操作
     *
     * @param isComment
     */
    public void isCommentStart(boolean isComment) {
        this.isComment = isComment;

    }

    public interface SuccessCallBack {
        void onSucess(Object... result);
    }

    public interface FailCallBack {
        void onFail(int errorCode);
    }

    private class MyThread extends Thread {
        private String cookie;
        private String content;
        private SuccessCallBack successCallBack;
        private FailCallBack failCallBack;

        public MyThread(String cookie, String content,
                        SuccessCallBack successCallBack, FailCallBack failCallBack) {
            this.cookie = cookie;
            this.content = content;
            this.successCallBack = successCallBack;
            this.failCallBack = failCallBack;
        }

        @Override
        public void run() {
            if (isLike) {
                //开始点赞
                QQZoneLike qqZoneLike = new QQZoneLike(getApplicationContext(), qqNum, gtk, content);
                qqZoneLike.startLike(new QQZoneLike.SuccessCallBack() {

                    @Override
                    public void onSucess(Object... result) {
                        if (successCallBack != null) {
                            successCallBack.onSucess(result);
                        }

                    }
                }, new QQZoneLike.FailCallBack() {

                    @Override
                    public void onFail(int errorcode) {
                        if (failCallBack != null) {
                            failCallBack.onFail(Code.Fail);
                        }
                    }
                });
            }
            if (isComment) {
                //开始评论
                String strcontent = selectCommCon();
                QQZoneComment qqZoneComment = new QQZoneComment(getApplicationContext(), qqNum, gtk, content, strcontent);
                qqZoneComment.startComment(new QQZoneComment.SuccessCallBack() {

                    @Override
                    public void onSucess(Object... result) {
                        if (successCallBack != null) {
                            successCallBack.onSucess(result);
                        }

                    }
                }, new QQZoneComment.FailCallBack() {

                    @Override
                    public void onFail(int errorcode) {
                        if (failCallBack != null) {
                            failCallBack.onFail(Code.Comment_Fail);
                        }
                    }
                });


            }
        }

    }

    /**
     * 随机选取一条评论语句
     *
     * @return
     */
    private String selectCommCon() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strCon = sp.getString("etCommCon", "秒赞！");
        String[] cons = strCon.split("/");
        int index = (int) (Math.random() * cons.length);
        return cons[index];

    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
