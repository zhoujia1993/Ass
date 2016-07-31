package com.xfzj.qqzoneass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.activity.BaseCommActivity;
import com.xfzj.qqzoneass.activity.MainAty;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.UserInfo;
import com.xfzj.qqzoneass.operation.BmobUtils;
import com.xfzj.qqzoneass.operation.Login;
import com.xfzj.qqzoneass.utils.UrlUtils;


public class MainActivity extends BaseCommActivity implements View.OnClickListener {
    private EditText etNumber;
    private EditText etPwd;
    private EditText etVer;
    private ImageView ivVer, ivHeadImage;
    private ButtonRectangle btnLogin;
    private Login login;
    private ProgressDialog pd;
    private String str;
    private static final int HEAD_SETTING = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           
            switch (msg.what) {
                case 0:
                    pd.dismiss();

                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                    Config.saveLaunchTime(getApplicationContext(), System.currentTimeMillis());
                    Intent intent = new Intent(getApplicationContext(), MainAty.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "登陆失败，请重试", Toast.LENGTH_SHORT).show();
                    if (ivVer.isShown()) {
                        startLogin(etNumber.getText().toString().trim(), etPwd.getText().toString().trim());
                    }
                    break;
                case 2:
                    pd.dismiss();
                    etVer.setVisibility(View.VISIBLE);
                    ivVer.setVisibility(View.VISIBLE);
                    ivVer.setImageBitmap((Bitmap) msg.obj);
                    etVer.requestFocus();
                    break;
                case HEAD_SETTING:
                    //更改头像
                    Bitmap bitmap = (Bitmap) msg.obj;
                    ivHeadImage.setImageBitmap(bitmap);
                    break;
            }
        }
    };

    private void assignViews() {
        etNumber = (EditText) findViewById(R.id.etNumber);
        etPwd = (EditText) findViewById(R.id.etPwd);
        etVer = (EditText) findViewById(R.id.etVer);
        ivVer = (ImageView) findViewById(R.id.ivVer);
        ivHeadImage = (ImageView) findViewById(R.id.ivHeadImage);
        btnLogin = (ButtonRectangle) findViewById(R.id.btnLogin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();

        Config.clearCookie(getApplicationContext());
        btnLogin.setOnClickListener(this);
        login = new Login(getApplicationContext());

        str = getIntent().getStringExtra("from");

        //从本地获取用户的QQ号和QQ密码
        final UserInfo info = Config.getUserInfo(getApplicationContext());
        if (null != info.number && !"".equals(info.number) && null != info.password && !"".equals(info.password) && !str.equals("MainAty")) {

            etNumber.setText(info.number);
            etPwd.setText(info.password);

        }
        Bitmap bitmap = getIntent().getParcelableExtra("verify");
        if (bitmap != null) {
            etVer.setVisibility(View.VISIBLE);
            ivVer.setVisibility(View.VISIBLE);
            ivVer.setImageBitmap(bitmap);
            etVer.requestFocus();

        }
        etPwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onClick(btnLogin);
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(etPwd.getWindowToken(), 0);
                    return true;


                }
                return false;
            }

        });

        etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(etNumber.getText().toString().trim())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadHeadImage();
                        }
                    }).start();
                }
            }
        });
    }

    /**
     * 获取用户的头像，设置到头像IvHeadImage
     */
    private void loadHeadImage() {
        String qqNum = etNumber.getText().toString().trim();
        String url = UrlUtils.HEADERIMAGE_URL + qqNum + "/" + qqNum + "/100";
        try {
            Bitmap bitmap = MainAty.getImage(url);
            Message msg = handler.obtainMessage();
            msg.what = HEAD_SETTING;
            msg.obj = bitmap;
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {
        final String number = etNumber.getText().toString().trim();
        final String password = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(getApplicationContext(), "QQ号不能为空", Toast.LENGTH_SHORT).show();
            etNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "QQ密码不能为空", Toast.LENGTH_SHORT).show();
            etPwd.requestFocus();
            return;
        }
        if (etVer.getVisibility() == View.VISIBLE) {
            String verify = etVer.getText().toString().trim();
            if (TextUtils.isEmpty(verify)) {
                Toast.makeText(getApplicationContext(), "验证码不能为空", Toast.LENGTH_SHORT).show();
                etVer.requestFocus();
                return;
            }
            pd = ProgressDialog.show(MainActivity.this, null, "正在登陆...", false, false);
            login.VerifyLogin(number, password, etVer.getText().toString().trim());
            return;
        }
        pd = ProgressDialog.show(MainActivity.this, null, "正在登陆...", false, true);
        //开始登陆

        startLogin(number, password);


    }

    public void startLogin(final String number, final String password) {
        login.beginLogin(number, password, new Login.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("aaa", result + "登陆成功");
                //debug
//                result = result.split("','")[2];
////                Log.i("aaa", result);
////                Config.saveHomeUrl(getApplicationContext(), result);
////                Log.i("aaa", "重新加载地址");
//                getsID(result);
//                url = result;
                Config.saveUserInfo(getApplicationContext(), number, password, null, null, null,null);
                //在服务器上建表
                BmobUtils.getInstance(getApplication()).createMember(getApplicationContext(), number);


                Message msg = handler.obtainMessage();
                msg.what = 0;
                if (null != str && !"".equals(str) && "MainAty".equals(str)) {
                    Intent broadcastIntent = new Intent("MainAty.finish()");
                    sendBroadcast(broadcastIntent);
                    handler.sendMessageDelayed(msg, 500);
                } else
                    handler.sendMessage(msg);


            }
        }, new Login.FailCallback() {
            @Override
            public void onFail() {
                Log.i("aaa", "登陆失败");
                Message msg = handler.obtainMessage();
                msg.what = 1;
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
