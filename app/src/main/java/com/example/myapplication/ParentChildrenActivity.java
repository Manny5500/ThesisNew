package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class ParentChildrenActivity extends AppCompatActivity {
    private String gmail;
    private FirebaseFirestore db;
    private ArrayList<Child> childrenList;
    private ArrayList<Child> validateChildList;
    private int currentIndex = 0;
    TextView childName, age, status,  textRecommendations,
            textHeight, textWeight, viewProgress;
    String dateString;
    int monthdiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_children);

        db = FirebaseFirestore.getInstance();
        childrenList = new ArrayList<>();
        childName = findViewById(R.id.textChildName);
        age = findViewById(R.id.textAge);
        status = findViewById(R.id.textStatus);
        textRecommendations = findViewById(R.id.textRecommendations);
        textHeight = findViewById(R.id.textHeight);
        textWeight = findViewById(R.id.textWeight);
        viewProgress = findViewById(R.id.viewProgress);



        validateChildList = new ArrayList<>();
        displayChildData();


    }



    private void displayChildData() {
        Child child = App.child;
        getTempEmail(child);

        childName.setText(child.getChildFirstName()+" " + child.getChildLastName());
        dateString = child.getBirthDate();
        textHeight.setText(""+child.getHeight() + " cm");
        textWeight.setText(""+child.getWeight() + " kg");
        textHeight.setTextColor(Color.parseColor("#000000"));
        textWeight.setTextColor(Color.parseColor("#000000"));
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(dateString);
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        } else {
        }

        if(monthdiff == 1){
            age.setText(String.valueOf(monthdiff)+ " " + "month");
        }else{
            age.setText(String.valueOf(monthdiff)+ " " + "months");
        }

        viewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentChildrenActivity.this, ProgressMonitoringBNS.class);
                intent.putExtra("Child", child);
                intent.putExtra("Type", "Parent");
                startActivity(intent);

                /*
                App.child = child;
                startActivity(new Intent(requireContext(), ProgressMonitoring.class));*/
            }
        });

        getChildH(child.getChildFirstName()+" "+child.getChildMiddleName()+" "+child.getChildLastName());

        ArrayList<String> statusList = child.getStatusdb();
        Set<String> recommendation = new HashSet<>();
        StringBuilder statusStringBuilder = new StringBuilder();
        StringBuilder statusStringBuilder2 = new StringBuilder();
        for (String status : statusList) {
            statusStringBuilder.append(status).append("\n");
        }

        recommendation = FindStatusWFA.Recommendations(statusList,monthdiff);

        if(String.valueOf(child.getForfeeding()).equals("Yes") && monthdiff>=24){
            recommendation.add("Your child is part of the feeding program.");
            showRecoDialog("feeding");
        }

        for (String recommend: recommendation){
            if (!recommend.endsWith("\n")) {
                statusStringBuilder2.append("*\t").append(recommend).append("\n\n");
            } else {
                statusStringBuilder2.append("*\t").append(recommend);
            }
        }
        status.setText(statusStringBuilder.toString());
        textRecommendations.setText(statusStringBuilder2.toString());
    }




    public void showRecoDialog(String type){
        final Dialog dialog = new Dialog(ParentChildrenActivity.this);
        dialog.setContentView(R.layout.reco_dialog);

        String message = "";

        ConstraintLayout upPanel = dialog.findViewById(R.id.panelUp);
        ImageView imgAct = dialog.findViewById(R.id.imgAct);
        TextView statusText = dialog.findViewById(R.id.status);
        ImageView imgReal = dialog.findViewById(R.id.imgReal);
        if(type.equals("feeding")){
            message = "Your child is qualified to\n Feeding Program";
            imgReal.setImageResource(R.drawable.diet);
            imgAct.setColorFilter(Color.parseColor("#FFEBC3"));
        }


        statusText.setText(message);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    public void getChildH(String fullname){
        db.collection("children_historical")
                .document(fullname)
                .collection("dates")
                .orderBy("dateid", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ChildH> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ChildH childh =document.toObject(ChildH.class);
                                childh.setId(document.getId());
                                arrayList.add(childh);
                            }
                            if(arrayList.size()>1){
                                if(arrayList.get(0).getHeight()>arrayList.get(1).getHeight()){
                                    textHeight.setText(""+arrayList.get(0).getHeight() + " cm" + "(Increased)");
                                    textHeight.setTextColor(Color.parseColor("#008000"));
                                } else if(arrayList.get(0).getHeight()<arrayList.get(1).getHeight()){
                                    textHeight.setText(""+arrayList.get(0).getHeight() + " cm" + "(Decreased)");
                                    textHeight.setTextColor(Color.parseColor("#FF0000"));
                                } else if(arrayList.get(0).getHeight()==arrayList.get(1).getHeight()){
                                    textHeight.setText(""+arrayList.get(0).getHeight() + " cm" + "(Same)");
                                }

                                if(arrayList.get(0).getWeight()>arrayList.get(1).getWeight()){
                                    textWeight.setText(""+arrayList.get(0).getWeight()+ " kg" + "(Increased)");
                                } else if(arrayList.get(0).getWeight()<arrayList.get(1).getWeight()){
                                    textWeight.setText(""+arrayList.get(0).getWeight()+ " kg" + "(Decreased)");
                                } else if(arrayList.get(0).getWeight()==arrayList.get(1).getWeight()){
                                    textWeight.setText(""+arrayList.get(0).getWeight()+ " kg" + "(Same)");
                                }
                            }
                        }else {

                        }
                    }
                });

    }

    private void showYesNoDialog(Child child) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentChildrenActivity.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Is this child related to parent/guardian");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                keepAsMyChildren(child);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeAsMyChildren(child);
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void validateChildren(Child child, TempEmail tempEmail){
        boolean cMN = !child.getChildMiddleName().equals(tempEmail.getParentMiddleName());
        boolean cLN = !child.getChildLastName().equals(tempEmail.getParentLastName());
        String cRelN = child.getRelated();
        if(cRelN==null){
            cRelN = "No";
        }
        boolean cRel = !cRelN.equals("Yes");
        if( (cMN || cLN) && cRel){
            showYesNoDialog(child);
        }

    }

    private void getTempEmail(Child child) {
        db.collection("tempEmail").document(App.child.getGmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TempEmail tempEmail = documentSnapshot.toObject(TempEmail.class);
                    tempEmail.setGmail(documentSnapshot.getId());
                    validateChildren(child, tempEmail);
                } else {

                }
            }
        });
    }

    private void removeAsMyChildren(Child child){
        CollectionReference collectionRef = db.collection("children");

        Query query = collectionRef.whereEqualTo("childFirstName", child.getChildFirstName())
                .whereEqualTo("childMiddleName", child.getChildMiddleName())
                .whereEqualTo("childLastName", child.getChildLastName())
                .whereEqualTo("gmail", gmail );

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().update(
                                "parentFirstName", "N/A",
                                "parentMiddleName", "N/A",
                                "parentLastName", "N/A",
                                "gmail", "N/A");
                    }

                } else {

                }
            }
        });

    }

    private void keepAsMyChildren(Child child){
        CollectionReference collectionRef = db.collection("children");

        Query query = collectionRef.whereEqualTo("childFirstName", child.getChildFirstName())
                .whereEqualTo("childMiddleName", child.getChildMiddleName())
                .whereEqualTo("childLastName", child.getChildLastName())
                .whereEqualTo("gmail", gmail );

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().update(
                                "related", "Yes");
                    }
                } else {

                }
            }
        });
    }


}