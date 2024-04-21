package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Executors;

public class PriorityClicked extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 1;
    String name = "", birthdate = "", age = "", referraldate = "",
            mothername = "", sex = "", municipality="", barangay="", houseno="",
            parent = "", phoneNumber =  "", email= "", address = "", sitio = "" ;
    Dialog dialog2;
    String pdfUrl = "";

    FirebaseFirestore db;

    TextView textName, textAge, textSex, textWeight,
            textHeight, textStatus, textParent, textEmail,
            textCellphone, textAddress;

    TextView textNameLabel, textAgeLabel, textGenderLabel,
            textWeightLabel, textHeightLabel, textStatusLabel,
            textParentLabel, textEmailLabel, textCellphoneLabel,
            textAddressLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority_clicked);

        db = FirebaseFirestore.getInstance();

        pdfUrl = App.child.getDownloadUrl();
        Button callButton = findViewById(R.id.btnCallDynamics);
        Button referral = findViewById(R.id.btnReferralDynamics);
        dialog2 = new Dialog(PriorityClicked.this);
        dialog2.setContentView(R.layout.dialog_loader);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PriorityClicked.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PriorityClicked.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    // Permission has already been granted
                    if(!phoneNumber.equals("N/A")){
                        makePhoneCall(phoneNumber);
                    }else{
                        Toast.makeText(PriorityClicked.this, "No number available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });


        sitio = App.child.getSitio();
        if(sitio == null){
            sitio = "";
        }else if(sitio.equals("placeholder")){
            sitio = "";
        }



        textName = findViewById(R.id.textNameDynamics);
        textAge = findViewById(R.id.textAgeDynamics);
        textSex = findViewById(R.id.textMaleDynamics);
        textWeight = findViewById(R.id.textWeightDynamics);
        textHeight = findViewById(R.id.textHeightDynamics);
        textStatus = findViewById(R.id.textStatusDynamics);
        textParent = findViewById(R.id.textParentDynamics);
        textEmail = findViewById(R.id.textEmailDynamics);
        textCellphone = findViewById(R.id.textCellphoneDynamics);
        textAddress = findViewById(R.id.textAddressDynamics);

        textNameLabel = findViewById(R.id.labelPCName);
        textAgeLabel = findViewById(R.id.labelAge);
        textGenderLabel = findViewById(R.id.labelGenderDynamics);
        textWeightLabel = findViewById(R.id.labelWeightDynamics);
        textHeightLabel = findViewById(R.id.labelHeightDynamics);
        textStatusLabel = findViewById(R.id.labelStatusDynamics);
        textParentLabel = findViewById(R.id.labelParentName);
        textEmailLabel = findViewById(R.id.labelEmail);
        textCellphoneLabel = findViewById(R.id.labelCellphone);
        textAddressLabel = findViewById(R.id.labelAddress);



        String statusmaker = "";

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int i=0;
        for(String status : App.child.getStatusdb()){
            if(i==App.child.getStatusdb().size()-1){
                statusmaker = statusmaker + status;
            }else{
                statusmaker = statusmaker + status + "\n";
            }
            i++;
        }
        textStatus.setText(statusmaker);

        name = App.child.getChildFirstName()+App.child.getChildFirstName();
        birthdate = App.child.getBirthDate();
        referraldate = currentDate.format(formatter);
        mothername = App.child.getParentFirstName() + " " + App.child.getParentLastName();
        municipality = "Magdalena, Laguna";
        barangay = App.child.getBarangay();
        houseno = App.child.getHouseNumber();
        sex = App.child.getSex();

        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(App.child.getBirthDate());

        int monthdiff = 0;
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
            age = String.valueOf(monthdiff);
            textAge.setText(String.valueOf(monthdiff)+ " months");
        } else {
            Toast.makeText(this, "Failed to parse the date", Toast.LENGTH_SHORT).show();
        }

        setTextSize(textAge,textStatus, textSex, textName, textWeight, textHeight, textParent, textEmail, textCellphone, textAddress);
        setTextSize(textAgeLabel, textStatusLabel, textGenderLabel, textNameLabel, textWeightLabel, textHeightLabel, textParentLabel,
                textEmailLabel, textCellphoneLabel, textAddressLabel);

        getPhoneNumber();
    }

    private void getPhoneNumber(){
        db.collection("users")
                .whereEqualTo("email", App.child.getGmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            phoneNumber = document.getString("contact");
                            if(phoneNumber != null){
                                break;
                            }
                        }

                        if(phoneNumber == ""){
                            phoneNumber = "N/A";
                        }
                        setTextText();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setTextText(){
        textCellphone.setText(phoneNumber);
        textName.setText(App.child.getChildFirstName()+" "+App.child.getChildLastName());
        textSex.setText(App.child.getSex());
        textWeight.setText(App.child.getWeight() + " kg");
        textHeight.setText(App.child.getHeight() + " cm");
        textParent.setText(App.child.getParentFirstName()+ " " + App.child.getParentLastName());
        textEmail.setText(App.child.getGmail());
        textAddress.setText(App.child.getHouseNumber() + " " + sitio + "\n"
                + App.child.getBarangay());
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(!phoneNumber.equals("N/A")){
                    makePhoneCall(phoneNumber);
                }else{
                    Toast.makeText(this, "No number available", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Permission denied, inform the user and handle accordingly
                Toast.makeText(this, "Please allow the phone permission in the settings", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setTextSize(TextView... textViews){
        for(TextView textView: textViews){
            textView.setTextSize(18);
        }
    }

    public void createPdf(){
        Rectangle customsize = new Rectangle(
                5.0f*72,
                6.5f*72
        );
        String[] value = {name, sex, birthdate, age, referraldate, mothername, "", phoneNumber, municipality,
                barangay, houseno};

        PY_PdfUtils pyPdfUtils = new PY_PdfUtils(customsize, value);
        byte[] pdfBytes = pyPdfUtils.PdfSetter();
        showPdfDialog(pdfBytes);
    }

    private void showPdfDialog(byte[] pdfBytes){
        final Dialog dialog = new Dialog(PriorityClicked.this);
        dialog.setContentView(R.layout.pdf_viewer);
        PDFView pdfView = dialog.findViewById(R.id.pdfView);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        Button cancelBtn = dialog.findViewById(R.id.btnCancel);
        Button exportBtn = dialog.findViewById(R.id.btnSavePdf);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
        displayPdfFromBytes(pdfBytes, pdfView, progressBar);

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "Priority List"  +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + ".pdf";
                Pdf_Utils pdfUtils = new Pdf_Utils(getContentResolver(), pdfBytes,
                        PriorityClicked.this, filename);
                pdfUtils.savePDFToStorage();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void displayPdfFromBytes(byte[] pdfBytes, PDFView pdfView, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    pdfView.fromBytes(pdfBytes)
                            .scrollHandle(new DefaultScrollHandle(this))
                            .load();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    e.printStackTrace();
                    Log.e("PDFViewer", "Error loading PDF: " + e.getMessage());
                });
            }
        });
    }

}
