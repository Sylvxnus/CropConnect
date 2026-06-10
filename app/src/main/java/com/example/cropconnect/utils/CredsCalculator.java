package com.example.cropconnect.utils;

import com.example.cropconnect.models.CreditHistoryItem;
import com.example.cropconnect.models.Credits;

import java.util.Locale;

public final class CredsCalculator {

    private CredsCalculator() {
    }

    public static String buildMonthlyCreditsLine(Credits credits) {
        int creditsPerKg = credits.getCreditsPerKg() > 0 ? credits.getCreditsPerKg() : 10;
        return "+" + credits.getCreditsThisMonth() + " credits this month · "
                + creditsPerKg + " credits per kg";
    }

    public static String buildCreditYearLine(Credits credits) {
        return "Credit year: " + safe(credits.getCreditYearStart()) + " to " + safe(credits.getCreditYearEnd());
    }

    public static String buildAverageMonthlyLine(Credits credits) {
        return String.format(Locale.UK, "%.2f kg average per month", credits.getAverageMonthlyKg());
    }

    public static String buildVisitsLine(Credits credits) {
        return credits.getDonationVisits() + " qualifying donation visits";
    }

    public static String buildDiscountValue(Credits credits) {
        return String.format(
                Locale.UK,
                "%.0f%% · £%.2f",
                credits.getDiscountPercentage(),
                credits.getDiscountAmount()
        );
    }

    public static String buildDiscountNote(Credits credits) {
        String prefix = credits.isDiscountEstimated()
                ? "Estimated next rent discount using current fallback rent"
                : "Next rent discount";
        return prefix + " · rent basis £"
                + String.format(Locale.UK, "%.2f", credits.getAnnualRentUsed());
    }

    public static String buildPlotTypeLine(Credits credits) {
        return "Plot type: " + safe(credits.getPlotType());
    }

    public static String buildHistoryMeta(CreditHistoryItem item) {
        return String.format(
                Locale.UK,
                "%.2f kg · %d credits · %s",
                item.getConfirmedKg(),
                item.getCreditsAwarded(),
                safe(shortDate(item.getCreatedAt()))
        );
    }

    public static String buildHistoryTitle(CreditHistoryItem item) {
        return safe(item.getFoodBankName()) + " · " + safe(item.getStatus());
    }

    public static String shortDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return "";
        }
        return raw.length() >= 10 ? raw.substring(0, 10) : raw;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}