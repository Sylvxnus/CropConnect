package com.example.cropconnect.activities.foodbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.utils.SessionManager;
import com.example.cropconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodBankLoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView textLoginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbank_login);
        bindViews();
        setupLoginButton();
    }

    private void bindViews() {
        tilEmail       = findViewById(R.id.tilFoodbankEmail);
        tilPassword    = findViewById(R.id.tilFoodbankPassword);
        etEmail        = findViewById(R.id.etFoodbankEmail);
        etPassword     = findViewById(R.id.etFoodbankPassword);
        btnLogin       = findViewById(R.id.btnFoodbankLogin);
        textLoginError = findViewById(R.id.textFoodbankLoginError);
    }
    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            if (validateForm()) loginFoodBank();
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            valid = false;
        } else { tilEmail.setError(null); }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            valid = false;
        } else { tilPassword.setError(null); }

        return valid;
    }

    private void loginFoodBank() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        FoodBank loginRequest = new FoodBank();
        loginRequest.setFoodEmail(email);
        loginRequest.setFoodPassword(password);

        btnLogin.setEnabled(false);

        ApiService apiService = ApiClient.getApiService();
        apiService.loginFoodBank(loginRequest).enqueue(new Callback<FoodBank>() {
            @Override
            public void onResponse(Call<FoodBank> call, Response<FoodBank> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    SessionManager.saveFoodBankSession(
                            FoodBankLoginActivity.this, response.body());
                    navigateToDashboard();
                } else {
                    showError("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<FoodBank> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("Connection failed. Is the server running?");
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, FOOD_Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showError(String message) {
        textLoginError.setText(message);
        textLoginError.setVisibility(View.VISIBLE);
    }
}