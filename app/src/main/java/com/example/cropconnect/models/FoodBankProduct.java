package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

// class for foodbank products with relevant getters and setters
public class FoodBankProduct {

    // Long (nullable) not long (primitive) — so new products send null not 0
    @SerializedName("productId")
    private Long productId;

    @SerializedName("fbId")
    private long fbId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productQuant")
    private int productQuant;

    @SerializedName("upcomingDonation")
    private int upcomingDonation;

    @SerializedName("category")
    private String category;

    @SerializedName("unit")
    private String unit;

    @SerializedName("expiryDate")
    private String expiryDate;

    @SerializedName("lastUpdated")
    private String lastUpdated;

    public FoodBankProduct() {}

    public FoodBankProduct(long fbId, String productName, int productQuant,
                           String category, String unit, String expiryDate) {
        this.fbId         = fbId;
        this.productName  = productName;
        this.productQuant = productQuant;
        this.upcomingDonation = 0;
        this.category     = category;
        this.unit         = unit;
        this.expiryDate   = expiryDate;
    }

    // productId is Long — returns null for new products (not sent in JSON)
    public Long getProductId()                           { return productId; }
    public void setProductId(Long productId)             { this.productId = productId; }

    public long getFbId()                                { return fbId; }
    public void setFbId(long fbId)                       { this.fbId = fbId; }

    public String getProductName()                       { return productName; }
    public void setProductName(String productName)       { this.productName = productName; }

    public int getProductQuant()                         { return productQuant; }
    public void setProductQuant(int productQuant)        { this.productQuant = productQuant; }

    public int getUpcomingDonation()                     { return upcomingDonation; }
    public void setUpcomingDonation(int upcomingDonation){ this.upcomingDonation = upcomingDonation; }

    public String getCategory()                          { return category; }
    public void setCategory(String category)             { this.category = category; }

    public String getUnit()                              { return unit; }
    public void setUnit(String unit)                     { this.unit = unit; }

    public String getExpiryDate()                        { return expiryDate; }
    public void setExpiryDate(String expiryDate)         { this.expiryDate = expiryDate; }

    public String getLastUpdated()                       { return lastUpdated; }
    public void setLastUpdated(String lastUpdated)       { this.lastUpdated = lastUpdated; }
}