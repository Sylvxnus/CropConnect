package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

public class FoodBank {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("password")
    private String password;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
    public String getPassword() { return password; }
}