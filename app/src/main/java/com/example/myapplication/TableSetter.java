package com.example.myapplication;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableSetter {
    public static void generateTable(Context context, TableLayout tableLayout, String[] headers, String[][] data) {
       clearTable(tableLayout);
        TableRow headerRow = new TableRow(context);

        for (int i = 0; i < headers.length; i++) {
            TextView headerTextView = new TextView(context);
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
            TableRow tableRow = new TableRow(context);

            // Create cells for each column in the row
            for (int i= 0; i<rowData.length; i++){
                TextView cellTextView = new TextView(context);
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
    public static void setTextSizeAndPaddingForTextViews(float textSize, int padding, TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setPadding(padding, padding, padding, padding);
        }
    }
    public static void Costumize(TableRow tableRow, TextView... textViews){
        tableRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        for (TextView textView : textViews){
            textView.setLayoutParams(new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }
    }

    public static void clearTable(TableLayout tableLayout) {
        // Get the number of rows in the table
        int rowCount = tableLayout.getChildCount();

        // Remove all rows starting from index 1 (index 0 is the header row)
        for (int i = 1; i < rowCount; i++) {
            View child = tableLayout.getChildAt(1);
            if (child instanceof TableRow) {
                // Remove the TableRow from the TableLayout
                tableLayout.removeViewAt(1);
            }
        }
    }


}
