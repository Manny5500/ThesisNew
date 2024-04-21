package com.example.myapplication;

import java.util.ArrayList;

public class GSBUtils {

    private ArrayList<Child> childList;
    public GSBUtils(ArrayList<Child> childList){
        this.childList = childList;
    }
    public int  getGSBAll(){
        int count = 0;
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean isMal = !child.getStatusdb().contains("Normal");
            boolean isLowest = child.getMonthlyIncome().equals("Less than 9,100");
            boolean isLower = child.getMonthlyIncome().equals("9,100 to 18,200");
            boolean isLow = child.getMonthlyIncome().equals("18,200 to 36,400");
            boolean isLowIncome = isLowest || isLower || isLow;

            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;

            if(isMal||isLowIncome||isGOne){
                count++;
            }
        }
        return count;
    }
    public int  getLI(){
        int count = 0;
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean isLowest = child.getMonthlyIncome().equals("Less than 9,100");
            boolean isLower = child.getMonthlyIncome().equals("9,100 to 18,200");
            boolean isLow = child.getMonthlyIncome().equals("18,200 to 36,400");
            boolean isLowIncome = isLowest || isLower || isLow;

            if(isLowIncome){
                count++;
            }
        }
        return count;
    }
    public int getMOC(){
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        int count = 0;
        for(Child child: fGmail){
            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;
            if(isGOne){
                count++;
            }
        }
        return count;
    }

    public  ArrayList<String> mocString(){
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        ArrayList<String> mocEmail = new ArrayList<>();
        int count = 0;
        for(Child child: fGmail){
            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;
            if(isGOne){
                mocEmail.add(child.getGmail());
            }
        }
        return mocEmail;

    }

    public int  getMal(){
        int count = 0;
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean haveMal = false;

            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail()) && !child1.getStatusdb().contains("Normal")){
                    haveMal = true;
                }
            }

            if(haveMal){
                count++;
            }
        }
        return count;
    }


}
