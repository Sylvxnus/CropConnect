package com.example.cropconnect.network;

import com.example.cropconnect.models.FoodBank;
import com.example.cropconnect.models.FoodBankProduct;
import com.example.cropconnect.models.FoodBankSearchResult;
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

    // ── FoodBank auth ──────────────────────────────────────────────────────
    @POST("api/foodbanks/login")
    Call<FoodBank> loginFoodBank(@Body FoodBank loginRequest);

    // ── FoodBank data ──────────────────────────────────────────────────────
    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

    @GET("api/foodbanks/search")
    Call<List<FoodBankSearchResult>> searchFoodBanksAndProducts(@Query("query") String query);

    // ── Producer auth ──────────────────────────────────────────────────────
    @POST("api/producers")
    Call<Producer> registerProducer(@Body Producer producer);

    @POST("api/producers/login")
    Call<Producer> loginProducer(@Body Producer loginRequest);

    // ── Products ───────────────────────────────────────────────────────────
    @GET("api/products")
    Call<List<FoodBankProduct>> getProducts(@Query("fbId") long fbId);

    @GET("api/products/{id}")
    Call<FoodBankProduct> getProductById(@Path("id") long productId);

    @POST("api/products")
    Call<FoodBankProduct> createProduct(@Body FoodBankProduct product);

    @PUT("api/products/{id}")
    Call<FoodBankProduct> updateProduct(@Path("id") long productId,
                                        @Body FoodBankProduct product);

    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") long productId);
}