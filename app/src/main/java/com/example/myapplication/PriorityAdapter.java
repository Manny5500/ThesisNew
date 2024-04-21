package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PriorityAdapter extends RecyclerView.Adapter<PriorityAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    private List<ComponentModel> items;
    private ItemClickListener itemClickListener;

    private Context context;

    public PriorityAdapter(Context context, List<ComponentModel> items, ItemClickListener itemClickListener) {
        this.context = context;
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView, textName;
        public View container;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textPriorityStatus);
            textName = itemView.findViewById(R.id.textPriorityName);
            container = itemView.findViewById(R.id.priorityContainer);
            button = itemView.findViewById(R.id.btnPriorityView);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.priority_component, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComponentModel item = items.get(position);
        holder.textView.setText(item.getText());
        holder.textName.setText(item.getName());
        holder.textView.setTextColor(ContextCompat.getColor(context, item.getTextColorResId()));
        holder.container.setBackgroundColor(ContextCompat.getColor(context, item.getBackgroundColorResId()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public ComponentModel getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }


}
