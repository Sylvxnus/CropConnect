package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.example.cropconnect.activities.MainActivity;
import com.example.cropconnect.models.Producer;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProducerSettingsActivity
 * ─────────────────────────
 * Lets a logged-in producer change their name and email.
 * Pre-fills from the saved session, sends a PUT to /api/producers/{id},
 * refreshes the session on success so the dashboard greeting stays in sync,
 * and offers a log out action. Password and postcode are not edited here.
 */
public class ProducerSettingsActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail;
    private TextInputEditText etName, etEmail;
    private MaterialButton btnSave, btnLogout, btnDeleteAccount;
    private ImageButton btnBack;
    private TextView textSettingsError;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_settings);

        session = new SessionManager(this);

        bindViews();
        applyWindowInsets();
        prefillFromSession();
        setupButtons();
        setupBottomNav();
    }

    private void bindViews() {
        tilName           = findViewById(R.id.tilName);
        tilEmail          = findViewById(R.id.tilEmail);
        etName            = findViewById(R.id.etName);
        etEmail           = findViewById(R.id.etEmail);
        btnSave           = findViewById(R.id.btnSave);
        btnLogout         = findViewById(R.id.btnLogout);
        btnBack           = findViewById(R.id.btnBack);
        btnDeleteAccount  = findViewById(R.id.btnDeleteAccount);
        textSettingsError = findViewById(R.id.textSettingsError);
    }

    private void prefillFromSession() {
        etName.setText(session.getProdName());
        etEmail.setText(session.getProdEmail());
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> { if (validateForm()) saveChanges(); });
        btnLogout.setOnClickListener(v -> logout());
        btnDeleteAccount.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_settings);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, PROD_Dashboard.class));
                finish();
                return true;
            }
            if (id == R.id.nav_donate) {
                startActivity(new Intent(this, ProcessDonations.class));
                finish();
                return true;
            }
            return id == R.id.nav_settings;
        });
    }


    private void applyWindowInsets() {
        View root = findViewById(R.id.settingsRoot);
        final int left   = root.getPaddingLeft();
        final int top    = root.getPaddingTop();
        final int right  = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(left, top + bars.top, right, bottom);
            return insets;
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String name  = etName.getText()  != null ? etName.getText().toString().trim()  : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            valid = false;
        } else { tilName.setError(null); }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email");
            valid = false;
        } else { tilEmail.setError(null); }

        return valid;
    }

    private void saveChanges() {
        final String name  = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final int prodId   = session.getProdId();

        // Only name and email are sent; the backend leaves password/postcode intact
        Producer update = new Producer();
        update.setProdName(name);
        update.setProdEmail(email);

        btnSave.setEnabled(false);
        textSettingsError.setVisibility(View.GONE);

        ApiService api = ApiClient.getApiService();
        api.updateProducer(prodId, update).enqueue(new Callback<Producer>() {
            @Override
            public void onResponse(Call<Producer> call, Response<Producer> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Keep the cached session current so the dashboard greeting updates
                    session.saveProducerSession(prodId, name, email);
                    Toast.makeText(ProducerSettingsActivity.this,
                            "Profile updated", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 409) {
                    showError("That email is already in use");
                } else {
                    showError("Couldn't save changes. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Producer> call, Throwable t) {
                btnSave.setEnabled(true);
                showError("Connection failed. Is the server running?");
            }
        });
    }

    private void confirmDeleteAccount() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete account?")
                .setMessage("This permanently deletes your account and can't be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .show();
    }

    private void deleteAccount() {
        final int prodId = session.getProdId();

        btnDeleteAccount.setEnabled(false);
        textSettingsError.setVisibility(View.GONE);

        ApiService api = ApiClient.getApiService();
        api.deleteProducer(prodId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    session.clearSession();
                    Toast.makeText(ProducerSettingsActivity.this,
                            "Account deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProducerSettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    btnDeleteAccount.setEnabled(true);
                    showError("Couldn't delete account. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnDeleteAccount.setEnabled(true);
                showError("Connection failed. Is the server running?");
            }
        });
    }

    private void logout() {
        session.clearSession();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        textSettingsError.setText(message);
        textSettingsError.setVisibility(View.VISIBLE);
    }
}