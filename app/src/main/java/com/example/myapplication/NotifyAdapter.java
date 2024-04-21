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

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    private List<ComponentModel> items;
    private ItemClickListener itemClickListener;

    private Context context;

    public NotifyAdapter(Context context, List<ComponentModel> items, ItemClickListener itemClickListener) {
        this.context = context;
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textNotifyStatus, textNotifyName;
        public View container;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            textNotifyStatus = itemView.findViewById(R.id.textNotifyStatus);
            textNotifyName = itemView.findViewById(R.id.textNotifyName);
            container = itemView.findViewById(R.id.notify_container);
            button = itemView.findViewById(R.id.btnNotifyView);

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_component, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComponentModel item = items.get(position);
        holder.textNotifyStatus.setText(item.getText());
        holder.textNotifyName.setText(item.getName());
        holder.textNotifyStatus.setTextColor(ContextCompat.getColor(context, item.getTextColorResId()));
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
