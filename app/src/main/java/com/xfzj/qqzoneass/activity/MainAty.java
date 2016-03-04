package com.xfzj.qqzoneass.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xfzj.qqzoneass.MainActivity;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.ShowContent;
import com.xfzj.qqzoneass.model.Type;
import com.xfzj.qqzoneass.model.UserInfo;
import com.xfzj.qqzoneass.model.Visitor;
import com.xfzj.qqzoneass.net.Result_Get_Net;
import com.xfzj.qqzoneass.operation.Login;
import com.xfzj.qqzoneass.service.LikeService;
import com.xfzj.qqzoneass.utils.Code;
import com.xfzj.qqzoneass.utils.Divider;
import com.xfzj.qqzoneass.utils.Funcation;
import com.xfzj.qqzoneass.utils.MyRecyleAdapter;
import com.xfzj.qqzoneass.utils.MyVisitorAdapter;
import com.xfzj.qqzoneass.utils.UrlUtils;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MainAty extends BaseCommActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener, MyRecyleAdapter.onRecyclerViewItemClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MainAty";
    private static final boolean DEBUG = true;
    private static final int HEAD_SETTING = 100;
    private static final int FEED_BACK_SUCCESS = 200;
    private static final int GET_VISITOR_HEADER = 300;
    private android.support.v7.widget.Toolbar toolbar;
    private ImageView ivHeadImage, ivDivider;
    private ButtonFloat tbLike;
    private ButtonFloat tbComment;
    private RecyclerView lv;
    private GridView gv;
    private LinearLayout ll, llVisitor;
    private Login login;
    private TextView tvName;
    private Intent likeService = null;
    private LikeService lservice;
    private boolean isLike, isComment;//用来记录两个按钮按下或开启的flag
    private boolean isRunning;
    private List<ShowContent> lists = new ArrayList<>();
    private List<Visitor> visitors = new ArrayList<>();
    private MyRecyleAdapter myAdapter;
    private boolean likeClicked = false, CommClicked = false;
    private MyBroadCstReceiver myBroadCstReceiver;
    private MyVisitorAdapter myVisitorAdapter;
    private TextView tvTodayTotal, tvTotal;
    private UMSocialService mController;
    private int preHeight, NowHeight;
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (isLike) {
                        doLike();

                    }
                    if (isComment) {
                        doComment();
                    }
                    break;
                case 1:
                    if ((boolean) msg.obj)
                        Toast.makeText(getApplicationContext(), "登陆失败，请重试", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "本次登陆需要验证码", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("verify", (Bitmap) msg.obj);
                    startActivity(intent);
                    finish();
                    break;
                case HEAD_SETTING:
                    //更改头像
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ivHeadImage.setImageBitmap(bitmap);
                    break;
                case FEED_BACK_SUCCESS:
                    //点赞或者评论成功反馈
                    myAdapter.notifyDataSetChanged();
                    break;
                case GET_VISITOR_HEADER:
                    Collections.sort(visitors);
                    myVisitorAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };


    private void assignViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivHeadImage = (ImageView) findViewById(R.id.ivHeadImage);
//        ivDivider = (ImageView) findViewById(R.id.ivDivider);
        tbLike = (ButtonFloat) findViewById(R.id.tbLike);
        gv = (GridView) findViewById(R.id.gv);
        tbComment = (ButtonFloat) findViewById(R.id.tbComment);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTodayTotal = (TextView) findViewById(R.id.tvTodayTotal);
        tvTotal = (TextView) findViewById(R.id.tvTotal);

        setButtonFloat(tbLike, R.drawable.like);
        setButtonFloat(tbComment, R.drawable.comment);
        lv = (RecyclerView) findViewById(R.id.lv);
        ll = (LinearLayout) findViewById(R.id.ll);
        llVisitor = (LinearLayout) findViewById(R.id.llVisitor);
        myAdapter = new MyRecyleAdapter(getApplicationContext(), lists);
        lv.addItemDecoration(new Divider(getApplicationContext()));

        lv.setLayoutManager(new LinearLayoutManager(this));

        lv.setAdapter(myAdapter);
        lv.setItemAnimator(new DefaultItemAnimator());
