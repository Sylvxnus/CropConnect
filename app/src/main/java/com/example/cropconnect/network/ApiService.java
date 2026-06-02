package com.example.cropconnect.network;

import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.models.Producer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface ApiService {

    // ── Foodbank endpoints (already existed) ───────────────────────────────
    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

    //
    @GET("api/foodbanks/search")
    Call<List<FoodBank>> searchFoodBanks(@Query("query") String query);

    // ── Producer (allotment donator) endpoints ─────────────────────────────

    // Register a new producer
    @POST("api/producers")
    Call<Producer> registerProducer(@Body Producer producer);

    // Login — returns the producer object if credentials match
    @POST("api/producers/login")
    Call<Producer> loginProducer(@Body Producer loginRequest);

    // Get a single producer by ID
    @GET("api/producers/{id}")
    Call<Producer> getProducer(@Path("id") int prodId);

    // ── Foodbank product (inventory) endpoints ─────────────────────────────

    // Get all products for a specific foodbank
    @GET("api/foodbanks/{fbId}/products")
    Call<List<FoodBankProduct>> getProductsByFoodbank(@Path("fbId") int fbId);

    // Get a single product
    @GET("api/products/{productId}")
    Call<FoodBankProduct> getProduct(@Path("productId") int productId);

    // Add a new product
    @POST("api/products")
    Call<FoodBankProduct> createProduct(@Body FoodBankProduct product);

    // Update an existing product
    @PUT("api/products/{productId}")
    Call<FoodBankProduct> updateProduct(@Path("productId") int productId,
                                        @Body FoodBankProduct product);

    // Delete a product
    @DELETE("api/products/{productId}")
    Call<Void> deleteProduct(@Path("productId") int productId);
}