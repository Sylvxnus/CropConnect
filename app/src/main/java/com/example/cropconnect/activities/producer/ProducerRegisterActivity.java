package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.models.Producer;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProducerRegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPostcode,
            tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPostcode,
            etPassword, etConfirmPassword;
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
        tilName            = findViewById(R.id.tilName);
        tilEmail           = findViewById(R.id.tilEmail);
        tilPostcode        = findViewById(R.id.tilPostcode);
        tilPassword        = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etName             = findViewById(R.id.etName);
        etEmail            = findViewById(R.id.etEmail);
        etPostcode         = findViewById(R.id.etPostcode);
        etPassword         = findViewById(R.id.etPassword);
        etConfirmPassword  = findViewById(R.id.etConfirmPassword);
        btnRegister        = findViewById(R.id.btnRegister);
        textRegisterError  = findViewById(R.id.textRegisterError);
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            if (validateForm()) registerProducer();
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String name     = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String postcode = etPostcode.getText() != null ? etPostcode.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm  = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (name.isEmpty()) { tilName.setError("Name is required"); valid = false; }
        else tilName.setError(null);

        if (email.isEmpty()) { tilEmail.setError("Email is required"); valid = false; }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email"); valid = false; }
        else tilEmail.setError(null);

        if (postcode.isEmpty()) { tilPostcode.setError("Postcode is required"); valid = false; }
        else if (postcode.length() < 5 || postcode.length() > 8) {
            tilPostcode.setError("Enter a valid UK postcode"); valid = false; }
        else tilPostcode.setError(null);

        if (password.isEmpty()) { tilPassword.setError("Password is required"); valid = false; }
        else if (password.length() < 6) {
            tilPassword.setError("At least 6 characters"); valid = false; }
        else tilPassword.setError(null);

        if (confirm.isEmpty()) { tilConfirmPassword.setError("Please confirm your password"); valid = false; }
        else if (!confirm.equals(password)) {
            tilConfirmPassword.setError("Passwords do not match"); valid = false; }
        else tilConfirmPassword.setError(null);

        return valid;
    }

    private void registerProducer() {
        Producer newProducer = new Producer(
                etName.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString().trim(),
                etPostcode.getText().toString().trim().toUpperCase()
        );

        btnRegister.setEnabled(false);

        ApiService apiService = ApiClient.getApiService();
        apiService.registerProducer(newProducer).enqueue(new Callback<Producer>() {
            @Override
            public void onResponse(Call<Producer> call, Response<Producer> response) {
                btnRegister.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ProducerRegisterActivity.this,
                            "Account created! Please log in.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(
                            ProducerRegisterActivity.this, ProducerEntryActivity.class);
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
}