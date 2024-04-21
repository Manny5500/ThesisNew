package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SitioAdapter extends RecyclerView.Adapter<SitioAdapter.ViewHolder> {
    Context context;
    private List<SitioModel> exampleList;
    private List<SitioModel> exampleListFull;

    int showTable = 0;

    public SitioAdapter(Context context, ArrayList<SitioModel> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public SitioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sitio_list, parent, false);
        return new SitioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SitioAdapter.ViewHolder holder, int position) {
        SitioModel sMod = exampleList.get(position);
        holder.nameSA.setText(String.valueOf(sMod.getSitioName()));
        holder.totalSA.setText(String.valueOf(sMod.getTotalCase()));

        int progress = 0;
        if(sMod.getTotalCase()>0){
            progress = sMod.getTotalCase()*100/9;
        }
        holder.pbarSA.setProgress(progress);
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
        TextView nameSA,  totalSA;
        ProgressBar pbarSA;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameSA = itemView.findViewById(R.id.nameSA);
            totalSA = itemView.findViewById(R.id.totalSA);
            pbarSA = itemView.findViewById(R.id.pbarSA);
        }
    }

}
