package com.xfzj.qqzoneass.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xfzj.qqzoneass.R;

import me.drakeet.materialdialog.MaterialDialog;

public class SettingsAty extends BaseCommActivity implements View.OnClickListener {
    private RelativeLayout rl;
    private LinearLayout llLike;
    private LinearLayout llComm;
    private LinearLayout llVip;
    private LinearLayout llUpdate;
    private LinearLayout llAbout,llFeedBack;
    private Toolbar toolbar;
    private ProgressDialog pd;
    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
        llLike = (LinearLayout) findViewById(R.id.llLike);
        llComm = (LinearLayout) findViewById(R.id.llComm);
        llVip = (LinearLayout) findViewById(R.id.llVip);
        llUpdate = (LinearLayout) findViewById(R.id.llUpdate);
        llAbout = (LinearLayout) findViewById(R.id.llAbout);
        llFeedBack=(LinearLayout) findViewById(R.id.llFeedBack);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        llLike.setOnClickListener(this);
        llComm.setOnClickListener(this);
        llVip.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llFeedBack.setOnClickListener(this);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_aty);
        assignViews();


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llLike:
                startActivity(new Intent(getApplicationContext(), Like_Setting_Aty.class));
                break;
            case R.id.llComm:
                startActivity(new Intent(getApplicationContext(), Comm_Setting_Aty.class));
                break;
            case R.id.llVip:
                startActivity(new Intent(getApplicationContext(), AdAty.class));
                break;
            case R.id.llUpdate:
                pd=ProgressDialog.show(SettingsAty.this,null,"正在检查更新...");
                UmengUpdateAgent.forceUpdate(SettingsAty.this);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                        pd.dismiss();
                        switch (i) {
                            case UpdateStatus.Yes: // has update
//                                UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                                break;
                            case UpdateStatus.No: // has no update
                                Toast.makeText(getApplicationContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                Toast.makeText(getApplicationContext(), "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout: // time out
                                Toast.makeText(getApplicationContext(), "获取最新版本超时，请重试", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                break;
            case R.id.llFeedBack:
                startActivity(new Intent(getApplicationContext(), FeedBack_Aty.class));
                break;
            case R.id.llAbout:
                final MaterialDialog dialog = new MaterialDialog(SettingsAty.this);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle("关于").setMessage(R.string.about).show();
              
                break;


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
    
}
