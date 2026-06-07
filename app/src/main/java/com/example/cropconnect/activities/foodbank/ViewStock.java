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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        SessionManager session = new SessionManager(this);
        fbId = session.getFbId();
        if (fbId == -1L) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerStock = findViewById(R.id.recyclerStock);
        progressBar   = findViewById(R.id.progressBar);
        tvEmpty       = findViewById(R.id.tvEmpty);
        etSearch      = findViewById(R.id.etSearch);
        Button btnAdd = findViewById(R.id.btnAddProduct);

        adapter = new StockAdapter(new ArrayList<>(), this);
        recyclerStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerStock.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString(), allProducts);
                tvEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ViewStock.this, AlterStock.class);
            intent.putExtra("fb_id", fbId);
            startActivity(intent);
        });

        apiService = ApiClient.getApiService();
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    // ── StockAdapter.OnProductClickListener ───────────────────────────────
    // Tap a product card → show Edit / Delete dialog
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