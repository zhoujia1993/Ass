package com.xfzj.qqzoneass.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.xfzj.qqzoneass.R;
import com.xfzj.qqzoneass.operation.NativeImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 2015/7/9.
 */
public class PicAdapter extends BaseAdapter {
    private Context context;
    private List<String> lists = new ArrayList<>();
    private GridView gridView;
    private Point point = new Point(0, 0);
    private TextView tvComplete;
    /**
     * 用来存储图片的选中情况
     */
    private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();


    public PicAdapter(Context context, List<String> lists, GridView gridView,TextView tvComplete) {
        this.context = context;
        this.gridView = gridView;
        this.lists = lists;

        this.tvComplete = tvComplete;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = lists.get(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.pic_select_item, null);
            viewHolder = new ViewHolder(convertView);
            //用来监听imageview的宽和高
            viewHolder.iv.setOnMeasureListener(new PicImageView.onMeasureListener() {
                @Override
                public void onMeasure(int width, int height) {
                    point.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.iv.setImageResource(R.drawable.friends_sends_pictures_no);

        }
        viewHolder.iv.setTag(path);
        //利用nativeImageLoader类来加载本地图片
        Bitmap bitmap = NativeImageLoader.getIstance().loadNativeImage(path, point, new NativeImageLoader.NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                //让界面刚进去的时候就加载图片显示
                ImageView imageView = (ImageView) gridView.findViewWithTag(path);
                if (bitmap != null && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });

        if (bitmap != null) {
            viewHolder.iv.setImageBitmap(bitmap);

        } else {
            viewHolder.iv.setImageResource(R.drawable.friends_sends_pictures_no);

        }
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mSelectMap.containsKey(position) || !mSelectMap.get(position)) {
                    addAnimation(viewHolder.cb);


                }
                mSelectMap.put(position, isChecked);
                int i = 0;
                boolean b=false;
                for (Map.Entry<Integer, Boolean> entry : mSelectMap.entrySet()) {
                   
                    if (entry.getValue()) {
                        i++;
                        b=true;
//                        Log.i("aaa", "i==" + i);
                        tvComplete.setVisibility(View.VISIBLE);
                        tvComplete.setText("完成(" + i + ")");
                    } 

                }
                if (!b) {
                    tvComplete.setVisibility(View.GONE);
                }

            }
        });
        viewHolder.cb.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
      
        
        
        return convertView;
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
     * @param cb
     */
    private void addAnimation(CheckBox cb) {

        float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(cb, "scaleX", vaules),
                ObjectAnimator.ofFloat(cb, "scaleY", vaules));
        set.setDuration(150);
        set.start(); 
        
    }

    /**
     * 返回被选中的图片路径
     * @return
     */
public ArrayList<String> getCheckedPicPath() {
    ArrayList<String> paths = new ArrayList<>();

    for (Map.Entry<Integer, Boolean> entry : mSelectMap.entrySet()) {
        if(entry.getValue()) {
           paths.add(lists.get(entry.getKey())) ;
//            Log.i("aaaa", "getCheckedPicPath=" + lists.get(entry.getKey()));
        }
    }
   
   return  paths;

}
    public class ViewHolder {
        public final PicImageView iv;
        public final CheckBox cb;
        public final View root;

        public ViewHolder(View root) {
            iv = (PicImageView) root.findViewById(R.id.iv);
            cb = (CheckBox) root.findViewById(R.id.cb);
            this.root = root;
        }
    }
    
    
    
}
