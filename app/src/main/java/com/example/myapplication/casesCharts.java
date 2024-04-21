package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
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
import java.util.List;
import java.util.Map;

public class casesCharts extends AppCompatActivity {

    ArrayList<Timestamp> timestampArrayList;

    String periodType;
    private FirebaseFirestore db;

    int currentTotalCase, currentObserve, currentChart, currentTable;

    int[] chartIdList, totalCaseList,
            observeList, tableIdList;
    String[][] statusList;
    String status1;
    String status2;
    boolean isSwitchEnabled = false;
    String[] tableHeader = {"Status", "Total", "Percentage"};

    String[] hexColor;
    int[] colors2;

    long duration;

    Switch switchToggleCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases_charts);
        switchToggleCases = findViewById(R.id.switchToggleCases);
        timestampArrayList = getIntent().getParcelableArrayListExtra("timestampArrayList");
        periodType = getIntent().getStringExtra("periodType");
        db = FirebaseFirestore.getInstance();
        isSwitchEnabled = false;
        periodSelector();
        switchToggleCases.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwitchEnabled = isChecked;
                periodSelector();
            }
        });
    }

    private void periodSelector(){
        switch (periodType){
            case "week":
                duration = 7;
                break;
            case "month":
                duration = 29;
                break;
            case "year":
                duration = 364;
                break;
        }
        firstFetch();
    }

    private static Map<String, Integer> aggregateDataByDay(List<Child> dataList,
                                                           Date startDate, Date currentDate) {
        String[] dateArray = DateParser.createDateArray(startDate, currentDate);
        Map<String, Integer> aggregatedData = new LinkedHashMap<>();

        for (String day: dateArray){
            int count = 0;
            for(Child child: dataList){
                if(child.dateString().equals(day)){
                    count++;
                }
            }
            aggregatedData.put(day, count);
        }

        return aggregatedData;
    }
    private static Map<String, Integer> aggregateDataByMonth(List<Child>dataList, Date startDate, Date currentDate){
        String[] dateArray = DateParser.createDateArrayForMonth(startDate, currentDate);
        Map<String, Integer> aggregatedData = new LinkedHashMap<>();

        for (String month: dateArray){
            int count = 0;
            for(Child child: dataList){
                if(child.dateString().substring(0,7).equals(month)){
                    count++;
                }
            }
            aggregatedData.put(month, count);
        }
        return aggregatedData;
    }

    private void firstFetch(){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(0))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(1))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Child> currentChildrenList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                currentChildrenList.add(child);}

                            secondFetch(currentChildrenList);

                        } else {
                            Toast.makeText(casesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(casesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void secondFetch(ArrayList<Child> currentChildrenList){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(2))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(3))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Child> previousChildrenList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                previousChildrenList.add(child);}

                            displayUI(currentChildrenList, previousChildrenList);

                        } else {
                            Toast.makeText(casesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(casesCharts.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private Map<String, Integer> getAggregatedData(int position1, int position2,
                                                   long duration, ArrayList<Child> filteredChildrenList){
        Map<String, Integer> aggregatedData = new LinkedHashMap<>();
        ArrayList<Date> startCurrentDateNow = DateParser.createCurrentStartDate(duration,periodType);
        if(periodType.equals("year")){
            aggregatedData = aggregateDataByMonth(filteredChildrenList, startCurrentDateNow.get(position2),
                    startCurrentDateNow.get(position1));

        }else{
            aggregatedData = aggregateDataByDay(filteredChildrenList, startCurrentDateNow.get(position2),
                    startCurrentDateNow.get(position1));

        }
        return aggregatedData;
    }

    private ArrayList<Child> filterChildrenList(ArrayList<Child> childArrayList){
        ArrayList<Child> filteredChildrenList = new ArrayList<>();
        for(Child child: childArrayList){
            if(child.getStatusdb().contains(status1)||child.getStatusdb().contains(status2))
                filteredChildrenList.add(child);
        }
        return filteredChildrenList;
    }

    private ArrayList<Child> filterChildrenList(ArrayList<Child> childArrayList, String status){
        ArrayList<Child> filteredChildrenList = new ArrayList<>();
        for(Child child: childArrayList){
            if(child.getStatusdb().contains(status))
                filteredChildrenList.add(child);
        }
        return filteredChildrenList;
    }

    private void updateLabels(int currentRecord, int previousRecord){
        TextView labelTotalCase = findViewById(currentTotalCase);
        TextView labelObservation = findViewById(currentObserve);

        int observation = previousRecord - currentRecord;
        String observeStatus = "";
        float differences = Math.abs(differences(currentRecord, previousRecord));

        int colorNow = Color.parseColor("#000000");
        String withPercent = String.format("%.2f", differences) + " %";

        if(currentRecord == observation){
            observeStatus = "Just as the same";
        } else if (currentRecord > observation) {
            observeStatus = withPercent + " more than previous " + periodType;
            colorNow = Color.parseColor("#FF0000");
        } else if (currentRecord < observation){
            observeStatus = withPercent + " less than previous " + periodType;
            colorNow = Color.parseColor("#097969");
        }

        labelTotalCase.setText(String.valueOf(currentRecord));
        labelObservation.setText(observeStatus);
        labelObservation.setTextColor(colorNow);

    }

    private float differences(int currentRecord, int previousRecord){
        float difference = currentRecord - previousRecord;
        float rate;
        if(previousRecord==0){
            rate = 1.00f;
        } else{
            rate = difference / (float) previousRecord ;
        }
        return rate * 100;
    }

    private void displayUI( ArrayList<Child> currentChildrenList, ArrayList<Child> previousChildrenList){

        chartIdList = new int[]{R.id.casesChartUW, R.id.casesChartOW, R.id.casesChartSS, R.id.casesChartWA};
        totalCaseList = new int[]{R.id.labelUWCase, R.id.labelOWCase, R.id.labelSSCase, R.id.labelWACase};
        observeList = new int[]{R.id.labelObsUW, R.id.labelObsOW, R.id.labelObsSS, R.id.labelObsWA};
        tableIdList = new int[]{R.id.casesTableUW, R.id.casesTableOW, R.id.casesTableSS, R.id.casesTableWA};
        statusList = StringUtils.statusList;

        for(int i=0; i<4;i++){
            status1 = statusList[i][0];
            status2 = statusList[i][1];
            currentTotalCase = totalCaseList[i];
            currentObserve = observeList[i];
            currentChart = chartIdList[i];
            currentTable = tableIdList[i];


            ColorUtils colorUtils = new ColorUtils(hexColor);
            colors2 = colorUtils.colorArray(i);

            if(isSwitchEnabled)
                fourDataSet(currentChildrenList, previousChildrenList);
            else
                twoDataSet(currentChildrenList, previousChildrenList);

        }
    }

    private String[][] fourTableData(int[] currentRecord){
        String[] statusList = {status1, status2};
        String[][] tableData = new String[2][3];
        int total = 0;
        for (int i=0; i<2; i++){
            total = currentRecord[i] + total;
        }
        for(int i=0; i<2; i++){
            tableData[i][0] = statusList[i];
            tableData[i][1] = String.valueOf(currentRecord[i]);
            tableData[i][2] = StringUtils.percentageFormat(currentRecord[i], total);
        }

        return tableData;
    }

    private String[][] twoTableData(int currentRecord){
        String[][] tableData = new String[1][2];
        tableData[0][0] = status1;
        tableData[0][1] = String.valueOf(currentRecord);

        return tableData;
    }

    private void twoDataSet(ArrayList<Child> currentChildrenList, ArrayList<Child> previousChildrenList){
        ArrayList<Child> filteredChildrenList = filterChildrenList(currentChildrenList);
        Map<String, Integer> aggregatedData = getAggregatedData(0,1, duration, filteredChildrenList);
        int currentRecord = filteredChildrenList.size();

        filteredChildrenList = filterChildrenList(previousChildrenList);
        //duration originally here is 6
        Map<String, Integer> aggregatedData2 = getAggregatedData(2, 3, duration, filteredChildrenList);
        int previousRecord = filteredChildrenList.size();

        LineChart lineChart = findViewById(currentChart);
        ChartMaker.editLineChart(lineChart,aggregatedData,aggregatedData2, periodType);


        TableLayout tableLayout = findViewById(currentTable);
        String[][] tableData = twoTableData(currentRecord);
        TableSetter.generateTable(this, tableLayout, new String[]{"Status", "Total"}, tableData);

        updateLabels(currentRecord, previousRecord);
    }

    private void fourDataSet(ArrayList<Child> currentChildrenList, ArrayList<Child> previousChildrenList){

        ArrayList<Child> filteredChildrenList = filterChildrenList(currentChildrenList, status1);
        Map<String, Integer> aggregatedData = getAggregatedData(0,1, duration, filteredChildrenList);
        int currentRecord1 = filteredChildrenList.size();

        //duration originally here is 6
        filteredChildrenList = filterChildrenList(previousChildrenList, status1);
        Map<String, Integer> aggregatedData2 = getAggregatedData(2, 3, duration, filteredChildrenList);
        int previousRecord1 = filteredChildrenList.size();

        filteredChildrenList = filterChildrenList(currentChildrenList, status2);
        Map<String, Integer> aggregatedData3 = getAggregatedData(0,1, duration, filteredChildrenList);
        int currentRecord2 = filteredChildrenList.size();

        //duration originally here is 6
        filteredChildrenList = filterChildrenList(previousChildrenList, status2);
        Map<String, Integer> aggregatedData4 = getAggregatedData(2, 3, duration, filteredChildrenList);
        int previousRecord2 = filteredChildrenList.size();

        LineChart lineChart = findViewById(currentChart);
        ChartMaker.fourDataLineChart(lineChart,aggregatedData,aggregatedData2, aggregatedData3,aggregatedData4,periodType);

        TableLayout tableLayout = findViewById(currentTable);
        String[][] tableData = fourTableData(new int[]{currentRecord1, currentRecord2});
        TableSetter.generateTable(this, tableLayout, tableHeader, tableData);

        updateLabels(currentRecord1+currentRecord2, previousRecord1+previousRecord2);

    }

}