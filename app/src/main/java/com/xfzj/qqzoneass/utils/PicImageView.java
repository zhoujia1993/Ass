package com.xfzj.qqzoneass.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zj on 2015/7/9.
 */
public class PicImageView extends ImageView {
    private onMeasureListener onMeasureListener;
    public PicImageView(Context context) {
        super(context,null);
    }

    public PicImageView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }
    
    public PicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PicImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(onMeasureListener!=null) {
           onMeasureListener.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        
        
    }

    public void setOnMeasureListener(onMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }
    public interface  onMeasureListener{
        void onMeasure(int width, int height);
    }
    
}
