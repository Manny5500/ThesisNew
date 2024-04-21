package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class barangayAnalyticsAdapter extends RecyclerView.Adapter<barangayAnalyticsAdapter.ViewHolder> {
    Context context;
    private List<BarangayModel> exampleList;
    private List<BarangayModel> exampleListFull;

    private SitioAdapter sitioAdapter;

    int showTable = 0;

    public barangayAnalyticsAdapter(Context context, ArrayList<BarangayModel> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public barangayAnalyticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.barangay_analytics_layout, parent, false);
        return new barangayAnalyticsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull barangayAnalyticsAdapter.ViewHolder holder, int position) {
        BarangayModel bMod = exampleList.get(position);
        holder.rankBA.setText(String.valueOf(bMod.getRank()));
        holder.nameBA.setText(String.valueOf(bMod.getBarangay()));
        holder.totalBA.setText(String.valueOf(bMod.getTotalCase()));


        String[] barangayHeaders = {"Barangay", "Total"};
        String[][] barangayData = new String[6][2];

        ArrayList<SitioModel> sitioList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : bMod.getSitioMap().entrySet()) {
            SitioModel sitioModel = new SitioModel();
            sitioModel.setSitioName(entry.getKey());
            sitioModel.setTotalCase(entry.getValue());
            sitioList.add(sitioModel);
        }

        Collections.sort(sitioList, new Comparator<SitioModel>() {
            @Override
            public int compare(SitioModel o1, SitioModel o2) {
                return o2.getTotalCase() - o1.getTotalCase();
            }
        });

        holder.sitioRecycler.setVisibility(View.GONE);
        sitioAdapter = new SitioAdapter(context,sitioList);
        holder.sitioRecycler.setAdapter(sitioAdapter);

        int progress = 0;
        if(bMod.getTotalCase()>0){
            progress = bMod.getTotalCase()*100/9;
        }
        holder.pbarBA.setProgress(progress);

        holder.itemView.setOnClickListener(v -> {

            if (holder.sitioRecycler.getVisibility() == View.VISIBLE) {
                holder.sitioRecycler.setVisibility(View.GONE);
            } else {
                holder.sitioRecycler.setVisibility(View.VISIBLE);
            }
        });

    }
    public float percentage(int number1, int number2){
        float result = (float) number1 / number2;
        float percentage = Math.round(result * 100.0f);
        return percentage;
    }
    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView  rankBA, nameBA, totalBA;

        RecyclerView sitioRecycler;
        ProgressBar pbarBA;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankBA = itemView.findViewById(R.id.rankBA);
            nameBA = itemView.findViewById(R.id.nameBA);
            totalBA = itemView.findViewById(R.id.totalBA);
            sitioRecycler = itemView.findViewById(R.id.sitioRecycler);
            pbarBA = itemView.findViewById(R.id.pbarBA);
        }
    }

}
