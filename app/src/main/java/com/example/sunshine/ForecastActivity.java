package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.sunshine.adapters.ForecastAdapter;
import com.example.sunshine.models.Weather;

import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity implements ForecastAdapter.MyAdapterListener {

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

        Double media = 0.0;
        Double minTemp = 200.0;
        Double maxTemp = -200.0;

        for(int i = 0; i < weatherArrayList.size(); i++){
            if(i % 7 == 0 && i != 0){

                if(maxTemp < weatherArrayList.get(i).getmMaxTemp()){
                    maxTemp = weatherArrayList.get(i).getmMaxTemp();
                }
                if(minTemp > weatherArrayList.get(i).getmMinTemp()){
                    minTemp = weatherArrayList.get(i).getmMinTemp();
                }

                media += weatherArrayList.get(i).getmMainTemp();
                media = media / 8;
                weatherArrayList.get(i).setmMinTemp(minTemp);
                weatherArrayList.get(i).setmMaxTemp(maxTemp);
                weatherArrayList.get(i).setmWeatherType(weatherArrayList.get(i - 4).getmWeatherType());
                Log.d("Media", weatherArrayList.get(i).getmMainTemp().toString());
                newWeatherArrayList.add(weatherArrayList.get(i));
                media = 0.0;
                minTemp = 200.0;
                maxTemp = -200.0;
            } else {
                if(maxTemp < weatherArrayList.get(i).getmMaxTemp()){
                    maxTemp = weatherArrayList.get(i).getmMaxTemp();
                }
                if(minTemp > weatherArrayList.get(i).getmMinTemp()){
                    minTemp = weatherArrayList.get(i).getmMinTemp();
                }
                media += weatherArrayList.get(i).getmMainTemp();
            }
        }


        ForecastAdapter adapter = new ForecastAdapter(newWeatherArrayList, getApplicationContext(), this);
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        forecastRecycler.setAdapter(adapter);


    }

    @Override
    public void onDayClick(Context context, Weather weather) {
        Intent detailDay = new Intent(context, DetailForecastActivity.class);
        detailDay.putExtra("weatherObject", weather);
        startActivity(detailDay);
    }
}