//        lv.setItemAnimator(new MyItemAnim());
        myAdapter.setOnRecyclerViewItemClickListener(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.share);
        //分享操作

        setShareConfig();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mController.openShare(MainAty.this, false);
            }
        });
        toolbar.setOnMenuItemClickListener(this);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int scerrenWidth = getResources().getDisplayMetrics().widthPixels;
        toolbar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight / 3));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tbLike.getLayoutParams();
        lp.topMargin = screenHeight / 3 - (int) (dip2pix(50) / 2);

        tbLike.setLayoutParams(lp);
        RelativeLayout.LayoutParams lpc = (RelativeLayout.LayoutParams) tbComment.getLayoutParams();
        lpc.topMargin = screenHeight / 3 - (int) (dip2pix(50) / 2);
        lpc.rightMargin = (int) dip2pix(16);
        tbComment.setLayoutParams(lpc);


        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        }
//        Toast.makeText(getApplicationContext(), toolbar.getBottom() + "", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置分享配置信息
     */
    private void setShareConfig() {
        UMImage umImage = new UMImage(MainAty.this, R.mipmap.ic_launcher);
        String url = "http://www.baidu.com";
        com.umeng.socialize.utils.Log.LOG = true;
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainAty.this, "1104775944",
                "JH79gqavdqVoZoev");
        qqSsoHandler.addToSocialSDK();
        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(MainAty.this, "1104775944",
                "JH79gqavdqVoZoev");
        qZoneSsoHandler.addToSocialSDK();


        QQShareContent qqShareContent = new QQShareContent();
//设置分享文字
        qqShareContent.setShareContent("空间说赞可以让你的说说更好玩，想在哪就在哪，任意选择机型，可插入图片，表情。更可以自动随时为朋友的说说点赞评论。");
//设置分享title
        qqShareContent.setTitle("任意位置、任意机型的发说说！");
//设置分享图片
        qqShareContent.setShareImage(umImage);
//设置点击分享内容的跳转链接
        qqShareContent.setTargetUrl(url);
        mController.setShareMedia(qqShareContent);


        QZoneShareContent qzone = new QZoneShareContent();
//设置分享文字
        qzone.setShareContent("空间说赞可以让你的说说更好玩，想在哪就在哪，任意选择机型，可插入图片，表情。更可以自动随时为朋友的说说点赞评论。");
//设置点击消息的跳转URL
        qzone.setTargetUrl(url);
//设置分享内容的标题
        qzone.setTitle("任意位置、任意机型的发说说！");
//设置分享图片
        qzone.setShareImage(umImage);
        mController.setShareMedia(qzone);


        String appID = "wx703222913e69c311";
        String appSecret = "a8e484f84ae08f2e39f16da69e1adc96";

        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
//设置分享文字
        weixinContent.setShareContent("空间说赞可以让你的说说更好玩，想在哪就在哪，任意选择机型，可插入图片，表情。更可以自动随时为朋友的说说点赞评论。");
//设置title
        weixinContent.setTitle("任意位置、任意机型的发说说！");
//设置分享内容跳转URL
        weixinContent.setTargetUrl(url);
//设置分享图片
//        weixinContent.setShareImage(new UMImage(getApplicationContext(),R.mipmap.ic_launcher));
        mController.setShareMedia(weixinContent);
//设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent("空间说赞可以让你的说说更好玩，想在哪就在哪，任意选择机型，可插入图片，表情。更可以自动随时为朋友的说说点赞评论。");
//设置朋友圈title
        circleMedia.setTitle("任意位置、任意机型的发说说！");
//        circleMedia.setShareImage(new UMImage(getApplicationContext(),R.mipmap.ic_launcher));
        circleMedia.setTargetUrl(url);
        mController.setShareMedia(circleMedia);

// 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(MainAty.this, appID, appSecret);
        wxHandler.addToSocialSDK();
// 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(MainAty.this, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
//设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        //设置腾讯微博SSO handler
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

