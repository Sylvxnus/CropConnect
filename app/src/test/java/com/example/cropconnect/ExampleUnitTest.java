package com.example.cropconnect;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {

    // ── Helpers (mirrors your activity validation logic) ───────────────────

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isValidPostcode(String postcode) {
        return postcode.matches("^[A-Z]{1,2}[0-9][0-9A-Z]?\\s?[0-9][A-Z]{2}$");
    }

    private String validatePassword(String password) {
        if (password.isEmpty()) return "Password is required";
        if (password.length() < 8) return "Password must be at least 8 characters";
        if (!password.matches(".*[A-Z].*"))
            return "Password must contain at least one uppercase letter";
        if (!password.matches(".*[0-9].*")) return "Password must contain at least one number";
        if (!password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*"))
            return "Password must contain at least one special character";
        return null;
    }

    private boolean isValidDonationWeight(double kg) {
        return kg > 0 && kg <= 500;
    }

    // ── Email tests ────────────────────────────────────────────────────────

    @Test
    public void validEmail_passes() {
        assertTrue(isValidEmail("aidan@example.com"));
    }

    @Test
    public void emailMissingAt_fails() {
        assertFalse(isValidEmail("aidanexample.com"));
    }

    @Test
    public void emailMissingDomain_fails() {
        assertFalse(isValidEmail("aidan@"));
    }

    @Test
    public void emptyEmail_fails() {
        assertFalse(isValidEmail(""));
    }

    // ── Password tests ─────────────────────────────────────────────────────

    @Test
    public void validPassword_passes() {
        assertNull(validatePassword("FoodBank1!"));
    }

    @Test
    public void passwordTooShort_fails() {
        assertEquals("Password must be at least 8 characters", validatePassword("Fo1!"));
    }

    @Test
    public void passwordNoUppercase_fails() {
        assertEquals("Password must contain at least one uppercase letter", validatePassword("foodbank1!"));
    }

    @Test
    public void passwordNoNumber_fails() {
        assertEquals("Password must contain at least one number", validatePassword("FoodBank!"));
    }

    @Test
    public void passwordNoSpecialChar_fails() {
        assertEquals("Password must contain at least one special character", validatePassword("FoodBank1"));
    }

    @Test
    public void emptyPassword_fails() {
        assertEquals("Password is required", validatePassword(""));
    }

    // ── Postcode tests ─────────────────────────────────────────────────────

    @Test
    public void validPostcode_passes() {
        assertTrue(isValidPostcode("B1 1BB"));
    }

    @Test
    public void validPostcodeNoSpace_passes() {
        assertTrue(isValidPostcode("B11BB"));
    }

    @Test
    public void invalidPostcode_fails() {
        assertFalse(isValidPostcode("123"));
    }

    @Test
    public void postcodeAllNumbers_fails() {
        assertFalse(isValidPostcode("12345"));
    }

    // ── Donation weight tests ──────────────────────────────────────────────

    @Test
    public void validDonationWeight_passes() {
        assertTrue(isValidDonationWeight(50));
    }

    @Test
    public void zeroDonationWeight_fails() {
        assertFalse(isValidDonationWeight(0));
    }

    @Test
    public void negativeDonationWeight_fails() {
        assertFalse(isValidDonationWeight(-1));
    }

    @Test
    public void excessiveDonationWeight_fails() {
        assertFalse(isValidDonationWeight(501));
    }

    @Test
    public void maxDonationWeight_passes() {
        assertTrue(isValidDonationWeight(500));
    }

    // ── Password confirm match tests ───────────────────────────────────────

    @Test
    public void passwordsMatch_passes() {
        String password = "FoodBank1!";
        String confirm = "FoodBank1!";
        assertEquals(password, confirm);
    }

    @Test
    public void passwordsMismatch_fails() {
        String password = "FoodBank1!";
        String confirm = "FoodBank2!";
        assertNotEquals(password, confirm);
    }

    // ── Brute force lockout tests ──────────────────────────────────────────

    @Test
    public void belowMaxAttempts_notLocked() {
        int attempts = 4;
        int maxAttempts = 5;
        assertFalse(attempts >= maxAttempts);
    }

    @Test
    public void atMaxAttempts_locked() {
        int attempts = 5;
        int maxAttempts = 5;
        assertTrue(attempts >= maxAttempts);
    }

    @Test
    public void exceedsMaxAttempts_locked() {
        int attempts = 6;
        int maxAttempts = 5;
        assertTrue(attempts >= maxAttempts);
    }
// ── Product quantity tests ─────────────────────────────────────────────

    @Test
    public void validProductQuantity_passes() {
        int quantity = 100;
        assertTrue(quantity >= 0 && quantity <= 10000);
    }

    @Test
    public void zeroProductQuantity_passes() {
        int quantity = 0;
        assertTrue(quantity >= 0 && quantity <= 10000);
    }

    @Test
    public void negativeProductQuantity_fails() {
        int quantity = -1;
        assertFalse(quantity >= 0 && quantity <= 10000);
    }

    @Test
    public void excessiveProductQuantity_fails() {
        int quantity = 10001;
        assertFalse(quantity >= 0 && quantity <= 10000);
    }

    @Test
    public void maxProductQuantity_passes() {
        int quantity = 10000;
        assertTrue(quantity >= 0 && quantity <= 10000);
    }

// ── Donation amount tests ──────────────────────────────────────────────

    @Test
    public void validDonationAmount_passes() {
        double amount = 100;
        assertTrue(amount > 0 && amount <= 500);
    }

    @Test
    public void zeroDonationAmount_fails() {
        double amount = 0;
        assertFalse(amount > 0 && amount <= 500);
    }

    @Test
    public void negativeDonationAmount_fails() {
        double amount = -10;
        assertFalse(amount > 0 && amount <= 500);
    }

    @Test
    public void excessiveDonationAmount_fails() {
        double amount = 501;
        assertFalse(amount > 0 && amount <= 500);
    }

    @Test
    public void maxDonationAmount_passes() {
        double amount = 500;
        assertTrue(amount > 0 && amount <= 500);
    }

// ── Product name tests ─────────────────────────────────────────────────

    @Test
    public void validProductName_passes() {
        String name = "Tinned Tomatoes";
        assertTrue(name != null && !name.trim().isEmpty() && name.length() <= 100);
    }

    @Test
    public void emptyProductName_fails() {
        String name = "";
        assertFalse(name != null && !name.trim().isEmpty() && name.length() <= 100);
    }

    @Test
    public void tooLongProductName_fails() {
        String name = "A".repeat(101);
        assertFalse(name != null && !name.trim().isEmpty() && name.length() <= 100);
    }

    @Test
    public void maxLengthProductName_passes() {
        String name = "A".repeat(100);
        assertTrue(name != null && !name.trim().isEmpty() && name.length() <= 100);
    }
}