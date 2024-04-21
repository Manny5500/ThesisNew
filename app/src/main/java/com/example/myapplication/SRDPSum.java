package com.example.myapplication;

import java.util.ArrayList;
import java.util.Date;

public class SRDPSum {
    ArrayList<Child> childList;

    String [] OPTCat = {"#Children 0-59 mos. affected by Undernutrition", "#Children 0-59 mos. with Overweight/Obesity:",
            "Total Number of Children 0-23 mos. old: ", "#Children 0-23 mos. affected by Undernutrition: "};

    String [] MotherCat = {"Total Number of M/Cs of children 0-59 mos. old: ", "#M/Cs of 0-59 mos children affected by Undernutrition",
            "#M/Cs of 0-59 mos. children with Overweight/Obesity: ", "Total Number of M/Cs of children 0-23 mos. old ",
            "#M/Cs of 0-23 mos. children affected by Undernutrition: "};

    String [] DataCat = {"#Children with weight but no height: ", "#Children with height but no weight: ", "#Children with missing information:",
            "#Children with names repeated: ", "#Children older than 59 months"};
    public  SRDPSum(ArrayList<Child> childList) {
        this.childList = childList;
    }
    public ArrayList<Child>[] monthFilter(){
        ArrayList<Child> cL23 = new ArrayList<>();
        ArrayList<Child> cL59 = new ArrayList<>();
        for(Child child: childList){
            FormUtils formUtils = new FormUtils();
            Date parsedDate = formUtils.parseDate(child.getBirthDate());
            int monthdiff = 0;
            if (parsedDate != null) {
                monthdiff = formUtils.calculateMonthsDifference(parsedDate);
            }
            boolean is23Mo = monthdiff < 24;
            boolean is59Mo = monthdiff < 60;
            if(is23Mo){
                cL23.add(child);
            }
            if(is59Mo){
                cL59.add(child);
            }
        }
        return new ArrayList[]{cL59, cL23};
    }
    public int[] countNow( ArrayList<Child>[] arrayLists){
        int dataArr[] = new int[4];
        int i=0;
        int j=0;
        for(ArrayList<Child> cL: arrayLists){
            for(Child child: cL){
                String WFAStatus = child.getStatus().get(0);
                String HFAStatus = child.getStatus().get(1);
                String WFHStatus = child.getStatus().get(2);
                boolean isOWWFA = WFAStatus.equals("Overweight");
                boolean isUW = WFAStatus.equals("Underweight");
                boolean isSUW = WFAStatus.equals("Severe Underweight");
                boolean isST = HFAStatus.equals("Stunted");
                boolean isSST = HFAStatus.equals("Severe Stunted");
                boolean isOWWFH = WFHStatus.equals("Overweight");
                boolean isOB = WFHStatus.equals("Obese");
                boolean isW = WFHStatus.equals("Wasted");
                boolean isSW = WFHStatus.equals("Severe Wasted");
                if(isUW || isSUW || isSST || isST || isSW|| isW){
                    dataArr[0+i]++;
                }
                if(j==0){
                    if(isOWWFA||isOWWFH||isOB){
                        dataArr[1]++;
                    }
                }

            }
            i=i+3;
            j++;
        }
        dataArr[2] = arrayLists[0].size();
        return dataArr;
    }

    public int[] countNowMother( ArrayList<Child>[] arrayLists){
        ArrayList<Child>[] ncDuple = new ArrayList[2];
        ncDuple[0] = RemoveDuplicates.rDGmail(arrayLists[0]);
        ncDuple[1] = RemoveDuplicates.rDGmail(arrayLists[1]);

        int dataArr[] = new int[5];
        int i=0;
        int j=0;
        for(ArrayList<Child> cL: arrayLists){
            for(Child child: cL){
                String WFAStatus = child.getStatus().get(0);
                String HFAStatus = child.getStatus().get(1);
                String WFHStatus = child.getStatus().get(2);
                boolean isOWWFA = WFAStatus.equals("Overweight");
                boolean isUW = WFAStatus.equals("Underweight");
                boolean isSUW = WFAStatus.equals("Severe Underweight");
                boolean isST = HFAStatus.equals("Stunted");
                boolean isSST = HFAStatus.equals("Severe Stunted");
                boolean isOWWFH = WFHStatus.equals("Overweight");
                boolean isOB = WFHStatus.equals("Obese");
                boolean isW = WFHStatus.equals("Wasted");
                boolean isSW = WFHStatus.equals("Severe Wasted");
                if(j==1){
                    if(isOWWFA||isOWWFH||isOB){
                        dataArr[2]++;
                    }
                }
                if(isUW || isSUW || isSST || isST || isSW|| isW){
                        dataArr[1+i]++;
                }
            }
            i=i+3;
            j++;
        }
        dataArr[0] = arrayLists[0].size();
        dataArr[3] = arrayLists[1].size();
        return dataArr;
    }

    public int[] countNowData(ArrayList<Child> cL){
        int[] dataArr = new int[5];
        for(Child child:cL){
            boolean hasHeight = child.getHeight() > 0;
            boolean hasWeight = child.getWeight() > 0;
            boolean hasDouble = duplicateFinder(cL, child);
            boolean isOlder59 = isOlder59(child);

            if(hasHeight && !hasWeight){
                dataArr[0]++;
            }
            if(hasWeight && !hasHeight){
                dataArr[1]++;
            }
            if(hasDouble){
                dataArr[3]++;
            }
            if(isOlder59){
                dataArr[4]++;
            }
        }

        dataArr[2] = dataArr[0]+dataArr[1];

        return  dataArr;
    }

    public boolean duplicateFinder(ArrayList<Child> cL, Child child){
        boolean hasDuplicate = false;
        for(Child child1:cL){
            int countName = 0;
            if(child1.getChildFirstName().equals(child.getChildFirstName())&&
            child1.getChildMiddleName().equals(child.getChildMiddleName())&&
            child.getChildLastName().equals(child.getChildLastName())){
               countName++;
            }
            if(countName>1){
                hasDuplicate = true;
            }
        }
        return hasDuplicate;
    }

    public boolean isOlder59(Child child){
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(child.getBirthDate());
        int monthdiff = 0;
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        }
        if(monthdiff>59){
            return true;
        }else{
            return false;
        }

    }
}
