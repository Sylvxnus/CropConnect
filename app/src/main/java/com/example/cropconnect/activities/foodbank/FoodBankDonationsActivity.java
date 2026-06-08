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
import com.example.cropconnect.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;
import com.example.cropconnect.activities.foodbank.ViewStock;

public class FoodBankDonationsActivity extends AppCompatActivity
        implements DonationAdapter.OnDonationActionListener {

    private DonationAdapter adapter;
    private TextView tvResultCount;
    private String currentStatus = "all";
    private String currentSearch = "";
    private RecyclerView rvDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbank_donations);

        tvResultCount = findViewById(R.id.tvResultCount);
        RecyclerView rv = findViewById(R.id.rvDonations);

        rvDonations = rv;
        adapter = new DonationAdapter(this, new ArrayList<>(), this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

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
            if (id == R.id.nav_stock) {                          // ← add this
                startActivity(new Intent(this, ViewStock.class)); // ← add this
                return true;                                      // ← add this
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
        updateDonationStatus(donation, "Confirmed");
    }

    @Override
    public void onMarkCollected(Donation donation) {
        updateDonationStatus(donation, "Collected");
    }

    @Override
    public void onViewAllotment(Donation donation) {
        // Optional: navigate to allotment detail screen
    }

    private void loadDonations(long fbId) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(
                        "http://10.0.2.2:8080/api/donations/foodbank/" + fbId);
                java.net.HttpURLConnection conn =
                        (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                if (conn.getResponseCode() != 200) return;

                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                org.json.JSONArray array = new org.json.JSONArray(sb.toString());
                List<Donation> donations = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject obj = array.getJSONObject(i);
                    donations.add(new Donation(
                            obj.getInt("donation_id"),
                            obj.optString("prod_name", "Unknown"),
                            obj.optString("items", ""),
                            (int) obj.optDouble("weight_kg", 0),
                            0f,
                            obj.optString("status", "Pending"),
                            obj.optString("created_at", ""),
                            obj.optString("note", ""),
                            obj.optString("food_type", "")
                    ));
                }

                runOnUiThread(() -> {
                    adapter.setDonations(donations);
                    updateCount();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionManager session = new SessionManager(this);
        long fbId = session.getFbId();
        if (fbId != -1L) loadDonations(fbId);
    }

    private void updateDonationStatus(Donation donation, String newStatus) {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(
                        "http://10.0.2.2:8080/api/donations/" + donation.getId() + "/status");
                java.net.HttpURLConnection conn =
                        (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                String body = "{\"status\":\"" + newStatus + "\"}";
                conn.getOutputStream().write(body.getBytes());
                conn.getOutputStream().close();

                int code = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (code == 200) {
                        // Update the donation status locally and refresh the list
                        donation.setStatus(newStatus);
                        adapter.notifyDataSetChanged();
                        android.widget.Toast.makeText(this,
                                "Marked as " + newStatus,
                                android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        android.widget.Toast.makeText(this,
                                "Update failed (" + code + ")",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> android.widget.Toast.makeText(this,
                        "Connection failed: " + e.getMessage(),
                        android.widget.Toast.LENGTH_LONG).show());
            }
        }).start();
    }

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