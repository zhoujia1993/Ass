package com.xfzj.qqzoneass.operation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zj on 2015/7/9.
 */
public class NativeImageLoader {
    private LruCache<String ,Bitmap>   lruCaches;
    private static  NativeImageLoader mIstance=new NativeImageLoader();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);


    private NativeImageLoader() {
        //获取应用程序的最大内存
        int maxMemory=(int)Runtime.getRuntime().maxMemory()/1024;
        //用最大内存的1/4来存储图片
        int picMemory=maxMemory/8;
        lruCaches = new LruCache<String, Bitmap>(picMemory){


            @Override
            protected int sizeOf(String key, Bitmap value) {
                return  value.getRowBytes()*value.getHeight()/1024;
            }
        };
        
    }


    public static NativeImageLoader getIstance() {
        return mIstance;
    }

    /**
     * 不裁剪，加载本地图片
     * @param path
     * @param nativeImageCallBack
     * @return
     */
    public Bitmap loadNativeImage(String path, NativeImageCallBack nativeImageCallBack) {
        return this.loadNativeImage(path, null, nativeImageCallBack);
    }

    /**
     * 加载本地图片，point封装imageview的宽和高，根据imageview的大小来裁剪图片
     * @param path
     * @param point
     * @param nativeImageCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final Point point, final NativeImageCallBack nativeImageCallBack) {
        //先获取内存中的bitmap
        Bitmap bitmap = getBitmapFromCache(path);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                nativeImageCallBack.onImageLoader((Bitmap) msg.obj, path);
            }
        };
         //若该bitmap不在内存缓存中，则启用线程去加载本地图片，并将bitmap加入到lruCaches中
        if(bitmap==null){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //先获取图片的缩略图
                    Bitmap bitmap1 = decodeThumbBitmapForFile(path, point == null ? 0 : point.x, point == null ? 0 : point.y);

                    Message msg = handler.obtainMessage();
                    msg.obj = bitmap1;
                    handler.sendMessage(msg);

                    //将图片加入到内存缓存
                    addBitmapToCache(path, bitmap1);
                    
                    
                }
            });
            
            
        }

        return bitmap;
        
    }

    /**
     * 根据view的宽和高来获取图片的缩略图
     * @param path
     * @param x
     * @param y
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int x, int y) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        //设置缩放比例
        options.inSampleSize = computeScale(options, x, y)*4;
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(path, options);
        
        
    }

    /**
     * 计算裁剪比例
     * @param options
     * @param x
     * @param y
     * @return
     */
    private int computeScale(BitmapFactory.Options options, int x, int y) {
        int defultScale=1;
        if (x == 0 || y == 0) {
            return  defultScale;
        }
        int width=options.outWidth;
        int height=options.outHeight;

        if (width > x || height > y) {
            int wScale = Math.round((float) width / (float) x);
            int hScale = Math.round((float) height / (float) y);
            defultScale= wScale > hScale ? hScale : wScale;
        }
        return defultScale;
    }


    private void addBitmapToCache(String path, Bitmap bitmap) {
//        Log.i("aaa", "addBitmapToCache");
        if (getBitmapFromCache(path) == null && (bitmap != null && path != null)) {
            lruCaches.put(path, bitmap);
//            Log.i("aaa", "加入到缓存");
        }
    }
    /**
     * 根据key来获取内存中的图片
     * @param path
     * @return
     */
    private Bitmap getBitmapFromCache(String path) {
//        Log.i("aaa", "getBitmapFromCache");
        return lruCaches.get(path);
    }

    /**
     * 加载本地图片的回调接口
     *
     * @author xiaanming
     *
     */
    public interface NativeImageCallBack{
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         * @param bitmap
         * @param path
         */
         void onImageLoader(Bitmap bitmap, String path);
    }
}
