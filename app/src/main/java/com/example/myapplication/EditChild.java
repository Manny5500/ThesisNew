package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EditChild extends AppCompatActivity {


    TextInputEditText childFirstName, childMiddleName, childLastName,
            parentFirstName, parentMiddleName, parentLastName,
            gmail, houseNumber, height, weight, bdate, expectedDate;
    MaterialAutoCompleteTextView barangayAC, sexAC,  sitio;
    Button edit;
    private FirebaseFirestore db;
    String  dateString;
    private int   monthdiff;
    private double height_true_val, weight_true_val;
    String childFirstNameValue, childMiddleNameValue, childLastNameValue,
            parentFirstNameValue, parentMiddleNameValue, parentLastNameValue,
            gmailValue, houseNumberValue, bdateValue, expectedDateValue,
            sexACValue,  barangayACValue, heightValue, weightValue, sitioVal;

    ArrayList<String> statusdb;
    ArrayList<String> status = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);


        String[] sex = getResources().getStringArray(R.array.sex);
        String[] belongs = getResources().getStringArray(R.array.yes_or_no);

        db = FirebaseFirestore.getInstance();

        childFirstName = findViewById(R.id.textChildfirstName);
        childMiddleName = findViewById(R.id.textChildMiddleName);
        childLastName = findViewById(R.id.textChildLastName);
        parentFirstName = findViewById(R.id.textParentFirstName);
        parentMiddleName = findViewById(R.id.textParentMiddleName);
        parentLastName = findViewById(R.id.textParentLastName);
        gmail = findViewById(R.id.textGmail);
        houseNumber = findViewById(R.id.textHouseNumber);
        height = findViewById(R.id.textHeight);
        weight = findViewById(R.id.textWeight);
        bdate = findViewById(R.id.textBdate);
        expectedDate = findViewById(R.id.textExpectedDate);
        sexAC = findViewById(R.id.textPregnant);
        edit = findViewById(R.id.btnEdit);
        sitio  = findViewById(R.id.spinnerSitio);

        gmail.setText(App.child.getGmail());
        houseNumber.setText(App.child.getHouseNumber());
        height.setText(String.valueOf(App.child.getHeight()));
        weight.setText(String.valueOf(App.child.getWeight()));

        childFirstName.setText(App.child.getChildFirstName());
        childMiddleName.setText(App.child.getChildMiddleName());
        childLastName.setText(App.child.getChildLastName());
        parentFirstName.setText(App.child.getParentFirstName());
        parentMiddleName.setText(App.child.getParentMiddleName());
        parentLastName.setText(App.child.getParentLastName());


        barangayACValue = App.child.getBarangay();
        sexAC.setText(App.child.getSex());
        bdate.setText(App.child.getBirthDate());
        expectedDate.setText(App.child.getExpectedDate());
        sitio.setText(App.child.getSitio());


        FormUtils.setAdapter(sex, sexAC, this);
        FormUtils.setAdapter(SitioUtils.getSitioList(barangayACValue),sitio,this);
        FormUtils.dateClicked(bdate,this);
        FormUtils.dateClicked(expectedDate, this);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMonthdiff();
                setAllTextInputData();
                String belongACValue = "No";
                boolean isFormValid = FormUtils.validateForm(childFirstNameValue, childMiddleNameValue, childLastNameValue,
                        parentFirstNameValue, parentMiddleNameValue, parentLastNameValue,
                        gmailValue, houseNumberValue, bdateValue, expectedDateValue,
                        sexACValue, belongACValue, heightValue, weightValue, monthdiff, sitioVal, EditChild.this);

                if (isFormValid) {
                    if(DateParser.getMonthYear().equals(App.child.getMonthAdded()))
                        updateFirestore(createMap());
                    else
                        addNewFirestore(createMap());
                }else {
                    Toast.makeText(EditChild.this, "Please fill out all fields and provide valid information", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void removeFirestoreData(){

        CollectionReference collectionRef = db.collection("children");

        Query query = collectionRef.whereEqualTo("childFirstName", childFirstNameValue)
                .whereEqualTo("childMiddleName", childMiddleNameValue)
                .whereEqualTo("childLastName", childLastNameValue)
                .whereEqualTo("gmail", gmailValue );

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                    finish();

                } else {

                }
            }
        });

    }
    private void setMonthdiff(){
        dateString = bdate.getText().toString();
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(dateString);

        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        } else {
            Toast.makeText(EditChild.this, "Failed to parse the date", Toast.LENGTH_SHORT).show();
        }
    }
    private void setAllTextInputData(){
        childFirstNameValue = childFirstName.getText().toString().trim();
        childMiddleNameValue = childMiddleName.getText().toString().trim();
        childLastNameValue = childLastName.getText().toString().trim();
        parentFirstNameValue = parentFirstName.getText().toString().trim();
        parentMiddleNameValue = parentMiddleName.getText().toString().trim();
        parentLastNameValue = parentLastName.getText().toString().trim();
        gmailValue = gmail.getText().toString().trim();
        houseNumberValue = houseNumber.getText().toString().trim();
        bdateValue = bdate.getText().toString().trim();
        expectedDateValue = expectedDate.getText().toString().trim();
        sexACValue = sexAC.getText().toString().trim();
        heightValue = height.getText().toString().trim();
        weightValue = weight.getText().toString().trim();
        sitioVal = sitio.getText().toString().trim();

    }
    private Map<String, Object> createMap(){
        setStatuses();
        Map<String, Object> user = new HashMap<>();
        user.put("childFirstName", childFirstNameValue);
        user.put("childMiddleName", childMiddleNameValue);
        user.put("childLastName", childLastNameValue);
        user.put("parentFirstName", parentFirstNameValue);
        user.put("parentMiddleName", parentMiddleNameValue);
        user.put("parentLastName", parentLastNameValue);
        user.put("gmail", gmailValue);
        user.put("houseNumber", houseNumberValue);
        user.put("height", height_true_val);
        user.put("weight", weight_true_val);
        user.put("birthDate", bdateValue);
        user.put("barangay", barangayACValue);
        user.put("sitio", sitioVal);
        user.put("sex", sexACValue);
        user.put("expectedDate", expectedDateValue);
        user.put("statusdb", statusdb);
        user.put("status", status);
        user.put("monthlyIncome", App.child.getMonthlyIncome());
        user.put("dateAdded", DateParser.getCurrentTimeStamp());
        user.put("monthAdded", DateParser.getMonthYear());
        return user;
    }

    private void setStatuses(){
        height_true_val = Double.parseDouble(heightValue);
        weight_true_val = Double.parseDouble(weightValue);
        statusdb = FindStatusWFA.CalculateMalnourished(EditChild.this, monthdiff, weight_true_val, height_true_val, sexACValue);
        String[] individualTest = FindStatusWFA.individualTest(EditChild.this, monthdiff, weight_true_val, height_true_val, sexACValue);

        for(String status_element: individualTest){
            status.add(status_element);
        }
    }
    private void addNewFirestore(Map<String, Object> user){
        db.collection("children").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(EditChild.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                savetoHistory();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditChild.this, "Failed to add user", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void updateFirestore(Map<String, Object> user){
        db.collection("children").document(App.child.getId()).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                savetoHistory();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditChild.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void savetoHistory(){
        String child_full_name = childFirstNameValue + " " + childMiddleNameValue + " " + childLastNameValue;
        String date_now = FormUtils.getCurrentDate();
        DocumentReference childDocRef = db.collection("children_historical").document(child_full_name);
        DocumentReference dateExaminedDocRef = childDocRef.collection("dates").document(date_now);
        Map<String,Object> childData = new HashMap<>();
        childData.put("height", height_true_val);
        childData.put("weight", weight_true_val);
        childData.put("dateid", Integer.parseInt(date_now));
        childData.put("status", status);
        childData.put("statusdb", statusdb);

        dateExaminedDocRef.set(childData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditChild.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                            App.AddFlag = 1;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 100);
                        } else {
                            Toast.makeText(EditChild.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}