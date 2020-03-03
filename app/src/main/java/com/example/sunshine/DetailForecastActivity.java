package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunshine.models.Weather;
import com.squareup.picasso.Picasso;

public class DetailForecastActivity extends AppCompatActivity {

    Weather weather;
    TextView weatherTypeTextView;
    TextView tempTextView;
    TextView minMaxTempTextView;
    ImageView weatherIcon;
    TextView feelsLikeTextView;
    TextView windTextView;
    TextView pressureTextView;
    TextView humidityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_forecast);

        weatherTypeTextView = (TextView) findViewById(R.id.modeTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        minMaxTempTextView = (TextView) findViewById(R.id.minMaxTemp);
        weatherIcon = (ImageView) findViewById(R.id.weatherModeIcon);
        feelsLikeTextView = (TextView) findViewById(R.id.feelLikeTemp);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureText);
        windTextView = (TextView) findViewById(R.id.windText);

        weather = (Weather) getIntent().getSerializableExtra("weatherObject");

        updateWeatherDay(weather);

    }

    private void updateWeatherDay(Weather newWeather){

        String mainTemp = String.format("%.0f", newWeather.getMain().getTemp());

        tempTextView.setText(mainTemp + "º");
        pressureTextView.setText(newWeather.getMain().getPressure().toString());
        windTextView.setText(newWeather.getWind().getSpeed().toString());
        weatherTypeTextView.setText(newWeather.getWeatherList().get(0).getMain());
        minMaxTempTextView.setText(newWeather.getMain().getTempMin() + "º / " + newWeather.getMain().getTempMax() + "º");
        feelsLikeTextView.setText("Feels like " + newWeather.getMain().getFeelsLike() + "º");
        humidityTextView.setText(newWeather.getMain().getHumidity().toString() + "%");

        Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + newWeather.getWeatherList().get(0).getIcon() +  "@2x.png").into(weatherIcon);

    }

}
