package com.snailyc.utils;

import android.graphics.Color;

public class ColorUtil {
    public static  int getMiddleColor(int startColor, int endColor, float scale) {
        int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * scale);
        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * scale);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * scale);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * scale);

        return Color.argb(a, r, g, b);
    }
}
