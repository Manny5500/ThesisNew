package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.Rectangle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;

public class ManageData extends Fragment {

    View view;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    private CDAdapter userAdapter;
    int whiteColor;
    private String  userid;

    String barangayString = "";


    FloatingActionButton addData, pdfMaker;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Initialize FirebaseApp when the fragment is attached to a context
        FirebaseApp.initializeApp(requireContext());
        whiteColor = ContextCompat.getColor(context, R.color.viola);
        userAdapter = new CDAdapter(requireContext(), new ArrayList<>());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_manage_data2, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler);
        addData = view.findViewById(R.id.addData);
        pdfMaker = view.findViewById(R.id.pdfMaker);

        userid = ((PersonnelActivity)getActivity()).userid;


        Populate();
        SearchView searchView = view.findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(whiteColor);
        searchEditText.setHintTextColor(whiteColor);
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

        addDataEvent();

        return view;
    }

    public void Populate(){

        db.collection("users").document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                barangayString = documentSnapshot.getString("barangay");
               Populate_now(String.valueOf(documentSnapshot.getString("barangay")));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void Populate_now(String barangayString){
        db.collection("children").whereEqualTo("barangay", barangayString).orderBy("dateAdded", Query.Direction.DESCENDING)
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

                    userAdapter = new CDAdapter(getContext(), RemoveDuplicates.removeDuplicates(arrayList));
                    recyclerView.setAdapter(userAdapter);
                    pdfMakerEvent(RemoveDuplicates.removeDuplicates(arrayList));

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase myexception", ""+e);
                Toast.makeText(requireContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(App.AddFlag == 1){
            Populate();
            App.AddFlag = 0;
        }
    }

    private void addDataEvent(){
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AddParentData.class));
            }
        });
    }

    private void pdfMakerEvent(ArrayList<Child> childList){
        pdfMaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf(childList);
            }
        });
    }

    public void createPdf(ArrayList<Child> arrayList){
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.optlogojp);
        Rectangle customsize = new Rectangle(
                13.0f*72,
                8.5f*72
        );
        ML_PdfUtils mlPdfUtils = new ML_PdfUtils(customsize,arrayList, drawable, barangayString, getDateNowFormatted());
        byte[] pdfBytes = mlPdfUtils.PdfSetter();
        showPdfDialog(pdfBytes);
    }

    private void showPdfDialog(byte[] pdfBytes){
        final Dialog dialog = new Dialog(requireContext());
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
                Pdf_Utils pdf_utils = new Pdf_Utils(requireContext().getContentResolver(), pdfBytes, requireContext(), filename);
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
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    pdfView.fromBytes(pdfBytes)
                            .scrollHandle(new DefaultScrollHandle(requireContext()))
                            .load();
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    e.printStackTrace();
                    Log.e("PDFViewer", "Error loading PDF: " + e.getMessage());
                });
            }
        });
    }

    public String getDateNowFormatted(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }





}