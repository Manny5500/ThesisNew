package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BarangayAdapter extends RecyclerView.Adapter<BarangayAdapter.ViewHolder> {
    Context context;
    private List<BarangayModel> exampleList;
    private List<BarangayModel> exampleListFull;

    OnItemClickListener onItemClickListener;

    public BarangayAdapter(Context context, ArrayList<BarangayModel> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public BarangayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.barangay_list_item, parent, false);
        return new BarangayAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BarangayAdapter.ViewHolder holder, int position) {
        BarangayModel currentObject = exampleList.get(position);
        holder.barangayTitle.setText(currentObject.getRank() + " " + currentObject.getBarangay());
        holder.malCases.setText(currentObject.getQueryType()+" : " + currentObject.getTotalCase());
        holder.NormalCases.setText("Normal : " + currentObject.getNormal());
        holder.optCoverage.setText("OPT Coverage : " + percentage(currentObject.getTotalAssess(), currentObject.getEstimatedChildren())+ "%");
        if(currentObject.getTotalAssess()>0){
            holder.malPercent.setText("(%) : " + percentage(currentObject.getTotalCase(), currentObject.getTotalAssess())+ "%");
            holder.NormalPercent.setText("(%) : " + percentage(currentObject.getNormal(), currentObject.getTotalAssess())+ "%");
        }else{
            holder.malPercent.setText("(%) : 0%");
            holder.NormalPercent.setText("(%) : 0%");
        }
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(currentObject));
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
        TextView barangayTitle, optCoverage, malCases, malPercent, NormalCases, NormalPercent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            barangayTitle = itemView.findViewById(R.id.barangayTitle);
            optCoverage = itemView.findViewById(R.id.optCoverage);
            malCases = itemView.findViewById(R.id.malCases);
            malPercent = itemView.findViewById(R.id.malPercent);
            NormalCases = itemView.findViewById(R.id.NormalCases);
            NormalPercent = itemView.findViewById(R.id.NormalPercent);
        }
    }

    public void setOnItemClickListener(BarangayAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(BarangayModel barangayModel);
    }
    /*
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Child> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Child item : exampleListFull)  {

                    String firstName = item.getChildFirstName().toLowerCase();
                    String lastName = item.getChildLastName().toLowerCase();
                    String fullName = firstName + " " + lastName;

                    if (firstName.contains(filterPattern)|| lastName.contains(filterPattern) || fullName.contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

     */
}
