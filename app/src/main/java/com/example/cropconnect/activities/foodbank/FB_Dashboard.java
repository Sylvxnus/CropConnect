package com.example.cropconnect.activities.foodbank;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cropconnect.R;
import com.example.cropconnect.adapters.ActivityAdapter;
import com.example.cropconnect.adapters.DonationAdapter;
import com.example.cropconnect.models.Donation;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class FB_Dashboard extends AppCompatActivity
        implements DonationAdapter.OnDonationActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbank_dashboard);

        setupMetricCards();
        setupPieChart();
        setupBarChart();
        setupStockLevels();
        setupRecentActivity();
        setupPendingDonations();
        setupBottomNav();
    }

    private void setupMetricCards() {
        // In a real app, pull these from your database or API.
        // For now they're hardcoded — replace with live data.
        ((TextView) findViewById(R.id.tvTotalKg)).setText("1,284");
        ((TextView) findViewById(R.id.tvActiveAllotments)).setText("23");
        ((TextView) findViewById(R.id.tvPendingCount)).setText("7");
        ((TextView) findViewById(R.id.tvFamiliesServed)).setText("341");
    }

    private void setupPieChart() {
        PieChart pieChart = findViewById(R.id.pieChart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(34f, "Green Gates"));
        entries.add(new PieEntry(22f, "Riverside"));
        entries.add(new PieEntry(18f, "Sunny Fields"));
        entries.add(new PieEntry(14f, "Oak Lane"));
        entries.add(new PieEntry(12f, "Others"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#639922"),
                Color.parseColor("#1D9E75"),
                Color.parseColor("#378ADD"),
                Color.parseColor("#BA7517"),
                Color.parseColor("#888780")
        );
        dataSet.setSliceSpace(3f);
        dataSet.setValueTextSize(11f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setHoleRadius(55f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(Color.GRAY);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(11f);
        pieChart.animateY(800);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        BarChart barChart = findViewById(R.id.barChart);

        float[] weeklyData = {180f, 210f, 165f, 240f, 198f, 291f};
        String[] labels    = {"Wk 1", "Wk 2", "Wk 3", "Wk 4", "Wk 5", "Wk 6"};

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < weeklyData.length; i++) {
            entries.add(new BarEntry(i, weeklyData[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "kg received");
        dataSet.setColor(Color.parseColor("#1D9E75"));
        dataSet.setValueTextColor(Color.GRAY);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);

        barChart.getAxisLeft().setTextColor(Color.GRAY);
        barChart.getAxisLeft().setGridColor(Color.parseColor("#EEEEEE"));
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(600);
        barChart.invalidate();
    }

    private void setupStockLevels() {
        // Each stock row is an <include> — find by include ID, then find children inside it
        bindStockRow(R.id.stockVeg,    "Vegetables", 86, Color.parseColor("#639922"));
        bindStockRow(R.id.stockFruit,  "Fruit",      20, Color.parseColor("#1D9E75"));
        bindStockRow(R.id.stockHerbs,  "Herbs",      37, Color.parseColor("#BA7517"));
        bindStockRow(R.id.stockGrains, "Grains",     98, Color.parseColor("#378ADD"));
        bindStockRow(R.id.stockLegumes,"Legumes",    21, Color.parseColor("#E24B4A"));

        // Show alert if any stock is below 30%
        TextView alert = findViewById(R.id.tvStockAlert);
        alert.setVisibility(android.view.View.VISIBLE);
    }

    private void bindStockRow(int includeId, String label, int percent, int color) {
        android.view.View row = findViewById(includeId);
        ((TextView)  row.findViewById(R.id.tvStockLabel)).setText(label);
        ((TextView)  row.findViewById(R.id.tvStockPercent)).setText(percent + "%");
        ProgressBar pb = row.findViewById(R.id.progressStock);
        pb.setProgress(percent);
        pb.getProgressDrawable().setColorFilter(
                color, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void setupRecentActivity() {
        List<ActivityAdapter.ActivityItem> items = new ArrayList<>();
        items.add(new ActivityAdapter.ActivityItem(
                "Green Gates donated 42kg vegetables", "2h ago",
                Color.parseColor("#639922")));
        items.add(new ActivityAdapter.ActivityItem(
                "Sunny Fields donation confirmed", "5h ago",
                Color.parseColor("#378ADD")));
        items.add(new ActivityAdapter.ActivityItem(
                "Riverside Plot: new producer joined", "1d ago",
                Color.parseColor("#1D9E75")));
        items.add(new ActivityAdapter.ActivityItem(
                "Oak Lane 18kg fruit collected", "2d ago",
                Color.parseColor("#BA7517")));
        items.add(new ActivityAdapter.ActivityItem(
                "Legume stock alert triggered", "2d ago",
                Color.parseColor("#E24B4A")));

        RecyclerView rv = findViewById(R.id.rvRecentActivity);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ActivityAdapter(this, items));
    }

    private void setupPendingDonations() {
        // Reuse your DonationAdapter — show only pending donations
        List<Donation> pending = new ArrayList<>();
        pending.add(new Donation(3, "Sunny Fields", "Apples, pears",      35, 2.7f, "Pending", "Yesterday",  "Best collected before Friday", "fruit"));
        pending.add(new Donation(4, "Oak Lane",     "Runner beans",        14, 0.8f, "Pending", "Yesterday",  "", "legume"));
        pending.add(new Donation(8, "Sunny Fields", "Sweetcorn",           22, 2.7f, "Pending", "3 days ago", "Ring ahead before collecting", "veg"));

        RecyclerView rv = findViewById(R.id.rvPendingDonations);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new DonationAdapter(this, pending, this));

        // "View all" taps through to the full donations screen
        findViewById(R.id.tvViewAllDonations).setOnClickListener(v ->
                startActivity(new Intent(this, FoodBankDonationsActivity.class)));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donations) {
                startActivity(new Intent(this, FoodBankDonationsActivity.class));
                return true;
            }
            if (id == R.id.nav_stock) {
                startActivity(new Intent(this, ViewStock.class));
                return true;
            }
            return id == R.id.nav_dashboard;
        });
    }

    // DonationAdapter.OnDonationActionListener — dashboard shows read-only
    // pending cards so these are no-ops here; full actions are on the donations screen
    @Override public void onConfirm(Donation donation) {}
    @Override public void onMarkCollected(Donation donation) {}
    @Override public void onViewAllotment(Donation donation) {}
}