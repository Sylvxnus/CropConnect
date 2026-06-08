package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class DashboardStats {

    @SerializedName("totalKgReceived")
    private float totalKgReceived;

    @SerializedName("pendingDonationCount")
    private long pendingDonationCount;

    @SerializedName("activeAllotmentCount")
    private int activeAllotmentCount;

    @SerializedName("stockByCategory")
    private List<Map<String, Object>> stockByCategory;

    public float getTotalKgReceived()                              { return totalKgReceived; }
    public long getPendingDonationCount()                          { return pendingDonationCount; }
    public int getActiveAllotmentCount()                           { return activeAllotmentCount; }
    public List<Map<String, Object>> getStockByCategory()          { return stockByCategory; }
}