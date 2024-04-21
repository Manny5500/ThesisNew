package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PMEdit extends AppCompatActivity {
    TextView title, name, age, status, heightText, weightText;
    TextInputEditText height, weight;

    double heightVal, weightVal;
    Button save;

    FirebaseFirestore db;
    ArrayList<String> statusdb;
    String[] statusInd;
    ArrayList<String> statusIndFor;

    ChildHLogic childHLogic;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmedit);

        childHLogic = (ChildHLogic) getIntent().getSerializableExtra("ChildHLogic");
        title = findViewById(R.id.title);
        name = findViewById(R.id.Name);
        age = findViewById(R.id.Age);
        status = findViewById(R.id.Status);
        height = findViewById(R.id.textHeight);
        weight = findViewById(R.id.textWeight);
        save = findViewById(R.id.btnEdit);
        heightText = findViewById(R.id.Height);
        weightText = findViewById(R.id.Weight);
        db = FirebaseFirestore.getInstance();

        type  = getIntent().getStringExtra("Type");
        if(type.equals("Parent")){
            height.setVisibility(View.GONE);
            weight.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
        }
        if(type.equals("BNS")){
            heightText.setVisibility(View.GONE);
            weightText.setVisibility(View.GONE);
        }

        setInitialValue();
        saveEvent();

    }
    private void setInitialValue(){
        String statusList = "Status : \n";
        for(int i=0; i<childHLogic.getStatusProgress().size(); i++){
            if(i==childHLogic.getStatusProgress().size()-1){
                statusList = statusList + "\t" + childHLogic.getStatusProgress().get(i);
            }else{
                statusList = statusList + "\t" + childHLogic.getStatusProgress().get(i) + "\n";
            }
        }

        //for converting the date
        String dateRaw = childHLogic.getId();
        int month = Integer.parseInt(dateRaw.substring(4, 6));
        String monthName = new DateFormatSymbols().getMonths()[month - 1];
        String dateVal = monthName + " " + dateRaw.substring(6, 8) + ", " + dateRaw.substring(0, 4);

        int ageVal = childHLogic.calcuMD();
        String hVN = String.valueOf(childHLogic.getHeight());
        String wVN = String.valueOf(childHLogic.getWeight());

        age.setText("Age: " + ageVal + " mos.");
        name.setText("Name: " + childHLogic.getFullname());
        title.setText(dateVal);
        status.setText(statusList);
        height.setText(hVN);
        weight.setText(wVN);
        heightText.setText("Height " + hVN + " cm");
        weightText.setText("Weight " + wVN + " kg");
        statusIndFor = new ArrayList<>();


    }


    private void saveEvent(){

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heightVal = Double.parseDouble(height.getText().toString());
                weightVal = Double.parseDouble(weight.getText().toString());
                statusdb = childHLogic.getStatusProgressUI(weightVal, heightVal, PMEdit.this);
                statusInd = childHLogic.getStatusProgressInd(weightVal, heightVal, PMEdit.this);

                for(String status: statusInd){
                    statusIndFor.add(status);
                }



                databaseEdit();
            }
        });

    }

    private void databaseEdit(){



        DocumentReference childDocRef = db.collection("children_historical").document(childHLogic.fullname);
        CollectionReference dateExaminedDocRef = childDocRef.collection("dates");
        DocumentReference specificDate = dateExaminedDocRef.document(childHLogic.getId());

        specificDate.update(setChildData()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                App.EditFlag = 1;
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private Map<String, Object> setChildData(){
        Map<String,Object> childData = new HashMap<>();
        childData.put("height", heightVal);
        childData.put("weight", weightVal);
        childData.put("statusdb", statusdb);
        childData.put("status", statusIndFor);
        return  childData;
    }
}