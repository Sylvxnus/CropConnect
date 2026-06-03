package com.example.cropconnect.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.example.cropconnect.activities.foodbank.FoodBankLoginActivity;
import com.example.cropconnect.activities.guest.Maps;
import com.example.cropconnect.activities.producer.ProducerEntryActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton btnAllotmentDonator = findViewById(R.id.btnAllotmentDonator);
        MaterialButton btnFoodBank         = findViewById(R.id.btnFoodBank);
        MaterialButton btnGuest            = findViewById(R.id.btnGuest);

        // Allotment Donator → ProducerEntryActivity (Login / Sign up)
        btnAllotmentDonator.setOnClickListener(v -> {
            startActivity(new Intent(this, ProducerEntryActivity.class));
        });

        // Food Bank → FoodBankLoginActivity (team's existing foodbank login)
        btnFoodBank.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodBankLoginActivity.class));
        });

        // Guest → Maps (existing guest map screen)
        btnGuest.setOnClickListener(v -> {
            startActivity(new Intent(this, Maps.class));
        });
    }
}