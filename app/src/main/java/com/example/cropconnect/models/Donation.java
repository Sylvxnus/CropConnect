package com.example.cropconnect.models;

/**
 * Donation model.
 * Constructor matches exactly what FB_Dashboard passes:
 * new Donation(id, allotment, items, weightKg, distanceKm, status, date, note, type)
 */
public class Donation {

    private int    id;
    private String allotment;
    private String items;
    private int    weightKg;
    private float  distanceKm;
    private String status;
    private String date;
    private String note;
    private String type;       // "fruit", "veg", "legume", etc.

    public Donation(int id, String allotment, String items, int weightKg,
                    float distanceKm, String status, String date,
                    String note, String type) {
        this.id         = id;
        this.allotment  = allotment;
        this.items      = items;
        this.weightKg   = weightKg;
        this.distanceKm = distanceKm;
        this.status     = status;
        this.date       = date;
        this.note       = note;
        this.type       = type;
    }

    public int    getId()          { return id; }
    public String getAllotment()   { return allotment; }
    public String getItems()       { return items; }
    public int    getWeightKg()    { return weightKg; }
    public float  getDistanceKm()  { return distanceKm; }
    public String getStatus()      { return status; }
    public String getDate()        { return date; }
    public String getNote()        { return note; }
    public String getType()        { return type; }

    public void setStatus(String status) { this.status = status; }
}