package com.example.cropconnect.activities.foodbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cropconnect.R;
import com.example.cropconnect.adapters.DonationAdapter;
import com.example.cropconnect.models.Donation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class FoodBankDonationsActivity extends AppCompatActivity
        implements DonationAdapter.OnDonationActionListener {

    private DonationAdapter adapter;
    private TextView tvResultCount;
    private String currentStatus = "all";
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbank_donations);

        tvResultCount = findViewById(R.id.tvResultCount);
        RecyclerView rv = findViewById(R.id.rvDonations);

        // Build sample data — replace with your real DB/API calls
        List<Donation> donations = getSampleDonations();

        adapter = new DonationAdapter(this, donations, this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        updateCount();

        // Search
        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s.toString();
                adapter.filter(currentSearch, currentStatus, "all");
                updateCount();
            }
            public void afterTextChanged(Editable s) {}
        });

        // Status chip filter
        ChipGroup chipGroup = findViewById(R.id.chipGroupStatus);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) { currentStatus = "all"; }
            else {
                int id = checkedIds.get(0);
                if      (id == R.id.chipPending)   currentStatus = "Pending";
                else if (id == R.id.chipConfirmed) currentStatus = "Confirmed";
                else if (id == R.id.chipTransit)   currentStatus = "In transit";
                else if (id == R.id.chipCollected) currentStatus = "Collected";
                else                               currentStatus = "all";
            }
            adapter.filter(currentSearch, currentStatus, "all");
            updateCount();
        });

        // Bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_donations);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new android.content.Intent(this, FB_Dashboard.class));
                return true;
            }
            return id == R.id.nav_donations;
        });
    }

    private void updateCount() {
        int count = adapter.getFilteredCount();
        tvResultCount.setText("Showing " + count + " donation" + (count != 1 ? "s" : ""));
    }

    @Override
    public void onConfirm(Donation donation) {
        // TODO: update donation status in the database/API
        // Then refresh the list
    }

    @Override
    public void onMarkCollected(Donation donation) {
        // TODO: update donation status in the database/API
    }

    @Override
    public void onViewAllotment(Donation donation) {
        // TODO: navigate to allotment detail screen
    }

    /*public void DonationsPageButton(View view) {
        startActivity(new Intent(CurrentActivity.this, DonationsPageButton.class));
    }*/

    private List<Donation> getSampleDonations() {
        List<Donation> list = new ArrayList<>();
        list.add(new Donation(1, "Green Gates", "Courgettes, tomatoes", 42, 1.4f, "Confirmed", "Today, 09:14", "Bagged and ready at the gate", "veg"));
        list.add(new Donation(2, "Riverside Plot", "Mixed herbs", 6, 3.1f, "In transit", "Today, 11:30", "", "herb"));
        list.add(new Donation(3, "Sunny Fields", "Apples, pears", 35, 2.7f, "Pending", "Yesterday", "Best collected before Friday", "fruit"));
        list.add(new Donation(4, "Oak Lane", "Runner beans", 14, 0.8f, "Pending", "Yesterday", "", "legume"));
        list.add(new Donation(5, "Green Gates", "Potatoes, carrots", 58, 1.4f, "Collected", "2 days ago", "", "veg"));
        list.add(new Donation(6, "Meadow View", "Wheat, oats", 30, 4.2f, "Confirmed", "2 days ago", "", "grain"));
        return list;
    }
}