package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;


public class SummaryReport extends AppCompatActivity  {

    ConstraintLayout naviData, naviConso, naviSum, naviPdf;
    MaterialAutoCompleteTextView textBarangay, textDate;
    String text_Date, text_Barangay;

    FirebaseFirestore db;

    ArrayList<Child> childrenList;
    FragmentEventListener frListener;

    String currentFragment="srList";

    int totalCount;
    int loadCount = 1;

    int estimatedChildren;
    int population;

    int blueColor;
    int vioColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);

        naviData = findViewById(R.id.naviData);
        naviConso = findViewById(R.id.naviConso);
        naviSum = findViewById(R.id.naviSum);
        naviPdf = findViewById(R.id.naviPdf);

        textDate = findViewById(R.id.textDate);
        textBarangay = findViewById(R.id.textBarangay);

        blueColor =  Color.parseColor("#51ADE5");
        vioColor =  Color.parseColor("#1888c8");


        db = FirebaseFirestore.getInstance();

        String[] brgyList = getResources().getStringArray(R.array.barangay);
        FormUtils.setAdapter(brgyList, textBarangay, this);
        dateAdapter();
        preSelected();
        Populate();
        textDateEvent();
        textBarangayEvent();
        changeColor();



    }

    public void textDateEvent(){
        textDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_Barangay = textBarangay.getText().toString();
                text_Date =  textDate.getText().toString();
                Populate();
            }
        });
    }

    public void textBarangayEvent(){
        textBarangay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                text_Barangay = textBarangay.getText().toString();
                text_Date =  textDate.getText().toString();
                Populate();
            }
        });
    }

    public void Populate(){
        db.collection("children")
                .whereEqualTo("monthAdded", text_Date)
                .whereEqualTo("barangay", text_Barangay )
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<Child> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                arrayList.add(child);
                            }
                            loadCount++;
                            if(loadCount==2){
                                replaceFragment(new fragment_sr_list());
                            }
                            childrenList = arrayList;
                            naviDataEvent();
                            naviConsoEvent();
                            naviSumEvent();
                            naviPdfEvent();


                            if(loadCount>2){
                                if(currentFragment.equals("srList")){
                                    frListener.onEventTrigerred();
                                } else if (currentFragment.equals("srConso")) {
                                    frListener.onEventTrigerred();
                                } else if(currentFragment.equals("srSum")){
                                    frListener.onEventTrigerred();
                                }
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SummaryReport.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void preSelected(){
        text_Date = getDateNowFormatted();
        text_Barangay = "Alipit";
        textDate.setText(getDateNowFormatted(), false);
        textBarangay.setText("Alipit",false);
    }
    public String getDateNowFormatted(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public void dateAdapter(){
        LocalDate currentDate = LocalDate.now();
        YearMonth targetMonth = YearMonth.of(2023,6);
        ArrayList<String> filteredMonths  = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (YearMonth month = targetMonth;
             !month.isAfter(YearMonth.from(currentDate));
             month = month.plusMonths(1)) {
            filteredMonths.add(month.atDay(1).format(formatter));
        }
        Collections.reverse(filteredMonths);
        ArrayAdapter<String> adapter = new
                ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,filteredMonths);
        textDate.setAdapter(adapter);
    }

    private void naviDataEvent(){
        naviData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragment_sr_list());
                currentFragment= "srList";
                changeColor();
            }
        });
    }

    private void naviConsoEvent(){
        naviConso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragment_sr_conso());
                currentFragment = "srConso";
                changeColor();
            }
        });
    }
    private void naviSumEvent(){
        naviSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragment_sr_sum());
                currentFragment = "srSum";
                changeColor();
            }
        });
    }

    private void naviPdfEvent(){
        naviPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseCallPdf();
            }
        });

    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutSR,fragment, "fragment_sr_list");
        fragmentTransaction.commit();
    }

    public void databaseCallPdf(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("barangay").document(text_Barangay);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        estimatedChildren = document.getLong("estimatedChildren").intValue();
                        population = document.getLong("population").intValue();
                    }
                    SRDPPdf srdpP = new SRDPPdf(childrenList);
                    createPdf(childrenList);
                }
            }
        });
    }

    public void createPdf(ArrayList<Child> arrayList){
        Rectangle customsize = new Rectangle(
                13.0f*72,
                8.5f*72
        );

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.optlogojp);
        SR_PdfUtils sPUtils = new SR_PdfUtils(customsize, text_Barangay,
                estimatedChildren, population, new SRDPPdf(childrenList), drawable );
        SRDPPdf la = new SRDPPdf(childrenList);
        byte[] pdfBytes = sPUtils.PdfSetter();
        showPdfDialog(pdfBytes);
    }


    private void showPdfDialog(byte[] pdfBytes){
        final Dialog dialog = new Dialog(SummaryReport.this);
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
                String filename = "Summary Report"  +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + ".pdf";
                Pdf_Utils pdf_utils = new Pdf_Utils(getContentResolver(), pdfBytes, SummaryReport.this, filename);
                pdf_utils.savePDFToStorage();
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

    public void updateApi(FragmentEventListener listener) {
        frListener = listener;
    }


    private void changeColor(){
       resetColor();
        String[] fragment = {"srList", "srConso", "srSum"};
        ConstraintLayout[] cLayout = {naviData, naviConso, naviSum};
        for(int i=0; i<3; i++){
            if(fragment[i].equals(currentFragment)){
                cLayout[i].setBackgroundColor(vioColor);
            }
        }
    }
    private void resetColor(){
        String[] fragment = {"srList", "srConso", "srSum"};
        ConstraintLayout[] cLayout = {naviData, naviConso, naviSum};
        for(int i=0; i<3; i++){
            if(fragment[i].equals(currentFragment)){
                continue;
            }
               cLayout[i].setBackgroundColor(blueColor);
        }

    }


}