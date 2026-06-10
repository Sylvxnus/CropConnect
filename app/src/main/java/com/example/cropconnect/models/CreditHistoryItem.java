package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

public class CreditHistoryItem {

    @SerializedName("donationId")
    private long donationId;

    @SerializedName("transactionType")
    private String transactionType;

    @SerializedName("foodBankName")
    private String foodBankName;

    @SerializedName("confirmedKg")
    private double confirmedKg;

    @SerializedName("creditsAwarded")
    private int creditsAwarded;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("status")
    private String status;

    public long getDonationId() {
        return donationId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getFoodBankName() {
        return foodBankName;
    }

    public double getConfirmedKg() {
        return confirmedKg;
    }

    public int getCreditsAwarded() {
        return creditsAwarded;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setDonationId(long donationId) {
        this.donationId = donationId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setFoodBankName(String foodBankName) {
        this.foodBankName = foodBankName;
    }

    public void setConfirmedKg(double confirmedKg) {
        this.confirmedKg = confirmedKg;
    }

    public void setCreditsAwarded(int creditsAwarded) {
        this.creditsAwarded = creditsAwarded;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}