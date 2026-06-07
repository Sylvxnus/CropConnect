package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

public class FoodBank {

    @SerializedName("id")
    private long fbId;

    @SerializedName("name")
    private String fbName;

    @SerializedName("email")
    private String fbEmail;

    @SerializedName("phone")
    private String fbPhone;

    @SerializedName("longitude")
    private double fbLong;

    @SerializedName("latitude")
    private double fbLat;

    @SerializedName("password")
    private String fbPassword;

    @SerializedName("noReferral")
    private boolean noReferral;

    @SerializedName("open")
    private boolean open;

    @SerializedName("hasFreshProduce")
    private boolean hasFreshProduce;

    public FoodBank() {}

    // ── Standard getters/setters ───────────────────────────────────────────
    public long getFbId()                        { return fbId; }
    public void setFbId(long fbId)               { this.fbId = fbId; }

    public String getFbName()                    { return fbName; }
    public void setFbName(String fbName)         { this.fbName = fbName; }

    public String getFbEmail()                   { return fbEmail; }
    public void setFbEmail(String fbEmail)       { this.fbEmail = fbEmail; }

    public String getFbPhone()                   { return fbPhone; }
    public void setFbPhone(String fbPhone)       { this.fbPhone = fbPhone; }

    public double getFbLong()                    { return fbLong; }
    public void setFbLong(double fbLong)         { this.fbLong = fbLong; }

    public double getFbLat()                     { return fbLat; }
    public void setFbLat(double fbLat)           { this.fbLat = fbLat; }

    public String getFbPassword()                { return fbPassword; }
    public void setFbPassword(String fbPassword) { this.fbPassword = fbPassword; }

    // ── Aliases for FoodBankLoginActivity ─────────────────────────────────
    public void setFoodEmail(String email)       { this.fbEmail = email; }
    public String getFoodEmail()                 { return fbEmail; }
    public void setFoodPassword(String password) { this.fbPassword = password; }
    public String getFoodPassword()              { return fbPassword; }

    // ── Aliases for Maps.java ──────────────────────────────────────────────
    public String getName()                      { return fbName; }
    public String getPhone()                     { return fbPhone; }
    public double getLatitude()                  { return fbLat; }
    public double getLongitude()                 { return fbLong; }
    public boolean isNoReferral()                { return noReferral; }
    public boolean isOpen()                      { return open; }
    public boolean isHasFreshProduce()           { return hasFreshProduce; }

    public void setNoReferral(boolean v)         { this.noReferral = v; }
    public void setOpen(boolean v)               { this.open = v; }
    public void setHasFreshProduce(boolean v)    { this.hasFreshProduce = v; }
}