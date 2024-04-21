package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class fragment_sr_conso extends Fragment {

    View view;
    ArrayList<Child> childrenList;
    String text_Date;

    FirebaseFirestore db;

    TableLayout allAgeTL, halfAgeTL;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_sr_conso, container, false);
        allAgeTL = view.findViewById(R.id.allAgeTL);
        halfAgeTL = view.findViewById(R.id.halfAgeTL);

        childrenList = ((SummaryReport)getActivity()).childrenList;
        text_Date = ((SummaryReport)getActivity()).text_Date;
        ((SummaryReport)getActivity()).updateApi(new FragmentEventListener() {
            @Override
            public void onEventTrigerred() {
                childrenList = ((SummaryReport)getActivity()).childrenList;
                text_Date = ((SummaryReport)getActivity()).text_Date;
                tableSetter();
            }

        });

        tableSetter();

        return view;
    }

    private void tableSetter(){
        TableSetter.clearTable(allAgeTL);
        TableSetter.clearTable(halfAgeTL);
        String[][] AAdata = new String[14][3];
        String[][] HAdata = new String[14][3];
        String [] headers =  {"Birth to 5 Years 0-59 months", "F1K  0-23 months"};
        String[] dataCat = {"WFA - Normal", "WFA - OW", "WFA - UW", "WFA - SUW",
                "HFA - Normal", "HFA - Tall", "HFA - ST", "HFA - SST",
                "WFH - Normal", "WFH - OW", "WFH - OB", "WFH - MW", "WFH - SW"};
        int l=1;




        SRDPConso srdpConso = new SRDPConso(childrenList);
        int counts[] = srdpConso.countNow(srdpConso.monthFilter());
        String perCount[] = srdpConso.getPercentage(counts);


        for(int i=0; i<14; i++){
            if(i==0){
                AAdata[i][0] = "";
                AAdata[i][1] = "Total";
                AAdata[i][2] = "Prev";

                HAdata[i][0] = "";
                HAdata[i][1] = "Total";
                HAdata[i][2] = "Prev";
            }else{
                AAdata[i][0] = dataCat[i-1];
                AAdata[i][1] = String.valueOf(counts[i-1]);
                AAdata[i][2] = perCount[i-1];

                HAdata[i][0] = dataCat[i-1];
                //kaya 12 instead 13 gawa ng header -1
                HAdata[i][1] =  String.valueOf(counts[i+12]);
                HAdata[i][2] = perCount[i+12];
            }
        }
        generateTable(allAgeTL,headers[0], AAdata);
        generateTable(halfAgeTL, headers[1], HAdata);

    }



    private void generateTable(TableLayout tableLayout, String header, String[][] data) {
        TableRow headerRow = new TableRow(requireContext());


        TextView headerTextView = new TextView(requireContext());
        headerTextView.setText(header);
        TableSetter.Costumize(headerRow, headerTextView);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setTypeface(null, Typeface.BOLD);
        TableSetter.setTextSizeAndPaddingForTextViews(18, 16, headerTextView);
        headerRow.addView(headerTextView);


        tableLayout.addView(headerRow);

        // Populate table rows with data
        for (String[] rowData : data) {
            TableRow tableRow = new TableRow(requireContext());

            // Create cells for each column in the row
            for (int i= 0; i<rowData.length; i++){
                TextView cellTextView = new TextView(requireContext());
                cellTextView.setText(rowData[i]);
                TableSetter.Costumize(tableRow, cellTextView);
                if(i==0){
                    cellTextView.setGravity(Gravity.START);
                }else{
                    cellTextView.setGravity(Gravity.END);
                }
                TableSetter.setTextSizeAndPaddingForTextViews(18, 16, cellTextView);
                tableRow.addView(cellTextView);
            }
            tableLayout.addView(tableRow);
        }
    }
}