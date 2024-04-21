package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class fragmentReports extends Fragment {
    View view;
    FirebaseFirestore db;
    int lightBlueColor, darkBlueColor, whiteColor;
    private Context applicationContext;

    TextView dateWeek, dateMonth, dateYear, labelTotalCase,
            labelObservation, categorySeeMore, genderSeeMore,
    barangaySeeMore, agesSeeMore ;


    Switch switchToggle;
    private static String periodType = "week";
    private static String switchStatus = "off";
    int duration = 6;
    ArrayList<Timestamp> timestampArrayList;

    public void onAttach(Context context) {
        super.onAttach(context);
        lightBlueColor = ContextCompat.getColor(context, R.color.colorLightBlue);
        darkBlueColor = ContextCompat.getColor(context, R.color.colorDarkBlue);
        whiteColor = ContextCompat.getColor(context, R.color.white);
        applicationContext = context.getApplicationContext();
        FirebaseApp.initializeApp(requireContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_reports, container, false);
        db = FirebaseFirestore.getInstance();
        dateWeek = view.findViewById(R.id.dateWeeks);
        dateMonth = view.findViewById(R.id.dateMonth);
        dateYear = view.findViewById(R.id.dateYear);
        labelTotalCase = view.findViewById(R.id.labelTotalCase);
        labelObservation = view.findViewById(R.id.labelObservation);
        switchToggle = view.findViewById(R.id.switchToggle);
        categorySeeMore = view.findViewById(R.id.categorySeeMore);
        genderSeeMore = view.findViewById(R.id.genderSeeMore);
        barangaySeeMore = view.findViewById(R.id.barangaySeeMore);
        agesSeeMore = view.findViewById(R.id.agesSeeMore);

        timestampArrayList = new ArrayList<>();

        seeMoreEvents();

        switchToggle.setVisibility(View.GONE);
        setPressed(dateWeek);
        configData(duration);
        final Drawable defaultBackground = ContextCompat.getDrawable(requireContext(), R.drawable.textview_default_background);

        periodEvents();

        switchToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchStatus = "on";
                } else {
                    switchStatus = "off";
                }
                configData(duration);
            }
        });
        return view;
    }

    private void seeMoreEvents(){
        categorySeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), casesCharts.class);
                intent.putParcelableArrayListExtra("timestampArrayList", timestampArrayList);
                intent.putExtra("periodType", periodType);
                startActivity(intent);
            }
        });
        genderSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), genderCharts.class);
                intent.putParcelableArrayListExtra("timestampArrayList", timestampArrayList);
                startActivity(intent);
            }
        });
        barangaySeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), barangayAnalytics.class);
                intent.putParcelableArrayListExtra("timestampArrayList", timestampArrayList);
                startActivity(intent);
            }
        });
        agesSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), agesCharts.class);
                intent.putParcelableArrayListExtra("timestampArrayList", timestampArrayList);
                startActivity(intent);
            }
        });
    }

    private void periodEvents(){
        dateWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressed(dateWeek);
                periodType = "week";
                duration = 6;
                configData(duration);
                switchToggle.setVisibility(View.GONE);
            }
        });

        dateMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressed(dateMonth);
                periodType = "month";
                duration = 29;
                configData(duration);
                switchToggle.setVisibility(View.VISIBLE);
                switchToggle.setText("Whole Month");
            }
        });

        dateYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPressed(dateYear);
                periodType = "year";
                duration = 364;
                configData(duration);
                switchToggle.setVisibility(View.GONE);

            }
        });
    }
    private void configData(long duration){
        String[] barangay = getResources().getStringArray(R.array.barangay);
        timestampArrayList = DateParser.createDate(duration, periodType);
        displayData(timestampArrayList, barangay, duration);
    }
    private void displayData(ArrayList<Timestamp> timestampArrayList, String[] barangay, long duration){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(0))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(1))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Child> childrenList = new ArrayList<>();
                            CategoryBarangay(task, childrenList, barangay);
                            childrenList.clear();
                            GetSexAgeStats(task, childrenList);

                            Map<String, Integer> aggregatedData = new LinkedHashMap<>();
                            ArrayList<Date> startCurrentDate = DateParser.createCurrentStartDate(duration, periodType);

                            if(periodType.equals("year")){
                                aggregatedData = aggregateDataByMonth(childrenList, startCurrentDate.get(1), startCurrentDate.get(0));

                            }else{
                                aggregatedData = aggregateDataByDay(childrenList, startCurrentDate.get(1), startCurrentDate.get(0));

                            }

                            int currentrecord = 0;
                            for (Map.Entry<String, Integer> entry : aggregatedData.entrySet()) {
                                currentrecord = currentrecord + entry.getValue();
                            }

                            getPreviousPeriod(timestampArrayList, currentrecord, aggregatedData);

                        } else {
                            Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setPressed(TextView textView){
        textView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.textview_pressed_background));
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        removePressed(textView);
    }
    private void removePressed( TextView currentTextView){
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(dateWeek);
        textViews.add(dateMonth);
        textViews.add(dateYear);
        for(TextView textView: textViews){
            if(textView==currentTextView){
                continue;
            }
            textView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.textview_default_background));
            textView.setTextColor(Color.parseColor("#000000"));
        }
    }
    private void GetSexAgeStats(Task<QuerySnapshot> task, ArrayList<Child> childrenList){
        int count_Male = 0, count_Female = 0;
        int age0_5, age6_11, age12_23, age24_35, age36_47, age48_59;
        age0_5 = age6_11 = age12_23 = age24_35 = age36_47 = age48_59 = 0;
        for (QueryDocumentSnapshot doc : task.getResult()) {
            Child child = doc.toObject(Child.class);
            child.setId(doc.getId());
            childrenList.add(child);
            Boolean isMale = child.getSex().equals("Male");
            Boolean isFemale = child.getSex().equals("Female");
            Boolean isNormal = child.getStatusdb().contains("Normal");
            if(isMale && !isNormal){
                count_Male++;
            } else if (isFemale && !isNormal) {
                count_Female++;
            }
            String dateString = child.getBirthDate();
            FormUtils formUtils = new FormUtils();
            Date parsedDate = formUtils.parseDate(dateString);
            int monthdiff = 0;
            if (parsedDate != null) {
                monthdiff = formUtils.calculateMonthsDifference(parsedDate);
            }
            if(monthdiff>=0 && monthdiff<=5 && !isNormal){
                age0_5++;
            } else if (monthdiff>=6 && monthdiff<=11 && !isNormal) {
                age6_11++;
            } else if (monthdiff>=12 && monthdiff<=23 && !isNormal) {
                age12_23++;
            } else if (monthdiff>=24 && monthdiff<=35 && !isNormal) {
                age24_35++;
            } else if (monthdiff>=36 && monthdiff<=47 && !isNormal) {
                age36_47++;
            } else if (monthdiff>=48 && monthdiff<=59 && !isNormal) {
                age48_59++;
            }
        }
        int[] colors1 = {lightBlueColor, darkBlueColor, whiteColor};
        PieChart sexChart =  ChartMaker.createPieChart(view, R.id.pieChart, colors1, count_Male, count_Female, count_Male+count_Female, "Gender Distribution");
        TableLayout sextableLayout = view.findViewById(R.id.sextableLayout);
        String[] sexHeaders = {"Sex", "Total", "Percentage"};
        double male_Percentage;
        double female_Percentage;
        if(count_Male==0 && count_Female == 0) {
            male_Percentage = 0;
            female_Percentage = 0;
        }
        else{
            male_Percentage = (double) count_Male/(count_Male+count_Female) * 100;
            female_Percentage = (double) count_Female/(count_Male+count_Female) * 100;
        }
        String[] testPercentage = {
                String.format("%.2f", male_Percentage) + " %",
                String.format("%.2f", female_Percentage) + " %"
        };
        String[][] sexData = {
                {"Male", String.valueOf(count_Male), testPercentage[0]},
                {"Female", String.valueOf(count_Female), testPercentage[1]}
        };
        TableSetter.generateTable(applicationContext, sextableLayout, sexHeaders, sexData);


        ArrayList<BarEntry> entries2 = new ArrayList<>();
        int[] ages_number = {age0_5, age6_11, age12_23, age24_35, age36_47, age48_59};
        for(int i=0; i<6; i++){ entries2.add(new BarEntry(i, ages_number[i]));}

        String[] labels2 = {"0-5", "6-11", "12-23", "24-35", "36-47", "48-59"};
        int[] colors2 = ColorTemplate.PASTEL_COLORS;

        BarChart barChart2 = ChartMaker.createBarChart(view, R.id.barChart2, entries2, labels2, colors2, "Age Group Distribution");

    }
    private void CategoryBarangay(Task<QuerySnapshot> task, ArrayList<Child> childrenList, String [] barangay){
        int [][] record = {{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}};
        //double [] malnutrition_rate = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int [] malnutrition_perbarangay = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int[] index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
        int count_U, count_SU, count_N, count_W, count_SW, count_S, count_SS, count_O;
        int count_UB, count_SUB, count_NB, count_WB, count_SWB, count_SB, count_SSB, count_OB;
        count_UB = count_SUB = count_NB = count_WB = count_SWB = count_SB = count_SSB = count_OB = 0;
        int count_ALL = 0;
        for (int i = 0; i < index.length; i++) {
            count_U = 0;
            count_SU = 0;
            count_N = 0;
            count_W = 0;
            count_SW = 0;
            count_S = 0;
            count_SS = 0;
            count_O = 0;
            count_ALL = 0;
            for (QueryDocumentSnapshot doc : task.getResult()) {
                Child child = doc.toObject(Child.class);
                child.setId(doc.getId());
                childrenList.add(child);
                Boolean barangaytrue;
                barangaytrue = child.getBarangay().equals(barangay[i]);
                Boolean isUnderWeight = child.getStatusdb().contains("Underweight");
                Boolean isNormal = child.getStatusdb().contains("Normal");
                Boolean isSevereUnderweight = child.getStatusdb().contains("Severe Underweight");
                Boolean isWasted = child.getStatusdb().contains("Wasted");
                Boolean isSevereWasted = child.getStatusdb().contains("Severe Wasted");
                Boolean isStunted = child.getStatusdb().contains("Stunted");
                Boolean isSevereStunted = child.getStatusdb().contains("Severe Stunted");
                Boolean isOverweight = child.getStatusdb().contains("Overweight");
                if(barangaytrue) {
                    if(!isNormal){
                        count_ALL++;
                    }
                    if (isUnderWeight) {
                        count_U++;
                        count_UB++;
                    }
                    if (isNormal) {
                        count_N++;
                        count_NB++;
                    }
                    if (isSevereStunted){
                        count_SS++;
                        count_SSB++;
                    }
                    if (isStunted){
                        count_S++;
                        count_SB++;
                    }
                    if (isOverweight){
                        count_O++;
                        count_OB++;
                    }
                    if (isWasted){
                        count_W++;
                        count_WB++;
                    }
                    if (isSevereWasted){
                        count_SW++;
                        count_SWB++;
                    }
                    if (isSevereUnderweight){
                        count_SU++;
                        count_SUB++;
                    }
                }
                record[i][0] = count_U;
                record[i][1] = count_N;
                record[i][2] = count_SS;
                record[i][3] = count_S;
                record[i][4] = count_O;
                record[i][5] = count_W;
                record[i][6] = count_SW;
                record[i][7] = count_SU;
                malnutrition_perbarangay[i] = count_ALL;
            }
            childrenList.clear();
        }
        RankBarangay(malnutrition_perbarangay, index, barangay);
        int[] numberCategory = {count_UB, count_SUB, count_OB, count_SB, count_SSB, count_WB, count_SWB};
        CategoryTable(numberCategory);

        ArrayList<BarEntry> entries = new ArrayList<>();
        String[] labels = {"U", "SU", "O", "S", "SS", "W", "SW"};
        for(int i = 0; i<labels.length; i++){
            entries.add(new BarEntry(i, numberCategory[i]));
        }
        int[] colors = ColorTemplate.COLORFUL_COLORS;
        BarChart barChart = ChartMaker.createBarChart(view, R.id.barChart, entries, labels, colors, "Malnourished per month");

    }
    private void RankBarangay(int[] malnutrition_perbarangay, int[] index, String[] barangay){
        for (int i = 0; i < malnutrition_perbarangay.length - 1; i++) {
            for (int j = i + 1; j < malnutrition_perbarangay.length; j++) {
                if (malnutrition_perbarangay[i] > malnutrition_perbarangay[j]) {
                    // Swap malnutrition_rate[i] and malnutrition_rate[j]
                    int tempRate = malnutrition_perbarangay[i];
                    malnutrition_perbarangay[i] = malnutrition_perbarangay[j];
                    malnutrition_perbarangay[j] = tempRate;

                    // Swap index[i] and index[j]
                    int tempIndex = index[i];
                    index[i] = index[j];
                    index[j] = tempIndex;
                }
            }
        }
        TableLayout tableLayout = view.findViewById(R.id.tableLayout3);
        String[] barangayHeaders = {"Barangay", "Total"};
        String[][] barangayData = new String[6][2];
        int k=0;
        for(int i=23; i>17; i--){
            barangayData[k][0] = barangay[index[i]];
            barangayData[k][1] = Integer.toString(malnutrition_perbarangay[i]);
            k++;
        }
        TableSetter.generateTable(applicationContext, tableLayout, barangayHeaders, barangayData);
    }
    private void CategoryTable(int[] numberCategory){
        TableLayout tableLayout1 = view.findViewById(R.id.tableLayout);
        String[] category = {"Underweight", "Severe Underweight", "Overweight", "Stunted", "Severe Stunted", "Wasted", "Severe Wasted"};
        String[] categoryHeaders = {"Category", "Total"};
        String[][] categoryData = new String[7][2];
        for(int i=0; i<7; i++){
            categoryData[i][0] = category[i];
            categoryData[i][1] = Integer.toString(numberCategory[i]);
        }
        TableSetter.generateTable(applicationContext,tableLayout1, categoryHeaders, categoryData);
    }

    private static Map<String, Integer> aggregateDataByDay(List<Child> dataList,
                                                           Date startDate, Date currentDate) {
        String[] dateArray = DateParser.createDateArray(startDate, currentDate);
        Map<String, Integer> aggregatedData = new LinkedHashMap<>();


        for (String day: dateArray){
            int count = 0;
            for(Child child: dataList){
                boolean isNormal = child.getStatusdb().contains("Normal");
                if(child.dateString().equals(day) && !isNormal){
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
                boolean isNormal = child.getStatusdb().contains("Normal");
                if(child.dateString().substring(0,7).equals(month) && !isNormal){
                    count++;
                }
            }
            aggregatedData.put(month, count);
        }
        return aggregatedData;
    }


    private void getPreviousPeriod(ArrayList<Timestamp> timestampArrayList, int currentrecord, Map<String, Integer> aggregatedData){
        db.collection("children") .whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(2))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(3))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Child> childrenList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                childrenList.add(child);}

                            int observation = task.getResult().size() - currentrecord;
                            String observeStatus = "";
                            float differences = Math.abs(differences(currentrecord, task.getResult().size()));
                            String withPercent = "" + String.format("%.2f", differences) + " %";
                            int colorNow = Color.parseColor("#000000");
                            if(currentrecord == observation){
                                observeStatus = "Just as the same";
                            } else if (currentrecord > observation) {
                                observeStatus = "" + withPercent + " more than previous " + periodType;
                                colorNow = Color.parseColor("#FF0000");
                            } else if (currentrecord < observation){
                                observeStatus = "" + withPercent + " less than previous " + periodType;
                                colorNow = Color.parseColor("#097969");
                            }

                            labelTotalCase.setText(""+currentrecord);
                            labelObservation.setText(observeStatus);
                            labelObservation.setTextColor(colorNow);

                            ArrayList<Date> startCurrentDate = DateParser.createCurrentStartDate(6,periodType);
                            Map<String, Integer> aggregatedData2 = new LinkedHashMap<>();
                            if(periodType.equals("year")){
                                aggregatedData2 = aggregateDataByMonth(childrenList, startCurrentDate.get(3), startCurrentDate.get(2));
                            }else{
                                aggregatedData2 = aggregateDataByDay(childrenList, startCurrentDate.get(3), startCurrentDate.get(2));
                            }

                            LineChart lineChart = ChartMaker.createLineChart(view, R.id.lineChart, aggregatedData, aggregatedData2, periodType, switchStatus);

                        } else {
                            Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore Error", "Error"+e);
                        Toast.makeText(requireContext(), "Failed to get children data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private float differences(int currentrecord, int previousrecord){
        float difference = currentrecord - previousrecord;
        float rate = 0.00f;
        if(previousrecord==0){
            rate = 1.00f;
        } else{
            rate = difference / (float) previousrecord ;
        }
        return rate * 100;
    }
}