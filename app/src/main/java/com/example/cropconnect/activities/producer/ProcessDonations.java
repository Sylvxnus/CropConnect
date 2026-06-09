package com.example.cropconnect.activities.producer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Color;

public class ProcessDonations extends AppCompatActivity {

    private View layoutStep1, layoutStep2;
    private TextView tvStep1, tvStep2, tvSelectedFoodBank, tvError;
    private TextInputEditText etItems, etWeight, etStorageReq, etExpiryDate, etNote;
    private Spinner spinnerFoodType;
    private ProgressBar progressBar;
    private RecyclerView rvFoodBanks;

    private FoodBank selectedFoodBank;
    private String selectedDate = null;
    private SessionManager session;

    private static final String[] FOOD_TYPES = {
            "Fresh Produce", "Dry Goods", "Tinned Goods",
            "Dairy", "Bakery", "Frozen", "Beverages", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_donation);

        session = new SessionManager(this);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (layoutStep2.getVisibility() == View.VISIBLE) {
                showStep1();
            } else {
                finish();
            }
        });

        // Bind views
        layoutStep1       = findViewById(R.id.layoutStep1);
        layoutStep2       = findViewById(R.id.layoutStep2);
        tvStep1           = findViewById(R.id.tvStep1);
        tvStep2           = findViewById(R.id.tvStep2);
        tvSelectedFoodBank= findViewById(R.id.tvSelectedFoodBank);
        tvError           = findViewById(R.id.tvError);
        etItems           = findViewById(R.id.etItems);
        etWeight          = findViewById(R.id.etWeight);
        etStorageReq      = findViewById(R.id.etStorageReq);
        etExpiryDate      = findViewById(R.id.etExpiryDate);
        etNote            = findViewById(R.id.etNote);
        spinnerFoodType   = findViewById(R.id.spinnerFoodType);
        progressBar       = findViewById(R.id.progressBar);
        rvFoodBanks       = findViewById(R.id.rvFoodBanks);

        // Food type spinner
        spinnerFoodType.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, FOOD_TYPES));

        // Date picker
        etExpiryDate.setOnClickListener(v -> showDatePicker());

        // Submit button
        findViewById(R.id.btnSubmit).setOnClickListener(v -> submitDonation());

        // Bottom nav
        setupBottomNav();

        // Load food banks
        loadFoodBanks();
    }

    private void loadFoodBanks() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getFoodBanks().enqueue(new Callback<List<FoodBank>>() {
            @Override
            public void onResponse(Call<List<FoodBank>> call,
                                   Response<List<FoodBank>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ProcessDonations.this,
                            "Could not load food banks", Toast.LENGTH_SHORT).show();
                    return;
                }
                setupFoodBankList(response.body());
            }

            @Override
            public void onFailure(Call<List<FoodBank>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProcessDonations.this,
                        "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFoodBankList(List<FoodBank> foodBanks) {
        rvFoodBanks.setLayoutManager(new LinearLayoutManager(this));
        rvFoodBanks.setAdapter(new RecyclerView.Adapter<FoodBankViewHolder>() {
            @NonNull
            @Override
            public FoodBankViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_foodbank_row, parent, false);
                return new FoodBankViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull FoodBankViewHolder h, int position) {
                FoodBank fb = foodBanks.get(position);
                String initials = fb.getFbName() != null && fb.getFbName().length() >= 2
                        ? fb.getFbName().substring(0, 2).toUpperCase() : "?";
                h.tvInitials.setText(initials);
                h.tvName.setText(fb.getFbName());

                String details = (fb.isOpen() ? "Open now" : "Closed")
                        + (fb.isNoReferral() ? " · No referral needed" : "")
                        + (fb.isHasFreshProduce() ? " · Accepts fresh produce" : "");
                h.tvDetails.setText(details);

                h.itemView.setOnClickListener(v -> {
                    selectedFoodBank = fb;
                    tvSelectedFoodBank.setText(fb.getFbName());
                    showStep2();
                });
            }

            @Override
            public int getItemCount() { return foodBanks.size(); }
        });
    }

    class FoodBankViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvDetails;
        FoodBankViewHolder(View v) {
            super(v);
            tvInitials = v.findViewById(R.id.tvFbInitials);
            tvName     = v.findViewById(R.id.tvFbName);
            tvDetails  = v.findViewById(R.id.tvFbDetails);
        }
    }

    private void showStep1() {
        layoutStep1.setVisibility(View.VISIBLE);
        layoutStep2.setVisibility(View.GONE);
        tvStep1.setTextColor(Color.parseColor("#639922"));
        tvStep2.setTextColor(Color.GRAY);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Make a Donation");
    }

    private void showStep2() {
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.VISIBLE);
        tvStep1.setTextColor(Color.GRAY);
        tvStep2.setTextColor(Color.parseColor("#639922"));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Donation Details");
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
            etExpiryDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitDonation() {
        // Validate
        String items      = etItems.getText() != null
                ? etItems.getText().toString().trim() : "";
        String weightStr  = etWeight.getText() != null
                ? etWeight.getText().toString().trim() : "";
        String storageReq = etStorageReq.getText() != null
                ? etStorageReq.getText().toString().trim() : "";
        String note       = etNote.getText() != null
                ? etNote.getText().toString().trim() : "";
        String foodType   = spinnerFoodType.getSelectedItem().toString();

        if (items.isEmpty()) { showError("Please enter the items you are donating."); return; }
        if (weightStr.isEmpty()) { showError("Please enter the estimated weight."); return; }
        if (selectedDate == null) { showError("Please select a best before date."); return; }
        if (storageReq.isEmpty()) { showError("Please enter storage requirements."); return; }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0) { showError("Weight must be greater than 0."); return; }
        } catch (NumberFormatException e) {
            showError("Please enter a valid weight.");
            return;
        }

        tvError.setVisibility(View.GONE);
        findViewById(R.id.btnSubmit).setEnabled(false);

        long prodId = session.getProdId();
        long fbId   = selectedFoodBank.getFbId();
        final double finalWeight = weight;
        final String finalItems  = items;
        final String finalStorage = storageReq;
        final String finalNote   = note;
        final String finalType   = foodType;
        final String finalDate   = selectedDate;

        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/submit");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                JSONObject body = new JSONObject();
                body.put("prod_id", prodId);
                body.put("fb_id", fbId);
                body.put("items", finalItems);
                body.put("weight_kg", finalWeight);
                body.put("donation_amount", finalWeight);
                body.put("food_type", finalType);
                body.put("storage_req", finalStorage);
                body.put("expiry_date", finalDate);
                body.put("note", finalNote);
                body.put("status", "Pending");

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    if (responseCode == 200 || responseCode == 201) {
                        Toast.makeText(this,
                                "Donation submitted! The food bank will confirm shortly.",
                                Toast.LENGTH_LONG).show();
                        // Clear form and go back to food bank list
                        etItems.setText("");
                        etWeight.setText("");
                        etStorageReq.setText("");
                        etExpiryDate.setText("");
                        etNote.setText("");
                        selectedDate = null;
                        showStep1();
                    } else {
                        showError("Submission failed (" + responseCode + "). Try again.");
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    showError("Connection failed: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_donate);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new android.content.Intent(this, PROD_Dashboard.class));
                finish();
                return true;
            }
            if (id == R.id.nav_settings) {
                startActivity(new android.content.Intent(this, ProducerSettingsActivity.class));
                finish();
                return true;
            }
            return id == R.id.nav_donate;
        });
    }
}