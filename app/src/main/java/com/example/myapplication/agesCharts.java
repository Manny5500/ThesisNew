package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class agesCharts extends AppCompatActivity {
    private FirebaseFirestore db;
    String status = "";
    ArrayList<Timestamp> timestampArrayList;
    String[] hexColor;
    String[] tableHeader = {"Ages and Status", "Total", "Percentage"};
    String[] labels = {"0-5", "6-11", "12-23", "24-35", "36-47", "48-59"};
    ArrayList<BarEntry> entries2 = new ArrayList<>();
    ArrayList<BarEntry> entries3 = new ArrayList<>();
    ArrayList<Integer> tableValues = new ArrayList<>();
    String query;
    int[] ages_number;
    int[] colors2;

    int total;

    int loop_count;
    String[][] statusList;

    Switch switchToggleAges;
    boolean checked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ages_charts);
        db = FirebaseFirestore.getInstance();
        timestampArrayList = getIntent().getParcelableArrayListExtra("timestampArrayList");
        switchToggleAges = findViewById(R.id.switchToggleAges);
        displayData();
        switchToggleAges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                displayData();
            }
        });
    }

    private  void displayData(){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(0))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(1))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            GetSexAgeStats(task);
                        } else {
                            Toast.makeText(agesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(agesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void GetSexAgeStats(Task<QuerySnapshot> task){

        statusList = StringUtils.statusList;
        int[] chartIdList = {R.id.agesChartUW, R.id.agesChartOW,
                R.id.agesChartSS, R.id.agesChartWA};
        int[] tableIdList = {R.id.agesTableUW, R.id.agesTableOW,
                R.id.agesTableSS, R.id.agesTableWA};

        tableValues.clear();
        total = 0;
        query = "all";
        ages_number = getAgesNumber(task);
        entries2 = populateEntries();
        colors2 = ColorTemplate.PASTEL_COLORS;
        barChartSetter(R.id.agesChartAll);
        tableSetter(R.id.agesTableAll);
        query = "";


        for (int i = 0; i < 4; i++) {
            tableValues.clear();
            total=0;
            status = statusList[i][0];
            ages_number = getAgesNumber(task);
            entries2 = populateEntries();

            status = statusList[i][1];
            ages_number = getAgesNumber(task);
            entries3 = populateEntries();

            ColorUtils colorUtils = new ColorUtils(hexColor);
            colors2 = colorUtils.colorArray(i);

            loop_count = i;
            barChartSetter(chartIdList[i]);
            tableSetter(tableIdList[i]);
        }

    }
    private void tableSetter(int tableId){
        TableLayout sextableLayout = findViewById(tableId);
        String[][] sexData;
        for(int n: tableValues){
            total = n+total;
        }
        if(query.equals("all")||!checked){
            sexData = withoutSevere();
        }else{
            sexData = withSevere();
        }
        TableSetter.generateTable(this, sextableLayout, tableHeader, sexData);
    }

    private String[][] withoutSevere(){
        String [][] ageData = new String[6][3];
        for (int i=0; i<6;i++){
            String label = "";
            label = labels[i];
            ageData[i][0] = label;
            ageData[i][1] = String.valueOf(tableValues.get(i));
            ageData[i][2] = StringUtils.percentageFormat(tableValues.get(i),total);
        }
        return ageData;
    }
    private String[][] withSevere(){
        String[][] sexData = new String[12][3];
        for(int i = 0; i<6;i++){
            String label = "";
            label = labels[i] + " (" +
                    StringUtils.labelChanger(statusList[loop_count][0]) + ")";
            sexData[i][0] = label;
            sexData[i][1] = String.valueOf(tableValues.get(i));
            sexData[i][2] = StringUtils.percentageFormat(tableValues.get(i),total);
        }
        for(int i = 0; i<6;i++){
            String label = "";
            label = labels[i] +  " (" +
                    StringUtils.labelChanger(statusList[loop_count][1]) + ")";
            int j= 6+i;
            sexData[j][0] = label;
            sexData[j][1] = String.valueOf(tableValues.get(j));
            sexData[j][2] = StringUtils.percentageFormat(tableValues.get(j),total);
        }
        query = "";
        return StringUtils.interLeave(sexData);
    }

    private ArrayList<BarEntry> populateEntries(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i=0; i<6; i++){
            entries.add(new BarEntry(i, ages_number[i]));
            tableValues.add(ages_number[i]);
        }
        return entries;
    }

    private int[] getAgesNumber(Task<QuerySnapshot> task){

        int age0_5, age6_11, age12_23, age24_35, age36_47, age48_59;
        age0_5 = age6_11 = age12_23 = age24_35 = age36_47 = age48_59 = 0;

        for (QueryDocumentSnapshot doc : task.getResult()) {

            Child child = doc.toObject(Child.class);
            child.setId(doc.getId());

            boolean condition = (query.equals("all")) ?
                    !child.getStatusdb().contains("Normal")
                    : child.getStatusdb().contains(status);

            int monthdiff = getMonthDiff(child.getBirthDate());

            if(monthdiff>=0 && monthdiff<=5 && condition){
                age0_5++;
            } else if (monthdiff>=6 && monthdiff<=11 && condition) {
                age6_11++;
            } else if (monthdiff>=12 && monthdiff<=23 && condition) {
                age12_23++;
            } else if (monthdiff>=24 && monthdiff<=35 && condition) {
                age24_35++;
            } else if (monthdiff>=36 && monthdiff<=47 && condition) {
                age36_47++;
            } else if (monthdiff>=48 && monthdiff<=59 && condition) {
                age48_59++;
            }
        }
        return new int[]{age0_5, age6_11, age12_23 , age24_35, age36_47, age48_59};
    }

    private  void barChartSetter(int chartId){
        BarChart barChart2 = findViewById(chartId);
        if(query.equals("all")||!checked){
            ChartMaker.editBarChart(barChart2,entries2, labels, colors2);

        }else{
            ChartMaker.editBarChart(barChart2,entries2, entries3, labels, colors2);
        }
    }
    private int getMonthDiff(String dateString){
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(dateString);
        int monthdiff = 0;
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        }
        return monthdiff;
    }

}