package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

public class Credits {

    @SerializedName("producerId")
    private long producerId;

    @SerializedName("creditsPerKg")
    private int creditsPerKg;

    @SerializedName("creditYearStart")
    private String creditYearStart;

    @SerializedName("creditYearEnd")
    private String creditYearEnd;

    @SerializedName("totalCollectedKg")
    private double totalCollectedKg;

    @SerializedName("averageMonthlyKg")
    private double averageMonthlyKg;

    @SerializedName("donationVisits")
    private int donationVisits;

    @SerializedName("totalCredits")
    private int totalCredits;

    @SerializedName("creditsThisMonth")
    private int creditsThisMonth;

    @SerializedName("currentTier")
    private String currentTier;

    @SerializedName("discountPercentage")
    private double discountPercentage;

    @SerializedName("discountAmount")
    private double discountAmount;

    @SerializedName("annualRentUsed")
    private double annualRentUsed;

    @SerializedName("plotType")
    private String plotType;

    @SerializedName("discountEstimated")
    private boolean discountEstimated;

    public long getProducerId() {
        return producerId;
    }

    public int getCreditsPerKg() {
        return creditsPerKg;
    }

    public String getCreditYearStart() {
        return creditYearStart;
    }

    public String getCreditYearEnd() {
        return creditYearEnd;
    }

    public double getTotalCollectedKg() {
        return totalCollectedKg;
    }

    public double getAverageMonthlyKg() {
        return averageMonthlyKg;
    }

    public int getDonationVisits() {
        return donationVisits;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public int getCreditsThisMonth() {
        return creditsThisMonth;
    }

    public String getCurrentTier() {
        return currentTier;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getAnnualRentUsed() {
        return annualRentUsed;
    }

    public String getPlotType() {
        return plotType;
    }

    public boolean isDiscountEstimated() {
        return discountEstimated;
    }

    public void setProducerId(long producerId) {
        this.producerId = producerId;
    }

    public void setCreditsPerKg(int creditsPerKg) {
        this.creditsPerKg = creditsPerKg;
    }

    public void setCreditYearStart(String creditYearStart) {
        this.creditYearStart = creditYearStart;
    }

    public void setCreditYearEnd(String creditYearEnd) {
        this.creditYearEnd = creditYearEnd;
    }

    public void setTotalCollectedKg(double totalCollectedKg) {
        this.totalCollectedKg = totalCollectedKg;
    }

    public void setAverageMonthlyKg(double averageMonthlyKg) {
        this.averageMonthlyKg = averageMonthlyKg;
    }

    public void setDonationVisits(int donationVisits) {
        this.donationVisits = donationVisits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public void setCreditsThisMonth(int creditsThisMonth) {
        this.creditsThisMonth = creditsThisMonth;
    }

    public void setCurrentTier(String currentTier) {
        this.currentTier = currentTier;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setAnnualRentUsed(double annualRentUsed) {
        this.annualRentUsed = annualRentUsed;
    }

    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }

    public void setDiscountEstimated(boolean discountEstimated) {
        this.discountEstimated = discountEstimated;
    }
}