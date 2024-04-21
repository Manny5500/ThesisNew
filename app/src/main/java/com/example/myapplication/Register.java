package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputEditText fname, mname, lname, gmail, password, cpassword, contact;
    MaterialAutoCompleteTextView month, day, year, sex, barangay, userrole;
    String fnameVal, mnameVal, lnameVal,gmailVal, passwordVal, cpasswordVal, contactVal,
    monthVal, dayVal, yearVal, sexVal, barangayVal, bdayfull, motono, userVal, tempEmailVal;

    FirebaseFirestore db;

    Button btnRegister;
    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String[] monthCol = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] barangayCol = getResources().getStringArray(R.array.barangay);
        String[] sexCol = getResources().getStringArray(R.array.sex);
        String[] roleCol = {"admin", "parent", "personnel"};


        fname = findViewById(R.id.textChildfirstName);
        mname = findViewById(R.id.textChildMiddleName);
        lname = findViewById(R.id.textChildLastName);
        gmail = findViewById(R.id.textGmail);
        password = findViewById(R.id.textPassword);
        cpassword = findViewById(R.id.textFamilySize);
        month = findViewById(R.id.textMonthlyIncome);
        day = findViewById(R.id.txtEmployment1);
        year = findViewById(R.id.txtEmployment2);
        sex = findViewById(R.id.textPregnant);
        barangay = findViewById(R.id.textBarangay);
        btnRegister = findViewById(R.id.buttonRegister);
        userrole = findViewById(R.id.textUserRole);
        contact = findViewById(R.id.textContact);

        FormUtils.setAdapter(monthCol, month, this);
        FormUtils.setAdapter(sexCol, sex, this);
        FormUtils.setAdapter(barangayCol, barangay, this);
        FormUtils.setAdapter(roleCol, userrole, this);


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumbers(currentYear-61, currentYear));
        year.setAdapter(yearAdapter);

        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, generateNumbers(1, 31));
        day.setAdapter(dayAdapter);



        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FormUtils formUtils = new FormUtils();
                fnameVal = fname.getText().toString().trim();
                mnameVal = mname.getText().toString().trim();
                lnameVal = lname.getText().toString().trim();
                gmailVal = gmail.getText().toString().trim();
                passwordVal = password.getText().toString().trim();
                cpasswordVal = cpassword.getText().toString().trim();
                monthVal = month.getText().toString().trim();
                dayVal = day.getText().toString().trim();
                yearVal = year.getText().toString().trim();
                sexVal = sex.getText().toString().trim();
                barangayVal = barangay.getText().toString().trim();
                motono = formUtils.MotoNo(monthVal);
                userVal = userrole.getText().toString().trim();
                contactVal = contact.getText().toString().trim();
                bdayfull = motono + "/" + dayVal + "/" + yearVal;

                boolean isFormValid = formUtils.validateForm_R(fnameVal, mnameVal, lnameVal, gmailVal,
                        bdayfull, sexVal, barangayVal, passwordVal, cpasswordVal, userVal, contactVal, userVal, Register.this);

                if(isFormValid){
                    getTempEmail();
                }
            }
        });
    }

    private void addtoFirebaseAuth(){
        mAuth.createUserWithEmailAndPassword(gmailVal, passwordVal)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserDataToFirestore();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed." ,
                                    Toast.LENGTH_SHORT).show();
                            Log.d("auth_err", ""+task.getException());


                        }
                    }
                });

    }

    private void addUserDataToFirestore() {

        // Create a new user object with desired data
        User user = new User(fnameVal, mnameVal, lnameVal, gmailVal, bdayfull, sexVal, barangayVal, userVal, contactVal, "No");

        // Add the user object to the "users" collection in Firestore
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid()) // Use user's UID as document ID
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data added successfully
                        //progressBar.setVisibility(View.GONE);
                        if(tempEmailVal==null){
                            savetoTempEmail();
                        }

                        if(userVal.equals("admin")){
                            sendEmailVerification();
                        }
                        if(userVal.equals("personnel")){
                            //Toast.makeText(Register.this, "Wait for verification first", Toast.LENGTH_SHORT).show();
                            //Intent intent = new Intent(getApplicationContext(), PersonnelActivity.class);
                            //startActivity(intent);
                            //finish();
                            sendEmailVerification();
                        }

                        if(userVal.equals("parent")){
                            sendEmailVerification();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        //progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this, "Failed to add data to Firestore.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private List<Integer> generateNumbers(int start, int end) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = end; i >= start; i--) {
            numbers.add(i);
        }
        return numbers;
    }

    private void savetoTempEmail() {
        Map<String, Object> tempEmail = new HashMap<>();
        tempEmail.put("parentFirstName", fnameVal);
        tempEmail.put("parentMiddleName", mnameVal);
        tempEmail.put("parentLastName", lnameVal);

        db.collection("tempEmail").document(gmailVal)
                .set(tempEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void getTempEmail() {

        db.collection("tempEmail").document(gmailVal).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TempEmail tempEmail = documentSnapshot.toObject(TempEmail.class);
                    tempEmail.setGmail(documentSnapshot.getId());
                    tempEmailVal = tempEmail.getGmail();

                    if(tempEmail!=null){
                        showYesNoDialog(tempEmail);
                    }
                } else {
                    addtoFirebaseAuth();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the failure here, e.g., log the error or show an error message.
                Toast.makeText(Register.this, ""+e, Toast.LENGTH_SHORT).show();
                Log.d("firestore_err", ""+e);
            }
        });
    }
    private void showYesNoDialog(TempEmail tempEmail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("This email is already associated with " + tempEmail.getFullName());

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fnameVal = tempEmail.getParentFirstName();
                mnameVal = tempEmail.getParentMiddleName();
                lnameVal = tempEmail.getParentLastName();
                addtoFirebaseAuth();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Register.this, "Change your email", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendEmailVerification() {
        mAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Verify your email first",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        } else {
                        }
                    }
                });
    }

}