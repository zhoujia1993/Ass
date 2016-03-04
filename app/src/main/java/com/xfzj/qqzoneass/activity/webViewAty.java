package com.xfzj.qqzoneass.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.config.Config;
import com.xfzj.qqzoneass.model.UserInfo;

public class webViewAty extends BaseCommActivity {
    private WebView wv;
    private ProgressBar pb;
    private Toolbar toolbar;
    public static final String URL = "url";
    public static final String QQNAME = "qqname";
    private void assignViews(String qqName) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        wv = (WebView) findViewById(R.id.wv);
        pb = (ProgressBar) findViewById(R.id.pb);
        toolbar.setTitle(qqName+"的空间");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_aty);
        String url = getIntent().getStringExtra(URL);
        String qqName= getIntent().getStringExtra(QQNAME);
        assignViews(qqName);
 
       
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        
        wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(getApplicationContext());
        }

        CookieManager manager = CookieManager.getInstance();
        UserInfo userInfo = Config.getUserInfo(getApplicationContext());
        manager.setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.removeSessionCookies(null);
        } else {

            manager.removeSessionCookie();
        }
        String[] cookie = userInfo.cookie.split("\\s");
        Log.i("sunzn", "sdfs=" + cookie.length + "");
        for (int i = 0; i < cookie.length; i++) {
            manager.setCookie("qzone.com", cookie[i] + " EXPIRES=Fri, 02-Jan-2020 00:00:00 GMT; PATH=/; DOMAIN=qzone.com");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
        wv.setWebViewClient(new MyClient());
        wv.setWebChromeClient(new MyChromeClient());
        wv.loadUrl(url);


    }


    private class MyClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            wv.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            super.onPageStarted(view, url, favicon);
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(0);


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb.setProgress(100);
            pb.setVisibility(View.GONE);
            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            Log.e("sunzn", "Cookies = " + CookieStr);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (wv.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
                wv.goBack();
                return true;
            }
        }
        finish();
        return false;
    }


    private class MyChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(newProgress);
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
        wv.clearCache(true);
        wv.clearFormData();
        wv.clearHistory();
    }
}
