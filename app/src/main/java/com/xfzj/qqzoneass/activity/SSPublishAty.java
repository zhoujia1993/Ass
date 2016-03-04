package com.xfzj.qqzoneass.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.xfzj.qqzoneass.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSPublishAty extends Activity implements AdapterView.OnItemClickListener {
    private GridView gv;
    private List<Map<String, Integer>> emojs = new ArrayList<>();
    private List<String> emojIndex = new ArrayList<>();

    private void assignViews() {
        gv = (GridView) findViewById(R.id.gv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emoj_show);
        assignViews();
        initDrawable();
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), emojs, R.layout.imageview_layout, new String[]{"id"}, new int[]{R.id.iv});
        gv.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        gv.setOnItemClickListener(this);

    }

    private void initDrawable() {
        Field[] fields = R.mipmap.class.getDeclaredFields();
        for (Field field : fields) {
            if (!"ic_launcher".equals(field.getName())) {
                try {
                    int index = field.getInt(R.mipmap.class);
                    Map<String, Integer> map = new HashMap<>();
                    map.put("id", index);
                    emojs.add(map);
                    emojIndex.add(field.getName().substring(1));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("aaa", emojIndex.get(position));
    }
}