package com.example.myapplication;

import android.view.ViewGroup;
import android.widget.TextView;

public class LayoutUtils {
    public static  void setTextSize(TextView... textViews){
        for(TextView textView: textViews){
            textView.setTextSize(18);
        }
    }

    public static void setMarginTop(float scale, TextView... textViews){
        int pixels = (int) (20 * scale + 0.5f);
        for(TextView textView: textViews){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.topMargin = pixels;
            textView.setLayoutParams(params);
        }
    }
}
