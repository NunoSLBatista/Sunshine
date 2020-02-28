package com.example.sunshine.network;

import com.example.sunshine.models.Weather;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("/data/2.5/weather")
    Call<ResponseBody> getWeather(
            @Query("q") String param1, @Query("appid") String param2, @Query("units") String param3);

}
