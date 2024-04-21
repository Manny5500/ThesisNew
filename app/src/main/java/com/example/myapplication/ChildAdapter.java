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

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> implements Filterable {
    Context context;
    private List<Child> exampleList;
    private List<Child> exampleListFull;

    OnItemClickListener onItemClickListener;

    public ChildAdapter(Context context, ArrayList<Child> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrayList<String> statusList = exampleList.get(position).getStatusdb();
            StringBuilder statusStringBuilder = new StringBuilder();
            if(statusList != null){
                int size = statusList.size();
                for (int i = 0; i < size; i++) {
                    statusStringBuilder.append(statusList.get(i));
                    if (i < size - 1) {
                        statusStringBuilder.append(", ");
                    }
                }
            }

        FormUtils formUtils = new FormUtils();
        String firstNames = exampleList.get(position).getChildFirstName();
        String lastNames = exampleList.get(position).getChildLastName();
        String expectedDates = exampleList.get(position).getExpectedDate();
        holder.name.setText(firstNames + " " + lastNames);
        holder.expectedDate.setText(expectedDates);
        Date parsedDate = formUtils.parseDate(expectedDates);
        int daydiff = 0;
        if (parsedDate != null) {
            daydiff = formUtils.calculateDaysDifference(parsedDate);
        }
        if(daydiff<=7){
            holder.expectedDate.setTextColor(Color.parseColor("#FF0000"));
        }
        holder.status.setText(statusStringBuilder.toString());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(exampleList.get(position)));
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,status, expectedDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            status = itemView.findViewById(R.id.list_item_barangay);
            expectedDate = itemView.findViewById(R.id.list_item_bio);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Child child);
    }
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
}