package com.example.cropconnect.activities.foodbank;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cropconnect.R;
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlterStock extends AppCompatActivity {

    // Intent extras keys (must match ViewStock.java)
    public static final String EXTRA_FB_ID        = "fb_id";
    public static final String EXTRA_PRODUCT_ID   = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";
    public static final String EXTRA_PRODUCT_QUANT= "product_quant";
    public static final String EXTRA_CATEGORY     = "category";
    public static final String EXTRA_UNIT         = "unit";
    public static final String EXTRA_EXPIRY_DATE  = "expiry_date";

    private EditText etProductName, etQuantity;
    private Spinner spinnerUnit, spinnerCategory;
    private TextView tvSelectedDate, tvError;
    private Button btnSave, btnPickDate;

    private String selectedDate = null;
    private long fbId;
    private long productId = -1L;  // -1 = create mode
    private boolean isEditMode = false;

    private ApiService apiService;

    private static final String[] UNITS = {"kg", "g", "litres", "ml", "units", "cans", "boxes"};
    private static final String[] CATEGORIES = {
            "Dry Goods", "Tinned Goods", "Fresh Produce",
            "Dairy", "Bakery", "Frozen", "Beverages", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodbank_alter_stock);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind views
        etProductName  = findViewById(R.id.etProductName);
        etQuantity     = findViewById(R.id.etQuantity);
        spinnerUnit    = findViewById(R.id.spinnerUnit);
        spinnerCategory= findViewById(R.id.spinnerCategory);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvError        = findViewById(R.id.tvError);
        btnSave        = findViewById(R.id.btnSave);
        btnPickDate    = findViewById(R.id.btnPickDate);

        // Populate spinners
        spinnerUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, UNITS));
        spinnerCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, CATEGORIES));

        // Read intent extras
        fbId = getIntent().getLongExtra(EXTRA_FB_ID, -1L);
        productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, -1L);
        isEditMode = productId != -1L;

        if (isEditMode) {
            // Pre-fill form with existing values
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Edit Product");
            etProductName.setText(getIntent().getStringExtra(EXTRA_PRODUCT_NAME));
            etQuantity.setText(String.valueOf(getIntent().getIntExtra(EXTRA_PRODUCT_QUANT, 0)));
            selectedDate = getIntent().getStringExtra(EXTRA_EXPIRY_DATE);
            tvSelectedDate.setText(selectedDate != null ? selectedDate : "No date selected");

            // Set spinner selections to match existing data
            setSpinnerValue(spinnerUnit, UNITS, getIntent().getStringExtra(EXTRA_UNIT));
            setSpinnerValue(spinnerCategory, CATEGORIES, getIntent().getStringExtra(EXTRA_CATEGORY));
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Add Product");
        }

        // Date picker
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // Save
        btnSave.setOnClickListener(v -> saveProduct());

        apiService = ApiClient.getApiService();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
            tvSelectedDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveProduct() {
        String name  = etProductName.getText().toString().trim();
        String quantStr = etQuantity.getText().toString().trim();

        // Validation
        if (name.isEmpty()) { showError("Product name is required."); return; }
        if (quantStr.isEmpty()) { showError("Quantity is required."); return; }

        int quant;
        try {
            quant = Integer.parseInt(quantStr);
            if (quant < 0) { showError("Quantity cannot be negative."); return; }
        } catch (NumberFormatException e) {
            showError("Enter a valid whole number for quantity.");
            return;
        }

        FoodBankProduct product = new FoodBankProduct();
        product.setFbId(fbId);
        product.setProductName(name);
        product.setProductQuant(quant);
        product.setCategory(spinnerCategory.getSelectedItem().toString());
        product.setUnit(spinnerUnit.getSelectedItem().toString());
        product.setExpiryDate(selectedDate);

        btnSave.setEnabled(false);
        tvError.setVisibility(View.GONE);

        if (isEditMode) {
            apiService.updateProduct(productId, product).enqueue(new Callback<FoodBankProduct>() {
                @Override
                public void onResponse(Call<FoodBankProduct> call, Response<FoodBankProduct> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(AlterStock.this, "Product updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError("Update failed (" + response.code() + ")");
                    }
                }
                @Override
                public void onFailure(Call<FoodBankProduct> call, Throwable t) {
                    btnSave.setEnabled(true);
                    showError("Connection error: " + t.getMessage());
                }
            });
        } else {
            apiService.createProduct(product).enqueue(new Callback<FoodBankProduct>() {
                @Override
                public void onResponse(Call<FoodBankProduct> call, Response<FoodBankProduct> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(AlterStock.this, "Product added!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError("Save failed (" + response.code() + ")");
                    }
                }
                @Override
                public void onFailure(Call<FoodBankProduct> call, Throwable t) {
                    btnSave.setEnabled(true);
                    showError("Connection error: " + t.getMessage());
                }
            });
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
    private void setSpinnerValue(Spinner spinner, String[] values, String target) {
        if (target == null) return;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equalsIgnoreCase(target)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}