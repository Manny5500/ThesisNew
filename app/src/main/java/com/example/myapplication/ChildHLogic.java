package com.example.myapplication;

import android.content.Context;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChildHLogic extends ChildH{

    String sex;

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    String bdate;
    String fullname;


    public ChildHLogic (ChildH childH, String sex, String bdate, String fullname){
        super();
        this.setHeight(childH.getHeight());
        this.setWeight(childH.getWeight());
        this.setId(childH.getId());
        this.sex = sex;
        this.bdate = bdate;
        this.fullname = fullname;
    }


    public ArrayList<String> getStatusProgress(){
        return  FindStatusWFA.CalculateMalnourishedProgress(
                calcuMD(), weight, height, sex);}

    public  String[] getStatusProgressInd(Double weight, Double height, Context context){
        return  FindStatusWFA.individualTest(
                context, calcuMD(), weight, height, sex);}

    public ArrayList<String> getStatusProgressUI(Double weight, Double height, Context context){
        return FindStatusWFA.CalculateMalnourished(context, calcuMD(), weight, height, sex);
    }
    public int calcuMD() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate localDate1 = LocalDate.parse(bdate, formatter);
        LocalDate localDate2 = LocalDate.parse(id, formatter2);

        Period period = Period.between(localDate1, localDate2);

        int monthsDifference = period.getYears() * 12 + period.getMonths();
        return monthsDifference;
    }


}
