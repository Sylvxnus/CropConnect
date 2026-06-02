package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;


//Model class representing a food bank fetched from the backend API
//Each field maps to a JSON key returned by /api/foodbanks
public class FoodBank {


    //Unique id identifier for the food bank
    @SerializedName("id")
    private Long id;

    //Display name for the food bank
    @SerializedName("name")
    private String name;

    //contact email
    @SerializedName("email")
    private String email;

    //contact phone number - shown on the map inside the pin
    @SerializedName("phone")
    private String phone;


    //GPS coords used to place the pin on the map
    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;


    //Filter flags - used in the filter buttons on the map screen
    @SerializedName("noReferral")
    private boolean noReferral;

    @SerializedName("open")
    private boolean open;
    @SerializedName("hasFreshProduce")
    private boolean hasFreshProduce;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
    public boolean isNoReferral() { return noReferral; }
    public boolean isOpen() { return open; }
    public boolean isHasFreshProduce() { return hasFreshProduce; }
}