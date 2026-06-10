package com.example.cropconnect.activities.producer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropconnect.R;
import com.example.cropconnect.utils.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PROD_Dashboard extends AppCompatActivity {

    private SessionManager session;
    private int prodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_dashboard);

        session = new SessionManager(this);
        prodId = session.getProdId();

        setupHeader();
        setupBottomNav();
        setupCreditActions();

        ((TextView) findViewById(R.id.tvTotalKg)).setText("...");
        ((TextView) findViewById(R.id.tvDonationCount)).setText("...");
        ((TextView) findViewById(R.id.tvAwaitingCount)).setText("...");
        ((TextView) findViewById(R.id.tvFoodBanksHelped)).setText("...");
        ((TextView) findViewById(R.id.tvCreditsNumber)).setText("...");
        ((TextView) findViewById(R.id.tvCreditsThisMonth)).setText("Loading...");

        if (prodId != -1) {
            loadSummary();
            loadCreditHero();
            loadRecentActivity();
            loadPieChart();
            loadBarChart();
            loadPendingDonations();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prodId != -1) {
            loadSummary();
            loadCreditHero();
            loadRecentActivity();
            loadPieChart();
            loadBarChart();
            loadPendingDonations();
        }
    }

    private void setupHeader() {
        String name = session.getProdName();
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (name != null && !name.isEmpty()) {
            tvWelcome.setText("Welcome back, " + name);
        }
    }

    private void loadSummary() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/producer/" + prodId + "/summary");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                double totalKg = json.getDouble("totalKg");
                int donationCount = json.getInt("donationCount");
                int awaitingCount = json.getInt("awaitingCount");
                int foodBanksHelped = json.getInt("foodBanksHelped");

                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.tvTotalKg)).setText(String.format("%.0f kg", totalKg));
                    ((TextView) findViewById(R.id.tvDonationCount)).setText(String.valueOf(donationCount));
                    ((TextView) findViewById(R.id.tvAwaitingCount)).setText(String.valueOf(awaitingCount));
                    ((TextView) findViewById(R.id.tvFoodBanksHelped)).setText(String.valueOf(foodBanksHelped));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadCreditHero() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/credits/producers/" + prodId + "/summary");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                int totalCredits = json.optInt("totalCredits", 0);
                int creditsThisMonth = json.optInt("creditsThisMonth", 0);
                int creditsPerKg = json.optInt("creditsPerKg", 10);
                String currentTier = json.optString("currentTier", "None");

                SessionManager.setCredits(this, totalCredits);

                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.tvCreditsNumber)).setText(String.valueOf(totalCredits));
                    ((TextView) findViewById(R.id.tvCreditsThisMonth)).setText(
                            "+" + creditsThisMonth + " credits this month · "
                                    + creditsPerKg + " credits per kg · Tier: " + currentTier
                    );
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadPieChart() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/producer/" + prodId + "/food-type-breakdown");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONArray array = new JSONArray(sb.toString());
                List<PieEntry> entries = new ArrayList<>();

                if (array.length() == 0) {
                    runOnUiThread(this::setupDefaultPieChart);
                    return;
                }

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String type = obj.getString("foodType");
                    float kg = (float) obj.getDouble("kg");
                    entries.add(new PieEntry(kg, type));
                }

                runOnUiThread(() -> {
                    PieChart chart = findViewById(R.id.pieChart);
                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(new int[]{
                            Color.parseColor("#639922"),
                            Color.parseColor("#BA7517"),
                            Color.parseColor("#1D9E75"),
                            Color.parseColor("#E24B4A"),
                            Color.parseColor("#888780")
                    });
                    dataSet.setValueTextSize(12f);
                    dataSet.setValueTextColor(Color.WHITE);
                    dataSet.setSliceSpace(3f);

                    chart.setData(new PieData(dataSet));
                    chart.setHoleRadius(50f);
                    chart.setTransparentCircleRadius(54f);
                    chart.getDescription().setEnabled(false);
                    chart.getLegend().setEnabled(true);
                    chart.setEntryLabelColor(Color.TRANSPARENT);
                    chart.animateY(800);
                    chart.invalidate();
                });

            } catch (Exception e) {
                runOnUiThread(this::setupDefaultPieChart);
            }
        }).start();
    }

    private void setupDefaultPieChart() {
        PieChart chart = findViewById(R.id.pieChart);
        chart.setNoDataText("No donation data yet");
        chart.invalidate();
    }

    private void loadBarChart() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/producer/" + prodId + "/monthly");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONArray array = new JSONArray(sb.toString());
                if (array.length() == 0) {
                    runOnUiThread(this::setupDefaultBarChart);
                    return;
                }

                List<BarEntry> entries = new ArrayList<>();
                String[] labels = new String[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    labels[i] = obj.getString("month");
                    entries.add(new BarEntry(i, (float) obj.getDouble("kg")));
                }

                final String[] finalLabels = labels;
                runOnUiThread(() -> {
                    BarChart chart = findViewById(R.id.barChart);
                    BarDataSet dataSet = new BarDataSet(entries, "kg donated");
                    dataSet.setColor(Color.parseColor("#639922"));
                    dataSet.setValueTextSize(11f);

                    BarData data = new BarData(dataSet);
                    data.setBarWidth(0.6f);
                    chart.setData(data);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(finalLabels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setGranularity(1f);

                    chart.getAxisLeft().setDrawGridLines(false);
                    chart.getAxisRight().setEnabled(false);
                    chart.getDescription().setEnabled(false);
                    chart.getLegend().setEnabled(false);
                    chart.animateY(600);
                    chart.invalidate();
                });

            } catch (Exception e) {
                runOnUiThread(this::setupDefaultBarChart);
            }
        }).start();
    }

    private void setupDefaultBarChart() {
        BarChart chart = findViewById(R.id.barChart);
        chart.setNoDataText("No donation data yet");
        chart.invalidate();
    }

    private void loadPendingDonations() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/producer/" + prodId + "/recent");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONArray array = new JSONArray(sb.toString());
                List<JSONObject> pending = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String status = obj.optString("status", "");
                    if (status.equals("Pending") || status.equals("Confirmed")) {
                        pending.add(obj);
                    }
                }

                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.llPendingContainer);
                    container.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);

                    if (pending.isEmpty()) {
                        TextView empty = new TextView(this);
                        empty.setText("No pending donations");
                        empty.setTextColor(Color.GRAY);
                        empty.setPadding(0, 16, 0, 16);
                        container.addView(empty);
                        return;
                    }

                    for (JSONObject obj : pending) {
                        try {
                            String fbName = obj.optString("fb_name", "Unknown");
                            String items = obj.optString("items", "");
                            double kg = obj.optDouble("weight_kg", 0);
                            String status = obj.optString("status", "Pending");
                            String date = obj.optString("created_at", "");
                            if (date.length() > 10) {
                                date = date.substring(0, 10);
                            }

                            View rowView = inflater.inflate(R.layout.item_pending_collection_row, container, false);

                            String initials = fbName.length() >= 2 ? fbName.substring(0, 2).toUpperCase() : "?";
                            ((TextView) rowView.findViewById(R.id.tvInitials)).setText(initials);
                            ((TextView) rowView.findViewById(R.id.tvFoodBankName)).setText(fbName);
                            ((TextView) rowView.findViewById(R.id.tvDonationMeta))
                                    .setText(items + " · " + (int) kg + " kg · " + date);
                            ((TextView) rowView.findViewById(R.id.tvCreditPreview))
                                    .setText("+" + toCredits(kg) + " credits on collection");

                            TextView tvStatus = rowView.findViewById(R.id.tvStatus);
                            tvStatus.setText(status);
                            if (status.equals("Confirmed")) {
                                tvStatus.setBackgroundResource(R.drawable.bg_badge_accepted);
                                tvStatus.setTextColor(Color.parseColor("#0C447C"));
                            } else {
                                tvStatus.setTextColor(Color.parseColor("#633806"));
                            }

                            container.addView(rowView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadRecentActivity() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/donations/producer/" + prodId + "/recent");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() != 200) {
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONArray array = new JSONArray(sb.toString());

                runOnUiThread(() -> {
                    LinearLayout container = findViewById(R.id.llActivityContainer);
                    container.removeAllViews();

                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject obj = array.getJSONObject(i);
                            String fbName = obj.optString("fb_name", "Unknown");
                            String items = obj.optString("items", "");
                            double kg = obj.optDouble("weight_kg", 0);
                            String status = obj.optString("status", "");
                            String date = obj.optString("created_at", "");
                            if (date.length() > 10) {
                                date = date.substring(0, 10);
                            }

                            String message;
                            int dotColor;

                            if (status.equals("Collected")) {
                                message = fbName + " collected your donation — " + items + " · +" + toCredits(kg) + " credits";
                                dotColor = Color.parseColor("#639922");
                            } else if (status.equals("Confirmed")) {
                                message = fbName + " confirmed your donation — " + items;
                                dotColor = Color.parseColor("#378ADD");
                            } else {
                                message = "You submitted a donation to " + fbName + " — " + items;
                                dotColor = Color.parseColor("#BA7517");
                            }

                            LinearLayout row = new LinearLayout(this);
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            rowParams.setMargins(0, 0, 0, 24);
                            row.setLayoutParams(rowParams);

                            View dot = new View(this);
                            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(20, 20);
                            dotParams.setMargins(0, 4, 20, 0);
                            dot.setLayoutParams(dotParams);
                            dot.setBackground(createCircleDrawable(dotColor));
                            row.addView(dot);

                            TextView tvText = new TextView(this);
                            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1f
                            );
                            tvText.setLayoutParams(textParams);
                            tvText.setText(message);
                            tvText.setTextSize(13f);
                            row.addView(tvText);

                            TextView tvTime = new TextView(this);
                            tvTime.setText(date);
                            tvTime.setTextSize(11f);
                            tvTime.setTextColor(Color.GRAY);
                            row.addView(tvTime);

                            container.addView(row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_dashboard);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donate) {
                startActivity(new Intent(this, ProcessDonations.class));
                return true;
            }
            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, ProducerSettingsActivity.class));
                return true;
            }
            return id == R.id.nav_dashboard;
        });
    }

    private void setupCreditActions() {
        View redeemButton = findViewById(R.id.btnRedeem);
        if (redeemButton != null) {
            redeemButton.setOnClickListener(v -> startActivity(new Intent(this, ProcessCredits.class)));
        }
    }

    private int toCredits(double kg) {
        return (int) Math.round(kg * 10.0);
    }

    private android.graphics.drawable.ShapeDrawable createCircleDrawable(int color) {
        android.graphics.drawable.ShapeDrawable d =
                new android.graphics.drawable.ShapeDrawable(
                        new android.graphics.drawable.shapes.OvalShape());
        d.getPaint().setColor(color);
        return d;
    }
}