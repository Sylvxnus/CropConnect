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
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.utils.SessionManager;
import android.widget.Toast;
import java.util.LinkedHashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FB_Dashboard extends AppCompatActivity
        implements DonationAdapter.OnDonationActionListener {

    private SessionManager session;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodbank_dashboard);

        session = new SessionManager(this);
        api = ApiClient.getApiService();

        setupMetricCards();
        setupDefaultPieChart();
        setupBarChart();
        setupStockLevels();
        setupPendingDonations();
        setupBottomNav();

        long fbId = session.getFbId();
        if (fbId != -1L) {
            loadMetricCards(fbId);
            loadStockLevels(fbId);
            loadPieChart(fbId);
            loadRecentActivity(fbId);
            loadWeeklyBarChart(fbId);
            loadFamiliesServed(fbId);
        }
    }

    private void setupMetricCards() {
        ((TextView) findViewById(R.id.tvTotalKg)).setText("...");
        ((TextView) findViewById(R.id.tvActiveAllotments)).setText("...");
        ((TextView) findViewById(R.id.tvPendingCount)).setText("...");
        ((TextView) findViewById(R.id.tvFamiliesServed)).setText("...");
    }

    private void setupDefaultPieChart() {
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

    private void loadPieChart(long fbId) {
        ApiClient.getApiService().getAllotmentBreakdown(fbId)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call,
                                           Response<List<Map<String, Object>>> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().isEmpty()) {
                            // Fall back to default chart if no data
                            setupDefaultPieChart();
                            return;
                        }

                        List<PieEntry> entries = new ArrayList<>();
                        for (Map<String, Object> row : response.body()) {
                            String name = String.valueOf(row.get("allotment"));
                            float kg    = ((Number) row.get("kg")).floatValue();
                            entries.add(new PieEntry(kg, name));
                        }

                        int[] chartColors = {
                                Color.parseColor("#639922"),
                                Color.parseColor("#1D9E75"),
                                Color.parseColor("#378ADD"),
                                Color.parseColor("#BA7517"),
                                Color.parseColor("#888780")
                        };

                        runOnUiThread(() -> {
                            PieChart pieChart = findViewById(R.id.pieChart);
                            PieDataSet dataSet = new PieDataSet(entries, "");
                            dataSet.setColors(chartColors);
                            dataSet.setSliceSpace(3f);
                            dataSet.setValueTextSize(11f);
                            dataSet.setValueTextColor(Color.WHITE);
                            pieChart.setData(new PieData(dataSet));
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
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                        setupDefaultPieChart();
                    }
                });
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
        TextView alert = findViewById(R.id.tvStockAlert);
        alert.setVisibility(android.view.View.GONE);
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

    private void loadMetricCards(long fbId) {
        ApiClient.getApiService().getProducts(fbId)
                .enqueue(new Callback<List<FoodBankProduct>>() {
                    @Override
                    public void onResponse(Call<List<FoodBankProduct>> call,
                                           Response<List<FoodBankProduct>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;
                        List<FoodBankProduct> products = response.body();

                        int totalUnits = 0;
                        int upcomingCount = 0;
                        for (FoodBankProduct p : products) {
                            totalUnits += p.getProductQuant();
                            if (p.getUpcomingDonation() > 0) upcomingCount++;
                        }

                        final int finalTotal    = totalUnits;
                        final int finalProducts = products.size();
                        final int finalUpcoming = upcomingCount;

                        runOnUiThread(() -> {
                            ((TextView) findViewById(R.id.tvTotalKg))
                                    .setText(String.valueOf(finalTotal));
                            ((TextView) findViewById(R.id.tvActiveAllotments))
                                    .setText(String.valueOf(finalProducts));
                            ((TextView) findViewById(R.id.tvPendingCount))
                                    .setText(String.valueOf(finalUpcoming));
                        });
                    }

                    @Override
                    public void onFailure(Call<List<FoodBankProduct>> call, Throwable t) {
                        Toast.makeText(FB_Dashboard.this,
                                "Could not load data: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadStockLevels(long fbId) {
        ApiClient.getApiService().getProducts(fbId)
                .enqueue(new Callback<List<FoodBankProduct>>() {
                    @Override
                    public void onResponse(Call<List<FoodBankProduct>> call,
                                           Response<List<FoodBankProduct>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        Map<String, Integer> totals = new LinkedHashMap<>();
                        for (FoodBankProduct p : response.body()) {
                            String cat = p.getCategory() != null ? p.getCategory() : "Other";
                            totals.put(cat, totals.getOrDefault(cat, 0) + p.getProductQuant());
                        }

                        int max = 1;
                        for (int v : totals.values()) if (v > max) max = v;

                        int[] rowIds = {
                                R.id.stockVeg, R.id.stockFruit,
                                R.id.stockHerbs, R.id.stockGrains, R.id.stockLegumes
                        };
                        int[] colors = {
                                Color.parseColor("#639922"), Color.parseColor("#1D9E75"),
                                Color.parseColor("#BA7517"), Color.parseColor("#378ADD"),
                                Color.parseColor("#E24B4A")
                        };

                        List<String> cats = new ArrayList<>(totals.keySet());
                        final int finalMax = max;

                        runOnUiThread(() -> {
                            for (int i = 0; i < Math.min(cats.size(), rowIds.length); i++) {
                                int qty = totals.get(cats.get(i));
                                int pct = (int) ((qty / (float) finalMax) * 100);
                                bindStockRow(rowIds[i], cats.get(i), pct, colors[i]);
                            }
                            boolean low = false;
                            for (int v : totals.values()) if (v < 15) { low = true; break; }
                            TextView alert = findViewById(R.id.tvStockAlert);
                            alert.setVisibility(low ?
                                    android.view.View.VISIBLE : android.view.View.GONE);
                        });
                    }

                    @Override
                    public void onFailure(Call<List<FoodBankProduct>> call, Throwable t) {}
                });
    }

    private void loadRecentActivity(long fbId) {
        ApiClient.getApiService().getRecentActivity(fbId)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call,
                                           Response<List<Map<String, Object>>> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().isEmpty()) return;

                        List<ActivityAdapter.ActivityItem> items = new ArrayList<>();
                        int[] colors = {
                                Color.parseColor("#639922"), Color.parseColor("#1D9E75"),
                                Color.parseColor("#378ADD"), Color.parseColor("#BA7517"),
                                Color.parseColor("#E24B4A")
                        };

                        List<Map<String, Object>> data = response.body();
                        for (int i = 0; i < data.size(); i++) {
                            Map<String, Object> row = data.get(i);
                            String producer = String.valueOf(row.get("producerName"));
                            String items2   = String.valueOf(row.get("items"));
                            String weight   = String.valueOf(row.get("weightKg"));
                            String status   = String.valueOf(row.get("status"));
                            String text     = producer + " donated " + weight + "kg — "
                                    + items2 + " (" + status + ")";
                            items.add(new ActivityAdapter.ActivityItem(
                                    text, "", colors[i % colors.length]));
                        }

                        runOnUiThread(() -> {
                            RecyclerView rv = findViewById(R.id.rvRecentActivity);
                            rv.setLayoutManager(new LinearLayoutManager(FB_Dashboard.this));
                            rv.setAdapter(new ActivityAdapter(FB_Dashboard.this, items));
                        });
                    }
                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
                });
    }

    private void loadWeeklyBarChart(long fbId) {
        ApiClient.getApiService().getWeeklyIntake(fbId)
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call,
                                           Response<List<Map<String, Object>>> response) {
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().isEmpty()) return;

                        List<BarEntry> entries = new ArrayList<>();
                        String[] labels = new String[response.body().size()];
                        List<Map<String, Object>> data = response.body();

                        for (int i = 0; i < data.size(); i++) {
                            labels[i] = String.valueOf(data.get(i).get("day"));
                            float kg  = ((Number) data.get(i).get("kg")).floatValue();
                            entries.add(new BarEntry(i, kg));
                        }

                        final String[] finalLabels = labels;
                        runOnUiThread(() -> {
                            BarChart barChart = findViewById(R.id.barChart);
                            BarDataSet dataSet = new BarDataSet(entries, "kg received");
                            dataSet.setColor(Color.parseColor("#1D9E75"));
                            dataSet.setValueTextColor(Color.GRAY);
                            dataSet.setValueTextSize(10f);
                            BarData data2 = new BarData(dataSet);
                            data2.setBarWidth(0.6f);
                            barChart.setData(data2);
                            XAxis xAxis = barChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(finalLabels));
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularity(1f);
                            xAxis.setDrawGridLines(false);
                            xAxis.setTextColor(Color.GRAY);
                            barChart.getAxisLeft().setTextColor(Color.GRAY);
                            barChart.getAxisRight().setEnabled(false);
                            barChart.getDescription().setEnabled(false);
                            barChart.getLegend().setEnabled(false);
                            barChart.animateY(600);
                            barChart.invalidate();
                        });
                    }
                    @Override
                    public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {}
                });
    }

    private void loadFamiliesServed(long fbId) {
        ApiClient.getApiService().getFamiliesServed(fbId)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;
                        int families = ((Number) response.body()
                                .get("familiesServed")).intValue();
                        runOnUiThread(() ->
                                ((TextView) findViewById(R.id.tvFamiliesServed))
                                        .setText(String.valueOf(families)));
                    }
                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
                });
    }

    // DonationAdapter.OnDonationActionListener — dashboard shows read-only
    // pending cards so these are no-ops here; full actions are on the donations screen
    @Override public void onConfirm(Donation donation) {}
    @Override public void onMarkCollected(Donation donation) {}
    @Override public void onViewAllotment(Donation donation) {}
}