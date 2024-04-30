package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UMEdit extends AppCompatActivity {

    TextView textName, textAge, textSex,  textBdate, textEmail,
            textCellphone, textAddress;

    TextView textNameLabel, textAgeLabel, textGenderLabel,
            textBirthdateLabel, textEmailLabel, textCellphoneLabel,
            textAddressLabel;

    String name = "", age = "", sex = "", bdate = "", email="", contact = "", address = "", role="";

    Button  archive, delete, unarchive, verified;

    String requestDeletion;
    private FirebaseFirestore db;

    TextView txtRole3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umedit);


        db = FirebaseFirestore.getInstance();

        textName = findViewById(R.id.textNameDynamics);
        textAge = findViewById(R.id.textAgeDynamics);
        textSex = findViewById(R.id.textMaleDynamics);
        textBdate = findViewById(R.id.textBirthdateDynamics);
        textEmail = findViewById(R.id.textEmailDynamics);
        textCellphone = findViewById(R.id.textCellphoneDynamics);
        textAddress = findViewById(R.id.textAddressDynamics);

        textNameLabel = findViewById(R.id.labelPCName);
        textAgeLabel = findViewById(R.id.labelAge);
        textGenderLabel = findViewById(R.id.labelGenderDynamics);
        textBirthdateLabel = findViewById(R.id.labelBirthdate);
        textEmailLabel = findViewById(R.id.labelEmail);
        textCellphoneLabel = findViewById(R.id.labelCellphone);
        textAddressLabel = findViewById(R.id.labelAddress);

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
            archive.setVisibility(View.GONE);
        }

        if(!role.equals("Verify")){
            verified.setVisibility(View.GONE);
        }

        textName.setText(App.user.getFirstName() + " " + App.user.getMiddleName() + " "
        + App.user.getLastName());
        textCellphone.setText(App.user.getContact());
        textBdate.setText(App.user.getBirthdate());
        textSex.setText(App.user.getSex());
        textEmail.setText(App.user.getEmail());
        textAddress.setText(App.user.getBarangay() + "\nMagdalena" );

        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(App.user.getBirthdate());
        int monthdiff = 0;
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
            age = String.valueOf(monthdiff/12);
            textAge.setText(age + " years old");
        } else {
            textAge.setText("Age error");
        }

        LayoutUtils.setTextSize(textName, textCellphone, textBdate, textSex, textEmail, textAddress, textAge);
        LayoutUtils.setTextSize(textNameLabel, textCellphoneLabel, textBirthdateLabel, textGenderLabel,
                textEmailLabel, textGenderLabel, textAgeLabel, textAddressLabel);

        float scale = getResources().getDisplayMetrics().density;
        LayoutUtils.setMarginTop(scale, textNameLabel, textCellphone, textBdate, textGenderLabel, textEmail, textAddress, textAgeLabel);

        String[] monthCol = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        /*
        month.setText(monthCol[Integer.parseInt(parts[0])-1]);
        day.setText(parts[1]);
        year.setText(parts[2]);
        sex.setText(App.user.getSex());*/

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