package com.example.myapplication;

import java.util.ArrayList;

public class StringUtils {

    String status1;
    String status2;
    String query;

    public StringUtils(String status1, String status2, String query){
        this.status1 = status1;
        this.status2 = status2;
        this.query = query;
    }

   public static String [][] statusList =  {
            {"Underweight", "Severe Underweight"},
            {"Overweight", "Obese"},
            {"Stunted", "Severe Stunted"},
            {"Wasted", "Severe Wasted"}
    };

    public String labelFormat(int i){
        String label;
        if(i>0){
            if(query.equals("all")){
                label = "Female";
            }else{
                label = "Female (" + labelChanger(status1)+")";
            }
        }else{
            if(query.equals("all")){
                label = "Male";
            }else{
                label = "Male (" + labelChanger(status1)+")";
            }
        }
        return label;
    }
    public String labelFormatFour(int i){
        String label;
        if(i>1){
            label = "Female (";
        }else{
            label = "Male (";
        }
        if(i%2==0){
            label = label + labelChanger(status1) + ")";
        }else{
            label = label + labelChanger(status2) + ")";
        }
        return label;
    }

    public static String labelChanger(String status){
        if(status.equals("Underweight")){
            status = "UW";
        }
        if(status.equals("Severe Underweight")){
            status = "SU";
        }
        if(status.equals("Overweight")){
            status = "OW";
        }
        if(status.equals("Obese")){
            status = "OB";
        }
        if(status.equals("Stunted")){
            status = "ST";
        }
        if(status.equals("Severe Stunted")){
            status = "SS";
        }
        if(status.equals("Wasted")){
            status = "W";
        }
        if(status.equals("Severe Wasted")){
            status = "SW";
        }
        return  status;
    }
    public static String percentageFormat(int number, int sum){
        //to prevent the NaN printing
        if(sum==0){
            sum = 1;
        }
        return String.format("%.2f", (double) number / sum * 100) + " %";
    };


    public static String[][] interLeave(String[][] tableData){
        String[][] tableDataNow = new String[12][3];
        int asc=0;
        int dsc=5;
        for(int i=0;i<12;i++){
            if(i%2==0){
                int index = i-asc;
                tableDataNow[i][0] = tableData[index][0];
                tableDataNow[i][1] = tableData[index][1];
                tableDataNow[i][2] = tableData[index][2];
                asc++;
            }else{
                int index = i+dsc;
                tableDataNow[i][0] = tableData[index][0];
                tableDataNow[i][1] = tableData[index][1];
                tableDataNow[i][2] = tableData[index][2];
                dsc--;
            }
        }
        return tableDataNow;
    }

}
