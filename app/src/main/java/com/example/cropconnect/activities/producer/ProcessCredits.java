package com.example.cropconnect.activities.producer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.cropconnect.R;
import com.example.cropconnect.models.CreditHistoryItem;
import com.example.cropconnect.models.Credits;
import com.example.cropconnect.network.ApiClient;
import com.example.cropconnect.network.ApiService;
import com.example.cropconnect.utils.CredsCalculator;
import com.example.cropconnect.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessCredits extends AppCompatActivity {

    private SessionManager sessionManager;
    private int producerId;

    private TextView tvCreditsTotal;
    private TextView tvTierValue;
    private TextView tvCreditsThisMonth;
    private TextView tvCreditYear;
    private TextView tvDonationVisits;
    private TextView tvAverageMonthlyKg;
    private TextView tvDiscountValue;
    private TextView tvDiscountNote;
    private TextView tvPlotType;
    private TextView tvError;
    private ProgressBar progressBar;
    private Button btnRetry;
    private LinearLayout llHistoryContainer;

    private int pendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producer_credits);

        sessionManager = new SessionManager(this);
        producerId = sessionManager.getProdId();

        bindViews();
        wireActions();
        loadCreditsScreen();
    }

    private void bindViews() {
        tvCreditsTotal = findViewById(R.id.tvCreditsTotal);
        tvTierValue = findViewById(R.id.tvTierValue);
        tvCreditsThisMonth = findViewById(R.id.tvCreditsThisMonth);
        tvCreditYear = findViewById(R.id.tvCreditYear);
        tvDonationVisits = findViewById(R.id.tvDonationVisits);
        tvAverageMonthlyKg = findViewById(R.id.tvAverageMonthlyKg);
        tvDiscountValue = findViewById(R.id.tvDiscountValue);
        tvDiscountNote = findViewById(R.id.tvDiscountNote);
        tvPlotType = findViewById(R.id.tvPlotType);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        btnRetry = findViewById(R.id.btnRetry);
        llHistoryContainer = findViewById(R.id.llHistoryContainer);
    }

    private void wireActions() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnRetry.setOnClickListener(v -> loadCreditsScreen());
    }

    private void loadCreditsScreen() {
        if (producerId < 0) {
            showError("No producer session found. Please log in again.");
            renderEmptyHistory("Credit history unavailable.");
            return;
        }

        showLoading();

        ApiService apiService = ApiClient.getApiService();
        pendingRequests = 2;

        apiService.getCreditSummary(producerId).enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindSummary(response.body());
                    hideError();
                } else {
                    showError("Could not load credit summary.");
                }
                finishRequest();
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                showError("Credit summary request failed. Check the backend is running.");
                finishRequest();
            }
        });

        apiService.getCreditHistory(producerId).enqueue(new Callback<List<CreditHistoryItem>>() {
            @Override
            public void onResponse(Call<List<CreditHistoryItem>> call, Response<List<CreditHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindHistory(response.body());
                } else {
                    renderEmptyHistory("No credit history available yet.");
                }
                finishRequest();
            }

            @Override
            public void onFailure(Call<List<CreditHistoryItem>> call, Throwable t) {
                renderEmptyHistory("Could not load credit history.");
                finishRequest();
            }
        });
    }

    private void bindSummary(Credits credits) {
        SessionManager.setCredits(this, credits.getTotalCredits());

        tvCreditsTotal.setText(String.valueOf(credits.getTotalCredits()));
        tvTierValue.setText(credits.getCurrentTier());
        tvCreditsThisMonth.setText(CredsCalculator.buildMonthlyCreditsLine(credits));
        tvCreditYear.setText(CredsCalculator.buildCreditYearLine(credits));
        tvDonationVisits.setText(CredsCalculator.buildVisitsLine(credits));
        tvAverageMonthlyKg.setText(CredsCalculator.buildAverageMonthlyLine(credits));
        tvDiscountValue.setText(CredsCalculator.buildDiscountValue(credits));
        tvDiscountNote.setText(CredsCalculator.buildDiscountNote(credits));
        tvPlotType.setText(CredsCalculator.buildPlotTypeLine(credits));
    }

    private void bindHistory(List<CreditHistoryItem> items) {
        llHistoryContainer.removeAllViews();

        if (items.isEmpty()) {
            renderEmptyHistory("No credit-eligible donations yet.");
            return;
        }

        for (CreditHistoryItem item : items) {
            llHistoryContainer.addView(buildHistoryCard(item));
        }
    }

    private View buildHistoryCard(CreditHistoryItem item) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dp(12));
        cardView.setCardElevation(dp(2));
        cardView.setUseCompatPadding(true);
        cardView.setCardBackgroundColor(Color.WHITE);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText(CredsCalculator.buildHistoryTitle(item));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        title.setTextColor(Color.parseColor("#27500A"));

        TextView meta = new TextView(this);
        meta.setText(CredsCalculator.buildHistoryMeta(item));
        meta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        meta.setTextColor(Color.parseColor("#555555"));

        TextView transaction = new TextView(this);
        transaction.setText(item.getTransactionType());
        transaction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        transaction.setTextColor(Color.parseColor("#3B6D11"));

        content.addView(title);
        content.addView(meta);
        content.addView(transaction);
        cardView.addView(content);

        return cardView;
    }

    private void renderEmptyHistory(String message) {
        llHistoryContainer.removeAllViews();

        TextView empty = new TextView(this);
        empty.setText(message);
        empty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        empty.setTextColor(Color.parseColor("#666666"));
        empty.setPadding(0, dp(8), 0, dp(8));

        llHistoryContainer.addView(empty);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
    }

    private void finishRequest() {
        pendingRequests--;
        if (pendingRequests <= 0) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }

    private int dp(int value) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        getResources().getDisplayMetrics()
                )
        );
    }
}