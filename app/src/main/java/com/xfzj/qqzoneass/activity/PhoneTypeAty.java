package com.xfzj.qqzoneass.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.drakeet.materialdialog.MaterialDialog;

public class PhoneTypeAty extends BaseCommActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    private List<String> phonetypes = new ArrayList<>();
    private RelativeLayout rl;
//    private TextView tvBack;
//    private TextView tv;
    private TextView tvAdd;
    private Toolbar toolbar;
    private ListView lv;
    private ArrayAdapter adapter;

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        lv = (ListView) findViewById(R.id.lv);
        toolbar.setTitle("机型");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_type_aty);
        assignViews();
        Set<String> sets = Config.getPhoneTYpe(getApplicationContext());
        phonetypes.addAll(sets);
        Collections.sort(phonetypes, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {

                if (rhs.equals(Build.MODEL))
                    return 1;
                if (rhs.equals("不显示机型")) {
                    return 1;
                }
                return -1;
            }
        });
        Log.i("aaa", phonetypes.toString());
//        adapter = new ArrayAdapter(PhoneTypeAty.this, R.layout.phonetype_content, phonetypes);
        adapter = new ArrayAdapter(PhoneTypeAty.this, R.layout.phonetype_content, R.id.tv, phonetypes);
        lv.setAdapter(adapter);
        
        lv.setOnItemClickListener(this);
        tvAdd.setOnClickListener(this);
        lv.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //将选择的机型信息返回上一个activity
        String phoneType = phonetypes.get(position);
        Intent intent = new Intent();
        intent.putExtra(Constants.PHONETYPE, phoneType);
        setResult(Constants.SELECT_PHONETYPE, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvAdd:

                AlertDialog.Builder builder = new AlertDialog.Builder(PhoneTypeAty.this);
                final View view = new EditText(PhoneTypeAty.this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                builder.setView(view);
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phonetypes.add(((EditText) view).getText().toString());
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                setResult(-1);
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Set<String> sets = new HashSet<>();
        sets.addAll(phonetypes);
        Config.savePhoneTYpe(getApplicationContext(), sets);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        //长按不能删除不显示机型和本机机型
        if (!"不显示机型".equals(phonetypes.get(position)) && !Build.MODEL.equals(phonetypes.get(position))) {
            final MaterialDialog dialog = new MaterialDialog(PhoneTypeAty.this);
            dialog.setMessage("删除" + phonetypes.get(position) + "？").setPositiveButton("删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonetypes.remove(position);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }).setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).show();
        }
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            setResult(-1);
            finish();
            return  true;
        }
        return false;
    }
}
