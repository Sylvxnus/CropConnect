package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.example.cropconnect.models.Producer;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProducerRegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPostcode;
    private TextInputLayout tilPlotType;
    private TextInputLayout tilAnnualRent;
    private TextInputLayout tilAllotmentReference;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPostcode;
    private TextInputEditText etPlotType;
    private TextInputEditText etAnnualRent;
    private TextInputEditText etAllotmentReference;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private SwitchMaterial switchActivePlotHolder;

    private MaterialButton btnRegister;
    private TextView textRegisterError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_register);
        bindViews();
        setupRegisterButton();
    }

    private void bindViews() {
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPostcode = findViewById(R.id.tilPostcode);
        tilPlotType = findViewById(R.id.tilPlotType);
        tilAnnualRent = findViewById(R.id.tilAnnualRent);
        tilAllotmentReference = findViewById(R.id.tilAllotmentReference);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPostcode = findViewById(R.id.etPostcode);
        etPlotType = findViewById(R.id.etPlotType);
        etAnnualRent = findViewById(R.id.etAnnualRent);
        etAllotmentReference = findViewById(R.id.etAllotmentReference);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        switchActivePlotHolder = findViewById(R.id.switchActivePlotHolder);

        btnRegister = findViewById(R.id.btnRegister);
        textRegisterError = findViewById(R.id.textRegisterError);
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                registerProducer();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = textOf(etName);
        String email = textOf(etEmail);
        String postcode = textOf(etPostcode);
        String plotType = textOf(etPlotType);
        String annualRent = textOf(etAnnualRent);
        String password = textOf(etPassword);
        String confirm = textOf(etConfirmPassword);

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            valid = false;
        } else {
            tilName.setError(null);
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        if (!postcode.matches("^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$")) {
            tilPostcode.setError("Enter a valid UK postcode");
            valid = false;
        } else {
            tilPostcode.setError(null);
        }

        if (!plotType.isEmpty() && !isValidPlotType(plotType)) {
            tilPlotType.setError("Use Mini, Small, Standard, or Large");
            valid = false;
        } else {
            tilPlotType.setError(null);
        }

        if (!annualRent.isEmpty()) {
            try {
                double value = Double.parseDouble(annualRent);
                if (value <= 0) {
                    tilAnnualRent.setError("Annual rent must be greater than 0");
                    valid = false;
                } else {
                    tilAnnualRent.setError(null);
                }
            } catch (NumberFormatException e) {
                tilAnnualRent.setError("Enter a valid rent amount");
                valid = false;
            }
        } else {
            tilAnnualRent.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            valid = false;
        } else if (password.length() < 8) {
            tilPassword.setError("Password must be at least 8 characters");
            valid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            tilPassword.setError("Password must contain at least one uppercase letter");
            valid = false;
        } else if (!password.matches(".*[0-9].*")) {
            tilPassword.setError("Password must contain at least one number");
            valid = false;
        } else if (!password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*")) {
            tilPassword.setError("Password must contain at least one special character");
            valid = false;
        } else {
            tilPassword.setError(null);
        }

        if (confirm.isEmpty()) {
            tilConfirmPassword.setError("Please confirm your password");
            valid = false;
        } else if (!confirm.equals(password)) {
            tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        return valid;
    }

    private void registerProducer() {
        Producer newProducer = new Producer();
        newProducer.setProdName(textOf(etName));
        newProducer.setProdEmail(textOf(etEmail));
        newProducer.setProdPassword(textOf(etPassword));
        newProducer.setProdPostcode(textOf(etPostcode).toUpperCase(Locale.UK));
        newProducer.setBccActivePlotHolder(switchActivePlotHolder.isChecked());

        String plotType = textOf(etPlotType);
        String annualRent = textOf(etAnnualRent);
        String allotmentReference = textOf(etAllotmentReference);

        if (!plotType.isEmpty()) {
            newProducer.setProdPlotType(toCanonicalPlotType(plotType));
        }
        if (!annualRent.isEmpty()) {
            newProducer.setProdAnnualRent(Double.parseDouble(annualRent));
        }
        if (!allotmentReference.isEmpty()) {
            newProducer.setAllotmentReference(allotmentReference);
        }

        btnRegister.setEnabled(false);
        textRegisterError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService();
        apiService.registerProducer(newProducer).enqueue(new Callback<Producer>() {
            @Override
            public void onResponse(Call<Producer> call, Response<Producer> response) {
                btnRegister.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(
                            ProducerRegisterActivity.this,
                            "Account created! Please log in.",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = new Intent(
                            ProducerRegisterActivity.this,
                            ProducerEntryActivity.class
                    );
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 409) {
                    showError("An account with this email already exists");
                } else {
                    showError("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Producer> call, Throwable t) {
                btnRegister.setEnabled(true);
                showError("Connection failed. Is the server running?");
            }
        });
    }

    private void showError(String message) {
        textRegisterError.setText(message);
        textRegisterError.setVisibility(View.VISIBLE);
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private boolean isValidPlotType(String plotType) {
        String normalized = plotType.trim().toLowerCase(Locale.UK);
        return normalized.equals("mini")
                || normalized.equals("small")
                || normalized.equals("standard")
                || normalized.equals("large");
    }

    private String toCanonicalPlotType(String plotType) {
        String normalized = plotType.trim().toLowerCase(Locale.UK);
        switch (normalized) {
            case "mini":
                return "MINI";
            case "small":
                return "SMALL";
            case "large":
                return "LARGE";
            case "standard":
            default:
                return "STANDARD";
        }
    }
}