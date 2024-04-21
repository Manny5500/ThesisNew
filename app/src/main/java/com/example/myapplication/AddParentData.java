package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddParentData extends AppCompatActivity {
    TextInputEditText parentFirstName, parentMiddleName, parentLastName,
            gmail, houseNumber, contact;
    TextView pDExist;


    MaterialAutoCompleteTextView  incomeAC, sitioAC;

    Button next;

    String pFVal, pMVal, pLVal, gMVal, hNVal, bEVal, iNVaL, sIVal, contactVal;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parent_data);
        db = FirebaseFirestore.getInstance();
        parentFirstName = findViewById(R.id.textParentFirstName);
        parentMiddleName = findViewById(R.id.textParentMiddleName);
        parentLastName = findViewById(R.id.textParentLastName);
        gmail = findViewById(R.id.textGmail);
        houseNumber = findViewById(R.id.textHouseNumber);
        incomeAC = findViewById(R.id.textIncome);
        sitioAC = findViewById(R.id.spinnerSitio);
        next = findViewById(R.id.btnNext);
        pDExist = findViewById(R.id.pDExist);
        contact = findViewById(R.id.textContact);

        bEVal = "No";

        String[] monthlyIncome = {"Less than 9,100", "9,100 to 18,200", "18,200 to 36,400",
                "36,400 to 63,700", "63,700 to 109,200", "109,200 to 182,000", "Above 182,000"};

        FormUtils.setAdapter(SitioUtils.getSitioList(App.user.getBarangay()), sitioAC, this);
        FormUtils.setAdapter(monthlyIncome, incomeAC, this);

        nextEvent();
        pDExistEvent();
    }
    private  void nextEvent(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllTextInputData();
                boolean isFormValid = FormUtils.validateForm_Parent( pFVal, pMVal, pLVal, gMVal, contactVal, AddParentData.this);

                if (isFormValid) {
                    savetoTempEmail();
                } else {
                    Toast.makeText(AddParentData.this, "Please fill out all fields and provide valid information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private  void pDExistEvent(){
        pDExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddParentData.this, SearchParent.class));
            }
        });
    }

    private void setAllTextInputData(){
        pFVal = parentFirstName.getText().toString().trim();
        pMVal = parentMiddleName.getText().toString().trim();
        pLVal = parentLastName.getText().toString().trim();
        gMVal = gmail.getText().toString().trim();
        hNVal = houseNumber.getText().toString().trim();
        iNVaL = incomeAC.getText().toString().trim();
        sIVal = sitioAC.getText().toString().trim();
        contactVal = contact.getText().toString().trim();
    }
    private void clearText(){
        parentFirstName.setText("");
        parentMiddleName.setText("");
        parentLastName.setText("");
        gmail.setText("");
        houseNumber.setText("");
        incomeAC.setText("");
        sitioAC.setText("");
        contact.setText("");
    }

    private Map<String, Object> createMap() {
        Map<String, Object> parent = new HashMap<>();
        parent.put("parentFirstName", pFVal);
        parent.put("parentMiddleName", pMVal);
        parent.put("parentLastName", pLVal);
        parent.put("gmail", gMVal);
        parent.put("houseNumber", hNVal);
        parent.put("monthlyIncome", iNVaL);
        parent.put("sitio", sIVal);
        parent.put("belongtoIP", bEVal);
        parent.put("barangay", App.user.getBarangay());
        parent.put("contactNo", contactVal);
        return parent;
    }

    private void setParentData (){
        TempEmail tempEmail = new TempEmail();
        tempEmail.setGmail(gMVal);
        tempEmail.setParentFirstName(pFVal);
        tempEmail.setParentMiddleName(pMVal);
        tempEmail.setParentLastName(pLVal);
        tempEmail.setHouseNumber(hNVal);
        tempEmail.setMonthlyIncome(iNVaL);
        tempEmail.setSitio(sIVal);
        tempEmail.setBarangay(App.user.getBarangay());
        tempEmail.setBelongtoIP(bEVal);
        tempEmail.setContactNo(contactVal);

        App.tempEmail = tempEmail;
    }
    private void savetoTempEmail() {
        Map<String, Object> parent = createMap();
        clearText();

        db.collection("tempEmail").document(gMVal)
                .set(parent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setParentData();
                        startActivity(new Intent(AddParentData.this, AddDataWithParent.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddParentData.this, "Failed to parent data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}