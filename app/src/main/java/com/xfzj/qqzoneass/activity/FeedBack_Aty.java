package com.xfzj.qqzoneass.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.model.FeedBackType;
import com.xfzj.qqzoneass.operation.BmobUtils;

import java.util.ArrayList;
import java.util.List;

public class FeedBack_Aty extends BaseCommActivity implements View.OnClickListener, BmobUtils.FeedTypeCallBack {
    private RelativeLayout rl;
    private Toolbar toolbar;
    private TextView tvSubmit;
    private TextView iv1;
    private Spinner sp;
    private TextView tvText;
    private TextView iv2;
    private TextView tv;
    private EditText etContent;
    private TextView iv3;
    private EditText etQq;
    private ProgressDialog pd;
    private List<FeedBackType> lists = new ArrayList<>();
    private List<String> types = new ArrayList<>();

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvSubmit = (TextView) findViewById(R.id.tvSubmit);
        iv1 = (TextView) findViewById(R.id.iv1);
        sp = (Spinner) findViewById(R.id.sp);
        tvText = (TextView) findViewById(R.id.tvText);
        iv2 = (TextView) findViewById(R.id.iv2);
        tv = (TextView) findViewById(R.id.tv);
        etContent = (EditText) findViewById(R.id.et_content);
        iv3 = (TextView) findViewById(R.id.iv3);
        etQq = (EditText) findViewById(R.id.et_qq);
        toolbar.setTitle("反馈建议");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back__aty);
        assignViews();
        pd = ProgressDialog.show(FeedBack_Aty.this, null, "正在加载配置...");
        lists = BmobUtils.getInstance(getApplication()).getFeedBacjkType(this);


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvSubmit) {
            if (checkItems()) {
                pd = ProgressDialog.show(FeedBack_Aty.this, null, "正在提交反馈...");
                BmobUtils.getInstance(getApplication()).submitFeedBack((String) sp.getSelectedItem(), etQq.getText().toString().trim(), etContent.getText().toString().trim(), Build.VERSION.SDK_INT + "", Build.MODEL, new BmobUtils.submitFeedCallBack() {
                    @Override
                    public void SuccessCallBack() {
                        pd.dismiss();
                        toast("提交成功，谢谢您的反馈！");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    }

                    @Override
                    public void failCallBack() {
                        pd.dismiss();
                        toast("提交失败，请重试！");
                    }
                });
            }
        }
    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();


    }

    private boolean checkItems() {
        if (sp.getSelectedItemId() == 0) {
            toast("请选择反馈类目");
            return false;
        }
        if (TextUtils.isEmpty(etContent.getText().toString().trim())) {
            toast("请填写反馈内容");
            return false;
        }
        return true;
    }

    @Override
    public void SuccessCallBack(List<FeedBackType> typess) {
        pd.dismiss();
        types.add(0, "请选择类目");
        for (FeedBackType type : typess) {
            types.add(type.type);
        }
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.view_complaint_item, R.id.view_complaint_textView, types);
        sp.setAdapter(adapter);
    }

    @Override
    public void failCallBack() {
        Toast.makeText(getApplicationContext(), "获取类目信息出错", Toast.LENGTH_SHORT).show();
        finish();
        pd.dismiss();
        return;
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
