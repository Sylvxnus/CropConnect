package com.example.cropconnect.activities.producer;

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
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class PROD_Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_dashboard);

        setupHeader();
        setupCreditsHero();
        setupMetricCards();
        setupPieChart();
        setupBarChart();
        setupPendingCollection();
        setupRecentActivity();
        setupBottomNav();
    }

    private void setupHeader() {
        String name = SessionManager.getProdName(this);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (!name.isEmpty()) {
            tvWelcome.setText("Welcome back, " + name);
        }
    }

    private void setupCreditsHero() {
        int credits = SessionManager.getCredits(this);
        ((TextView) findViewById(R.id.tvCreditsNumber)).setText(String.valueOf(credits));
        // Replace 35 with real this-month figure from your DB
        ((TextView) findViewById(R.id.tvCreditsThisMonth))
                .setText("+35 credits this month · 1 credit per kg");

        findViewById(R.id.btnRedeem).setOnClickListener(v -> {
            // TODO: navigate to a credits redemption screen
        });
    }

    private void setupMetricCards() {
        // Replace these hardcoded values with real DB queries
        ((TextView) findViewById(R.id.tvTotalKg)).setText("420 kg");
        ((TextView) findViewById(R.id.tvDonationCount)).setText("18");
        ((TextView) findViewById(R.id.tvAwaitingCount)).setText("3");
        ((TextView) findViewById(R.id.tvFoodBanksHelped)).setText("5");
    }

    private void setupPieChart() {
        PieChart chart = findViewById(R.id.pieChart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(45f, "Vegetables"));
        entries.add(new PieEntry(25f, "Fruit"));
        entries.add(new PieEntry(15f, "Herbs"));
        entries.add(new PieEntry(10f, "Legumes"));
        entries.add(new PieEntry(5f,  "Grains"));

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
    }

    private void setupBarChart() {
        BarChart chart = findViewById(R.id.barChart);

        float[] values = {30f, 20f, 45f, 60f, 55f, 80f};
        String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "kg donated");
        dataSet.setColor(Color.parseColor("#639922"));
        dataSet.setValueTextSize(11f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        chart.setData(data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(600);
        chart.invalidate();
    }

    private void setupPendingCollection() {
        LinearLayout container = findViewById(R.id.llPendingContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        // Replace this with real data from your DB
        String[][] pending = {
                {"NB", "Norwich B Food Bank",    "Courgettes, tomatoes · 28 kg · 2 days ago", "Accepted", "28"},
                {"EN", "Eaton Neighbourhood Pantry", "Mixed herbs · 6 kg · Yesterday",         "Accepted", "6"},
                {"WC", "Wensum Community Hub",   "Apples · 14 kg · Awaiting response",         "Pending",  "14"},
        };

        for (String[] row : pending) {
            View rowView = inflater.inflate(R.layout.item_pending_collection_row, container, false);

            ((TextView) rowView.findViewById(R.id.tvInitials)).setText(row[0]);
            ((TextView) rowView.findViewById(R.id.tvFoodBankName)).setText(row[1]);
            ((TextView) rowView.findViewById(R.id.tvDonationMeta)).setText(row[2]);
            ((TextView) rowView.findViewById(R.id.tvCreditPreview))
                    .setText("+" + row[4] + " credits on collection");

            TextView tvStatus = rowView.findViewById(R.id.tvStatus);
            tvStatus.setText(row[3]);
            if (row[3].equals("Accepted")) {
                tvStatus.setBackgroundResource(R.drawable.bg_badge_accepted);
                tvStatus.setTextColor(Color.parseColor("#0C447C"));
            } else {
                //tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
                tvStatus.setTextColor(Color.parseColor("#633806"));
            }

            container.addView(rowView);
        }
    }

    private void setupRecentActivity() {
        LinearLayout container = findViewById(R.id.llActivityContainer);

        // Each entry: {dot colour, message, time}
        String[][] activities = {
                {"#639922", "Norwich B Food Bank collected your courgette donation — +32 credits", "3 days ago"},
                {"#1D9E75", "Eaton Pantry accepted your herbs donation",                           "4 days ago"},
                {"#BA7517", "You logged a new donation — 14 kg apples",                           "5 days ago"},
                {"#639922", "Wensum Hub collected your runner beans — +18 credits",                "1 week ago"},
                {"#888780", "Your allotment profile was viewed by 3 food banks",                   "1 week ago"},
        };

        for (String[] item : activities) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 12);
            row.setLayoutParams(rowParams);

            // Dot
            View dot = new View(this);
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(20, 20);
            dotParams.setMargins(0, 4, 20, 0);
            dot.setLayoutParams(dotParams);
            dot.setBackgroundColor(Color.parseColor(item[0]));
            // Make it a circle programmatically
            dot.setBackground(createCircleDrawable(Color.parseColor(item[0])));
            row.addView(dot);

            // Text
            TextView tvText = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvText.setLayoutParams(textParams);
            tvText.setText(item[1]);
            tvText.setTextSize(13f);
            tvText.setTextColor(getColor(android.R.color.primary_text_light));
            row.addView(tvText);

            // Time
            TextView tvTime = new TextView(this);
            tvTime.setText(item[2]);
            tvTime.setTextSize(11f);
            tvTime.setTextColor(getColor(android.R.color.secondary_text_light));
            row.addView(tvTime);

            container.addView(row);
        }
    }

    private android.graphics.drawable.ShapeDrawable createCircleDrawable(int color) {
        android.graphics.drawable.ShapeDrawable d =
                new android.graphics.drawable.ShapeDrawable(
                        new android.graphics.drawable.shapes.OvalShape());
        d.getPaint().setColor(color);
        return d;
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_dashboard);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donations) {
                // TODO: startActivity to producer donations screen
                return true;
            } else if (id == R.id.nav_produce) {
                // TODO: startActivity to produce listing screen
                return true;
            } else if (id == R.id.nav_settings) {
                // TODO: startActivity to settings screen
                return true;
            }
            return true;
        });
    }
}