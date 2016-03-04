package com.xfzj.qqzoneass.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.MainActivity;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.UserInfo;
import com.xfzj.qqzoneass.model.Weather;
import com.xfzj.qqzoneass.operation.Login;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Splash extends Activity implements TencentLocationListener {
    private static final String TAG = "Splash";
    private static final boolean DEBUG = false;
    private static final int DUTIATIONTIME = 2500;
    private String number;
    private String password;
    private boolean isContinue = true;
    private long startTime;
    private long time;
    private Login login;
    private RelativeLayout rl;
    private TextView tvName;
    private TextView tvWeather;
    private TextView tvTemp;
    private TencentLocationManager manager;
    private UserInfo info;

    private void assignViews() {
        rl = (RelativeLayout) findViewById(R.id.rl);
        tvName = (TextView) findViewById(R.id.tvName);
        tvWeather = (TextView) findViewById(R.id.tvWeather);
        tvTemp = (TextView) findViewById(R.id.tvTemp);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    startActivity(new Intent(getApplicationContext(), MainAty.class));
                    finish();
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = new Login(getApplicationContext());
//        getWeatherInfo();
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }


        assignViews();
//        Log.i("xxx", getDeviceInfo(this));
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        boolean isConnected = false;
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }
        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
            builder.setMessage("请检查你的网络情况!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    MobclickAgent.onKillProcess(getApplicationContext());
                    finish();
                }
            }).setCancelable(false).create().show();
            return;
        }
        time = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strdate = format.format(time);
        if (DEBUG) {

            Log.i(TAG, "onCreate :lineNumber:(94) " + strdate);
        }
        info = Config.getUserInfo(getApplicationContext());
        if (info == null || null == info.number || "".equals(info.number) || null == info.password || "".equals(info.password)) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.obj = false;
            handler.sendMessageDelayed(msg, DUTIATIONTIME);
//            finish();
            isContinue = false;
            return;
        }
        Weather weather = Config.getWeatherInfo(getApplicationContext());
        //本地有天气信息，则直接显示，否则获取之后再显示
        if (weather.date.equals(strdate.substring(2))) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/HYShiGuangTiW.ttf");
            rl.setVisibility(View.VISIBLE);
            tvName.setTypeface(typeface);
            tvWeather.setTypeface(typeface);
            tvTemp.setTypeface(typeface);
            tvName.setText("你好： " + weather.name);
            tvWeather.setText("今日天气：" + weather.weather);
            tvTemp.setText("今日温度：" + weather.temp + "℃");
        } else {
            //从网络获取天气信息
            lockLocation();

        }
        //从本地拿到上次的启动时间
        startTime = Config.getLaunchTime(getApplicationContext());
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            Config.saveLaunchTime(getApplicationContext(), startTime);
        } else {
            long nowTime = System.currentTimeMillis();
            if (nowTime - startTime < 4 * 60 * 60 * 1000) {
                if (DEBUG) {
                    Log.i(TAG, "onCreate :lineNumber:(157) nowtime=" + nowTime + " starttime=" + startTime);
                }
                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessageDelayed(msg, DUTIATIONTIME);
                isContinue = false;
                return;
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isContinue) {
            return;
        }


        number = info.number;
        password = info.password;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login.getPt_login_sig(new Login.Login_Sig_CallBack() {
                    @Override
                    public void onSigGetSucc(String sig) {
//                        Log.i("aaa", "onSigGetSucc");
                        startLogin();
                    }
                });
            }
        }, 3000);
    }

    private void startLogin() {
        final long endTime = System.currentTimeMillis();
        login.beginLogin(number, password, new Login.SuccessCallback() {
            @Override
            public void onSuccess(String result) {

                Config.saveLaunchTime(getApplicationContext(), System.currentTimeMillis());
                Message msg = handler.obtainMessage();
                msg.what = 0;
                if (endTime - time < DUTIATIONTIME)
                    handler.sendMessageDelayed(msg, endTime - time);
                else
                    handler.sendMessage(msg);
            }
        }, new Login.FailCallback() {
            @Override
            public void onFail() {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = true;
                if (endTime - time < DUTIATIONTIME)
                    handler.sendMessageDelayed(msg, endTime - time);
                else
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
                            if (endTime - time < DUTIATIONTIME)
                                handler.sendMessageDelayed(msg, endTime - time);
                            else
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

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 定位
     */
    private void lockLocation() {

        TencentLocationRequest request = TencentLocationRequest.create();
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        request.setAllowCache(true);
        request.setInterval(20000);
        manager = TencentLocationManager.getInstance(getApplicationContext());
        int error = manager.requestLocationUpdates(request, this);
//        Log.i("aaa", "error=" + error);
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if (TencentLocation.ERROR_OK == i) {
            //定位成功
            String city = tencentLocation.getCity();
            if (DEBUG) {
                Log.i(TAG, "onLocationChanged :lineNumber:(288) city=" + city + tencentLocation.getAddress());
            }
            manager.removeUpdates(this);
            new AsyncTask<String, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(String... params) {
                    List<String> list = new ArrayList<>();
                    try {

                        HttpGet get = new HttpGet("http://apis.baidu.com/apistore/weatherservice/cityname?cityname=" + URLEncoder.encode(params[0].substring(0, params[0].length() - 1), "utf-8"));
                        get.setHeader("apikey", "4d0b90561f69132b53b48c136a2962e0");

                        HttpResponse response = new DefaultHttpClient().execute(get);
                        String result = EntityUtils.toString(response.getEntity());
                        if (DEBUG) {
                            Log.i(TAG, "doInBackground :lineNumber:(299) result=" + result + params[0]);
                        }
                        JSONObject object = new JSONObject(result);
                        //得到正确的结果
                        if (object.getInt("errNum") == 0 && object.getString("errMsg").equals("success")) {
                            JSONObject jsonObject = object.getJSONObject("retData");
                            String strCity = jsonObject.getString("city");
                            String strDate = jsonObject.getString("date");
                            String strWeather = jsonObject.getString("weather");
                            String strTemp = jsonObject.getString("temp");
                            list.add(strCity);
                            list.add(strDate);
                            list.add(strWeather);
                            list.add(strTemp);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    return list;
                }

                @Override
                protected void onPostExecute(List<String> strings) {
                    super.onPostExecute(strings);
                    if (strings.size() > 0) {
                        Config.saveWeatherInfo(getApplicationContext(), new Weather(strings.get(0), strings.get(1), strings.get(2), strings.get(3), null));
                        Weather weather = Config.getWeatherInfo(getApplicationContext());
                    }
                }
            }.execute(city);


        } else {

        }


    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }
}
