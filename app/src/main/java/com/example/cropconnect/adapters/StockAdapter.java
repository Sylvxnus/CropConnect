package com.example.cropconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBankProduct;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(FoodBankProduct product);
    }

    private List<FoodBankProduct> products;
    private final OnProductClickListener listener;

    public StockAdapter(List<FoodBankProduct> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void setProducts(List<FoodBankProduct> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public void filter(String query, List<FoodBankProduct> allProducts) {
        if (query == null || query.isEmpty()) {
            products = new ArrayList<>(allProducts);
        } else {
            String lower = query.toLowerCase();
            List<FoodBankProduct> filtered = new ArrayList<>();
            for (FoodBankProduct p : allProducts) {
                if (p.getProductName().toLowerCase().contains(lower)
                        || (p.getCategory() != null
                        && p.getCategory().toLowerCase().contains(lower))) {
                    filtered.add(p);
                }
            }
            products = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_product, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder h, int position) {
        FoodBankProduct p = products.get(position);

        h.textProductName.setText(p.getProductName());
        h.chipCategory.setText(p.getCategory() != null ? p.getCategory() : "Uncategorised");

        // Quantity + unit
        String qty = p.getProductQuant() + (p.getUnit() != null ? " " + p.getUnit() : "");
        // Append incoming donation if present
        if (p.getUpcomingDonation() > 0) {
            qty += "  (+" + p.getUpcomingDonation() + " incoming)";
        }
        h.textQuantity.setText(qty);

        // Expiry
        h.textExpiry.setText(p.getExpiryDate() != null
                ? "Exp: " + p.getExpiryDate() : "No expiry set");

        // Tap card to edit
        h.itemView.setOnClickListener(v -> listener.onProductClick(p));
    }

    @Override
    public int getItemCount() { return products.size(); }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView textProductName, textQuantity, textExpiry;
        Chip     chipCategory;

        StockViewHolder(@NonNull View v) {
            super(v);
            textProductName = v.findViewById(R.id.textProductName);
            chipCategory    = v.findViewById(R.id.chipCategory);
            textQuantity    = v.findViewById(R.id.textQuantity);
            textExpiry      = v.findViewById(R.id.textExpiry);
        }
    }
}