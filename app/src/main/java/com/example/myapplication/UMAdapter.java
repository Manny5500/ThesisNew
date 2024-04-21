package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UMAdapter extends RecyclerView.Adapter<UMAdapter.ViewHolder> implements Filterable {

    Context context;
    private List<User> exampleList;
    private List<User> exampleListFull;

    UMAdapter.OnItemClickListener onItemClickListener;

    public UMAdapter(Context context, ArrayList<User> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }

    @NonNull
    @Override
    public UMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new UMAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UMAdapter.ViewHolder holder, int position) {
        String firstNames = exampleList.get(position).getFirstName();
        String lastNames = exampleList.get(position).getLastName();
        String imageUrl = exampleList.get(position).getImageUrl();
        String barangay = exampleList.get(position).getBarangay();

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.nutriassist_logo)
                .error(R.drawable.nutriassist_logo)
                .override(300, 300)
                .centerCrop()
                .into(holder.image);


        holder.name.setText(firstNames + " " + lastNames);
        holder.barangay.setText(barangay);
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(exampleList.get(position)));
    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, barangay;
        CircleImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            image = itemView.findViewById(R.id.Picture);
            barangay = itemView.findViewById(R.id.list_item_barangay);
        }
    }

    public void setOnItemClickListener(UMAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(User child);
    }
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (User item : exampleListFull)  {

                    String firstName = item.getFirstName().toLowerCase();
                    String lastName = item.getLastName().toLowerCase();
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

