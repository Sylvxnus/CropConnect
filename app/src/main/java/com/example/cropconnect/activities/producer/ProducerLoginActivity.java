package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.models.Producer;
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

public class ProducerLoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView textLoginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_login);
        bindViews();
        setupLoginButton();
    }

    private void bindViews() {
        tilEmail       = findViewById(R.id.tilEmail);
        tilPassword    = findViewById(R.id.tilPassword);
        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        btnLogin       = findViewById(R.id.btnLogin);
        textLoginError = findViewById(R.id.textLoginError);
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            if (validateForm()) loginProducer();
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

    private void loginProducer() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Producer loginRequest = new Producer();
        loginRequest.setProdEmail(email);
        loginRequest.setProdPassword(password);

        btnLogin.setEnabled(false);

        ApiService apiService = ApiClient.getApiService();
        apiService.loginProducer(loginRequest).enqueue(new Callback<Producer>() {
            @Override
            public void onResponse(Call<Producer> call, Response<Producer> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    SessionManager.saveProducerSession(
                            ProducerLoginActivity.this, response.body());
                    navigateToDashboard();
                } else {
                    showError("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<Producer> call, Throwable t) {
                btnLogin.setEnabled(true);
                showError("Connection failed. Is the server running?");
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, PROD_Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showError(String message) {
        textLoginError.setText(message);
        textLoginError.setVisibility(View.VISIBLE);
    }
}