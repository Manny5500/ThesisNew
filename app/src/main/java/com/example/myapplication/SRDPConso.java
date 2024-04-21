package com.example.myapplication;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class SRDPConso {
    ArrayList<Child> childList;

    public  SRDPConso(ArrayList<Child> childList) {
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
        int dataArr[] = new int[26];
        int i=0;
        int j=0;
        for(ArrayList<Child> cL: arrayLists){
            for(Child child: cL){
                String WFAStatus = child.getStatus().get(0);
                String HFAStatus = child.getStatus().get(1);
                String WFHStatus = child.getStatus().get(2);
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
                if(isNormalWFA){
                    dataArr[0+i]++;
                }
                if(isOWWFA){
                    dataArr[1+i]++;
                }
                if(isUW){
                    dataArr[2+i]++;
                }
                if(isSUW){
                    dataArr[3+i]++;
                }
                if(isNormalHFA){
                    dataArr[4+i]++;
                }
                if(isTall){
                    dataArr[5+i]++;
                }
                if(isST){
                    dataArr[6+i]++;
                }
                if(isSST){
                    dataArr[7+i]++;
                }
                if(isNormalWFH){
                    dataArr[8+i]++;
                }
                if(isOWWFH){
                    dataArr[9+i]++;
                }
                if(isOB){
                    dataArr[10+i]++;
                }
                if(isW){
                    dataArr[11+i]++;
                }

                if(isSW){
                    dataArr[12+i]++;
                }

            }
            i=i+13;
            j=j+5;
        }
        return dataArr;
    }

    public String[] getPercentage (int counts[]){
        String [] perList = new String[26];
        int k=0;
        for(int i=0; i<2; i++){
            for(int j=0; j<13; j++){
                float resultAA=0;
                float prevPerAA = 0;
                if(j<4){
                    resultAA = (float) counts[j+k] / getSum(0,4, counts, k);
                } else if (j>3 && j<8) {
                    resultAA = (float) counts[j+k] / getSum(4, 8, counts, k);
                } else if(j>7){
                    resultAA = (float) counts[j+k] / getSum(8, 13, counts, k) ;
                }
                prevPerAA = (resultAA * 100.00f);
                perList[j+k] = String.format("%.2f", prevPerAA) + " %";
            }
            k= k+13;
        }
        return perList;
    }

    public int getSum(int start, int end, int counts[], int k){
        int sum=0;
        for(int l=start; l<end; l++){
            sum = sum + counts[l+k];
        }
        if(sum==0){
            sum = 1;
        }
        return sum;
    }

}
