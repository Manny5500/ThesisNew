package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UMEdit extends AppCompatActivity {

    TextInputEditText firstName, middleName, lastName, contact;
    MaterialAutoCompleteTextView month, day, year, sex, barangay;

    String fnameVal, mnameVal, lnameVal, contactVal, monthVal, dayVal,
            yearVal, sexVal, barangayVal, bdayfull, motono, role;

    Button edit, archive, delete, unarchive, verified;

    String requestDeletion;
    private FirebaseFirestore db;

    TextView txtRole3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umedit);

        String[] barangays = getResources().getStringArray(R.array.barangay);
        String[] sexs = getResources().getStringArray(R.array.sex);

        db = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.textChildfirstName);
        middleName = findViewById(R.id.textChildMiddleName);
        lastName = findViewById(R.id.textChildLastName);
        contact = findViewById(R.id.textContact);
        month = findViewById(R.id.textMonthlyIncome);
        day = findViewById(R.id.txtEmployment1);
        year = findViewById(R.id.txtEmployment2);
        barangay = findViewById(R.id.textBarangay);
        sex = findViewById(R.id.textPregnant);
        edit = findViewById(R.id.buttonEdit);
        archive = findViewById(R.id.buttonArchive);
        delete = findViewById(R.id.buttonDelete);
        txtRole3 = findViewById(R.id.txtRole3);
        unarchive = findViewById(R.id.buttonUnarchive);
        verified = findViewById(R.id.buttonVerify);

        role = getIntent().getStringExtra("role");

        if(role.equals("personnel"))
            role = "BNS";

        txtRole3.setText(role.toUpperCase());

        if(!role.equals("Request for Deletion")){
            delete.setVisibility(View.GONE);
        }

        if(!role.equals("Archive")){
            unarchive.setVisibility(View.GONE);
        }

        if(!role.equals("parent") && !role.equals("BNS")){
            edit.setVisibility(View.GONE);
            archive.setVisibility(View.GONE);
        }

        if(!role.equals("Verify")){
            verified.setVisibility(View.GONE);
        }

        firstName.setText(App.user.getFirstName());
        middleName.setText(App.user.getLastName());
        lastName.setText(App.user.getLastName());
        contact.setText(App.user.getContact());
        bdayfull = App.user.getBirthdate();
        String[] parts = bdayfull.split("/");
        String[] monthCol = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        month.setText(monthCol[Integer.parseInt(parts[0])-1]);
        day.setText(parts[1]);
        year.setText(parts[2]);
        sex.setText(App.user.getSex());
        barangay.setText(App.user.getBarangay());

        FormUtils.setAdapter(monthCol, month, this);
        FormUtils.setAdapter(sexs, sex, this);
        FormUtils.setAdapter(barangays, barangay, this);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumbers(currentYear-61, currentYear));
        year.setAdapter(yearAdapter);
        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumbers(1, 31));
        day.setAdapter(dayAdapter);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormUtils formUtils = new FormUtils();
                fnameVal = Objects.requireNonNull(firstName.getText()).toString().trim();
                mnameVal = Objects.requireNonNull(middleName.getText()).toString().trim();
                lnameVal = lastName.getText().toString().trim();
                contactVal = contact.getText().toString().trim();
                monthVal = month.getText().toString().trim();
                dayVal = day.getText().toString().trim();
                yearVal = year.getText().toString().trim();
                sexVal = sex.getText().toString().trim();
                barangayVal = barangay.getText().toString().trim();
                motono = formUtils.MotoNo(monthVal);
                bdayfull = motono + "/" + dayVal + "/" + yearVal;

                boolean isFormValid = formUtils.validateForm_UM(fnameVal, mnameVal, lnameVal,
                        contactVal, sexVal, barangayVal,  bdayfull, UMEdit.this);
                if(isFormValid){
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", fnameVal);
                    user.put("middleName", mnameVal);
                    user.put("lastName", lnameVal);
                    user.put("contact", contactVal);
                    user.put("birthdate", bdayfull);
                    user.put("sex", sexVal);
                    user.put("barangay", barangayVal);
                    db.collection("users").document(App.user.getId()).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UMEdit.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UMEdit.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(UMEdit.this, "Please fill out all fields and provide valid information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> userMap= new HashMap<>();
                userMap.put("isArchive", "Yes");
                db.collection("users").document(App.user.getId()).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UMEdit.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
                }
        });

        verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> userMap= new HashMap<>();
                userMap.put("verified", "Yes");
                db.collection("users").document(App.user.getId()).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UMEdit.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccountDialog();
            }
        });

        unarchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> userMap= new HashMap<>();
                userMap.put("isArchive", "No");
                db.collection("users").document(App.user.getId()).update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UMEdit.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Allow the user to delete account");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteUser.grantRequestForDeletionAdmin(db, UMEdit.this, UMEdit.this);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteUser.undoRequestForDeletionAdmin(db, App.user.getId(), UMEdit.this, UMEdit.this);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private List<Integer> generateNumbers(int start, int end) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = end; i >= start; i--) {
            numbers.add(i);
        }
        return numbers;
    }

}