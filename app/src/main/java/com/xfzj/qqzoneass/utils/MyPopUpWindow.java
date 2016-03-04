package com.xfzj.qqzoneass.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.xfzj.qqzoneass.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2015/7/5.
 */
public class MyPopUpWindow extends PopupWindow implements AdapterView.OnItemClickListener {
    private Context context;
    private GridView gv;
    private List<Map<String, Integer>> emojs = new ArrayList<>();
    private List<String> emojIndex = new ArrayList<>();
    private IdCallBck idCallBck;

    private void assignViews(View emojView) {
        gv = (GridView) emojView.findViewById(R.id.gv);
    }

    public MyPopUpWindow(Context context, IdCallBck idCallBck) {
        this.context = context;
        this.idCallBck = idCallBck;
        initDrawable();
        View EmojView = LayoutInflater.from(context).inflate(R.layout.emoj_show, null);
        setContentView(EmojView);
        assignViews(EmojView);

        SimpleAdapter adapter = new SimpleAdapter(context, emojs, R.layout.imageview_layout, new String[]{"id"}, new int[]{R.id.iv});
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        
        
        setFocusable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(0xb0ffffff));
        EmojView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = gv.getTop();
                int y = (int) event.getY();
//                Log.i("aaa", "heeight=" + height + "y=" + y);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {
                        dismiss();
                    }
                }


                return true;
            }
        });


    }

    public void setWindowHeight(int height) {
       setHeight(height);
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
                    emojIndex.add(field.getName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (idCallBck != null) {
            idCallBck.getId(emojs.get(position).get("id"),emojIndex.get(position));
        }
    }

    public interface IdCallBck {
        void getId(int id,String name);
    }

}
