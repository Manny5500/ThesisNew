package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class fragmentParentAddInfo extends Fragment {

    View view;
    private String userid, hasPregnantVal, employment1Val, employment2Val,
            monthlyIncomeVal, familySizeString, famStats;
    Button register;
    private int   familySizeVal = 0;
    TextInputEditText familySize;
    MaterialAutoCompleteTextView employment1, employment2, monthlyIncome, hasPregnant;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_parent_add_info, container, false);
        userid = ((ParentActivity)getActivity()).userid;
        familySize = view.findViewById(R.id.textFamilySize);
        employment1 = view.findViewById(R.id.textEmployment1);
        employment2 = view.findViewById(R.id.textEmployment2);
        monthlyIncome = view.findViewById(R.id.textMonthlyIncome);
        hasPregnant = view.findViewById(R.id.textPregnant);
        register = view.findViewById(R.id.buttonRegister);

        String[] monthlyIncomeArray = {"less than 9,100", "9,101 to 18,200", "18,201 to 36,400", "36,401 to 63,700",
        "63,701 to 109,200", "109,201 to 182,000", "Above 182,000"};

        String[] employmentStatusArray = {"Full Time", "Part Time", "Self-Employed", "Unemployed"};
        String[] hasPregnantArray = {"Yes", "No"};

        FormUtils.setAdapter(monthlyIncomeArray, monthlyIncome, requireContext());
        FormUtils.setAdapter(employmentStatusArray, employment1, requireContext());
        FormUtils.setAdapter(employmentStatusArray, employment2, requireContext());
        FormUtils.setAdapter(hasPregnantArray, hasPregnant, requireContext());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                familySizeString = familySize.getText().toString();
                if(!familySizeString.isEmpty()) {
                    familySizeVal = Integer.parseInt(familySize.getText().toString());
                }
                employment1Val = employment1.getText().toString();
                employment2Val = employment2.getText().toString();
                monthlyIncomeVal = monthlyIncome.getText().toString();
                hasPregnantVal = hasPregnant.getText().toString();
                famStats = "";
                if(familySizeVal>1){
                    famStats = getFamilyStatus();
                }
                boolean hasfamStats = famStats.equals("");

                boolean isFormValid = FormUtils.validateForm_ADIN(employment1Val, employment2Val, monthlyIncomeVal, hasPregnantVal, requireContext());
                if(isFormValid && !hasfamStats){
                    addDatatoFirestore();
                }
            }
        });
        return view;
    }

    private void addDatatoFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("familySize", familySizeVal);
        user.put("employment1", employment1Val);
        user.put("employment2", employment2Val);
        user.put("monthlyIncome", monthlyIncomeVal);
        user.put("hasPregnant", hasPregnantVal);
        user.put("famStats", famStats);
        db.collection("users")
                .document(userid) // Use user's UID as document ID
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Data added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        //progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Failed to add data to Firestore.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String getFamilyStatus(){
        String familyStatus;
        int[] index = {0, 0, 0, 0, 0};

        if(familySizeVal>1){
            if(familySizeVal>4 && familySizeVal<7){
                index[0] = 2;
            } else if (familySizeVal>7 && familySizeVal<10) {
                index[0] = 3;
            } else if (familySizeVal<4){
                index[0] = 1;
            } else if (familySizeVal>10 && familySizeVal<13) {
                index[0] = 4;
            } else if (familySizeVal>13 && familySizeVal<16) {
                index[0] = 5;
            } else if (familySizeVal>16) {
                index[0] = 6;
            }
        }

        if(!monthlyIncomeVal.isEmpty()){
            if(monthlyIncomeVal.equals("less than 9,100")){
                index[3] = 7;
            } else if (monthlyIncomeVal.equals("9,101 to 18,200")){
                index[3] = 6;
            } else if (monthlyIncomeVal.equals("18,201 to 36,400")) {
                index[3] = 5;
            } else if (monthlyIncomeVal.equals( "36,401 to 63,700")){
                index[3] = 4;
            } else if (monthlyIncomeVal.equals("63,701 to 109,200")){
                index[3] = 3;
            } else if (monthlyIncomeVal.equals("109,201 to 182,000")){
                index[3] = 2;
            } else if (monthlyIncomeVal.equals("Above 182,000")){
                index[3] = 1;
            }
        }

        if(!employment1Val.isEmpty()){
            if(employment1Val.equals("Full Time")){
                index[1] = 1;
            } else if (employment1Val.equals("Self-Employed")){
                index[1] = 2;
            } else if (employment1Val.equals("Part Time")) {
                index[1] = 3;
            } else if (employment1Val.equals( "Unemployed")){
                index[1] = 4;
            }
        }

        if(!employment2Val.isEmpty()){
            if(employment2Val.equals("Full Time")){
                index[2] = 1;
            } else if (employment2Val.equals("Self-Employed")){
                index[2] = 2;
            } else if (employment2Val.equals("Part Time")) {
                index[2] = 3;
            } else if (employment2Val.equals( "Unemployed")){
                index[2] = 4;
            }
        }

        if(!hasPregnantVal.isEmpty()){
            if(hasPregnantVal.equals("Yes")){
                index[4] = 2;
            } else if (hasPregnantVal.equals("No")) {
                index[4] = 1;
            }
        }

        double sum = 0;
        double ave = 0;
        double weighted_index = 0;
        for(int i = 0; i<index.length; i++){
            if(i==3){
                weighted_index = 0.4 * index[i];
            } else if (i==2) {
                weighted_index = 0.15 * index[i];
            }else if (i==1){
                weighted_index = 0.15 * index[i];
            }else if (i==0){
                weighted_index = 0.2 * index[i];
            }else if (i==4){
                weighted_index = 0.1 * index[i];
            }
            sum = sum + weighted_index;
        }
        ave = sum/5;
        familyStatus = familyStatus(ave);
        Toast.makeText(requireContext(), "" + ave, Toast.LENGTH_SHORT).show();
        return familyStatus;
    }

    private String familyStatus(double ave){
        String status="";
        if(ave>=0.2 && ave <0.52){
            status = "Low Risk";
        }if(ave>=0.52 && ave<0.84){
            status = "Medium Risk";
        } if(ave>=0.84 && ave< 1.17){
            status = "High Risk";
        }
        return status;
    }

}