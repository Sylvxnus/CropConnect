package com.example.cropconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.models.Producer;

/**
 * Persists the currently logged-in user across activities.
 *
 * FoodBankLoginActivity calls:
 *   SessionManager.saveFoodBankSession(context, foodBank)  — static
 *
 * ProducerLoginActivity calls:
 *   SessionManager.saveProducerSession(context, producer)  — static
 *
 * PROD_Dashboard calls:
 *   SessionManager.getProdName(context)   — static
 *   SessionManager.getCredits(context)    — static
 *
 * ViewStock / other foodbank screens call:
 *   new SessionManager(context).getFbId() — instance
 */
public class SessionManager {

    private static final String PREF_NAME = "CropConnectSession";

    private static final String KEY_FB_ID = "fb_id";
    private static final String KEY_FB_NAME = "fb_name";
    private static final String KEY_FB_EMAIL = "fb_email";

    private static final String KEY_PROD_ID = "prod_id";
    private static final String KEY_PROD_NAME = "prod_name";
    private static final String KEY_PROD_EMAIL = "prod_email";
    private static final String KEY_PROD_POSTCODE = "prod_postcode";
    private static final String KEY_PROD_PLOT_TYPE = "prod_plot_type";
    private static final String KEY_PROD_ANNUAL_RENT = "prod_annual_rent";
    private static final String KEY_ALLOTMENT_REFERENCE = "allotment_reference";
    private static final String KEY_BCC_ACTIVE_PLOT_HOLDER = "bcc_active_plot_holder";

    private static final String KEY_CREDITS = "credits";
    private static final String KEY_USER_TYPE = "user_type";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ── Static helpers — called by login activities ────────────────────────

    /** Called by FoodBankLoginActivity */
    public static void saveFoodBankSession(Context context, FoodBank foodBank) {
        new SessionManager(context).saveFoodBankSession(
                foodBank.getFbId(),
                foodBank.getFbName(),
                foodBank.getFbEmail()
        );
    }

    /** Called by ProducerLoginActivity */
    public static void saveProducerSession(Context context, Producer producer) {
        new SessionManager(context).saveProducerSession(producer);
    }

    /** Called by PROD_Dashboard */
    public static String getProdName(Context context) {
        return new SessionManager(context).getProdName();
    }

    /** Called by PROD_Dashboard */
    public static int getCredits(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_CREDITS, 0);
    }

    public static void setCredits(Context context, int credits) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_CREDITS, credits)
                .apply();
    }

    // ── FoodBank instance methods ──────────────────────────────────────────

    public void saveFoodBankSession(long fbId, String fbName, String fbEmail) {
        editor.putLong(KEY_FB_ID, fbId);
        editor.putString(KEY_FB_NAME, fbName != null ? fbName : "");
        editor.putString(KEY_FB_EMAIL, fbEmail != null ? fbEmail : "");
        editor.putString(KEY_USER_TYPE, "foodbank");
        editor.apply();
    }

    public void saveFoodBankSession(long fbId, String fbName) {
        saveFoodBankSession(fbId, fbName, "");
    }

    public long getFbId() {
        return prefs.getLong(KEY_FB_ID, -1L);
    }

    public String getFbName() {
        return prefs.getString(KEY_FB_NAME, "");
    }

    public String getFbEmail() {
        return prefs.getString(KEY_FB_EMAIL, "");
    }

    // ── Producer instance methods ──────────────────────────────────────────

    // Integer (nullable) matches Producer.getProdId() return type — guards
    // against NPE if the server response omits prod_id on registration
    public void saveProducerSession(Integer prodId, String prodName, String prodEmail) {
        editor.putInt(KEY_PROD_ID, prodId != null ? prodId : -1);
        editor.putString(KEY_PROD_NAME, prodName != null ? prodName : "");
        editor.putString(KEY_PROD_EMAIL, prodEmail != null ? prodEmail : "");
        editor.putString(KEY_USER_TYPE, "producer");
        editor.apply();
    }

    public void saveProducerSession(Producer producer) {
        if (producer == null) {
            return;
        }

        editor.putInt(KEY_PROD_ID, producer.getProdId() != null ? producer.getProdId() : -1);
        editor.putString(KEY_PROD_NAME, producer.getProdName() != null ? producer.getProdName() : "");
        editor.putString(KEY_PROD_EMAIL, producer.getProdEmail() != null ? producer.getProdEmail() : "");
        editor.putString(KEY_PROD_POSTCODE, producer.getProdPostcode() != null ? producer.getProdPostcode() : "");
        editor.putString(KEY_PROD_PLOT_TYPE, producer.getProdPlotType() != null ? producer.getProdPlotType() : "");
        if (producer.getProdAnnualRent() != null) {
            editor.putFloat(KEY_PROD_ANNUAL_RENT, producer.getProdAnnualRent().floatValue());
        } else {
            editor.remove(KEY_PROD_ANNUAL_RENT);
        }
        editor.putString(KEY_ALLOTMENT_REFERENCE,
                producer.getAllotmentReference() != null ? producer.getAllotmentReference() : "");
        editor.putBoolean(KEY_BCC_ACTIVE_PLOT_HOLDER,
                producer.getBccActivePlotHolder() != null ? producer.getBccActivePlotHolder() : true);
        editor.putString(KEY_USER_TYPE, "producer");
        editor.apply();
    }

    public int getProdId() {
        return prefs.getInt(KEY_PROD_ID, -1);
    }

    public String getProdName() {
        return prefs.getString(KEY_PROD_NAME, "");
    }

    public String getProdEmail() {
        return prefs.getString(KEY_PROD_EMAIL, "");
    }

    public String getProdPostcode() {
        return prefs.getString(KEY_PROD_POSTCODE, "");
    }

    public String getProdPlotType() {
        return prefs.getString(KEY_PROD_PLOT_TYPE, "");
    }

    public Double getProdAnnualRent() {
        if (!prefs.contains(KEY_PROD_ANNUAL_RENT)) {
            return null;
        }
        return (double) prefs.getFloat(KEY_PROD_ANNUAL_RENT, 0f);
    }

    public String getAllotmentReference() {
        return prefs.getString(KEY_ALLOTMENT_REFERENCE, "");
    }

    public boolean getBccActivePlotHolder() {
        return prefs.getBoolean(KEY_BCC_ACTIVE_PLOT_HOLDER, true);
    }

    // ── General ────────────────────────────────────────────────────────────

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, "");
    }

    public boolean isLoggedIn() {
        return !getUserType().isEmpty();
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}