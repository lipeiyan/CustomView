package com.payne.customview.util;

import android.content.Context;

public class Utils {
    /**
     * @param context
     * @param dpValue
     * @return dp转px
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
}
