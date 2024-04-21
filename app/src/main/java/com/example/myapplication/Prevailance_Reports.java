package com.example.myapplication;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Rectangle;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class Prevailance_Reports extends AppCompatActivity {
    MaterialAutoCompleteTextView textType, textDate;
    TextInputLayout textTypeLayout;

    View up_View;
    TextView textThisMonth,  textTypeTitle, textDataThisMonth,
            textIncreased, textDataIncreased;

    FloatingActionButton pdfMaker;
    FirebaseFirestore db;
    RecyclerView barangayRecycler;
    private BarangayAdapter userAdapter;
    private int statusPointer = -1;

    Dialog dialog2;
    String methodType = "WEIGHT FOR AGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevailance_reports);
        db = FirebaseFirestore.getInstance();
        textType = findViewById(R.id.textType);
        textDate = findViewById(R.id.textDate);
        up_View = findViewById(R.id.up_View);
        textThisMonth = findViewById(R.id.textThisMonth);
        textIncreased = findViewById(R.id.textIncreased);
        pdfMaker = findViewById(R.id.floatingActionButton);
        textTypeLayout = findViewById(R.id.textTypeLayout);
        textTypeTitle = findViewById(R.id.textTypeTitle);
        textDataThisMonth = findViewById(R.id.textDataThisMonth);
        textDataIncreased = findViewById(R.id.textDataIncreased);

        dialog2 = new Dialog(Prevailance_Reports.this);
        dialog2.setContentView(R.layout.dialog_loader);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);

        String[] mType = {"Underweight", "Overweight", "Stunting", "Wasting"};
        FormUtils.setAdapter(mType, textType, this);
        dateAdapter();
        preSelected();
        barangayRecycler = findViewById(R.id.barangayRecycler);
        textType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text_Date = textDate.getText().toString();
                String selectedType = (String) parent.getItemAtPosition(position);
                contentMaker(text_Date, selectedType);
            }
        });

        textDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text_Status = textType.getText().toString();
                String text_Date =  textDate.getText().toString();
                contentMaker(text_Date, text_Status);
            }
        });
    }

    public void contentMaker(String text_Date, String text_Status){
        String query_Type = "";
        if(text_Status.equals("Stunting")){
            themeSetter("Stunting");
            query_Type = setQueryType("S");
            statusPointer = 1;
            methodType = "HEIGHT FOR AGE";
        } else if (text_Status.equals("Overweight")) {
            themeSetter("Overweight");
            query_Type = setQueryType("O");
            statusPointer = 2;
            methodType = "WEIGHT FOR LENGTH/HEIGHT";
        } else if (text_Status.equals("Underweight")) {
            themeSetter("Underweight");
            query_Type = setQueryType("U");
            statusPointer = 3;
            methodType = "WEIGHT FOR LENGTH/HEIGHT";
        } else if (text_Status.equals("Wasting")){
            themeSetter("Wasting");
            query_Type = setQueryType("W");
            statusPointer = 4;
            methodType = "WEIGHT FOR AGE";
        }
        String[] status_array = getStatusArray();
        Populate(status_array, query_Type, text_Date);

    }
    public String[] getStatusArray(){
        String[] status_arrays = {"",""};
        if(statusPointer==1){
            status_arrays[0] = "Stunted";
            status_arrays[1] = "Severe Stunted";
        } else if (statusPointer==2) {
            status_arrays[0] = "Overweight";
            status_arrays[1] = "Obese";
        } else if(statusPointer==3){
            status_arrays[0] = "Underweight";
            status_arrays[1] = "Severe Underweight";
        } else if (statusPointer==4){
            status_arrays[0] = "Wasted";
            status_arrays[1] = "Severe Wasted";
        }
        return status_arrays;
    }
    public void themeSetter(String choice){
        int colorString;
        if(choice.equals("Stunting")){
            colorString = Color.parseColor("#77DD77");
            UIChanger(colorString, choice);
        } else if (choice.equals("Underweight")) {
            colorString = Color.parseColor("#51ADE5");
            UIChanger(colorString, choice);
        } else if (choice.equals("Overweight")) {
            colorString = Color.parseColor("#FF6961");
            UIChanger(colorString, choice);
        } else if (choice.equals("Wasting")) {
            colorString = Color.parseColor("#FFB347");
            UIChanger(colorString, choice);
        }
    }
    public void statusBarChanger(int color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor((color));
        }
    }
    public void UIChanger(int colorString, String type){
        ColorStateList colorStateList = ColorStateList.valueOf(colorString);
        up_View.setBackgroundTintList(colorStateList);
        textThisMonth.setTextColor(colorStateList);
        textIncreased.setTextColor(colorStateList);
        pdfMaker.setBackgroundTintList(colorStateList);
        statusBarChanger(colorString);
        textChanger(type);
        if(type.equals("Stunting")){
            textChanger("Stunted and Severe Stunted");
        } else if (type.equals("Underweight")) {
            textChanger("Underweight and Severe Underweight");
        } else if (type.equals("Overweight")) {
            textChanger("Overweight and Severe Overweight");
        } else if (type.equals("Wasting")) {
            textChanger("Wasted and Severe Wasted");
        }
    }
    public void textChanger(String typeTitle){
        textTypeTitle.setText(typeTitle);
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
    public void preSelected(){
        String text_Date = getDateNowFormatted();
        String[] status_array = {"Underweight", "Severe Underweight"};
        textDate.setText(getDateNowFormatted(), false);
        textType.setText("Underweight",false);
        int preColor = Color.parseColor("#51ADE5");
        UIChanger(preColor,"Underweight");
        statusBarChanger(preColor);
        Populate(status_array, "U and SU", text_Date);
    }
    public String getDateNowFormatted(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public void Populate(String[] status_array, String query_Type, String text_Date){
        dialog2.show();
        db.collection("children")
                .whereEqualTo("monthAdded", text_Date)
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
                    ArrayList<BarangayModel> barangayModels = dataFiltering(arrayList, status_array ,query_Type);
                    barangayQuery(barangayModels, arrayList.size(), status_array);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Prevailance_Reports.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<BarangayModel> dataFiltering(ArrayList<Child> arrayList, String[] status_array ,String queryType){
        ArrayList<BarangayModel> barangayModels = new ArrayList<>();
        String[] barangayList = getResources().getStringArray(R.array.barangay);
        for(String barangay: barangayList){
            BarangayModel barangayModel = new BarangayModel();
            int count = 0;
            int count_normal = 0;
            int count_barangay = 0;
            for(Child arrayList1: arrayList){
                boolean isBarangay = arrayList1.getBarangay().equals(barangay);
                boolean isStatus1 = arrayList1.getStatus().contains(status_array[0]);
                boolean isStatus2 = arrayList1.getStatus().contains(status_array[1]);
                boolean isNormalWFA = arrayList1.getStatus().get(0).equals("Normal");
                boolean isNormalHFA = arrayList1.getStatus().get(1).equals("Normal");
                boolean isNormalWFH = arrayList1.getStatus().get(2).equals("Normal") ||
                        arrayList1.getStatus().get(2).equals("");

                if(isBarangay && (isStatus1|| isStatus2)){
                    count++;
                }
                if(isBarangay){
                    count_barangay++;
                    if(queryType.equals("U and SU")){
                        if(isNormalWFA){
                            count_normal++;
                        }
                    } else if (queryType.equals("OW and OB")) {
                        if(isNormalWFH){
                            count_normal++;
                        }
                    } else if (queryType.equals(("S and SS"))) {
                        if(isNormalHFA){
                            count_normal++;
                        }
                    } else if (queryType.equals("W and SW")) {
                        if(isNormalWFH){
                            count_normal++;
                        }
                    }
                }
            }
            barangayModel.setNormal(count_normal);
            barangayModel.setTotalAssess(count_barangay);
            barangayModel.setBarangay(barangay);
            barangayModel.setTotalCase(count);
            barangayModel.setQueryType(queryType);
            barangayModels.add(barangayModel);
        }
        Collections.sort(barangayModels, new Comparator<BarangayModel>() {
            @Override
            public int compare(BarangayModel b1, BarangayModel b2) {
                return Integer.compare(b2.getTotalCase(), b1.getTotalCase());
            }
        });

        int count_rank = 1;
        for(BarangayModel barangayNow: barangayModels){
            barangayNow.setRank(count_rank);
            count_rank++;
        }
        return barangayModels;
    }

    public String setQueryType(String type){
        String queryType = "";
        if(type.equals("S")){
            queryType = "S and SS";
        }else if(type.equals("O")){
            queryType = "OW and OB";
        }
        else if(type.equals("U")){
            queryType = "U and SU";
        }
        else if(type.equals("W")){
            queryType = "W and SW";
        }
        return  queryType;
    }

    public void barangayQuery(ArrayList<BarangayModel> barangayModels, int childTotal, String[] status_array){
        db.collection("barangay")
                .whereNotEqualTo("identifier", "All Beneficiaries")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<BarangayModel> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                BarangayModel barangayModel = doc.toObject(BarangayModel.class);
                                barangayModel.setBarangay(doc.getId());
                                arrayList.add(barangayModel);
                            }
                            int count = 0;
                            for(BarangayModel myFinal: barangayModels){
                                for(BarangayModel list: arrayList){
                                    if(myFinal.getBarangay().equals(list.getBarangay())){
                                        myFinal.setEstimatedChildren(list.getEstimatedChildren());
                                        break;
                                    }
                                }
                                count = count + myFinal.getTotalCase();
                            }


                            int totalChildren = count;
                            float result = (float) totalChildren / childTotal ;
                            float prevPer = Math.round(result * 100.0f);
                            textDataThisMonth.setText("" + totalChildren);
                            textDataIncreased.setText("" + prevPer + "%");
                            userAdapter = new BarangayAdapter(Prevailance_Reports.this, barangayModels);
                            barangayRecycler.setAdapter(userAdapter);
                            dialog2.dismiss();

                            pdfMaker.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createPdf(barangayModels, status_array);
                                }
                            });
                            userAdapter.setOnItemClickListener(new BarangayAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(BarangayModel barangayModel) {

                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Prevailance_Reports.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createPdf(ArrayList<BarangayModel> arrayList, String[] status_array){
        Rectangle customsize = new Rectangle(
                8.5f*72,
                13.0f*72
        );
        String type = status_array[0] + " + " + status_array[1];
        PR_PdfUtils pdfUtils = new PR_PdfUtils(status_array, methodType, arrayList, customsize, type);
        byte[] pdfBytes = pdfUtils.PdfSetter();
        showPdfDialog(pdfBytes, status_array);
    }

    private void showPdfDialog(byte[] pdfBytes, String[] status_array){
        final Dialog dialog = new Dialog(Prevailance_Reports.this);
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
                String filename = "PrevailanceReports" + status_array[0] + "_" + status_array[1] +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + ".pdf";
                Pdf_Utils pdf_utils = new Pdf_Utils(getContentResolver(), pdfBytes, Prevailance_Reports.this, filename);
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

}