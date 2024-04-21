package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChildHAdapter  extends RecyclerView.Adapter<ChildHAdapter.ViewHolder> {
    Context context;
    private List<ChildH> exampleList;

    ChildAdapter.OnItemClickListener onItemClickListener;

    public ChildHAdapter(Context context, ArrayList<ChildH> exampleList){
        this.context = context;
        this.exampleList = exampleList;
    }

    @NonNull
    @Override
    public ChildHAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.progress_item, parent, false);
        return new ChildHAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildHAdapter.ViewHolder holder, int position) {
        String dateRaw = "" + exampleList.get(position).getId();
        int month = Integer.parseInt(dateRaw.substring(4, 6));
        String monthName = new DateFormatSymbols().getMonths()[month - 1];

        String dateVal = monthName + " " + dateRaw.substring(6, 8) + ", " + dateRaw.substring(0, 4);
        String heightVal = "" + exampleList.get(position).getHeight() + " cm";
        String weightVal = "" + exampleList.get(position).getWeight() + " kg";

        ArrayList<String> statusdb = exampleList.get(position).getStatusdb();

        double heightDiff;
        double weightDiff;
        if(position+1<exampleList.size()){
            heightDiff = getDifference(exampleList.get(position).getHeight(),
                    exampleList.get(position+1).getHeight());
            weightDiff = getDifference(exampleList.get(position).getWeight(),
                    exampleList.get(position+1).getWeight());

            holder.progressHeight.setText(setTextValue(heightVal, "Height: ", heightDiff));
            holder.progressWeight.setText(setTextValue(weightVal, "Weight: ", weightDiff));
            holder.progressHeight.setTextColor(setChangeColor(heightDiff));
            if(statusdb!=null && statusdb.size()>0){
                holder.progressWeight.setTextColor(setChangeColorWeight(weightDiff, statusdb));
            }
        }else{
            holder.progressHeight.setText("Height: " + heightVal);
            holder.progressWeight.setText("Weight: " + weightVal);
        }
        holder.progressDate.setText("" + dateVal);
        if(statusdb!=null && statusdb.size()>0)
        {
            holder.progressStatus.setText(seperateStatus(statusdb));
        }
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView progressDate, progressHeight, progressWeight, progressStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressDate = itemView.findViewById(R.id.progressDate);
            progressHeight = itemView.findViewById(R.id.progressHeight);
            progressWeight = itemView.findViewById(R.id.progressWeight);
            progressStatus = itemView.findViewById(R.id.progressStatus);
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
