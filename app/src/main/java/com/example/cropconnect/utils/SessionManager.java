package com.example.cropconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager
 * ───────────────
 * Stores the logged-in producer's details in SharedPreferences
 * so they persist across screens without passing through Intents.
 *
 * Usage:
 *   // Save after login:
 *   SessionManager.saveProducerSession(context, producer);
 *
 *   // Read anywhere:
 *   int id = SessionManager.getProdId(context);
 *
 *   // Clear on logout:
 *   SessionManager.clearSession(context);
 */
public class SessionManager {

    private static final String PREFS_NAME = "cropconnect_prefs";
    private static final String KEY_PROD_ID       = "prod_id";
    private static final String KEY_PROD_NAME     = "prod_name";
    private static final String KEY_PROD_EMAIL    = "prod_email";
    private static final String KEY_PROD_POSTCODE = "prod_postcode";
    private static final String KEY_IS_LOGGED_IN  = "is_logged_in";

    // ── Save producer session after login ──────────────────────────────────
    public static void saveProducerSession(Context context,
                                           com.example.cropconnect.models.Producer producer) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(KEY_PROD_ID, producer.getProdId());
        editor.putString(KEY_PROD_NAME, producer.getProdName());
        editor.putString(KEY_PROD_EMAIL, producer.getProdEmail());
        editor.putString(KEY_PROD_POSTCODE, producer.getProdPostcode());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public static int getProdId(Context context) {
        return getPrefs(context).getInt(KEY_PROD_ID, -1);
    }

    public static String getProdName(Context context) {
        return getPrefs(context).getString(KEY_PROD_NAME, "");
    }

    public static String getProdEmail(Context context) {
        return getPrefs(context).getString(KEY_PROD_EMAIL, "");
    }

    public static String getProdPostcode(Context context) {
        return getPrefs(context).getString(KEY_PROD_POSTCODE, "");
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ── Clear session on logout ────────────────────────────────────────────
    public static void clearSession(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    // ── Helper ─────────────────────────────────────────────────────────────
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}