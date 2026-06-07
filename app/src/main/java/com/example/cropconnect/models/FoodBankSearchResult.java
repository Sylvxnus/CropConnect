package com.example.cropconnect.models;

public class FoodBankSearchResult {
    private FoodBank foodBank;
    private String matchedProduct;
    private int matchedQuantity;
    private String matchedUnit;

    public FoodBank getFoodBank() { return foodBank; }
    public void setFoodBank(FoodBank foodBank) { this.foodBank = foodBank; }
    public String getMatchedProduct() { return matchedProduct; }
    public void setMatchedProduct(String matchedProduct) { this.matchedProduct = matchedProduct; }
    public int getMatchedQuantity() { return matchedQuantity; }
    public void setMatchedQuantity(int matchedQuantity) { this.matchedQuantity = matchedQuantity; }
    public String getMatchedUnit() { return matchedUnit; }
    public void setMatchedUnit(String matchedUnit) { this.matchedUnit = matchedUnit; }
}