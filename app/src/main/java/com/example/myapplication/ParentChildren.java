package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ParentChildren extends Fragment {
    View view;

    private String gmail;
    private FirebaseFirestore db;
    private ArrayList<Child> childrenList;
    private ArrayList<Child> validateChildList;
    private int currentIndex = 0;
    TextView childName, age, status, textCounts, textRecommendations,
    textHeight, textWeight, viewProgress;
    String dateString;
    int monthdiff;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FirebaseApp.initializeApp(requireContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parent_children, container, false);
        db = FirebaseFirestore.getInstance();
        childrenList = new ArrayList<>();
        childName = view.findViewById(R.id.textChildName);
        age = view.findViewById(R.id.textAge);
        status = view.findViewById(R.id.textStatus);
        textCounts = view.findViewById(R.id.textCounts);
        textRecommendations = view.findViewById(R.id.textRecommendations);
        textHeight = view.findViewById(R.id.textHeight);
        textWeight = view.findViewById(R.id.textWeight);
        viewProgress = view.findViewById(R.id.viewProgress);

        gmail = ((ParentActivity)getActivity()).email;
        if(!gmail.isEmpty())
            firstFetch();

        validateChildList = new ArrayList<>();

        ImageView nextButton = view.findViewById(R.id.btnnext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < childrenList.size() - 1) {
                    currentIndex++;
                    displayChildData(currentIndex);
                } else {
                    // Handle the case when there are no more children to display
                   // Toast.makeText(requireContext(), "No more children to display", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle back button click
        ImageView backButton = view.findViewById(R.id.btnprev);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayChildData(currentIndex);
                } else {
                    // Handle the case when the user is at the first child
                    //Toast.makeText(requireContext(), "Already at the first child", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void firstFetch(){
        db.collection("children").whereEqualTo("gmail", gmail)
                .orderBy("dateAdded", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String gulayanStatus = "";
                    String feedingStatus = "";
                    ArrayList<Child> arrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Child child = doc.toObject(Child.class);
                        child.setId(doc.getId());
                        arrayList.add(child);

                        if(child.getForgulayan()!=null && child.getForgulayan().equals("Yes"))
                            gulayanStatus = "Yes";
                        if(child.getForfeeding()!=null && child.getForfeeding().equals("Yes"))
                            feedingStatus = "Yes";
                    }



                    childrenList = RemoveDuplicates.removeDuplicates(arrayList);

                    if(task.getResult().size()>0)
                        displayChildData(currentIndex);

                    if(gulayanStatus.equals("Yes"))
                        showRecoDialog("gulayan");

                    if(feedingStatus.equals("Yes"))
                        showRecoDialog("feeding");

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to get all users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayChildData(int index) {
        Child child = childrenList.get(index);
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
            Toast.makeText(requireContext(), "Failed to parse the date", Toast.LENGTH_SHORT).show();
        }

        if(monthdiff == 1){
            age.setText(String.valueOf(monthdiff)+ " " + "month");
        }else{
            age.setText(String.valueOf(monthdiff)+ " " + "months");
        }

        viewProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ProgressMonitoringBNS.class);
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
        }

        for (String recommend: recommendation){
            if (!recommend.endsWith("\n")) {
                statusStringBuilder2.append("*\t").append(recommend).append("\n\n");
            } else {
                statusStringBuilder2.append("*\t").append(recommend);
            }
        }
        textCounts.setText("Child " + String.valueOf(currentIndex+1) + " of " + String.valueOf(childrenList.size()));
        status.setText(statusStringBuilder.toString());
        textRecommendations.setText(statusStringBuilder2.toString());
    }

    private void showDialog(String type){
        if(isAdded() && requireContext() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            if(type.equals("gulayan")) {
                builder.setTitle("Gulayan sa Bakuran")
                        .setMessage("You are qualified to Gulayan sa Bakuran Program");
                builder.setCancelable(true);
            }
            if(type.equals("feeding")){
                builder.setTitle("Feeding Program")
                        .setMessage("You're child is qualified to Program");
                builder.setCancelable(true);
            }
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    public void showRecoDialog(String type){
        if(isAdded() && requireContext() != null){
            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.reco_dialog);

            String message = "";

            ConstraintLayout upPanel = dialog.findViewById(R.id.panelUp);
            ImageView imgAct = dialog.findViewById(R.id.imgAct);
            TextView statusText = dialog.findViewById(R.id.status);
            ImageView imgReal = dialog.findViewById(R.id.imgReal);

            if(type.equals("gulayan")) {
                upPanel.setBackgroundColor(Color.parseColor("#32BA7B"));
                message = "You are qualified to Gulayan\n sa Bakuran Program";
                imgReal.setImageResource(R.drawable.vegetable);
                imgAct.setColorFilter(Color.parseColor("#FFEBC3"));
            }
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
                            Toast.makeText(requireContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void showYesNoDialog(Child child) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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

    private void reloadFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ParentChildren newInstance = new ParentChildren();
        fragmentTransaction.replace(R.id.frameLayout, newInstance);

        fragmentTransaction.commit();
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
        db.collection("tempEmail").document(gmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                    reloadFragment();

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

                    reloadFragment();

                } else {

                }
            }
        });
    }

}
