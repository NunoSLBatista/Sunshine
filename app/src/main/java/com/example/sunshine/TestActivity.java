package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.example.sunshine.adapters.CityAdapter;
import com.example.sunshine.data.WeatherDbHelper;
import com.example.sunshine.models.City;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity implements CityAdapter.ClickListener {

    ArrayList<City> citiesArray;
    RecyclerView citiesRecyclerView;
    CityAdapter cityAdapter;
    public static final String CITY_DATA = "CITY_DATA";
    public static final String CITY_DATA2 = "CITY_DATA2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        citiesRecyclerView = findViewById(R.id.citiesRecylerView);

        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());
        citiesArray = weatherDbHelper.getAllCities();

        cityAdapter = new CityAdapter(citiesArray, getApplicationContext(), this);
        citiesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        citiesRecyclerView.setAdapter(cityAdapter);

    }

    @Override
    public void onDayClick(Context context, City city) {
        final Intent data = new Intent();
        data.putExtra(CITY_DATA, city.getName());
        data.putExtra(CITY_DATA2, city.getId());
        setResult(Activity.RESULT_OK, data);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}
