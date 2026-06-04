package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.google.android.material.button.MaterialButton;

/**
 * ProducerEntryActivity
 * ──────────────────────
 * Shown when user taps "Allotment Donator" on the main screen.
 * Two options: Log in or Sign up.
 */
public class ProducerEntryActivity extends AppCompatActivity {

    private MaterialButton btnLogin;
    private MaterialButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_entry);

        btnLogin  = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProducerLoginActivity.class);
            startActivity(intent);
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProducerRegisterActivity.class);
            startActivity(intent);
        });
    }
}