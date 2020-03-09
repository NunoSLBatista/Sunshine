package com.example.sunshine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.adapters.Forecast3Adapter;
import com.example.sunshine.models.ForecastResult;

public class ForecastFragment extends Fragment {

    Forecast3Adapter forecast3Adapter;
    RecyclerView forecastRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_forecast, container, false);

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forecastRecycler = view.findViewById(R.id.forecastRecycler2);
        assert getArguments() != null;
        ForecastResult weatherArrayList = (ForecastResult) getArguments().getSerializable("weather");
        forecast3Adapter = new Forecast3Adapter(weatherArrayList.getListWeather(), getContext());
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        forecastRecycler.setAdapter(forecast3Adapter);

    }

}

