package com.example.cropconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cropconnect.R;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    public static class ActivityItem {
        public String text;
        public String time;
        public int dotColor;
        public ActivityItem(String text, String time, int dotColor) {
            this.text = text; this.time = time; this.dotColor = dotColor;
        }
    }

    private final Context context;
    private final List<ActivityItem> items;

    public ActivityAdapter(Context context, List<ActivityItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_activity_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ActivityItem item = items.get(position);
        h.tvText.setText(item.text);
        h.tvTime.setText(item.time);
        h.dot.setBackgroundColor(item.dotColor);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        View dot;
        ViewHolder(View v) {
            super(v);
            tvText = v.findViewById(R.id.tvActivityText);
            tvTime = v.findViewById(R.id.tvActivityTime);
            dot    = v.findViewById(R.id.viewDot);
        }
    }
}