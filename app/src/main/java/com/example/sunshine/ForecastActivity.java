package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.sunshine.adapters.ForecastAdapter;
import com.example.sunshine.models.Weather;

import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity {

    ArrayList<Weather> weatherArrayList = new ArrayList<Weather>();
    ArrayList<Weather> newWeatherArrayList = new ArrayList<Weather>();
    RecyclerView forecastRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        forecastRecycler = (RecyclerView) findViewById(R.id.forecastRecycler);

        weatherArrayList = (ArrayList<Weather>) getIntent().getSerializableExtra("listForecast");
        Log.d("MainTemp", weatherArrayList.get(0).getmMaxTemp().toString());

        for(int i = 0; i < weatherArrayList.size(); i+=8){
            newWeatherArrayList.add(weatherArrayList.get(i));
        }

        ForecastAdapter adapter = new ForecastAdapter(newWeatherArrayList, getApplicationContext());
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        forecastRecycler.setAdapter(adapter);


    }
}
