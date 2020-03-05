package com.example.sunshine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.R;
import com.example.sunshine.models.Weather;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

public class Forecast3Adapter extends RecyclerView.Adapter<Forecast3Adapter.WeatherHolder> {

    // List to store all the contact details
    private ArrayList<Weather> weatherList;
    private Context mContext;

    // Counstructor for the Class
    public Forecast3Adapter(ArrayList<Weather> weatherList, Context context) {
        this.weatherList = weatherList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public WeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.forecast_item2, parent, false);
        return new WeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHolder holder, int position) {

        boolean expandend = weatherList.get(position).getExpanded();

        if(expandend){
            holder.subItem.setVisibility(View.VISIBLE);
        } else {
            holder.subItem.setVisibility(View.GONE);
        }

        String minTemp = weatherList.get(position).getMain().getTempMin().toString() + "º";
        String maxTemp = weatherList.get(position).getMain().getTempMax().toString() + "º";
        String humidity = weatherList.get(position).getMain().getHumidity().toString() + "%";
        String pressure = weatherList.get(position).getMain().getPressure().toString();
        String timeText = null;
        try {
            timeText = weatherList.get(position).getDateFormated();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txtMinTemp.setText(minTemp);
        holder.textMaxTemp.setText(maxTemp);
        holder.timeTextView.setText(timeText);
        holder.pressure.setText(pressure);
        holder.humidityTextView.setText(humidity);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expandend = weatherList.get(position).getExpanded();
                weatherList.get(position).setExpanded(!expandend);
                notifyItemChanged(position);
            }
        });

        Picasso.with(mContext).load("https://openweathermap.org/img/wn/" + weatherList.get(position).getWeatherList().get(0).getIcon() + "@2x.png").into(holder.iconWeather);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    // This is your ViewHolder class that helps to populate data to the view
    public class WeatherHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView txtMinTemp;
        private TextView textMaxTemp;
        private TextView humidityTextView;
        private TextView windTextView;
        private TextView pressure;
        private ImageView iconWeather;
        private LinearLayout subItem;

        public WeatherHolder(View itemView) {
            super(itemView);

            timeTextView = itemView.findViewById(R.id.timeTextView);
            textMaxTemp = itemView.findViewById(R.id.maxTemp);
            txtMinTemp = itemView.findViewById(R.id.minTemp);
            iconWeather = itemView.findViewById(R.id.iconWeather);
            subItem = itemView.findViewById(R.id.sub_item);
            humidityTextView = itemView.findViewById(R.id.humdityTextView);
            windTextView = itemView.findViewById(R.id.windTextView);
            pressure = itemView.findViewById(R.id.pressureTextView);


        }
    }
}
