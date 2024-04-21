package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FindStatusWFA {

    public String StatusFinder(int age, double weight,
                               double[] nsd3, double[] nsd2,
                               double[] nsd1, double[] median,
                               double[] sd1, double[] sd2, double[] sd3) {

        String status = "";

        for (int i = 0; i <= 59; i++) {
            if (weight < nsd2[age] && weight >= nsd3[age]) {
                status = "Underweight";
                break;
            } else if (weight < nsd3[age]) {
                status = "Severe Underweight";
                break;
            } else if(weight > sd2[age]){
                status = "Overweight";
                break;
            }
        }
        return status;
    }

    public String StatusFinder_Stunted(int age, double height,
                               double[] nsd3, double[] nsd2,
                               double[] nsd1, double[] median,
                               double[] sd1, double[] sd2, double[] sd3) {

        String status = "";

        for (int i = 0; i <= nsd1.length; i++) {
            if (height < nsd2[age] && height >= nsd3[age]) {
                status = "Stunted";
                break;
            } else if (height < nsd3[age]) {
                status = "Severe Stunted";
                break;
            } else if (height > sd2[age]) {
                status = "Tall";
                break;
            }
        }
        return status;
    }

    public String StatusFinder_Wasted(double weight, double height,
                                       double[] nsd3, double[] nsd2,
                                       double[] nsd1, double[] median,
                                       double[] sd1, double[] sd2, double[] sd3, int position) {

        String status = "";

        for (int i = 0; i <= nsd1.length; i++) {
            if (weight < nsd2[position] && weight >= nsd3[position]) {
                status = "Wasted";
                break;
            } else if (weight < nsd3[position]) {
                status = "Severe Wasted";
                break;
            } else if (weight > sd2[position] && weight <= sd3[position]) {
                status = "Overweight";
                break;
            } else if (weight > sd3[position]) {
                status = "Obese";
                break;
            }
        }
        return status;
    }


    public static ArrayList<String> CalculateMalnourished(Context context, int age, double weight, double height, String sex){
        ArrayList<String> status = new ArrayList<>();
        ArrayList<String> statusdb = new ArrayList<>();

        if(sex.equals("Male") && age<60 && age>= 0){
            WFA_Boys wfa = new WFA_Boys();
            if(!wfa.WFA_Boys_M(age,weight).equals("")){
                status.add(wfa.WFA_Boys_M(age, weight));
            }
            if(age>=0 && age<24)
            {
                LFA_Boys lfaBoys = new LFA_Boys();
                if(!lfaBoys.LFA_Boys_M(height,age).equals("")){
                    status.add(lfaBoys.LFA_Boys_M(height, age));
                }

                WFL_Boys wflBoys = new WFL_Boys();
                if(!wflBoys.WFL_Boys_M(weight,height).equals("")){
                    status.add(wflBoys.WFL_Boys_M(weight, height));
                }

            } else if (age>= 24 && age <60) {
                HFA_Boys hfaBoys = new HFA_Boys();
                if(!hfaBoys.HFA_Boys_M(height, age).equals("")){
                    status.add(hfaBoys.HFA_Boys_M(height, age));
                }

                WFH_Boys wfhBoys = new WFH_Boys();
                if(!wfhBoys.WFH_Boys_M(weight, height).equals("")){
                    status.add(wfhBoys.WFH_Boys_M(weight, height));
                }
            }else{
                Toast.makeText(context, "Invalid age", Toast.LENGTH_SHORT).show();
            }
            if(status.isEmpty()){
                status.add("Normal");
            }
            int count=0;
            //previously may obese dito
            for(String cstats: status){
                if(cstats.equals("Overweight") ||
                cstats.equals("Underweight") || cstats.equals("Severe Underweight")){
                    count++;
                }
            }
            if(count>1){
                if(status.get(0).equals("Overweight") || status.get(0).equals("Underweight")){
                    status.remove(0);
                }
            }

            statusdb = showDialogMalnourished(context, status);

        } else if (sex.equals("Female") && age<60 && age>=0) {
            WFA_Girls wfag = new WFA_Girls();
            if(!wfag.WFA_Girls_M(age,weight).equals("")){
                status.add(wfag.WFA_Girls_M(age, weight));
            }
            if(age>=0 && age<24)
            {
                LFA_Girls lfaGirls = new LFA_Girls();
                if(!lfaGirls.LFA_Girls_M(height,age).equals("")){
                    status.add(lfaGirls.LFA_Girls_M(height, age));
                }
                WFL_Girls wflGirls = new WFL_Girls();
                if(!wflGirls.WFL_Girls_M(weight, height).equals("")){
                    status.add(wflGirls.WFL_Girls_M(weight, height));
                }

            } else if (age>= 24 && age <60) {
                HFA_Girls  hfaGirls = new HFA_Girls();
                if(!hfaGirls.HFA_Girls_M(height,age).equals("")){
                    status.add(hfaGirls.HFA_Girls_M(height, age));
                }
                WFH_Girls wfhGirls = new WFH_Girls();
                if(!wfhGirls.WFH_Girls_M(weight, height).equals("")){
                    status.add(wfhGirls.WFH_Girls_M(weight, height));
                }
            }else{
                Toast.makeText(context, "Invalid age", Toast.LENGTH_SHORT).show();
            }
            if(status.isEmpty()||status.get(0).equals("")){
                status.add("Normal");
            }
            int count=0;
            for(String cstats: status){
                if(cstats.equals("Overweight") || cstats.equals("Obese")||
                        cstats.equals("Underweight") || cstats.equals("Severe Underweight")){
                    count++;
                }
            }
            if(count>1){
                if(status.get(0).equals("Overweight") || status.get(0).equals("Underweight")){
                    status.remove(0);
                }
            }
            statusdb = showDialogMalnourished(context, status);

        } else {
            Toast.makeText(context, "Invalid ages", Toast.LENGTH_SHORT).show();
        }

        return statusdb;
    }

    public static ArrayList<String> CalculateMalnourishedProgress( int age, double weight, double height, String sex){
        ArrayList<String> status = new ArrayList<>();
        ArrayList<String> statusdb = new ArrayList<>();

        if(sex.equals("Male") && age<60 && age>= 0){
            WFA_Boys wfa = new WFA_Boys();
            if(!wfa.WFA_Boys_M(age,weight).equals("")){
                status.add(wfa.WFA_Boys_M(age, weight));
            }
            if(age>=0 && age<24)
            {
                LFA_Boys lfaBoys = new LFA_Boys();
                if(!lfaBoys.LFA_Boys_M(height,age).equals("")){
                    status.add(lfaBoys.LFA_Boys_M(height, age));
                }

                WFL_Boys wflBoys = new WFL_Boys();
                if(!wflBoys.WFL_Boys_M(weight,height).equals("")){
                    status.add(wflBoys.WFL_Boys_M(weight, height));
                }

            } else if (age>= 24 && age <60) {
                HFA_Boys hfaBoys = new HFA_Boys();
                if(!hfaBoys.HFA_Boys_M(height, age).equals("")){
                    status.add(hfaBoys.HFA_Boys_M(height, age));
                }

                WFH_Boys wfhBoys = new WFH_Boys();
                if(!wfhBoys.WFH_Boys_M(weight, height).equals("")){
                    status.add(wfhBoys.WFH_Boys_M(weight, height));
                }
            }else{

            }
            if(status.isEmpty()){
                status.add("Normal");
            }
            int count=0;
            //previously may obese dito
            for(String cstats: status){
                if(cstats.equals("Overweight") ||
                        cstats.equals("Underweight") || cstats.equals("Severe Underweight")){
                    count++;
                }
            }
            if(count>1){
                if(status.get(0).equals("Overweight") || status.get(0).equals("Underweight")){
                    status.remove(0);
                }
            }

            statusdb = showMalnourishedPM( status);

        } else if (sex.equals("Female") && age<60 && age>=0) {
            WFA_Girls wfag = new WFA_Girls();
            if(!wfag.WFA_Girls_M(age,weight).equals("")){
                status.add(wfag.WFA_Girls_M(age, weight));
            }
            if(age>=0 && age<24)
            {
                LFA_Girls lfaGirls = new LFA_Girls();
                if(!lfaGirls.LFA_Girls_M(height,age).equals("")){
                    status.add(lfaGirls.LFA_Girls_M(height, age));
                }
                WFL_Girls wflGirls = new WFL_Girls();
                if(!wflGirls.WFL_Girls_M(weight, height).equals("")){
                    status.add(wflGirls.WFL_Girls_M(weight, height));
                }

            } else if (age>= 24 && age <60) {
                HFA_Girls  hfaGirls = new HFA_Girls();
                if(!hfaGirls.HFA_Girls_M(height,age).equals("")){
                    status.add(hfaGirls.HFA_Girls_M(height, age));
                }
                WFH_Girls wfhGirls = new WFH_Girls();
                if(!wfhGirls.WFH_Girls_M(weight, height).equals("")){
                    status.add(wfhGirls.WFH_Girls_M(weight, height));
                }
            }else{
            }
            if(status.isEmpty()||status.get(0).equals("")){
                status.add("Normal");
            }
            int count=0;
            for(String cstats: status){
                if(cstats.equals("Overweight") || cstats.equals("Obese")||
                        cstats.equals("Underweight") || cstats.equals("Severe Underweight")){
                    count++;
                }
            }
            if(count>1){
                if(status.get(0).equals("Overweight") || status.get(0).equals("Underweight")){
                    status.remove(0);
                }
            }
            statusdb = showMalnourishedPM(status);

        } else {
        }

        return statusdb;
    }


    public static String[] individualTest(Context context, int age, double weight, double height, String sex){
        String[] status = {"Normal","Normal","Normal"};
        if(sex.equals("Male") && age<60 && age>= 0){
            WFA_Boys wfa = new WFA_Boys();
            if(!wfa.WFA_Boys_M(age,weight).equals("")){
                status[0] = wfa.WFA_Boys_M(age, weight);
            }
            if(age>=0 && age<24)
            {
                LFA_Boys lfaBoys = new LFA_Boys();
                if(!lfaBoys.LFA_Boys_M(height,age).equals("")){
                    status[1] = lfaBoys.LFA_Boys_M(height, age);
                }

                WFL_Boys wflBoys = new WFL_Boys();
                if(!wflBoys.WFL_Boys_M(weight,height).equals("")){
                    status[2] =wflBoys.WFL_Boys_M(weight, height);
                }

            } else if (age>= 24 && age <60) {
                HFA_Boys hfaBoys = new HFA_Boys();
                if(!hfaBoys.HFA_Boys_M(height, age).equals("")){
                    status[1] = hfaBoys.HFA_Boys_M(height, age);
                }

                WFH_Boys wfhBoys = new WFH_Boys();
                if(!wfhBoys.WFH_Boys_M(weight, height).equals("")){
                    status[2] = wfhBoys.WFH_Boys_M(weight, height);
                }
            }else{
            }

        } else if (sex.equals("Female") && age<60 && age>=0) {
            WFA_Girls wfag = new WFA_Girls();
            if(!wfag.WFA_Girls_M(age,weight).equals("")){
                status[0] = wfag.WFA_Girls_M(age, weight);
            }
            if(age>=0 && age<24)
            {
                LFA_Girls lfaGirls = new LFA_Girls();
                if(!lfaGirls.LFA_Girls_M(height,age).equals("")){
                    status[1] = lfaGirls.LFA_Girls_M(height, age);
                }
                WFL_Girls wflGirls = new WFL_Girls();
                if(!wflGirls.WFL_Girls_M(weight, age).equals("")){
                    status[2] = wflGirls.WFL_Girls_M(weight, height);
                }

            } else if (age>= 24 && age <60) {
                HFA_Girls  hfaGirls = new HFA_Girls();
                if(!hfaGirls.HFA_Girls_M(height,age).equals("")){
                    status[1] = hfaGirls.HFA_Girls_M(height, age);
                }
                WFH_Girls wfhGirls = new WFH_Girls();
                if(!wfhGirls.WFH_Girls_M(weight, height).equals("")){
                   status[2] =  wfhGirls.WFH_Girls_M(weight, height);
                }
            }else{
            }

        } else {
        }

        return status;
    }


    public static ArrayList<String> showDialogMalnourished(Context context, ArrayList<String> status){

        LinkedHashSet<String> setWithoutDuplicates = new LinkedHashSet<>(status);
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);

        String message = "";
        for(String element: listWithoutDuplicates){
            message = message + "\t" + element + "\n";
        }

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.status_dialog);

        ConstraintLayout upPanel = dialog.findViewById(R.id.panelUp);
        ImageView imgAct = dialog.findViewById(R.id.imgAct);
        TextView statusText = dialog.findViewById(R.id.status);

        if(listWithoutDuplicates.size()>1){
            upPanel.setBackgroundColor(Color.parseColor("#FF6174"));
            statusText.setTextSize(25);
            imgAct.setImageResource(R.drawable.warning);
        }else if(listWithoutDuplicates.contains("Normal")){
            upPanel.setBackgroundColor(Color.parseColor("#32BA7B"));
            imgAct.setImageResource(R.drawable.checked);
        } else if (listWithoutDuplicates.size()==1 && !listWithoutDuplicates.contains("Normal")) {
            upPanel.setBackgroundColor(Color.parseColor("#FF914D"));
            imgAct.setImageResource(R.drawable.information__1_);
        }


        statusText.setText(message);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        return listWithoutDuplicates;
    }

    public static ArrayList<String> showMalnourishedPM(ArrayList<String> status){

        LinkedHashSet<String> setWithoutDuplicates = new LinkedHashSet<>(status);
        ArrayList<String> listWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);

        String message = "";
        for(String element: listWithoutDuplicates){
            message = message + "\t" + element + "\n";
        }
        return listWithoutDuplicates;
    }




    public static Set<String> Recommendations(ArrayList<String> statusList, int age){

        Set<String> recommendationSet = new HashSet<>();
        StringBuilder recommendation = new StringBuilder();
        ArrayList<String> statuses = new ArrayList<>();
        //ArrayList<String> recommendationSet = new ArrayList<>();
        statuses.add("Severe Underweight");
        statuses.add("Severe Wasted");
        String[] recommendations = {
                "Exclusive breastfeeding", "Refer to pediatrician",
                "Regularly monitor your child's growth", "Immediate Medical Care",
                "Go to the nearest medical facility", "Emergency Cases",
                "Portion control", "Give nutritious food",
                "Frequent, Balanced Meal", "Refer to pediatrician if necessary",
                "Encourage balanced diet", "Control the food portion",
                "Go to the nearest hospital", "Encourage physical activity"
        };

        for(String status:statusList){
            Boolean isUnderweight = status.equals("Underweight");
            Boolean isSevereUnderweight = status.equals("Severe Underweight");
            Boolean isWasted = status.equals("Wasted");
            Boolean isSevereWasted = status.equals("Severe Wasted");
            Boolean isStunted = status.equals("Stunted");
            Boolean isSevereStunted = status.equals("Severe Stunted");
            Boolean isOverweight = status.equals("Overweight");
            Boolean isObese = status.equals("Obese");
            if(age<=6){
                recommendationSet.add(recommendations[0]);

                if(isUnderweight || isOverweight || isObese){
                    recommendationSet.add(recommendations[1]);
                    recommendationSet.add(recommendations[2]);
                }
                if(isSevereUnderweight||isStunted||isWasted){
                    recommendationSet.add(recommendations[3]);
                    recommendationSet.add(recommendations[4]);
                }
                if(isSevereStunted||isSevereWasted){
                    recommendationSet.add(recommendations[5]);
                    recommendationSet.add(recommendations[3]);
                    recommendationSet.add(recommendations[4]);
                }
            }

            if(age>6 && age<=23){
                if(isUnderweight){
                    recommendationSet.add(recommendations[1]);
                }
                if(isOverweight||isObese){
                    recommendationSet.add(recommendations[6]);
                }
                if(isStunted){
                    recommendationSet.add(recommendations[1]);
                    recommendationSet.add(recommendations[7]);
                }
                if(isSevereUnderweight||isSevereWasted||isSevereStunted||isWasted){
                    recommendationSet.add(recommendations[5]);
                    recommendationSet.add(recommendations[3]);
                    recommendationSet.add(recommendations[4]);
                }
            }
            if(age>=23 && age<=59){
                if(isUnderweight||isStunted){
                    recommendationSet.add(recommendations[7]);
                    recommendationSet.add(recommendations[8]);
                    recommendationSet.add(recommendations[9]);
                }
                if(isOverweight||isObese){
                    recommendationSet.add(recommendations[10]);
                    recommendationSet.add(recommendations[11]);
                }
                
                if(isSevereUnderweight||isSevereWasted||isSevereStunted||isWasted){
                    recommendationSet.add(recommendations[5]);
                    recommendationSet.add(recommendations[3]);
                    recommendationSet.add(recommendations[4]);
                }
            }

        }
        return recommendationSet;
    }
}
