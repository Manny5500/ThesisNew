package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class genderCharts extends AppCompatActivity {
    private FirebaseFirestore db;
    String status1 = "";
    String status2 = "";
    ArrayList<Timestamp> timestampArrayList;
    Switch switchToggleGender;
    String[] hexColor;
    int[] statusCount;
    String[] tableHeader = {"Sex and Status", "Total", "Percentage"};

    String query;

    boolean checked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_charts);
        switchToggleGender = findViewById(R.id.switchToggleGender);

        db = FirebaseFirestore.getInstance();
        timestampArrayList = getIntent().getParcelableArrayListExtra("timestampArrayList");
        displayData();
        switchToggleGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                displayData();
            }
        });
    }
    public void displayData(){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(0))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(1))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            GetSexAgeStats(task);
                        } else {
                            Toast.makeText(genderCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(genderCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GetSexAgeStats(Task<QuerySnapshot> task){
        statusCount = getCount(task);
        int result_Count = statusCount[0]+statusCount[1];
        int[] colors1 = {Color.parseColor("#e5aeb3"), Color.parseColor("#e5ceb3"), Color.WHITE};
        PieChart sexChart = findViewById(R.id.genderChartAll);
        ChartMaker.editPieChart(sexChart, colors1, statusCount[0], statusCount[1], result_Count, "Gender Distribution");
        tableSetterAll(R.id.genderTableAll);

        String[][] statusList = {
                {"Underweight", "Severe Underweight"},
                {"Overweight", "Obese"},
                {"Stunted", "Severe Stunted"},
                {"Wasted", "Severe Wasted"}
        };

        String[][] hexColorList = {
                {"#FF7000", "#FFAC4A", "#FFE9C9", "#F9C87C"},
                {"#F8858B", "#FF6663", "#E54C38", "#C23A22"},
                {"#0E361C", "#19541F", "#446C48", "#7C9060"},
                {"#bf8bff", "#cca3ff", "#dabcff", "#e5d0ff"}
        };

        int[] pieChartIdList = {R.id.genderChartUW, R.id.genderChartOW,
                R.id.genderChartSS, R.id.genderChartWA};

        int[] tableIdList = {R.id.genderTableUW, R.id.genderTableOW,
        R.id.genderTableSS, R.id.genderTableWA};

        for(int i = 0; i<4; i++){
            status1 = statusList[i][0];
            status2 = statusList[i][1];
            hexColor = new String[] {
                    hexColorList[i][0], hexColorList[i][1],
                    hexColorList[i][2], hexColorList[i][3]
            };
            statusCount = getCountCat(task);
            pieChartSetter(pieChartIdList[i]);
            tableSetter(tableIdList[i]);
        }
    }

    private  void pieChartSetter(int chartId){
        int[] colors2 = colorArray(hexColor);
        PieChart uwChart = findViewById(chartId);
        ChartMaker.editPieChart(uwChart, colors2, status1,
                status1, status2, statusCount, checked);

    }

    private void tableSetter(int tableId){
        TableLayout sextableLayout = findViewById(tableId);
        int totalCount = 0;
        for(int i=0; i<4; i++){
            totalCount = totalCount + statusCount[i];
        }
        String[][] sexData;
        sexData = dataSex(totalCount);
        TableSetter.generateTable(this, sextableLayout, tableHeader, sexData);
    }
    private void tableSetterAll(int tableId){
        TableLayout sextableLayout = findViewById(tableId);
        int totalCount = 0;
        for(int i=0; i<2; i++){
            totalCount = totalCount + statusCount[i];
        }
        String[][] sexData;
        query="all";
        sexData = dataSex(totalCount);
        TableSetter.generateTable(this, sextableLayout, tableHeader, sexData);
    }
    private String[][] dataSex(int totalCount){
        double[] doublePercentage = {0,0};
        String[] testPercentage = {"",""};
        String[][] sexData = {
                {"","",""},{"","",""}
        };
        if(checked && statusCount.length==4){
            doublePercentage = new double[]{0, 0, 0, 0};
            testPercentage = new String[]{"","","",""};
            sexData = new String[][]{{"","",""},{"","",""},{"","",""},{"","",""}};
        } else if(!query.equals("all")){
            statusCount = new int[]{statusCount[0],statusCount[2]};
        }
        if(totalCount == 0) {
            for(int i = 0; i<statusCount.length; i++){
                doublePercentage[i] = 0;
            }
        }
        else{
            for(int i = 0; i<statusCount.length; i++){
                doublePercentage[i] = (double) statusCount[i]/totalCount*100;
            }
        }

        for(int i = 0; i<statusCount.length; i++){
            testPercentage[i] = String.format("%.2f", doublePercentage[i]) + " %";
        }

        return updateSexData(sexData,testPercentage);
    }
    private String[][] updateSexData(String[][] sexData, String[] testPercentage){
        StringUtils stringUtils = new StringUtils(status1,status2,query);
        for(int i = 0; i<testPercentage.length;i++){
            String label = "";
                if(testPercentage.length==2){
                    label = stringUtils.labelFormat(i);
                } else if (testPercentage.length==4) {
                    label = stringUtils.labelFormatFour(i);
                }
                sexData[i][0] = label;
                sexData[i][1] = String.valueOf(statusCount[i]);
                sexData[i][2] = testPercentage[i];
        }
        query = "";
        return sexData;
    }
    private int[] getCount(Task<QuerySnapshot> task){
        int[] counts = {0,0};
        for (QueryDocumentSnapshot doc : task.getResult()) {
            Child child = doc.toObject(Child.class);
            child.setId(doc.getId());
            boolean isMale = child.getSex().equals("Male");
            boolean isFemale = child.getSex().equals("Female");
            boolean isNormal = child.getStatusdb().contains("Normal");
            if(isMale && !isNormal){
                counts[0]++;
            }
            if (isFemale && !isNormal) {
                counts[1]++;
            }
        }
        return counts;
    }

    private int[] getCountCat(Task<QuerySnapshot> task){
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int count4 = 0;
        for (QueryDocumentSnapshot doc : task.getResult()) {
            Child child = doc.toObject(Child.class);
            child.setId(doc.getId());
            boolean isMale = child.getSex().equals("Male");
            boolean isFemale = child.getSex().equals("Female");
            boolean isStatus1 = child.getStatusdb().contains(status1);
            boolean isStatus2 = child.getStatusdb().contains(status2);
            if (isMale && isStatus1){
                count1++;
            }
            if(isMale && isStatus2) {
                count2++;
            }
            if(isFemale && isStatus1) {
                count3++;
            }
            if(isFemale && isStatus2) {
                count4++;
            }
        }
        return new int[]{count1, count2, count3, count4};
    }
    public int[] colorArray(String[] hexColor){
        int[] arrayColor = { 0, 0, 0, 0};
        for(int i=0; i<4; i++){
            arrayColor[i] = Color.parseColor(hexColor[i]);
        }
        return arrayColor;
    }


}