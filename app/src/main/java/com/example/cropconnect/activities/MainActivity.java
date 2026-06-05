package com.example.cropconnect.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.example.cropconnect.activities.foodbank.FB_Dashboard;
import com.example.cropconnect.activities.foodbank.FoodBankLoginActivity;
import com.example.cropconnect.activities.guest.Maps;
import com.example.cropconnect.activities.producer.PROD_Dashboard;
import com.example.cropconnect.activities.producer.ProducerEntryActivity;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import android.content.res.Configuration;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MaterialButton btnAllotmentDonator = findViewById(R.id.btnAllotmentDonator);
        MaterialButton btnFoodBank         = findViewById(R.id.btnFoodBank);
        MaterialButton btnGuest            = findViewById(R.id.btnGuest);

        // Language Selector
        ImageButton btnLanguage = findViewById(R.id.btnLanguage);

        btnLanguage.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, btnLanguage);

            popupMenu.getMenu().add(0, 1, 0, getString(R.string.english));
            popupMenu.getMenu().add(0, 2, 1, getString(R.string.urdu));

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    setLocale("en");
                    return true;
                }

                if (item.getItemId() == 2) {
                    setLocale("ur");
                    return true;
                }

                return false;
            });

            popupMenu.show();
        });

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
    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);

        getResources().updateConfiguration(
                configuration,
                getResources().getDisplayMetrics()
        );

        recreate();
    }
}