package com.example.cropconnect.activities.foodbank;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cropconnect.R;
import com.example.cropconnect.adapters.StockAdapter;
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewStock extends AppCompatActivity implements StockAdapter.OnProductClickListener {

    private RecyclerView recyclerStock;
    private StockAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private EditText etSearch;

    private List<FoodBankProduct> allProducts = new ArrayList<>();
    private ApiService apiService;
    private long fbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodbank_view_stock);

        // Toolbar — no back button, this is a top-level nav destination
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get logged-in foodbank ID from session
        SessionManager session = new SessionManager(this);
        fbId = session.getFbId();
        if (fbId == -1L) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        recyclerStock = findViewById(R.id.recyclerStock);
        progressBar   = findViewById(R.id.progressBar);
        tvEmpty       = findViewById(R.id.tvEmpty);
        etSearch      = findViewById(R.id.etSearch);
        Button btnAdd = findViewById(R.id.btnAddProduct);

        // Set up RecyclerView
        adapter = new StockAdapter(new ArrayList<>(), this);
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setAdapter(adapter);

        // Live search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), allProducts);
                tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Add product → open AlterStock in create mode
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ViewStock.this, AlterStock.class);
            intent.putExtra("fb_id", fbId);
            startActivity(intent);
        });

        // ── Bottom navigation ─────────────────────────────────────────
        setupBottomNav();

        apiService = ApiClient.getApiService();
        loadProducts();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        // Mark Stock as the selected tab
        bottomNav.setSelectedItemId(R.id.nav_stock);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                // Navigate back to dashboard — clear stack so back button doesn't loop
                Intent intent = new Intent(this, FB_Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            if (id == R.id.nav_donations) {
                startActivity(new Intent(this, FoodBankDonationsActivity.class));
                return true;
            }
            // nav_stock — already here, do nothing
            return id == R.id.nav_stock;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from AlterStock
        loadProducts();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        apiService.getProducts(fbId).enqueue(new Callback<List<FoodBankProduct>>() {
            @Override
            public void onResponse(Call<List<FoodBankProduct>> call,
                                   Response<List<FoodBankProduct>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    adapter.setProducts(allProducts);
                    tvEmpty.setVisibility(allProducts.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showError("Failed to load stock (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<FoodBankProduct>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Connection error: " + t.getMessage());
            }
        });
    }

    // ── StockAdapter.OnProductClickListener ───────────────────────────
    // Tap a product card → show Edit / Delete options
    @Override
    public void onProductClick(FoodBankProduct product) {
        new AlertDialog.Builder(this)
                .setTitle(product.getProductName())
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) openEditScreen(product);
                    else            confirmDelete(product);
                })
                .show();
    }

    private void openEditScreen(FoodBankProduct product) {
        Intent intent = new Intent(this, AlterStock.class);
        intent.putExtra("fb_id", fbId);
        intent.putExtra("product_id", product.getProductId());
        intent.putExtra("product_name", product.getProductName());
        intent.putExtra("product_quant", product.getProductQuant());
        intent.putExtra("category", product.getCategory());
        intent.putExtra("unit", product.getUnit());
        intent.putExtra("expiry_date", product.getExpiryDate());
        startActivity(intent);
    }

    private void confirmDelete(FoodBankProduct product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Delete \"" + product.getProductName() + "\" from stock?")
                .setPositiveButton("Delete", (d, w) -> deleteProduct(product))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(FoodBankProduct product) {
        apiService.deleteProduct(product.getProductId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ViewStock.this,
                            product.getProductName() + " removed.", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    showError("Delete failed (" + response.code() + ")");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showError("Connection error: " + t.getMessage());
            }
        });
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}