package com.xfzj.qqzoneass.operation;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xfzj.qqzoneass.activity.MyApp;
import com.xfzj.qqzoneass.model.FeedBack;
import com.xfzj.qqzoneass.model.FeedBackType;
import com.xfzj.qqzoneass.model.Member;
import com.xfzj.qqzoneass.utils.Funcation;
import com.xfzj.qqzoneass.utils.NetTimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zj on 2015/8/23.
 */
public class BmobUtils {
    private static final String TAG = "BmobUtils";
    private static final boolean DEBUG = false;
    private static MyApp myApp;
    private static BmobUtils instance;

    public static BmobUtils getInstance(Application application) {
        if (instance == null) {
            instance = new BmobUtils();
            myApp = (MyApp) application;
        }
        return instance;
    }

    public static final long DEFAULT_AVAILABLETIME = 24 * 60 * 60 * 1000;

    /**
     * 建表，如果数据库中没有用户信息，则创建一个表，否则不创。
     *
     * @param context
     * @param number
     * @return
     */
    public void createMember(final Context context, final String number) {
        BmobQuery<Member> query = new BmobQuery<>();
        query.addWhereEqualTo(Member.QQNUMBER, number);
        query.findObjects(context, new FindListener<Member>() {
            @Override
            public void onSuccess(List<Member> list) {
//                Toast.makeText(context, "list.size=" + list.size(), Toast.LENGTH_SHORT).show();
                if (list.size() == 0) {
                    //建表
                    Member member = new Member(number, System.currentTimeMillis() + DEFAULT_AVAILABLETIME + "", 0);
                    member.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Funcation.init(myApp, number);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }else {
                    Funcation.init(myApp, number);
                }
            }

            @Override
            public void onError(int i, String s) {
//                Toast.makeText(context, "s=" + s, Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * 获得用户过期时间
     *
     * @param context
     * @param number
     */
    public void getMemberInfo(final Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            return;
        }
        BmobQuery<Member> query = new BmobQuery<>();
        query.addWhereEqualTo(Member.QQNUMBER, number);
        query.findObjects(context, new FindListener<Member>() {
            @Override
            public void onSuccess(List<Member> list) {
                if (list.size() > 0) {
                    myApp.objectId = list.get(0).getObjectId();
                    myApp.availablrTime = Long.valueOf(list.get(0).availableTime);
                    myApp.adCount = list.get(0).adCount;
                    if (DEBUG) {
                        Log.i(TAG, "onSuccess :lineNumber:(90) id=" + myApp.objectId + "  time=" + myApp.availablrTime + "  adcount=" + myApp.adCount);
                    }
                } else {
                    myApp.availablrTime = 0;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "应用配置获取失败，请重启应用", Toast.LENGTH_SHORT).show();
                myApp.availablrTime = 0;
            }
        });
    }

    /**
     * 设置用户的过期时间，如果过期时间<当前时间，则将当前时间+增加的时间写入数据库，否则，则将过期时间+增加的时间写入到数据库
     *
     * @param context
     * @param number
     * @param type    类型，分享为0，下载广告为1
     */
    public void setAvailableTime(final Context context, String number, final int type, final int count, final SubmitCallBack submitCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long addTime = 0;
                    Member member = new Member();
                    switch (type) {
                        case 0:
                            addTime = DEFAULT_AVAILABLETIME;
                            break;
                        case 1:
                            addTime = Long.valueOf(((count * count + (5 * count)) / 2) * DEFAULT_AVAILABLETIME);
                            member.increment("adCount", count);
                            myApp.adCount += count;
                            break;
                    }
                    long availableTime = 0;
                    long currentTime = NetTimeUtils.getNetTime();
                    //当前时间小于过期时间，则将过期时间加上增加的时间写入到数据库
                    if (currentTime < myApp.availablrTime) {
                        availableTime = myApp.availablrTime + addTime;
                    } else {
                        availableTime = currentTime + addTime;
                    }
                    myApp.availablrTime = availableTime;
                    member.availableTime = String.valueOf(availableTime);
                    member.update(context, myApp.objectId, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            if (submitCallBack != null) {
                                submitCallBack.SuccessCallBack(formatTime(myApp.availablrTime), myApp.adCount);
                            }

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            if (submitCallBack != null) {
                                submitCallBack.failCallBack();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void toast( String s) {
        Toast.makeText(myApp.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public static String formatTime(long availableTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return dateFormat.format(new Date(availableTime));
    }

    public interface SubmitCallBack {
        void SuccessCallBack(String availableTime, int adCount);

        void failCallBack();
    }

    /**
     * 返回反馈的类目
     * @return
     */
    public List<FeedBackType> getFeedBacjkType(final FeedTypeCallBack feedTypeCallBack) {
        final List<FeedBackType> types=new ArrayList<>() ;
        BmobQuery<FeedBackType> query = new BmobQuery<>();
        query.findObjects(myApp.getApplicationContext(), new FindListener<FeedBackType>() {
            @Override
            public void onSuccess(List<FeedBackType> list) {
                if (list.size() > 0) {
                    if (feedTypeCallBack != null) {
                        feedTypeCallBack.SuccessCallBack(list);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                if (feedTypeCallBack != null) {
                    feedTypeCallBack.failCallBack();
                }
            }
        });

        return  types;
    }

    /**
     * 提交反馈建议
     * @param type
     * @param qq
     * @param content
     * @param submitFeedCallBack
     */
    public  void  submitFeedBack(String type,String qq,String content, String sdk,String model,final submitFeedCallBack submitFeedCallBack) {
        FeedBack feedBack = new FeedBack(qq,type,content,sdk,model);
        feedBack.save(myApp.getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                if (submitFeedCallBack != null) {
                    submitFeedCallBack.SuccessCallBack();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                if (submitFeedCallBack != null) {
                    submitFeedCallBack.failCallBack();
                }
            }
        });


    }
    
    public interface  submitFeedCallBack{
        void SuccessCallBack();

        void failCallBack();
    }
    public interface  FeedTypeCallBack{
        void SuccessCallBack(List<FeedBackType> types);

        void failCallBack();
    }

}
