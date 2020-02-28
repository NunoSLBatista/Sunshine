package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.WeatherDbHelper;
import com.example.sunshine.models.TypeWeather;
import com.example.sunshine.models.Weather;
import com.example.sunshine.network.GetDataService;
import com.example.sunshine.network.RetrofitClientInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "9d7443ba0f8ff7df7afafd12f4006dca";


    TextView weatherTypeTextView;
    TextView tempTextView;
    TextView minMaxTempTextView;
    TextView descriptionTextView;
    ImageView weatherIcon;
    TextView feelsLikeTextView;
    RecyclerView weatherHourRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTypeTextView = (TextView) findViewById(R.id.modeTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        minMaxTempTextView = (TextView) findViewById(R.id.minMaxTemp);
        weatherIcon = (ImageView) findViewById(R.id.weatherModeIcon);
        feelsLikeTextView = (TextView) findViewById(R.id.feelLikeTemp);

        GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> call = retrofitInterface.getWeather("Sydney", API_KEY, "Metric");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String stringResponse = response.body().string();
                    Weather weather = serializeJson(stringResponse);

                    WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                    db.checkWeatherType(weather.getmWeatherType());
                    db.addWeather(weather);

                    String mainTemp = String.format("%.0f", weather.getmMainTemp());

                    tempTextView.setText(mainTemp + "º");
                    weatherTypeTextView.setText(weather.getmWeatherType().getMain());
                    minMaxTempTextView.setText(weather.getmMinTemp() + "º / " + weather.getmMaxTemp() + "º");
                    feelsLikeTextView.setText("Feels like " + weather.getmFeelsLikeTemp()+ "º");


                    Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + weather.getmWeatherType().getIcon() +  "@2x.png").into(weatherIcon);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                List<Weather> weatherList = db.getAll("", "2147714");

                Weather weather = weatherList.get(0);

                String mainTemp = String.format("%.0f", weather.getmMainTemp());
                tempTextView.setText(mainTemp + "º");
                weatherTypeTextView.setText(weather.getmWeatherType().getMain());
                minMaxTempTextView.setText(weather.getmMinTemp() + "º / " + weather.getmMaxTemp() + "º");
                feelsLikeTextView.setText("Feels like " + weather.getmFeelsLikeTemp()+ "º");

            }
        });

    }

    public Weather serializeJson(String response){

        Weather weather = new Weather();
        TypeWeather typeWeather = new TypeWeather();

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONObject mainObj = jsonObject.getJSONObject("main");


            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject sysObj = jsonObject.getJSONObject("sys");
            JSONObject jsonObject1 = weatherArray.getJSONObject(0);

            Integer typeId = jsonObject1.getInt("id");
            String main = jsonObject1.getString("main");
            String description = jsonObject1.getString("description");
            String icon = jsonObject1.getString("icon");

            typeWeather.setId(typeId);
            typeWeather.setMain(main);
            typeWeather.setDescription(description);
            typeWeather.setIcon(icon);
            weather.setmWeatherType(typeWeather);

            String sunriseHour = sysObj.getString("sunrise");
            String sunsetHour = sysObj.getString("sunset");
            Integer cityId = jsonObject.getInt("id");
            String date = jsonObject.getString("dt");
            Double mainTemp = mainObj.getDouble("temp");
            Double minTemp = mainObj.getDouble("temp_min");
            Double maxTemp = mainObj.getDouble("temp_max");
            Double pressure = mainObj.getDouble("pressure");
            Double humidity = mainObj.getDouble("humidity");
            Double feelLikeTemp = mainObj.getDouble("feels_like");

            weather.setmCityId(cityId);
            weather.setmMainTemp(mainTemp);
            weather.setmMinTemp(minTemp);
            weather.setmMaxTemp(maxTemp);
            weather.setmPressure(pressure);
            weather.setmHumidity(humidity);
            weather.setmFeelsLikeTemp(feelLikeTemp);
            weather.setmSunrise(sunriseHour);
            weather.setmSunset(sunsetHour);
            weather.setmDate(date);


        } catch (JSONException e){
            e.printStackTrace();
        }

        return weather;

    }

}
