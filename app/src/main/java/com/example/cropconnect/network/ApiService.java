package com.example.cropconnect.network;

import com.example.cropconnect.models.FoodBank;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

    @GET("api/foodbanks/search")
    Call<List<FoodBank>> searchFoodBanks(@Query("query") String query);

}
