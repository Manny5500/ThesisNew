package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
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

public class Malnourished_List extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView recyclerView;

    MaterialAutoCompleteTextView textBarangay, textDate;
    String text_Date, text_Barangay;

    private ChildAdapter userAdapter;

    Dialog dialog2;

    FloatingActionButton pdfMaker;
    ArrayList<Child> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_malnourished_list);
        db = FirebaseFirestore.getInstance();

        textDate = findViewById(R.id.textDate);
        textBarangay = findViewById(R.id.textBarangay);

        recyclerView = findViewById(R.id.recycler);

        String[] brgyList = getResources().getStringArray(R.array.barangay);
        FormUtils.setAdapter(brgyList, textBarangay, this);
        dateAdapter();
        preSelected();
        Populate();

        textDateEvent();
        textBarangayEvent();

        SearchView searchView = findViewById(R.id.searchView);
        dialog2 = new Dialog(Malnourished_List.this);
        dialog2.setContentView(R.layout.dialog_loader);
        pdfMaker = findViewById(R.id.pdfMaker);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    public void Populate(){
        db.collection("children")
                .whereEqualTo("monthAdded", text_Date )
                .whereEqualTo("barangay", text_Barangay )
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            arrayList.clear();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                arrayList.add(child);
                            }

                            arrayList = RemoveDuplicates.removeDuplicates(arrayList);
                            ArrayList<Child> fAL = new ArrayList<>();
                            for(Child child: arrayList){
                                /*
                                boolean isNormal = child.getStatusdb().contains("Normal");
                                if(!isNormal) {
                                    fAL.add(child);
                                }*/
                                fAL.add(child);
                            }
                            userAdapter = new ChildAdapter(Malnourished_List.this, fAL);
                            recyclerView.setAdapter(userAdapter);

                            userAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Child child) {
                                }
                            });

                            pdfMaker.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    createPdf(arrayList);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore Error", "Error: "+e);
                        Toast.makeText(Malnourished_List.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void createPdf(ArrayList<Child> arrayList){
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.optlogojp);
        Rectangle customsize = new Rectangle(
                13.0f*72,
                8.5f*72
        );
        ML_PdfUtils mlPdfUtils = new ML_PdfUtils(customsize,arrayList, drawable, text_Barangay, text_Date);
        byte[] pdfBytes = mlPdfUtils.PdfSetter();
        showPdfDialog(pdfBytes);
    }

    private void showPdfDialog(byte[] pdfBytes){
        final Dialog dialog = new Dialog(Malnourished_List.this);
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
                String filename = "Malnourished List"  +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS")) + ".pdf";
                Pdf_Utils pdf_utils = new Pdf_Utils(getContentResolver(), pdfBytes, Malnourished_List.this, filename);
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

}