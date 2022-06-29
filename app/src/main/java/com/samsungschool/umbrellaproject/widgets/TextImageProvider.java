package com.samsungschool.umbrellaproject.widgets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.DisplayMetrics;

import com.yandex.runtime.image.ImageProvider;

public class TextImageProvider extends ImageProvider {

    private final DisplayMetrics metrics;
    private final String text;

    public TextImageProvider(DisplayMetrics metrics, String text) {
        this.metrics = metrics;
        this.text = text;
    }

    @Override
    public String getId() {
        return "text_" + text;
    }

    @Override
    public Bitmap getImage() {
        Paint textPaint = new Paint();
        FontMetrics textMetrics = textPaint.getFontMetrics();

        textPaint.setTextSize(15 * metrics.density);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

        float widthF = textPaint.measureText(text);
        float heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top);
        float textRadius = (float) Math.sqrt(widthF * widthF + heightF * heightF) / 2;
        float internalRadius = textRadius + 3 * metrics.density;
        float externalRadius = internalRadius + 3 * metrics.density;

        int width = (int) (2 * externalRadius + 0.5);

        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.BLUE);
        canvas.drawCircle(width / 2f, width / 2f, externalRadius, backgroundPaint);

        backgroundPaint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2f, width / 2f, internalRadius, backgroundPaint);

        canvas.drawText(text, width / 2f, width / 2f - (textMetrics.ascent + textMetrics.descent) / 2, textPaint);

        return bitmap;
    }
}
