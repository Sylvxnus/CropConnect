package com.example.cropconnect.models;

import com.google.gson.annotations.SerializedName;

/**
 * Producer
 * ─────────
 * Matches the crop_producers table in PostgreSQL.
 * Used by Retrofit/Gson to serialise/deserialise API responses.
 *
 * Table columns:
 *   prod_id, prod_name, prod_email, prod_password, prod_postcode,
 *   prod_plot_type, prod_annual_rent, allotment_reference,
 *   bcc_active_plot_holder
 *
 * Note: prodId is Integer (nullable) not int (primitive) so that Gson
 * omits it from the JSON body on registration. Sending prodId=0 causes
 * Hibernate to attempt a merge on id=0 instead of an insert, throwing
 * ObjectOptimisticLockingFailureException.
 */
public class Producer {

    @SerializedName("prod_id")
    private Integer prodId;   // Integer not int — null = new record, omitted from JSON

    @SerializedName("prod_name")
    private String prodName;

    @SerializedName("prod_email")
    private String prodEmail;

    // Never send this back to the UI — only used when registering/updating
    @SerializedName("prod_password")
    private String prodPassword;

    @SerializedName("prod_postcode")
    private String prodPostcode;

    @SerializedName("prod_plot_type")
    private String prodPlotType;

    @SerializedName("prod_annual_rent")
    private Double prodAnnualRent;

    @SerializedName("allotment_reference")
    private String allotmentReference;

    @SerializedName("bcc_active_plot_holder")
    private Boolean bccActivePlotHolder;

    // ── Constructors ───────────────────────────────────────────────────────
    public Producer() {}

    public Producer(String prodName, String prodEmail,
                    String prodPassword, String prodPostcode) {
        this.prodName = prodName;
        this.prodEmail = prodEmail;
        this.prodPassword = prodPassword;
        this.prodPostcode = prodPostcode;
    }

    public Producer(String prodName, String prodEmail,
                    String prodPassword, String prodPostcode,
                    String prodPlotType, Double prodAnnualRent,
                    String allotmentReference, Boolean bccActivePlotHolder) {
        this.prodName = prodName;
        this.prodEmail = prodEmail;
        this.prodPassword = prodPassword;
        this.prodPostcode = prodPostcode;
        this.prodPlotType = prodPlotType;
        this.prodAnnualRent = prodAnnualRent;
        this.allotmentReference = allotmentReference;
        this.bccActivePlotHolder = bccActivePlotHolder;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public Integer getProdId() { return prodId; }
    public String getProdName() { return prodName; }
    public String getProdEmail() { return prodEmail; }
    public String getProdPassword() { return prodPassword; }
    public String getProdPostcode() { return prodPostcode; }
    public String getProdPlotType() { return prodPlotType; }
    public Double getProdAnnualRent() { return prodAnnualRent; }
    public String getAllotmentReference() { return allotmentReference; }
    public Boolean getBccActivePlotHolder() { return bccActivePlotHolder; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setProdId(Integer prodId) { this.prodId = prodId; }
    public void setProdName(String prodName) { this.prodName = prodName; }
    public void setProdEmail(String prodEmail) { this.prodEmail = prodEmail; }
    public void setProdPassword(String prodPassword) { this.prodPassword = prodPassword; }
    public void setProdPostcode(String prodPostcode) { this.prodPostcode = prodPostcode; }
    public void setProdPlotType(String prodPlotType) { this.prodPlotType = prodPlotType; }
    public void setProdAnnualRent(Double prodAnnualRent) { this.prodAnnualRent = prodAnnualRent; }
    public void setAllotmentReference(String allotmentReference) { this.allotmentReference = allotmentReference; }
    public void setBccActivePlotHolder(Boolean bccActivePlotHolder) { this.bccActivePlotHolder = bccActivePlotHolder; }
}