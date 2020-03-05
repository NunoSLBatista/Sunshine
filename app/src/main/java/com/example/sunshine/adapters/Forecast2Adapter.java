package com.example.sunshine.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sunshine.ForecastFragment;
import com.example.sunshine.models.ForecastResult;
import com.example.sunshine.models.Weather;

import java.util.ArrayList;

public class Forecast2Adapter extends FragmentStateAdapter {

    ArrayList<ForecastResult> weatherArrayList = new ArrayList<>();

    public Forecast2Adapter(@NonNull FragmentActivity fragmentActivity, ArrayList<ForecastResult> weatherArrayList) {
        super(fragmentActivity);
        this.weatherArrayList = weatherArrayList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       Fragment fragment = new ForecastFragment();
       Bundle args = new Bundle();
       args.putSerializable("weather", weatherArrayList.get(position));
       fragment.setArguments(args);
       return fragment;
    }

    @Override
    public int getItemCount() {
        return weatherArrayList.size();
    }

}