// 设置分享内容
        mController.setShareContent("空间说赞可以让你的说说更好玩，想在哪就在哪，任意选择机型，可插入图片，表情。更可以自动随时为朋友的说说点赞评论。");
// 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(umImage);
        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN.WEIXIN_CIRCLE,SHARE_MEDIA.SINA);
//        mController.postShare(MainAty.this, SHARE_MEDIA.QQ, snsPostListener);
//        mController.postShare(MainAty.this, SHARE_MEDIA.QZONE, snsPostListener);
//        mController.postShare(MainAty.this, SHARE_MEDIA.TENCENT, snsPostListener);
//        mController.postShare(MainAty.this, SHARE_MEDIA.SINA, snsPostListener);

    }
    SocializeListeners.SnsPostListener snsPostListener=new SocializeListeners.SnsPostListener() {
        @Override
        public void onStart() {
            Toast.makeText(getApplicationContext(), "开始分享.", Toast.LENGTH_SHORT).show(); 
        }

        @Override
        public void onComplete(SHARE_MEDIA share_media, int eCode, SocializeEntity socializeEntity) {
            if (eCode == 200) {
                Toast.makeText(getApplicationContext(), "分享成功.", Toast.LENGTH_SHORT).show();
            } else {
                String eMsg = "";
                if (eCode == -101) {
                    eMsg = "没有授权";
                }
                Toast.makeText(getApplicationContext(), "分享失败[" + eCode + "] " +
                        eMsg, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void setButtonFloat(ButtonFloat btn, int resourceId) {

//        btn.setBackgroundColor(getResources().getColor(R));// 设定按钮背景

        btn.setBackgroundColor(Color.parseColor("#03A9F4"));// 设定按钮背景

        btn.setDrawableIcon(getResources().getDrawable(resourceId));// 设定按钮的图片

        btn.setRippleColor(R.color.primary);// 设置涟漪颜色

//        btn.setRippleColor(getResources().getColor(R.color.baseColor));// 设置涟漪颜色

        btn.setRippleSpeed(5);// 设置涟漪扩散速度


    }

    private float dip2pix(int i) {
        float density = getResources().getDisplayMetrics().density;
        return i * density + 0.5f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        assignViews();


        gv.setVerticalScrollBarEnabled(false);
        tbLike.setOnClickListener(this);
        tbComment.setOnClickListener(this);
        ivHeadImage.setOnClickListener(this);
        login = new Login(getApplicationContext());
        tvName.setText(Config.getWeatherInfo(getApplicationContext()).name);
//        lvParams = (LinearLayout.LayoutParams) lv.getLayoutParams();
//        lvParams.height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
//        lv.setLayoutParams(lvParams);
        myBroadCstReceiver = new MyBroadCstReceiver();
        IntentFilter filter = new IntentFilter("MainAty.finish()");
        registerReceiver(myBroadCstReceiver, filter);
        myVisitorAdapter = new MyVisitorAdapter(getApplicationContext(), visitors);
        gv.setAdapter(myVisitorAdapter);
        gv.setOnItemClickListener(this);
        //获取用户的头像
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadHeadImage();
            }
        }).start();
        getVisitor();

    }

    /**
     * 获取访客的数据
     */
    private void getVisitor() {
        UserInfo info = Config.getUserInfo(getApplicationContext());
        String url = UrlUtils.VISITOR1_URL + info.number + UrlUtils.VISITOR2_URL + info.gtk;
        new Result_Get_Net(getApplicationContext(), url, new Result_Get_Net.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {

                    result = result.replaceFirst("_Callback\\(", "");
                    result = result.substring(0, result.length() - 2);
                    System.out.println(result);
                    JSONObject object = new JSONObject(result);
                    String todaycount = object.getString("todaycount");
                    String totalcount = object.getString("totalcount");
                    llVisitor.setVisibility(View.VISIBLE);
                    tvTodayTotal.setText(todaycount);
                    tvTotal.setText(totalcount);
                    JSONArray array = object.getJSONArray("items");
                    parseJsonArray(array, visitors);
                    getVisitorHeader();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onSuccess(HttpResponse response) {

            }
        }, new Result_Get_Net.FailCallback() {
            @Override
            public void onFail() {

            }
        });


    }

    /**
     * 得到好友的头像
     *
     * @param
     */
    private void getVisitorHeader() {

        new MyThreadv().start();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Visitor visitor = visitors.get(position);
        openQQZone(visitor.uin, visitor.name);
    }

 
    private class MyThreadv extends Thread {


        @Override
        public void run() {
            try {

                for (Visitor visitor : visitors) {
                    String url = UrlUtils.HEADERIMAGE_URL + visitor.uin + "/" + visitor.uin + "/100";
                    Bitmap bitmap = getImage(url);
                    visitor.bitmap = bitmap;
                    Message msg = handler.obtainMessage();
                    msg.what = GET_VISITOR_HEADER;
                    handler.sendMessage(msg);
                }
//                for (Visitor visitor : lists) {
//                    if (null == visitor.bitmap) {
//                        //to do i don't know why!!!!!!!!!!!!
//                    }
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void parseJsonArray(JSONArray array, List<Visitor> lists) {
        int count = array.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                String uin = jsonObject.getString("uin");
                String time = jsonObject.getString("time");
                String name = jsonObject.getString("name");
                lists.add(new Visitor(uin, time, name));
                parseJsonArray(jsonObject.getJSONArray("uins"), lists);
            } catch (JSONException e) {

            }

        }
    }

    /**
     * 获取用户的头像，设置到头像IvHeadImage
     */
    private void loadHeadImage() {
        String qqNum = Config.getUserInfo(getApplicationContext()).number;
        String url = UrlUtils.HEADERIMAGE_URL + qqNum + "/" + qqNum + "/100";
        try {
            Bitmap bitmap = getImage(url);
            Message msg = handler.obtainMessage();
            msg.what = HEAD_SETTING;
            msg.obj = bitmap;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Bitmap getImage(String url) throws Exception {
        //log.i("aaa", "getImage");
        Bitmap bitmap = null;
        URL u = new URL(url);
        URLConnection connection = u.openConnection();

        connection.setDoInput(true);
        InputStream is = connection.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = is.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivHeadImage:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("from", "MainAty");
                startActivity(intent);
                break;
            case R.id.tbLike:
                if (!Funcation.isOK(getApplication(), time)) {
                    showSorryDialog(MainAty.this);
                    return;
                }

                if (DEBUG) {
                    Log.i(TAG, "onClick :lineNumber:(495) likeclicked=" + likeClicked);
                }
                likeClicked = !likeClicked;
                if (likeClicked) {
                    doLike();
                } else {
                    if (DEBUG) {
                        Log.i(TAG, "onClick :lineNumber:(281) ");
                    }
                    isLike = false;
                    //布局设置，隐藏listview
                    hideListView();
                    doAnimClose(tbLike);
                    //结束点赞
                    lservice.isLikeStart(false);
                }


                break;
            case R.id.tbComment:

                if (!Funcation.isOK(getApplication(), time)) {
                    showSorryDialog(MainAty.this);
                    return;
                }
                CommClicked = !CommClicked;
                if (CommClicked) {

                    doComment();

                } else {
                    doAnimClose(tbComment);
                    isComment = false;
                    hideListView();
                    //结束评论
                    lservice.isCommentStart(false);
                }

                break;
        }
    }

    /**
     * 按钮按下进行点赞
     */
    public void doLike() {

        if (DEBUG) {
            Log.i(TAG, "onClick :lineNumber:(268) ");
        }
        MobclickAgent.onEvent(this, "Like");
        //布局设置，显示listview
        isLike = true;
        showListView();
        doAnimOpen(tbLike);
        //开始点赞
        bindService(getServiceIntent(likeService), conn, Context.BIND_AUTO_CREATE);
        if (isRunning) {
            lservice.isLikeStart(true);
        }
    }

    /**
     * 按钮按下进行评论
     */
    public void doComment() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String comCon = sp.getString("etCommCon", "");
        if (null == comCon || "".equals(comCon)) {
            final MaterialDialog materialDialog = new MaterialDialog(MainAty.this);
            materialDialog.setTitle("评论内容").setMessage("请先设置评论内容后再使用此功能哦!").setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
            CommClicked = false;
            return;
        }

        MobclickAgent.onEvent(this, "Comment");
        isComment = true;
        showListView();
        doAnimOpen(tbComment);
        //开始评论
        bindService(getServiceIntent(likeService), conn, Context.BIND_AUTO_CREATE);
        if (isRunning) {
            lservice.isCommentStart(true);
        }
    }

    /**
     * 功能无法开启的对话框
     */
    public static void showSorryDialog(Activity activity) {
        final MaterialDialog materialDialog = new MaterialDialog(activity);
        materialDialog.setTitle("应用到期").setMessage("对不起，应用使用时间已过期，若要继续使用，请到设置中下载广告").setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }

    /**
     * 结束动画
     *
     * @param Button
     */
    private void doAnimClose(ButtonFloat Button) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(Button, "translationY", 0, 0));
        set.setDuration(200).start();
    }

    /**
     * 开始动画
     *
     * @param Button
     */
    private void doAnimOpen(ButtonFloat Button) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(Button, "translationY", 0, -dip2pix(25)));
        set.setDuration(200).start();


    }

    /**
     * 得到开启服务的intent
     *
     * @param likeService
     */
    private Intent getServiceIntent(Intent likeService) {
        if (likeService == null) {
            likeService = new Intent(getApplicationContext(), LikeService.class);
        }
        return likeService;


    }

    /**
     * 布局设置，隐藏listview
     */
    private void hideListView() {
        if (isComment || isLike) {
            return;
        }

//        nowllHeight = (int) ivHeadImage.getY();
        //结束点赞时，先清除之前的记录
        if (lists.size() > 0) {
            lists.clear();
            myAdapter.notifyDataSetChanged();
        }
        setAnim(lv, llVisitor, "close");
        lv.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(tvTotal.getText().toString()))
            llVisitor.setVisibility(View.VISIBLE);
        if (isRunning) {
            unbindService(conn);
            isRunning = false;
        }
    }


    /**
     * 布局设置，显示listview
     */
    private void showListView() {
        if (isLike && isComment) {
            return;
        }
        setAnim(lv, llVisitor, "open");
        lv.setVisibility(View.VISIBLE);
        llVisitor.setVisibility(View.GONE);
        //开始点赞时，先清除之前的记录
        if (lists.size() > 0) {
            lists.clear();
        }
    }

    /**
     * 设置动画
     *
     * @param lv
     * @param llVisitor
     */
    private void setAnim(RecyclerView lv, LinearLayout llVisitor, String type) {
        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animreserve);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animreserve2);
        switch (type) {
            case "open":
                lv.startAnimation(animation2);
                llVisitor.setAnimation(animation1);
                break;
            case "close":
                lv.startAnimation(animation1);
                llVisitor.setAnimation(animation2);
                break;
        }


    }

    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.i("aaa", "onServiceConnected");
            isRunning = true;
            LikeService.MyBinder myBinder = (LikeService.MyBinder) service;
            lservice = myBinder.getService();
            lservice.startLoadShuoShuo(new LikeService.SuccessCallBack() {
                @Override
                public void onSucess(Object... result) {
                    Thread thread = new Thread(new MyThread(new ShowContent((String) result[0], (String) result[1], (Type) result[2], (String) result[3])));

                    thread.start();
//                    Log.i("aaa", result[0] + "点赞成功" + result[1]);


                }
            }, new LikeService.FailCallBack() {
                @Override
                public void onFail(int errorCode) {
                    if (errorCode == Code.LOGIN_Fail) {
                        login.getPt_login_sig(new Login.Login_Sig_CallBack() {
                            @Override
                            public void onSigGetSucc(String sig) {
//                                Log.i("aaa", "onSigGetSucc="+sig);
                                startLogin();

                            }
                        });
                    }
                }
            });
            if (isLike)
                lservice.isLikeStart(true);
            if (isComment)
                lservice.isCommentStart(true);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startLogin() {
        UserInfo info = Config.getUserInfo(getApplicationContext());
        String number = info.number;
        String password = info.password;
        if (number == null || "".equals(number) || password == null || "".equals(password)) {
            return;
        }

        login.beginLogin(number, password, new Login.SuccessCallback() {
            @Override
            public void onSuccess(String result) {

                Config.saveLaunchTime(getApplicationContext(), System.currentTimeMillis());
                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }, new Login.FailCallback() {
            @Override
            public void onFail() {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = true;
                handler.sendMessage(msg);
            }
        }, new Login.VerifyCallback() {
            @Override
            public void onShowVerify(final String verify) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Log.i("aaa", "加载验证码");
                            Message msg = handler.obtainMessage();
                            msg.what = 2;
                            msg.obj = MainAty.getImage(verify);
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (!Funcation.isOK(getApplication(), time)) {
                    showSorryDialog(MainAty.this);
                } else
                    startActivity(new Intent(getApplicationContext(), WriteAty.class));
                break;
            case R.id.action_more:
                Toast.makeText(getApplicationContext(),"尽情期待",Toast.LENGTH_SHORT).show();
                
                break;
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsAty.class));
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(View view, String qq, String name) {
        openQQZone(qq, name);
    }

    /**
     * 打开好友的QQ空间
     *
     * @param qq
     * @param name
     */
    public void openQQZone(String qq, String name) {
        String url = UrlUtils.FRIEND1_URL + qq + UrlUtils.FRIEND2_URL + System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), webViewAty.class);
        intent.putExtra(webViewAty.URL, url);
        intent.putExtra(webViewAty.QQNAME, name);
        startActivity(intent);
    }


    private class MyThread implements Runnable {
        private ShowContent content;

        public MyThread(ShowContent content) {
            this.content = content;
        }

        @Override
        public void run() {
            synchronized (lists) {
                boolean isEqual = false;
//                if (lists.size() == 0) {
//                    lists.add(content);
//                }
                for (ShowContent sc : lists) {
                    //如果最新的展示内容包括以前的，则比较他们的type是否是一致的，如果一致不做操作，否则就更新type类型
                    //如果不是以前的，则直接插入到lists之中
                    if (sc.content.equals(content.content)) {
                        isEqual = true;
//                        Log.i("aaa", "sc.content.equals(content.content)");
                        if (sc.type != content.type) {
                            sc.type = Type.Both;
                        }
                    }
                }
                if (!isEqual) {
                    lists.add(content);
                }
                Message msg = handler.obtainMessage();
                msg.what = FEED_BACK_SUCCESS;
                handler.sendMessage(msg);

            }


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCstReceiver);
        if (isRunning) {
            lservice.isLikeStart(false);
            lservice.isCommentStart(false);
            unbindService(conn);
            isRunning = false;
        }
        if (DEBUG) {
            Log.i(TAG, "onDestroy :lineNumber:(413) isrunning=" + isRunning);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class MyBroadCstReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MainAty.finish()")) {
                if (DEBUG) {
                    Log.i(TAG, "onReceive :lineNumber:(645) onReceiver");
                }
                finish();
            }
        }
    }


    private class MyItemAnim extends RecyclerView.ItemAnimator {
        List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<RecyclerView.ViewHolder>();

        @Override
        public void runPendingAnimations() {
            if (mAnimationAddViewHolders.isEmpty()) {
                return;
            }
            AnimatorSet set;
            View target;
            for (RecyclerView.ViewHolder holder : mAnimationAddViewHolders) {
                target = holder.itemView;
                set = new AnimatorSet();
                set.playTogether(ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                        ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f));
                set.setTarget(target);

                set.setDuration(2000).start();

            }


        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder holder) {
            return mAnimationAddViewHolders.add(holder);
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
            return true;
        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return !mAnimationAddViewHolders.isEmpty();
        }

    }
}
