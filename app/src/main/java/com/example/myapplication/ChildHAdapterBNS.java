package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildHAdapterBNS  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    Child child;
    private List<ChildH> exampleList;

    private List<PMChart> pmChartList;

    private static final int VIEW_TYPE_HEADER = 0;

    private static final int VIEW_TYPE_HEADER2 = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private static String type;


    ChildAdapter.OnItemClickListener onItemClickListener;

    public ChildHAdapterBNS(Context context, ArrayList<ChildH> exampleList, Child child, ArrayList<PMChart> pmCharts, String type){
        this.context = context;
        this.exampleList = exampleList;
        this.child = child;
        this.pmChartList = pmCharts;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == VIEW_TYPE_HEADER || viewType == VIEW_TYPE_HEADER2){
            View header = inflater.inflate(R.layout.progress_chart, parent, false);
            return new HeaderViewHolder(header);
        }else{
            View itemView = inflater.inflate(R.layout.progress_item_bns, parent, false);
            return new ChildHAdapterBNS.ViewHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ViewHolder){
            ViewHolder itemHolder = (ViewHolder) holder;
            int adjustedPosition = position - 2;
            String dateRaw = "" + exampleList.get(adjustedPosition).getId();
            int month = Integer.parseInt(dateRaw.substring(4, 6));
            String monthName = new DateFormatSymbols().getMonths()[month - 1];

            String dateVal = monthName + " " + dateRaw.substring(6, 8) + ", " + dateRaw.substring(0, 4);
            String heightVal = "" + exampleList.get(adjustedPosition).getHeight() + " cm";
            String weightVal = "" + exampleList.get(adjustedPosition).getWeight() + " kg";

            ArrayList<String> statusdb = exampleList.get(adjustedPosition).getStatusdb();

            double heightDiff;
            double weightDiff;
            if(adjustedPosition+1<exampleList.size()){
                heightDiff = getDifference(exampleList.get(adjustedPosition).getHeight(),
                        exampleList.get(adjustedPosition+1).getHeight());
                weightDiff = getDifference(exampleList.get(adjustedPosition).getWeight(),
                        exampleList.get(adjustedPosition+1).getWeight());

                itemHolder.progressHeight.setText(setTextValue(heightVal, "Height: ", heightDiff));
                itemHolder.progressWeight.setText(setTextValue(weightVal, "Weight: ", weightDiff));
                itemHolder.progressHeight.setTextColor(setChangeColor(heightDiff));
                if(statusdb!=null && statusdb.size()>0){
                    itemHolder.progressWeight.setTextColor(setChangeColorWeight(weightDiff, statusdb));
                }
            }else{
                itemHolder.progressHeight.setText("Height: " + heightVal);
                itemHolder.progressWeight.setText("Weight: " + weightVal);
            }
            itemHolder.progressDate.setText("" + dateVal);
        /*
        if(statusdb!=null && statusdb.size()>0)
        {
            holder.progressStatus.setText(seperateStatus(statusdb));
        }*/


            String   full_name = child.getChildFirstName() + " " +
                    child.getChildMiddleName() + " " +
                    child.getChildLastName();

            ChildHLogic childHLogic = new ChildHLogic(exampleList.get(adjustedPosition), child.getSex(), child.getBirthDate(), full_name);

            ArrayList<String> newStatus = childHLogic.getStatusProgress();
            int monthdiff = childHLogic.calcuMD();


            if(newStatus.size()<1){
                newStatus.add("Invalid age ( " +  monthdiff + " mos." + " )" );
            }


            itemHolder.progressStatus.setText(seperateStatus(newStatus));

            if(type.equals("Parent")){
                itemHolder.progressMenu.setVisibility(View.GONE);
            }
            itemHolder.progressMenu.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, PMEdit.class);
                intent.putExtra("ChildHLogic", childHLogic);
                intent.putExtra("Type", type);
                context.startActivity(intent);
            });
            if(type.equals("Parent")){
                itemHolder.itemView.setOnClickListener(v->{
                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(context, PMEdit.class);
                    intent.putExtra("ChildHLogic", childHLogic);
                    intent.putExtra("Type", type);
                    context.startActivity(intent);
                });
            }

        }else if(holder instanceof HeaderViewHolder){
            HeaderViewHolder hVH = (HeaderViewHolder) holder;
            PMChart pmC = pmChartList.get(position);
            ArrayList<Double> dataList = pmC.dataList;
            ArrayList<String> labels = pmC.labelList;
            ChartMaker.editLineChartPM(hVH.lineChart,dataList, labels, pmC.chartTitle, pmC.color);
        }


    }


    @Override
    public int getItemCount() {
        return exampleList.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        // Return view type as header if position is 0, otherwise return item view type
        if(position == 0){
            return VIEW_TYPE_HEADER;
        } else if (position == 1) {
            return VIEW_TYPE_HEADER2;
        }else{
            return VIEW_TYPE_ITEM;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView progressDate, progressHeight, progressWeight, progressStatus;
        ImageView progressMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressDate = itemView.findViewById(R.id.progressDate);
            progressHeight = itemView.findViewById(R.id.progressHeight);
            progressWeight = itemView.findViewById(R.id.progressWeight);
            progressStatus = itemView.findViewById(R.id.progressStatus);
            progressMenu = itemView.findViewById(R.id.progressMenu);

        }
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        LineChart lineChart;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            lineChart = itemView.findViewById(R.id.lineChart);
        }
    }


    public double getDifference(double current, double previous){

        return current-previous;

    }

    public String getChanges(double value){
        String change = "Same";
        if(value>0){
            change = "Increased";
        } else if (value<0) {
            change = "Decreased";
        }
        return change;
    }

    public int setChangeColor(double value){
        String changeColor = "#000000";
        if(value>0){
            changeColor = "#008000";
        }else if(value<0){
            changeColor = "#FF0000";
        }
        return Color.parseColor(changeColor);
    }

    public int setChangeColorWeight(double value, ArrayList<String> statusdb){
        String changeColor = "#000000";
        boolean isOB = statusdb.contains("Obese");
        boolean isOW = statusdb.contains("Overweight");
        boolean isNormal = statusdb.contains("Normal");
        if(value>0){
            changeColor = "#008000";
        }
        if(value<0){
            changeColor = "#FF0000";
        }
        if(isOB||isOW) {
            if(value>0){
                changeColor = "#FF0000";
            } else if (value<0) {
                changeColor = "#008000";
            }
        }
        if(isNormal){
            changeColor = "#008000";
        }
        return Color.parseColor(changeColor);
    }
    public String setTextValue(String value, String label, double valDiff){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(label);
        stringBuilder.append(value);
        stringBuilder.append(" (");
        stringBuilder.append(getChanges(valDiff));
        if(!getChanges(valDiff).equals("Same")){
            stringBuilder.append(" ");
            stringBuilder.append(String.format("%.2f", Math.abs(valDiff)));
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public String seperateStatus(ArrayList<String> statusdb){
        StringBuilder statusStringBuilder = new StringBuilder();
        int i=0;
        for (String status : statusdb) {
            statusStringBuilder.append(status);
            if(i!=statusdb.size()-1){
                statusStringBuilder.append(", ");
            }
            i++;
        }
        return statusStringBuilder.toString();
    }


}