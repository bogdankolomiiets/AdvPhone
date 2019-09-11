package com.epam.rd.advphone.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextDrawable extends Drawable {
    private Paint paint;
    private String text;

    public TextDrawable(String text, int color){
        this.text = text;
        paint = new Paint();

        paint.setColor(color);
        paint.setTextSize(20f);
        paint.setStyle(Paint.Style.FILL);
    }

    public TextDrawable drawText(){
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawText(text, 0, 0, paint);
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
