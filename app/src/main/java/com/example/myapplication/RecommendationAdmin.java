package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class RecommendationAdmin extends AppCompatActivity {
    FirebaseFirestore db;
    int [] feeding_barangay = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    int[] index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    public ArrayList<String> cMOC = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_admin);

        db = FirebaseFirestore.getInstance();
        String [] barangay = getResources().getStringArray(R.array.barangay);


        db.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Child> childrenList = new ArrayList<>();
                    FeedingProgramBarangay(task,childrenList,barangay);
                    childrenList.clear();

                } else {
                    Toast.makeText(RecommendationAdmin.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RecommendationAdmin.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void generateTable(TableLayout tableLayout, String[] headers, String[][] data, String which) {
        TableRow headerRow = new TableRow(this);
        FireStoreUtility fireStoreUtility = new FireStoreUtility(cMOC);

        for (int i = 0; i < headers.length; i++) {
            TextView headerTextView = new TextView(this);
            headerTextView.setText(headers[i]);
            TableSetter.Costumize(headerRow, headerTextView);
            if(i==0){
                headerTextView.setGravity(Gravity.START);
            }else{
                headerTextView.setGravity(Gravity.END);
            }
            TableSetter.setTextSizeAndPaddingForTextViews(18, 16, headerTextView);
            headerRow.addView(headerTextView);
        }

        tableLayout.addView(headerRow);

        // Populate table rows with data
        for (String[] rowData : data) {
            TableRow tableRow = new TableRow(this);

            // Create cells for each column in the row
            for (int i= 0; i<rowData.length; i++){
                TextView cellTextView = new TextView(this);
                cellTextView.setText(rowData[i]);
                TableSetter.Costumize(tableRow, cellTextView);
                if(i==0){
                    cellTextView.setGravity(Gravity.START);
                }else{
                    cellTextView.setGravity(Gravity.END);
                }
                TableSetter.setTextSizeAndPaddingForTextViews(18, 16, cellTextView);
                tableRow.addView(cellTextView);
                String maybe = rowData[0];
                //rowData.length-1 means the last column
                if(i==rowData.length-1 && which.equals("barangay")){
                    if(Integer.parseInt(rowData[1])>0) {
                        cellTextView.setTextColor(Color.parseColor("#0000FF"));
                        fireStoreUtility.getBarangayStatus(maybe, cellTextView, db);
                        cellTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(RecommendationAdmin.this);
                                dialog.setContentView(R.layout.dialog_assign);
                                fireStoreUtility.getBarangayDetails(maybe, dialog, db,RecommendationAdmin.this);
                                dialog.show();
                            }
                        });
                    } else{
                        cellTextView.setTextColor(Color.parseColor("#FF0000"));
                    }
                }

                if(i==rowData.length-1 && which.equals("gulayan")){

                    if(Integer.parseInt(rowData[1])>0) {
                        cellTextView.setTextColor(Color.parseColor("#0000FF"));
                        fireStoreUtility.getGulayanStatus(rowData[0], cellTextView, db);

                        cellTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(RecommendationAdmin.this);
                                dialog.setContentView(R.layout.dialog_assign);
                                fireStoreUtility.getGulayanDetails(rowData[0], dialog, rowData[0],db,RecommendationAdmin.this);
                                dialog.show();
                            }
                        });

                    } else{
                        cellTextView.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
            }
            tableLayout.addView(tableRow);
        }
    }

    public void FeedingProgramBarangay(Task<QuerySnapshot> task, ArrayList<Child> childrenList, String [] barangay){
        for (QueryDocumentSnapshot doc: task.getResult()){
            Child child = doc.toObject(Child.class);
            child.setId(doc.getId());
            childrenList.add(child);
        }
        for (int i=0; i<24; i++){
            int count_FEEDING = 0;
            for(Child child: childrenList){
                boolean barangayChild = child.getBarangay().equals(barangay[i]);
                boolean isUW = child.getStatusdb().contains("Underweight");
                boolean isW = child.getStatusdb().contains("Wasted");
                boolean isS = child.getStatusdb().contains("Normal");
                String bdate = child.getBirthDate();
                FormUtils formUtils = new FormUtils();
                Date parsedDate = formUtils.parseDate(bdate);
                int monthdiff = 0;
                if (parsedDate != null) {
                    monthdiff = formUtils.calculateMonthsDifference(parsedDate);
                }
                if(barangayChild){
                    if((isUW||isS||isW) && (monthdiff>23)){
                        count_FEEDING++;
                    }

                }
            }
            feeding_barangay[i] = count_FEEDING;
        }
        RankBarangay();
        feedingTable(barangay, task.getResult().size());
        GSBUtils gUt = new GSBUtils(childrenList);
        cMOC = gUt.mocString();
        gsbTable(gUt.getMOC(), gUt.getMal(),
                gUt.getLI(), gUt.getGSBAll());
    }

    private void RankBarangay(){
        for (int i = 0; i < feeding_barangay.length - 1; i++) {
            for (int j = i + 1; j < feeding_barangay.length; j++) {
                if (feeding_barangay[i] > feeding_barangay[j]) {
                    // Swap malnutrition_rate[i] and malnutrition_rate[j]
                    int tempRate = feeding_barangay[i];
                    feeding_barangay[i] = feeding_barangay[j];
                    feeding_barangay[j] = tempRate;

                    // Swap index[i] and index[j]
                    int tempIndex = index[i];
                    index[i] = index[j];
                    index[j] = tempIndex;
                }
            }
        }
    }

    private void feedingTable(String[] barangay, int totalcase){
        TableLayout tableLayout = findViewById(R.id.feedingProgramTable);
        String[] barangayHeaders = {"Barangay", "Total", "Rate", "Action"};
        int[] topNumbers = {feeding_barangay[23], feeding_barangay[22], feeding_barangay[21]};
        String[][] barangayData = {
                {barangay[index[23]], Integer.toString(topNumbers[0]), String.format("%.2f",(double) topNumbers[0]/totalcase*100) + " %", "Set"},
                {barangay[index[22]], Integer.toString(topNumbers[1]), String.format("%.2f", (double) topNumbers[1]/totalcase*100) + " %", "Set"},
                {barangay[index[21]], Integer.toString(topNumbers[2]), String.format("%.2f",(double) topNumbers[2]/totalcase*100) + " %", "Set"}
        };
        generateTable(tableLayout, barangayHeaders, barangayData, "barangay");
    }

    private void gsbTable(int count_MBL, int count_M, int count_L, int count_GSB){
        String[] gulayanHeaders = {"Beneficiaries","Total", "Action"};
        String[][] gulayanData = {{"Parent with more than 1 child", Integer.toString(count_MBL),"Set"},
                {"Malnourished", Integer.toString(count_M),"Set"},
                {"Low Income", Integer.toString(count_L),"Set"},
                {"All Beneficiaries", Integer.toString(count_GSB), "Set All"}};
        TableLayout tableLayout = findViewById(R.id.GulayanTable);
        generateTable(tableLayout, gulayanHeaders, gulayanData, "gulayan" );
    }
}