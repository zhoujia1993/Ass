package com.xfzj.qqzoneass.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.utils.Constants;
import com.xfzj.qqzoneass.utils.PicAdapter;

import java.util.ArrayList;
import java.util.List;

public class PicSelectAty extends BaseCommActivity implements View.OnClickListener{
    private RelativeLayout rl;
//    private TextView tvBack;
    private TextView tvComplete;
    private GridView gv;
    private ProgressDialog pd;
    private Toolbar  toolbar;
    private PicAdapter picAdapter;
    private final static int SCAN_OK = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_OK:
                    pd.dismiss();

                    picAdapter = new PicAdapter(getApplicationContext(), lists, gv, tvComplete);

                    gv.setAdapter(picAdapter);

                    break;
            }
        }
    };
    private List<String> lists = new ArrayList<>();

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
//        tvBack = (TextView) findViewById(R.id.tvBack);
        tvComplete = (TextView) findViewById(R.id.tvComplete);
        gv = (GridView) findViewById(R.id.gv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tvBack.setOnClickListener(this);
        tvComplete.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_select_aty);
        assignViews();
        getImages();
        toolbar.setTitle("相册");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setOnMenuItemClickListener(this);

    }

    /**
     * 获取图片
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "暂无外部存储", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //显示进度条
        pd = ProgressDialog.show(PicSelectAty.this, null, "正在加载图片...", false, false);
        allScan();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = getContentResolver();
                String[] projection = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA};
                String  selection= MediaStore.Images.Media.SIZE+">?";
                Cursor cursor = resolver.query(imageUri, projection, selection, new String[]{"1024"}, MediaStore.Images.Media.DATE_MODIFIED+" desc");
                while (cursor.moveToNext()) {
                    //获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    lists.add(path);
                }

                cursor.close();
                handler.sendEmptyMessage(SCAN_OK);

            }
        }).start();


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tvComplete:
                String str = tvComplete.getText().toString();
               //// Log.i("aaa", "str=" + str);
                int num = Integer.valueOf(str.substring(str.indexOf("(") + 1, str.indexOf(")")));
                if (num > 30) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PicSelectAty.this);
                    builder.setTitle("提示").setMessage("最多只能选择30张图片").setPositiveButton("确定", null).create().show();
                    return;
                }
                intent.putStringArrayListExtra(Constants.PIC, picAdapter.getCheckedPicPath());
                setResult(Constants.SELECT_PIC, intent);
                finish();
                break;

        }
    }

    // 必须在查找前进行全盘的扫描，否则新加入的图片是无法得到显示的(加入对sd卡操作的权限)  
    public void allScan() {
        //判断sdk 版本是否高于4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{
                    Environment.getExternalStorageDirectory().toString()
            }, null, null);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent intent = new Intent();

                intent.putStringArrayListExtra(Constants.PIC, new ArrayList<String>());
                setResult(Constants.SELECT_PIC, intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_pic_select_aty, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//      
//        return false;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(Constants.PIC, new ArrayList<String>());
            setResult(Constants.SELECT_PIC, intent);
            finish();
            return  true;
        }
        return false;
    }
}
