package com.example.myapplication;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class SRDPList {
    ArrayList<Child> childList;
    public  SRDPList(ArrayList<Child> childList){
        this.childList = childList;
    }

    public ArrayList<Child>[] monthFilter(){
        ArrayList<Child> cL0 = new ArrayList<>();
        ArrayList<Child> cL6 = new ArrayList<>();
        ArrayList<Child> cL12 = new ArrayList<>();
        ArrayList<Child> cL24 = new ArrayList<>();
        ArrayList<Child> cL36 = new ArrayList<>();
        ArrayList<Child> cL48 = new ArrayList<>();
        for(Child child: childList){
            FormUtils formUtils = new FormUtils();
            Date parsedDate = formUtils.parseDate(child.getBirthDate());
            int monthdiff = 0;
            if (parsedDate != null) {
                monthdiff = formUtils.calculateMonthsDifference(parsedDate);
            }
            boolean is05Mo = monthdiff < 6;
            boolean is611Mo = monthdiff > 6 && monthdiff < 12;
            boolean is1223Mo = monthdiff > 11 && monthdiff < 24;
            boolean is2435Mo = monthdiff > 24 && monthdiff < 36;
            boolean is3647Mo = monthdiff > 35 && monthdiff < 48;
            boolean is4859Mo = monthdiff > 47 && monthdiff < 60;
            if(is05Mo){
                cL0.add(child);
            }
            if(is611Mo){
                cL6.add(child);
            }
            if(is1223Mo){
                cL12.add(child);
            }
            if(is2435Mo){
                cL24.add(child);
            }
            if(is3647Mo){
                cL36.add(child);
            }
            if(is4859Mo){
                cL48.add(child);
            }
        }
        return new ArrayList[]{cL0, cL6, cL12, cL24, cL36, cL48};
    }

    public int[][] countNow( ArrayList<Child>[] arrayLists){
        int dataArr[][] = new int[78][2];
        int i=0;
        int j=0;
        for(ArrayList<Child> cL: arrayLists){
            for(Child child: cL){
                String WFAStatus = child.getStatus().get(0);
                String HFAStatus = child.getStatus().get(1);
                String WFHStatus = child.getStatus().get(2);
                boolean isMale = child.getSex().equals("Male");
                boolean isFemale = child.getSex().equals("Female");
                boolean isNormalWFA = WFAStatus.equals("Normal");
                boolean isOWWFA = WFAStatus.equals("Overweight");
                boolean isUW = WFAStatus.equals("Underweight");
                boolean isSUW = WFAStatus.equals("Severe Underweight");
                boolean isNormalHFA = HFAStatus.equals("Normal");
                boolean isTall = HFAStatus.equals("Tall");
                boolean isST = HFAStatus.equals("Stunted");
                boolean isSST = HFAStatus.equals("Severe Stunted");
                boolean isNormalWFH = WFHStatus.equals("Normal") || WFHStatus.equals("");
                boolean isOWWFH = WFHStatus.equals("Overweight");
                boolean isOB = WFHStatus.equals("Obese");
                boolean isW = WFHStatus.equals("Wasted");
                boolean isSW = WFHStatus.equals("Severe Wasted");
                if(isMale && isNormalWFA){
                    dataArr[0+i][0]++;
                }
                if(isFemale && isNormalWFA){
                    dataArr[0+i][1]++;
                }
                if(isMale && isOWWFA){
                    dataArr[1+i][0]++;
                }
                if(isFemale && isOWWFA){
                    dataArr[1+i][1]++;
                }
                if(isMale && isUW){
                    dataArr[2+i][0]++;
                }
                if(isFemale && isUW){
                    dataArr[2+i][1]++;
                }
                if(isMale && isSUW){
                    dataArr[3+i][0]++;
                }
                if(isFemale && isSUW){
                    dataArr[3+i][1]++;
                }

                if(isMale && isNormalHFA){
                    dataArr[0+i+24][0]++;
                }
                if(isFemale && isNormalHFA){
                    dataArr[0+i+24][1]++;
                }
                if(isMale && isTall){
                    dataArr[1+i+24][0]++;
                }
                if(isFemale && isTall){
                    dataArr[1+i+24][1]++;
                }
                if(isMale && isST){
                    dataArr[2+i+24][0]++;
                }
                if(isFemale && isST){
                    dataArr[2+i+24][1]++;
                }
                if(isMale && isSST){
                    dataArr[3+i+24][0]++;
                }
                if(isFemale && isSST){
                    dataArr[3+i+24][1]++;
                }

                if(isMale && isNormalWFH){
                    dataArr[0+j+48][0]++;
                }
                if(isFemale && isNormalWFH){
                    dataArr[0+j+48][1]++;
                }
                if(isMale && isOWWFH){
                    dataArr[1+j+48][0]++;
                }
                if(isFemale && isOWWFH){
                    dataArr[1+j+48][1]++;
                }
                if(isMale && isOB){
                    dataArr[2+j+48][0]++;
                }
                if(isFemale && isOB){
                    dataArr[2+j+48][1]++;
                }
                if(isMale && isW){
                    dataArr[3+j+48][0]++;
                }
                if(isFemale && isW){
                    dataArr[3+j+48][1]++;
                }

                if(isMale && isSW){
                    dataArr[4+j+48][0]++;
                }
                if(isFemale && isSW){
                    dataArr[4+j+48][1]++;
                }

            }
            i=i+4;
            j=j+5;
        }

        return dataArr;
    }


    public int[] tfAges(ArrayList<Child>[] childList){
        int dataArr[] = new int[18];
        for(int i=0; i<6; i++){
            for(Child child: childList[i]){
                boolean isMale = child.getSex().equals("Male");
                boolean isFemale = child.getSex().equals("Female");
                if(isMale){
                    dataArr[i*3]++;
                }
                if(isFemale){
                    dataArr[(i*3)+1]++;
                }
            }

            dataArr[(i*3)+2] = dataArr[i*3] + dataArr[(i*3)+1];
        }
        return dataArr;

    }



}
