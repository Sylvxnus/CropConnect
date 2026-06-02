package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

/**
 * Producer
 * ─────────
 * Matches the crop_producers table in PostgreSQL.
 * Used by Retrofit/Gson to serialise/deserialise API responses.
 *
 * Table columns:
 *   prod_id, prod_name, prod_email, prod_password, prod_postcode
 */
public class Producer {

    @SerializedName("prod_id")
    private int prodId;

    @SerializedName("prod_name")
    private String prodName;

    @SerializedName("prod_email")
    private String prodEmail;

    // Never send this back to the UI — only used when registering
    @SerializedName("prod_password")
    private String prodPassword;

    @SerializedName("prod_postcode")
    private String prodPostcode;

    // ── Constructors ───────────────────────────────────────────────────────
    public Producer() {}

    public Producer(String prodName, String prodEmail,
                    String prodPassword, String prodPostcode) {
        this.prodName     = prodName;
        this.prodEmail    = prodEmail;
        this.prodPassword = prodPassword;
        this.prodPostcode = prodPostcode;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int getProdId()          { return prodId; }
    public String getProdName()     { return prodName; }
    public String getProdEmail()    { return prodEmail; }
    public String getProdPassword() { return prodPassword; }
    public String getProdPostcode() { return prodPostcode; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setProdId(int prodId)             { this.prodId = prodId; }
    public void setProdName(String prodName)      { this.prodName = prodName; }
    public void setProdEmail(String prodEmail)    { this.prodEmail = prodEmail; }
    public void setProdPassword(String p)         { this.prodPassword = p; }
    public void setProdPostcode(String postcode)  { this.prodPostcode = postcode; }
}