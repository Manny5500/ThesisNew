package com.example.myapplication;

import android.graphics.Color;

public class ColorUtils {
    String[] hexColor;
    public ColorUtils(String[] hexColor){
        this.hexColor = hexColor;
    }
    public  String[][] hexColorList = {
            {"#FF7000", "#FFAC4A", "#FFE9C9", "#F9C87C"},
            {"#F8858B", "#FF6663", "#E54C38", "#C23A22"},
            {"#0E361C", "#19541F", "#446C48", "#7C9060"},
            {"#bf8bff", "#cca3ff", "#dabcff", "#e5d0ff"}
    };

    public int[] colorArray(int n){
        hexColor = new String[]{
                hexColorList[n][0],hexColorList[n][3]
        };
        int[] arrayColor = { 0, 0, 0, 0};
        for(int i=0; i<2; i++){
            arrayColor[i] = Color.parseColor(hexColor[i]);
        }
        return arrayColor;
    }

}
