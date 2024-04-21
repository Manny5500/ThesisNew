package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> implements Filterable {
    Context context;
    private List<TempEmail> exampleList;
    private List<TempEmail> exampleListFull;

    OnItemClickListener onItemClickListener;

    public ParentAdapter(Context context, ArrayList<TempEmail> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_parent_component, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fullName = exampleList.get(position).getFullName();
        holder.name.setText(fullName);
        holder.gmail.setText(exampleList.get(position).getGmail());
        holder.sitio.setText(exampleList.get(position).getSitio());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(exampleList.get(position)));
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,gmail, sitio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            gmail = itemView.findViewById(R.id.list_item_gmail);
            sitio = itemView.findViewById(R.id.list_item_sitio);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(TempEmail tempEmail);
    }
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<TempEmail> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (TempEmail item : exampleListFull)  {

                    String firstName = item.getParentFirstName().toLowerCase();
                    String lastName = item.getParentLastName().toLowerCase();
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