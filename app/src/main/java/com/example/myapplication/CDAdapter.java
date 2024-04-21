package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CDAdapter extends  RecyclerView.Adapter<CDAdapter.ViewHolder> implements Filterable {
    Context context;
    private List<Child> exampleList;
    private List<Child> exampleListFull;


    public CDAdapter(Context context, ArrayList<Child> exampleList){
        this.context = context;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
    }


    @NonNull
    @Override
    public CDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cd_item, parent, false);
        return new CDAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CDAdapter.ViewHolder holder, int position) {
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

        holder.btnEdit.setOnClickListener(v -> {
            App.child = exampleList.get(position);
            Context context = holder.itemView.getContext();
            context.startActivity(new Intent(context, EditChild.class));
        });

        holder.btnDelete.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            showDeleteDialog(context,exampleList.get(position),position);
        });


        holder.btnProgress.setOnClickListener(v -> {
            App.child = exampleList.get(position);
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ProgressMonitoringBNS.class);
            intent.putExtra("Child", exampleList.get(position));
            intent.putExtra("Type", "BNS");
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,status, expectedDate;
        ImageButton btnEdit, btnDelete, btnProgress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.list_item_name);
            status = itemView.findViewById(R.id.list_item_barangay);
            expectedDate = itemView.findViewById(R.id.list_item_bio);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnProgress = itemView.findViewById(R.id.btnProgress);
        }
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

    private void showDeleteDialog(Context context, Child child, int position){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_dialog);

        Button buttonNo = dialog.findViewById(R.id.buttonNo);
        Button buttonYes = dialog.findViewById(R.id.buttonYes);


        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFirestoreData(dialog, child, position);
            }
        });
    }

    private void removeFirestoreData(Dialog dialog, Child child, int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("children");

        Query query = collectionRef.whereEqualTo("childFirstName", child.getChildFirstName())
                .whereEqualTo("childMiddleName", child.getChildMiddleName())
                .whereEqualTo("childLastName", child.getChildLastName())
                .whereEqualTo("gmail", child.getGmail() );

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                    App.DeleteFlag = 1;
                    dialog.dismiss();
                    Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                    exampleList.remove(position);
                    notifyItemRemoved(position);


                } else {

                }
            }
        });

    }
}
