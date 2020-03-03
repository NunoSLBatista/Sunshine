package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.sunshine.models.WeatherResult;
import com.example.sunshine.network.GetDataService;
import com.example.sunshine.network.RetrofitClientInstance;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sunshine.MainActivity.API_KEY;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<WeatherResult> call = retrofitInterface.getWeather("Porto", API_KEY, "Metric");

        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResult> call, @NonNull Response<WeatherResult> response) {

                if(response.raw().code() != 404){

                    Log.d("Temperature", response.body().getWeatherList().get(0).getDescription());

                }

            }

            @Override
            public void onFailure(@NonNull Call<WeatherResult> call, @NonNull Throwable t) {
                Log.d("Error Message", t.getMessage());
            }
        });

    }
}
