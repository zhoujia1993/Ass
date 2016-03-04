package com.xfzj.qqzoneass.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.utils.NetTimeUtils;

public class BaseCommActivity extends AppCompatActivity {
public  long time=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            TextView tv = new TextView(this);
            tv.setBackgroundResource(R.color.primary_dark);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()));
            ViewGroup group = (ViewGroup) window.getDecorView();
            group.addView(tv);

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                time = NetTimeUtils.getNetTime();
            }
        }).start();


    }

    private int getStatusBarHeight() {
        int height = 0;
        int resuouceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        height = getResources().getDimensionPixelSize(resuouceId);
        return height;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return false;
    }
}
