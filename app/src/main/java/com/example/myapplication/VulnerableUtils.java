package com.example.myapplication;

import java.util.ArrayList;

public class VulnerableUtils  {
    public ArrayList<Child> childList;
    public VulnerableUtils(ArrayList<Child> childList){
        this.childList = childList;
    }

    public ArrayList<Child>  getMalList(){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean haveMal = false;

            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail()) && !child1.getStatusdb().contains("Normal")){
                    haveMal = true;
                }
            }

            if(haveMal){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    public ArrayList<Child>  getVList(){
        ArrayList<Child> vulnerableList = new ArrayList<>();
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
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    public ArrayList<Child>  getLIList(){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean isLowest = child.getMonthlyIncome().equals("Less than 9,100");
            boolean isLower = child.getMonthlyIncome().equals("9,100 to 18,200");
            boolean isLow = child.getMonthlyIncome().equals("18,200 to 36,400");
            boolean isLowIncome = isLowest || isLower || isLow;


            if(isLowIncome){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    public ArrayList<Child>  getMOCList(){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;
            if(isGOne){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }

}
