package com.example.myapplication;

import java.util.ArrayList;
import java.util.Map;

public class PMChart {
    ArrayList<Double> dataList;
    ArrayList<String> labelList;
    String chartTitle;
    int color;
    public PMChart(ArrayList<Double> dataList, ArrayList<String> labelList ,
                   String chartTitle, int color){
        this.dataList = dataList;
        this.chartTitle = chartTitle;
        this.color = color;
        this.labelList = labelList;
    }

}
