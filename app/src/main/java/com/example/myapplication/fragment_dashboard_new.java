package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class fragment_dashboard_new extends Fragment {


    MaterialCardView prioView, recoView, reportView;

    TextView examBod, malBod;
    View view;

    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_dashboard_new, container, false);

        prioView = view.findViewById(R.id.prioView);
        recoView = view.findViewById(R.id.recoView);
        reportView = view.findViewById(R.id.reportView);
        examBod = view.findViewById(R.id.examBod);
        malBod = view.findViewById(R.id.malBod);

        db = FirebaseFirestore.getInstance();

        dashboardData();

        prioViewEvent();
        recoViewEvent();
        reportViewEvent();

        return view;
    }

    private void prioViewEvent(){
        prioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ActivityPriority.class);
                startActivity(intent);

            }
        });
    }

    private void recoViewEvent(){
        recoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), RecommendationAdmin.class);
                startActivity(intent);
            }
        });
    }
    private void reportViewEvent(){
        reportView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ReportsSelector.class);
                startActivity(intent);
            }
        });
    }

    public void dashboardData(){
        db.collection("children")
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Child> childrenList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                childrenList.add(child);
                            }


                            int count_P = 0, count_M = 0;
                            childrenList = RemoveDuplicates.removeDuplicates(childrenList);
                            for(Child child: childrenList){
                                Boolean isUnderWeight = child.getStatusdb().contains("Underweight");
                                Boolean isNormal = child.getStatusdb().contains("Normal");
                                Boolean isSevereUnderweight = child.getStatusdb().contains("Severe Underweight");
                                Boolean isWasted = child.getStatusdb().contains("Wasted");
                                Boolean isSevereWasted = child.getStatusdb().contains("Severe Wasted");
                                Boolean isStunted = child.getStatusdb().contains("Stunted");
                                Boolean isSevereStunted = child.getStatusdb().contains("Severe Stunted");
                                Boolean isOverweight = child.getStatusdb().contains("Overweight");
                                Boolean isObese = child.getStatusdb().contains("Obese");
                                if(isSevereStunted||isSevereWasted||isSevereUnderweight||isObese){
                                    count_P++;
                                }
                                if (!isNormal){
                                    count_M++;
                                }
                            }
                            examBod.setText(String.valueOf(task.getResult().size()));
                            malBod.setText(String.valueOf(count_M));
                        } else {
                            Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}