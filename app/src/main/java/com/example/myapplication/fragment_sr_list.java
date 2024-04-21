package com.example.myapplication;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class fragment_sr_list extends Fragment {

    View view;

    ArrayList<Child> childrenList;

    TableLayout WFATL, HFATL, WFHTL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_sr_list, container, false);


        WFATL = view.findViewById(R.id.WFATL);
        HFATL = view.findViewById(R.id.HFATL);
        WFHTL = view.findViewById(R.id.WFHTL);

        childrenList = ((SummaryReport)getActivity()).childrenList;

        ((SummaryReport)getActivity()).updateApi(new FragmentEventListener() {
            @Override
            public void onEventTrigerred() {
                childrenList = ((SummaryReport)getActivity()).childrenList;
                tableSetter();
            }

        });

        tableSetter();

        return view;
    }

    private void tableSetter(){
        TableSetter.clearTable(WFATL);
        TableSetter.clearTable(HFATL);
        TableSetter.clearTable(WFHTL);
        String[] ageGroup = {"0-5 mo", "6-11 mo", "12-23 mo", "24-35 mo", "36-47 mo", "48-59 mo"};
        String[] WFACat = {"Normal", "OW", "UW", "SUW"};
        String[] HFACat = {"Normal", "Tall", "ST", "SST"};
        String[] WFHCat = {"Normal", "OW", "OB", "MW", "SW"};
        String[][] WFAdata = new String[30][4];
        String[][] HFAdata = new String[30][4];
        String[][] WFHdata = new String[36][4];
        String [] headers =  {"WFA/ Weight for Age", "HFA/ Height for Age", "WFH/ Weight for Height"};
        SRDPList srdpList = new SRDPList(childrenList);
        int counts[][] = srdpList.countNow(srdpList.monthFilter());
        int k=0;
        int l=0;
        int m=0;
        for(int i=0; i<30; i++){
            if(i%5==0){
                int index = i/5;
                WFAdata[i][0] = ageGroup[index];
                WFAdata[i][1] = "Boys";
                WFAdata[i][2] = "Girls";
                WFAdata[i][3] = "Total";

                HFAdata[i][0] = ageGroup[index];
                HFAdata[i][1] = "Boys";
                HFAdata[i][2] = "Girls";
                HFAdata[i][3] = "Total";
                m++;
            }else{
                if(k>3){
                    k=0;
                }
                WFAdata[i][0] = WFACat[k];
                WFAdata[i][1] = String.valueOf(counts[i-m][0]);
                WFAdata[i][2] = String.valueOf(counts[i-m][1]);
                WFAdata[i][3] = String.valueOf(counts[i-m][0]+counts[i-m][1]);

                HFAdata[i][0] = HFACat[k];
                HFAdata[i][1] = String.valueOf(counts[i-m+24][0]);
                HFAdata[i][2] = String.valueOf(counts[i-m+24][1]);
                HFAdata[i][3] = String.valueOf(counts[i-m+24][0] + counts[i-m+24][1]);
                k++;
            }
        }

        m=0;
        for(int i=0; i<36; i++){
            if(i%6==0){
                int index = i/6;
                WFHdata[i][0] = ageGroup[index];
                WFHdata[i][1] = "Boys";
                WFHdata[i][2] = "Girls";
                WFHdata[i][3] = "Total";
                m++;
            }else{
                if(l>4){
                    l=0;
                }
                WFHdata[i][0] = WFHCat[l];
                WFHdata[i][1] = String.valueOf(counts[i-m+48][0]);
                WFHdata[i][2] = String.valueOf(counts[i-m+48][1]);
                WFHdata[i][3] = String.valueOf(counts[i-m+48][0] + counts[i-m+48][1]);
                l++;
            }
        }
        generateTable(WFATL,headers[0], WFAdata);
        generateTable(HFATL, headers[1], HFAdata);
        generateTable(WFHTL, headers[2], WFHdata);

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