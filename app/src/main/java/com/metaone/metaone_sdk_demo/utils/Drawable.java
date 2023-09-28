package com.metaone.metaone_sdk_demo.utils;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;




public class Drawable {
  public   GradientDrawable createBorderDrawable(int borderColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(2, borderColor); // Set border width and color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.setPadding(8, 8, 8, 8);
        }
        return drawable;
    }
}