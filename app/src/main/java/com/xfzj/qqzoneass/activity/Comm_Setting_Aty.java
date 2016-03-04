package com.xfzj.qqzoneass.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;

public class Comm_Setting_Aty extends BaseCommActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getFragmentManager().beginTransaction().replace(R.id.frag, new MyPreference()).commit();
        toolbar.setTitle("点赞设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public static class MyPreference extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.comm_setting);
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
