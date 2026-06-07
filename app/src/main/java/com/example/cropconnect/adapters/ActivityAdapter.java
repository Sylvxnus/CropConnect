package com.example.cropconnect.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropconnect.R;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    public static class ActivityItem {
        public final String text;
        public final String time;
        public final int    dotColor;

        public ActivityItem(String text, String time, int dotColor) {
            this.text     = text;
            this.time     = time;
            this.dotColor = dotColor;
        }
    }

    private final Context context;
    private final List<ActivityItem> items;

    public ActivityAdapter(Context context, List<ActivityItem> items) {
        this.context = context;
        this.items   = items;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_activity_row, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder h, int position) {
        ActivityItem item = items.get(position);
        h.tvActivityText.setText(item.text);
        h.tvActivityTime.setText(item.time);
        h.viewDot.setBackgroundTintList(ColorStateList.valueOf(item.dotColor));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityText, tvActivityTime;
        View     viewDot;

        ActivityViewHolder(@NonNull View v) {
            super(v);
            tvActivityText = v.findViewById(R.id.tvActivityText);
            tvActivityTime = v.findViewById(R.id.tvActivityTime);
            viewDot        = v.findViewById(R.id.viewDot);
        }
    }
}