package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;


//Model class representing a food bank fetched from the backend API
//Each field maps to a JSON key returned by /api/foodbanks
public class FoodBank {


    //Unique id identifier for the food bank
    @SerializedName("id")
    private Long FoodId;

    //Display name for the food bank
    @SerializedName("name")
    private String FoodName;

    //contact email
    @SerializedName("email")
    private String FoodEmail;

    //contact phone number - shown on the map inside the pin
    @SerializedName("phone")
    private String FoodPhone;

    //password
    @SerializedName("password")
    private String FoodPassword;

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

    public Long getId() { return FoodId; }
    public String getName() { return FoodName; }
    public String getFoodEmail() { return FoodEmail; }
    public String getPhone() { return FoodPhone; }

    public long getFoodId() { return FoodId; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
    public boolean isNoReferral() { return noReferral; }
    public boolean isOpen() { return open; }
    public boolean isHasFreshProduce() { return hasFreshProduce; }

    public void setFoodEmail(String foodEmail){
        this.FoodEmail = foodEmail;
    }

    public void setFoodPassword(String foodPassword){
        this.FoodPassword = foodPassword;
    }
}