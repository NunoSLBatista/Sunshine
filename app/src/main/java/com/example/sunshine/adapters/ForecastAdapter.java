package com.example.sunshine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.R;
import com.example.sunshine.models.Weather;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.WeatherHolder> {

    // List to store all the contact details
    private ArrayList<Weather> weatherList;
    private Context mContext;
    String daysWeek[] = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};


    // Counstructor for the Class
    public ForecastAdapter(ArrayList<Weather> weatherList, Context context) {
        this.weatherList = weatherList;
        this.mContext = context;
    }

    // This method creates views for the RecyclerView by inflating the layout
    // Into the viewHolders which helps to display the items in the RecyclerView
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.forecast_item, parent, false);
        return new WeatherHolder(view);
    }

    @Override
    public int getItemCount() {
        return weatherList == null ? 0 : weatherList.size();
    }

    // This method is called when binding the data to the views being created in RecyclerView
    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, final int position) {
        final Weather weather = weatherList.get(position);

        String maxTemp = String.format("%.0f", weather.getmMaxTemp()) + "º";
        String minTemp = String.format("%.0f", weather.getmMinTemp()) + "º";

        holder.textMaxTemp.setText(maxTemp);
        holder.txtMinTemp.setText(minTemp);
        try {
            Calendar cal = weather.getDateCalendar();
            holder.txtDayWeek.setText(daysWeek[weather.getDateCalendar().get(Calendar.DAY_OF_WEEK) - 1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(mContext).load("https://openweathermap.org/img/wn/" + weather.getmWeatherType().getIcon() + "@2x.png").into(holder.iconWeather);

    }

    // This is your ViewHolder class that helps to populate data to the view
    public class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView txtDayWeek;
        private TextView txtMinTemp;
        private TextView textMaxTemp;
        private ImageView iconWeather;

        public WeatherHolder(View itemView) {
            super(itemView);

            txtDayWeek = itemView.findViewById(R.id.weekDay);
            txtMinTemp = itemView.findViewById(R.id.maxTemp);
            textMaxTemp = itemView.findViewById(R.id.minTemp);
            iconWeather = itemView.findViewById(R.id.tempIcon);


        }
    }
}