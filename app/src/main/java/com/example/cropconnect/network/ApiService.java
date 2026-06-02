package com.example.cropconnect.network;

import com.example.cropconnect.models.FoodBank;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("api/foodbanks")
    Call<List<FoodBank>> getFoodBanks();

}
