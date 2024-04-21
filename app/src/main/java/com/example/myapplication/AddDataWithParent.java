package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDataWithParent extends AppCompatActivity {
    TextInputEditText childFirstName, childMiddleName, childLastName,
          height, weight, bdate, expectedDate;


    MaterialAutoCompleteTextView sexAC;
    String cFVal, cMVal, cLVal, hVal, wVal, bDVal, eDVal, sXVal;
    private double height_true_val, weight_true_val;
    ArrayList<String> status = new ArrayList<>();
    private int monthdiff;
    ArrayList<String> statusdb;

    Button submit;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_with_parent);
        childFirstName = findViewById(R.id.textChildfirstName);
        childMiddleName = findViewById(R.id.textChildMiddleName);
        childLastName = findViewById(R.id.textChildLastName);
        height = findViewById(R.id.textHeight);
        weight = findViewById(R.id.textWeight);
        bdate = findViewById(R.id.textBdate);
        expectedDate = findViewById(R.id.textExpectedDate);
        sexAC = findViewById(R.id.textPregnant);
        submit = findViewById(R.id.btnSubmit);
        db = FirebaseFirestore.getInstance();

        String[] sex = getResources().getStringArray(R.array.sex);
        FormUtils.setAdapter(sex, sexAC, this);

        FormUtils.dateClicked(bdate, this);
        FormUtils.dateClicked(expectedDate, this);

        submitEvent();

    }
    private void submitEvent(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllTextData();
                boolean isFormValid = FormUtils.validateForm_Child(cFVal, cMVal, cLVal, bDVal,AddDataWithParent.this);
                if (isFormValid) {
                    checkName();

                }

            }
        });
    }
    private void setAllTextData(){
        cFVal = childFirstName.getText().toString().trim();
        cMVal = childMiddleName.getText().toString().trim();
        cLVal = childLastName.getText().toString().trim();
        hVal = height.getText().toString().trim();
        wVal = weight.getText().toString().trim();
        bDVal = bdate.getText().toString().trim();
        eDVal = expectedDate.getText().toString().trim();
        sXVal = sexAC.getText().toString().trim();
    }

    private void checkName(){
        boolean isMiddleNameMatch = App.tempEmail.parentMiddleName.equals(cMVal);
        boolean isLastNameMatch = App.tempEmail.parentLastName.equals(cLVal);
        if((isMiddleNameMatch && isLastNameMatch)){
            AddNewFirestore();
        }else{
            showRelatedDialog();
        }
    }


    private void AddNewFirestore() {
        Map<String, Object> child = createMap();
        db.collection("children").add(child).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                clearInputs();
                savetoHistory();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDataWithParent.this, "Failed to add user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStatuses() {
        setMonthdiff();
        height_true_val = Double.parseDouble(hVal);
        weight_true_val = Double.parseDouble(wVal);
        statusdb = FindStatusWFA.CalculateMalnourished(AddDataWithParent.this, monthdiff, weight_true_val, height_true_val, sXVal);
        String[] individualTest = FindStatusWFA.individualTest(AddDataWithParent.this, monthdiff, weight_true_val, height_true_val, sXVal);

        for (String status_element : individualTest) {
            status.add(status_element);
        }
    }
    private Map<String, Object> createMap() {
        status.clear();
        setStatuses();
        Map<String, Object> user = new HashMap<>();
        user.put("childFirstName", cFVal);
        user.put("childMiddleName", cMVal);
        user.put("childLastName", cLVal);
        user.put("parentFirstName", App.tempEmail.getParentFirstName());
        user.put("parentMiddleName", App.tempEmail.getParentMiddleName());
        user.put("parentLastName", App.tempEmail.getParentLastName());
        user.put("gmail", App.tempEmail.getGmail());
        user.put("houseNumber", App.tempEmail.getHouseNumber());
        user.put("height", height_true_val);
        user.put("weight", weight_true_val);
        user.put("birthDate", bDVal);
        user.put("belongtoIP", App.tempEmail.getBelongtoIP());
        user.put("barangay", App.tempEmail.getBarangay());
        user.put("sitio", App.tempEmail.getSitio());
        user.put("sex", sXVal);
        user.put("expectedDate", eDVal);
        user.put("statusdb", statusdb);
        user.put("status", status);
        user.put("dateAdded", DateParser.getCurrentTimeStamp());
        user.put("monthAdded", DateParser.getMonthYear());
        user.put("monthlyIncome", App.tempEmail.getMonthlyIncome());
        return user;
    }

    private void setMonthdiff() {
        String dateString = bdate.getText().toString();
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(dateString);

        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        } else {
            Toast.makeText(AddDataWithParent.this, "Failed to parse the date", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        childFirstName.setText("");
        childMiddleName.setText("");
        childLastName.setText("");
        height.setText("");
        weight.setText("");
        bdate.setText("");
        expectedDate.setText("");
        sexAC.setText("");
    }



    private void savetoHistory() {
        String child_full_name = cFVal + " " + cMVal + " " + cLVal;
        String date_now = FormUtils.getCurrentDate();
        DocumentReference childDocRef = db.collection("children_historical").document(child_full_name);
        DocumentReference dateExaminedDocRef = childDocRef.collection("dates").document(date_now);
        Map<String, Object> childData = new HashMap<>();
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
                            Toast.makeText(AddDataWithParent.this, "Form submitted successfully!", Toast.LENGTH_SHORT).show();
                            App.AddFlag = 1;
                            finish();
                        } else {
                            Toast.makeText(AddDataWithParent.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void showRelatedDialog(){
        final Dialog dialog = new Dialog(AddDataWithParent.this);
        dialog.setContentView(R.layout.related_dialog);

        TextView textcFN = dialog.findViewById(R.id.textcFN);
        TextView textpFN = dialog.findViewById(R.id.textpFN);

        Button buttonNo = dialog.findViewById(R.id.buttonNo);
        Button buttonYes = dialog.findViewById(R.id.buttonYes);

        String cFN = cFVal + " " + cMVal + " " + cLVal;
        String pFN = App.tempEmail.getParentFirstName() + " "
                + App.tempEmail.getParentMiddleName() + " "
                + App.tempEmail.getParentLastName();
        textcFN.setText(cFN);
        textpFN.setText(pFN);

        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(AddDataWithParent.this, "Make sure the surname and middle name are match", Toast.LENGTH_SHORT).show();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AddNewFirestore();

            }
        });
    }

}