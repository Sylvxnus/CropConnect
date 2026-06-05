package com.example.cropconnect.models;

public class Donation {
    private int donationId;
    private String allotmentName;
    private String items;
    private float weightKg;
    private float distanceMiles;
    private String status;   // "Pending", "Confirmed", "In transit", "Collected"
    private String dateLabel;
    private String note;
    private String foodType; // "veg", "fruit", "herb", "grain", "legume"

    public Donation(int donationId, String allotmentName, String items,
                    float weightKg, float distanceMiles, String status,
                    String dateLabel, String note, String foodType) {
        this.donationId = donationId;
        this.allotmentName = allotmentName;
        this.items = items;
        this.weightKg = weightKg;
        this.distanceMiles = distanceMiles;
        this.status = status;
        this.dateLabel = dateLabel;
        this.note = note;
        this.foodType = foodType;
    }

    // Getters
    public int getDonationId()        { return donationId; }
    public String getAllotmentName()  { return allotmentName; }
    public String getItems()          { return items; }
    public float getWeightKg()        { return weightKg; }
    public float getDistanceMiles()   { return distanceMiles; }
    public String getStatus()         { return status; }
    public String getDateLabel()      { return dateLabel; }
    public String getNote()           { return note; }
    public String getFoodType()       { return foodType; }

    // Returns initials from allotment name e.g. "Green Gates" → "GG"
    public String getInitials() {
        String[] words = allotmentName.trim().split("\\s+");
        if (words.length >= 2) return ("" + words[0].charAt(0) + words[1].charAt(0)).toUpperCase();
        if (words.length == 1 && words[0].length() >= 2) return words[0].substring(0, 2).toUpperCase();
        return "?";
    }
}