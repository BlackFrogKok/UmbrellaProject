package com.samsungschool.umbrellaproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.runtime.image.ImageProvider;

public class ClusterListenerClass implements ClusterListener {

    private Resources resource;
    private WindowManager manager;

    public ClusterListenerClass(Resources resource, WindowManager manager){
        this.resource = resource;
        this.manager = manager;
    }

    @Override
    public void onClusterAdded(@NonNull Cluster cluster) {
       // View v = (View) resource.getLayout(R.layout.umbrella_icon);
        cluster.getAppearance().setIcon(
                new TextImageProvider(manager, Integer.toString(cluster.getSize())));;
        cluster.addClusterTapListener(new ClusterTapLisenerClass());
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = resource.getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public class TextImageProvider extends ImageProvider {

        private WindowManager manager;
        private final String text;

        public TextImageProvider(WindowManager manager, String text){
            this.manager = manager;
            this.text = text;
        }

        @Override
        public String getId() {
            return "text_" + text;
        }




        @Override
        public Bitmap getImage() {
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);

            Paint textPaint = new Paint();
            textPaint.setTextSize(15 * metrics.density);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setAntiAlias(true);

            float widthF = textPaint.measureText(text);
            Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
            float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
            float textRadius = (float)Math.sqrt(widthF * widthF + heightF * heightF) / 2;
            float internalRadius = textRadius + 3 * metrics.density;
            float externalRadius = internalRadius + 3 * metrics.density;

            int width = (int) (2 * externalRadius + 0.5);

            Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setColor(Color.BLUE);
            canvas.drawCircle(width / 2, width / 2, externalRadius, backgroundPaint);

            backgroundPaint.setColor(Color.WHITE);
            canvas.drawCircle(width / 2, width / 2, internalRadius, backgroundPaint);

            canvas.drawText(
                    text,
                    width / 2,
                    width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
                    textPaint);

            return bitmap;
        }

        public TextImageProvider(String text) {
            this.text = text;
        }
    }


}


