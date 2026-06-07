package com.example.cropconnect.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropconnect.R;
import com.example.cropconnect.models.Donation;

import java.util.ArrayList;
import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    public interface OnDonationActionListener {
        void onConfirm(Donation donation);
        void onMarkCollected(Donation donation);
        void onViewAllotment(Donation donation);
    }

    private final Context context;
    private final List<Donation> allDonations;   // full unfiltered list
    private List<Donation> filteredDonations;    // what the RecyclerView shows
    private final OnDonationActionListener listener;

    public DonationAdapter(Context context, List<Donation> donations,
                           OnDonationActionListener listener) {
        this.context           = context;
        this.allDonations      = new ArrayList<>(donations);
        this.filteredDonations = new ArrayList<>(donations);
        this.listener          = listener;
    }

    public void setDonations(List<Donation> donations) {
        allDonations.clear();
        allDonations.addAll(donations);
        filteredDonations = new ArrayList<>(donations);
        notifyDataSetChanged();
    }

    // ── Called by FoodBankDonationsActivity ───────────────────────────────
    // filter(searchQuery, statusFilter, "all")
    // statusFilter: "all" | "Pending" | "Confirmed" | "In transit" | "Collected"
    public void filter(String search, String status, String ignored) {
        filteredDonations = new ArrayList<>();
        String lowerSearch = search == null ? "" : search.toLowerCase().trim();

        for (Donation d : allDonations) {
            boolean matchesStatus = "all".equalsIgnoreCase(status)
                    || (d.getStatus() != null && d.getStatus().equalsIgnoreCase(status));

            boolean matchesSearch = lowerSearch.isEmpty()
                    || (d.getAllotment() != null && d.getAllotment().toLowerCase().contains(lowerSearch))
                    || (d.getItems()    != null && d.getItems().toLowerCase().contains(lowerSearch));

            if (matchesStatus && matchesSearch) {
                filteredDonations.add(d);
            }
        }
        notifyDataSetChanged();
    }

    // ── Called by FoodBankDonationsActivity.updateCount() ─────────────────
    public int getFilteredCount() {
        return filteredDonations.size();
    }

    // ── RecyclerView ───────────────────────────────────────────────────────

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_donation_card, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder h, int position) {
        Donation d = filteredDonations.get(position);

        // Initials avatar
        String initials = d.getAllotment() != null && !d.getAllotment().isEmpty()
                ? String.valueOf(d.getAllotment().charAt(0)).toUpperCase() : "?";
        h.tvInitials.setText(initials);

        h.tvAllotment.setText(d.getAllotment());
        h.tvItems.setText(d.getItems());
        h.tvDate.setText(d.getDate());
        h.tvWeight.setText(d.getWeightKg() + " kg");
        h.tvDistance.setText(d.getDistanceKm() + " km");

        if (d.getNote() != null && !d.getNote().isEmpty()) {
            h.tvNote.setText(d.getNote());
            h.tvNote.setVisibility(View.VISIBLE);
        } else {
            h.tvNote.setVisibility(View.GONE);
        }

        // Status badge colour
        String status = d.getStatus() != null ? d.getStatus() : "";
        h.tvStatus.setText(status);
        switch (status.toLowerCase()) {
            case "pending":
                h.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                h.tvStatus.setTextColor(Color.parseColor("#E65100"));
                break;
            case "confirmed":
                h.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                h.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "in transit":
                h.tvStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                h.tvStatus.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "collected":
                h.tvStatus.setBackgroundColor(Color.parseColor("#F3E5F5"));
                h.tvStatus.setTextColor(Color.parseColor("#6A1B9A"));
                break;
            default:
                h.tvStatus.setBackgroundColor(Color.TRANSPARENT);
                h.tvStatus.setTextColor(Color.GRAY);
        }

        // Action buttons
        boolean isPending   = "pending".equalsIgnoreCase(status);
        boolean isConfirmed = "confirmed".equalsIgnoreCase(status);
        h.btnConfirm.setVisibility(isPending   ? View.VISIBLE : View.GONE);
        h.btnMarkCollected.setVisibility(isConfirmed ? View.VISIBLE : View.GONE);
        h.btnViewAllotment.setVisibility(View.VISIBLE);

        h.btnConfirm.setOnClickListener(v -> listener.onConfirm(d));
        h.btnMarkCollected.setOnClickListener(v -> listener.onMarkCollected(d));
        h.btnViewAllotment.setOnClickListener(v -> listener.onViewAllotment(d));
    }

    @Override
    public int getItemCount() { return filteredDonations.size(); }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvAllotment, tvItems, tvDate, tvNote,
                tvWeight, tvDistance, tvStatus;
        Button btnConfirm, btnMarkCollected, btnViewAllotment;

        DonationViewHolder(@NonNull View v) {
            super(v);
            tvInitials       = v.findViewById(R.id.tvInitials);
            tvAllotment      = v.findViewById(R.id.tvAllotment);
            tvItems          = v.findViewById(R.id.tvItems);
            tvDate           = v.findViewById(R.id.tvDate);
            tvNote           = v.findViewById(R.id.tvNote);
            tvWeight         = v.findViewById(R.id.tvWeight);
            tvDistance       = v.findViewById(R.id.tvDistance);
            tvStatus         = v.findViewById(R.id.tvStatus);
            btnConfirm       = v.findViewById(R.id.btnConfirm);
            btnMarkCollected = v.findViewById(R.id.btnMarkCollected);
            btnViewAllotment = v.findViewById(R.id.btnViewAllotment);
        }
    }
}