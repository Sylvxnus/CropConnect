package com.example.cropconnect.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cropconnect.R;
import com.example.cropconnect.models.Donation;
import java.util.ArrayList;
import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    public interface OnDonationActionListener {
        void onConfirm(Donation donation);
        void onMarkCollected(Donation donation);
        void onViewAllotment(Donation donation);
    }

    private final Context context;
    private List<Donation> fullList;
    private List<Donation> filteredList;
    private OnDonationActionListener listener;

    private String currentSearch = "";
    private String currentStatus = "all";
    private String currentType   = "all";

    public DonationAdapter(Context context, List<Donation> donations,
                           OnDonationActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.fullList = new ArrayList<>(donations);
        this.filteredList = new ArrayList<>(donations);
    }

    public void filter(String search, String status, String type) {
        currentSearch = search.toLowerCase().trim();
        currentStatus = status;
        currentType   = type;

        filteredList.clear();
        for (Donation d : fullList) {
            boolean matchSearch = currentSearch.isEmpty()
                    || d.getAllotmentName().toLowerCase().contains(currentSearch)
                    || d.getItems().toLowerCase().contains(currentSearch);
            boolean matchStatus = currentStatus.equals("all")
                    || d.getStatus().equals(currentStatus);
            boolean matchType = currentType.equals("all")
                    || d.getFoodType().equals(currentType);

            if (matchSearch && matchStatus && matchType) filteredList.add(d);
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() { return filteredList.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_donation_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Donation d = filteredList.get(position);

        h.tvInitials.setText(d.getInitials());
        h.tvAllotment.setText(d.getAllotmentName());
        h.tvItems.setText(d.getItems());
        h.tvDate.setText(d.getDateLabel());
        h.tvWeight.setText(String.format("%.0f kg", d.getWeightKg()));
        h.tvDistance.setText(String.format("%.1f mi", d.getDistanceMiles()));
        h.tvStatus.setText(d.getStatus());
        h.tvNote.setText(d.getNote());
        h.tvNote.setVisibility(d.getNote().isEmpty() ? View.GONE : View.VISIBLE);

        // Avatar background colour cycles through 5 tints
        int[] avatarBgs = {0xFFEAF3DE, 0xFFFAEEDA, 0xFFE1F5EE, 0xFFE6F1FB, 0xFFFBEAF0};
        int[] avatarFg  = {0xFF27500A, 0xFF633806, 0xFF085041, 0xFF0C447C, 0xFF4B1528};
        int idx = position % 5;
        h.tvInitials.setBackgroundColor(avatarBgs[idx]);
        h.tvInitials.setTextColor(avatarFg[idx]);

        // Status badge background
        applyStatusBadge(h.tvStatus, d.getStatus());

        // Action buttons
        boolean isPending  = d.getStatus().equals("Pending");
        boolean isTransit  = d.getStatus().equals("In transit");

        h.btnConfirm.setVisibility(isPending ? View.VISIBLE : View.GONE);
        h.btnCollected.setVisibility(isTransit ? View.VISIBLE : View.GONE);
        h.btnViewAllotment.setVisibility(isPending || isTransit ? View.VISIBLE : View.GONE);

        h.btnConfirm.setOnClickListener(v -> listener.onConfirm(d));
        h.btnCollected.setOnClickListener(v -> listener.onMarkCollected(d));
        h.btnViewAllotment.setOnClickListener(v -> listener.onViewAllotment(d));
    }

    private void applyStatusBadge(TextView tv, String status) {
        switch (status) {
            case "Pending":
                tv.setBackgroundResource(R.drawable.badge_pending);
                tv.setTextColor(Color.parseColor("#633806")); break;
            case "Confirmed":
                tv.setBackgroundResource(R.drawable.badge_confirmed);
                tv.setTextColor(Color.parseColor("#27500A")); break;
            case "In transit":
                tv.setBackgroundResource(R.drawable.badge_transit);
                tv.setTextColor(Color.parseColor("#0C447C")); break;
            default:
                tv.setBackgroundResource(R.drawable.badge_pending);
                tv.setTextColor(Color.parseColor("#444441")); break;
        }
    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvAllotment, tvItems, tvDate,
                tvWeight, tvDistance, tvStatus, tvNote;
        android.widget.Button btnConfirm, btnCollected, btnViewAllotment;

        ViewHolder(View v) {
            super(v);
            tvInitials    = v.findViewById(R.id.tvInitials);
            tvAllotment   = v.findViewById(R.id.tvAllotment);
            tvItems       = v.findViewById(R.id.tvItems);
            tvDate        = v.findViewById(R.id.tvDate);
            tvWeight      = v.findViewById(R.id.tvWeight);
            tvDistance    = v.findViewById(R.id.tvDistance);
            tvStatus      = v.findViewById(R.id.tvStatus);
            tvNote        = v.findViewById(R.id.tvNote);
            btnConfirm    = v.findViewById(R.id.btnConfirm);
            btnCollected  = v.findViewById(R.id.btnMarkCollected);
            btnViewAllotment = v.findViewById(R.id.btnViewAllotment);
        }
    }
}