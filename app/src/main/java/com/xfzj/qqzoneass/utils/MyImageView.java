package com.xfzj.qqzoneass.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zj on 2015/7/1.
 */
public class MyImageView extends ImageView {

    public MyImageView(Context context) {
        super(context, null);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;

        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        final Paint paint = new Paint();
        final Rect rect1 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas1 = new Canvas(output);
        canvas1.drawCircle(bitmap
                .getWidth() / 2.0f, bitmap.getHeight() / 2.0f, bitmap.getWidth() / 2.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas1.drawBitmap(bitmap, rect1, rect1, paint);
        float sx =( (float) getWidth())/  (float)output.getWidth();
        float sy = ((float) getHeight())/ (float)output.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy);
        output = Bitmap.createBitmap(output, 0, 0, output.getWidth(), output.getHeight(), matrix, true);
        canvas.drawBitmap(output, 0, 0, null);
    }

}
