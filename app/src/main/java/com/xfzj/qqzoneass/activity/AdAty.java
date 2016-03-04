package com.xfzj.qqzoneass.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.appx.BDInterstitialAd;
import com.gc.materialdesign.views.ButtonRectangle;
import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.operation.BmobUtils;

import me.drakeet.materialdialog.MaterialDialog;

public class AdAty extends BaseCommActivity implements View.OnClickListener {
    private static final String TAG = "AdAty";
    private static final boolean DEBUG = false;
    private RelativeLayout rl;
    private Toolbar toolbar;
    private TextView tvAvailableTime;
    private TextView tvAdCount;
    private ImageView ivQuestion;
    private ButtonRectangle brOpenAd;
    private ButtonRectangle brSubmit;
    private int count = 0;
    private MyApp myApp;
    private BDInterstitialAd appxInterstitialAdView;
    private boolean isOpen=false;
    private static final String APPId = "1104775944";
    private static final String I_POS_ID = "5090309547852809";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String availableTime = (String) msg.obj;
                    int adCount = (int) msg.arg1;
                    tvAvailableTime.setText(availableTime);
                    tvAdCount.setText(adCount + "个");
                    break;

            }


        }
    };

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvAvailableTime = (TextView) findViewById(R.id.tvAvailableTime);
        tvAdCount = (TextView) findViewById(R.id.tvAdCount);
        ivQuestion = (ImageView) findViewById(R.id.ivQuestion);
        brOpenAd = (ButtonRectangle) findViewById(R.id.brOpenAd);
        brSubmit = (ButtonRectangle) findViewById(R.id.brSubmit);
        toolbar.setTitle("广告");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivQuestion.setOnClickListener(this);
        brOpenAd.setOnClickListener(this);
        brSubmit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_aty);
        assignViews();
        myApp = (MyApp) getApplication();
        tvAvailableTime.setText(BmobUtils.formatTime(myApp.availablrTime));
        tvAdCount.setText(myApp.adCount + "个");

        // 创建广告视图
        // 发布时请使用正确的ApiKey和广告位ID
        // 此处ApiKey和推广位ID均是测试用的
        // 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
        // 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
        appxInterstitialAdView = new BDInterstitialAd(this,
                "4GFjH9PYZiqjDDbhyVRGYXpG ", "FnitrVC9BBK8L5h1potZydws");

        // 设置插屏广告行为监听器
        appxInterstitialAdView.setAdListener(new BDInterstitialAd.InterstitialAdListener() {

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Log.e(TAG, "load failure");
            }

            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                Log.e(TAG, "load success");
                if (isOpen) {
                    appxInterstitialAdView.showAd();
                }
            }

            @Override
            public void onAdvertisementViewDidClick() {
                count++;
                Log.e(TAG, "on click");
            }

            @Override
            public void onAdvertisementViewDidHide() {
                Log.e(TAG, "on hide");
            }

            @Override
            public void onAdvertisementViewDidShow() {
                Log.e(TAG, "on show");
            }

            @Override
            public void onAdvertisementViewWillStartNewIntent() {
                Log.e(TAG, "leave");
            }

        });

        // 加载广告
        appxInterstitialAdView.loadAd();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            showDialog();
            return true;
        }
        return false;
    }

    private void showDialog() {
        if (count <= 0) {
            finish();
            return;
        }
        final MaterialDialog dialog = new MaterialDialog(AdAty.this);
        dialog.setTitle("提示").setMessage("您当前还有" + count + "个广告未提交，确定退出吗？").setNegativeButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }).setPositiveButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivQuestion:
                final MaterialDialog materialDialog = new MaterialDialog(AdAty.this);
                materialDialog.setTitle("提示").setMessage(R.string.tips).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();

                break;
            case R.id.brOpenAd:
                Toast.makeText(getApplicationContext(), "正在努力加载广告中,如未能成功加载，请多尝试几次", Toast.LENGTH_SHORT).show();
                // 展示插屏广告前先请先检查下广告是否加载完毕
                if (appxInterstitialAdView.isLoaded()) {
                    appxInterstitialAdView.showAd();
                } else {
                    Log.i(TAG, "AppX Interstitial Ad is not ready");
                    appxInterstitialAdView.loadAd();
                    isOpen=true;
                }
//                final InterstitialAD ad = new InterstitialAD(AdAty.this,
//                        APPId, I_POS_ID);
//                ad.setADListener(new InterstitialADListener() {
//
//                    @Override
//                    public void onADReceive() {
//                        ad.show();
//                    }
//
//                    @Override
//                    public void onNoAD(int i) {
//                        Toast.makeText(getApplicationContext(), "广告未能成功加载，请重新尝试，如果您有安装屏蔽广告的软件，请停止使用后再尝试", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onADOpened() {
//
//                    }
//
//                    @Override
//                    public void onADExposure() {
//
//                    }
//
//                    @Override
//                    public void onADClicked() {
//                        count++;
//                    }
//
//                    @Override
//                    public void onADLeftApplication() {
//
//                    }
//
//                    @Override
//                    public void onADClosed() {
//
//                    }
//                });
//                ad.loadAD();


                break;
            case R.id.brSubmit:
                if (count <= 0) {
                    Toast.makeText(getApplicationContext(), "您还未下载广告，请下载广告后再进行提交", Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobUtils.getInstance(getApplication()).setAvailableTime(getApplicationContext(), Config.getUserInfo(getApplicationContext()).number, 1, count, new BmobUtils.SubmitCallBack() {
                    @Override
                    public void SuccessCallBack(String availableTime, int adCount) {
                        count = 0;
                        Message msg = handler.obtainMessage();
                        msg.obj = availableTime;
                        msg.arg1 = adCount;
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void failCallBack() {

                    }
                });

                break;
        }


    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
