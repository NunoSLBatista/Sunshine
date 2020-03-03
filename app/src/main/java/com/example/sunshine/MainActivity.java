package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.sunshine.adapters.WeatherForecastAdapter;
import com.example.sunshine.data.WeatherDbHelper;
import com.example.sunshine.models.TypeWeather;
import com.example.sunshine.models.Weather;
import com.example.sunshine.network.GetDataService;
import com.example.sunshine.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "9d7443ba0f8ff7df7afafd12f4006dca";

    Weather currentWeather;
    ArrayList<Weather> currentWeatherArrayList;
    ArrayList<Weather> weatherArrayList = new ArrayList<>();
    ArrayList<Weather> tomorrowWeatherArrayList = new ArrayList<>();
    TextView weatherTypeTextView;
    TextView tempTextView;
    TextView minMaxTempTextView;
    ImageView weatherIcon;
    TextView feelsLikeTextView;
    RecyclerView weatherHourRecycler;
    TextView sunriseTextView;
    TextView sunsetTextView;
    TextView humidityTextView;
    TextView todayBox;
    TextView tomorrowBox;
    TextView next5DaysBox;
    TextView windTextView;
    TextView pressureTextView;
    Calendar cal = Calendar.getInstance();

    private final int REQUEST_LOCATION_PERMISSION = 1;
    EditText citySearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherTypeTextView =  findViewById(R.id.modeTextView);
        tempTextView =  findViewById(R.id.tempTextView);
        minMaxTempTextView =  findViewById(R.id.minMaxTemp);
        weatherIcon =  findViewById(R.id.weatherModeIcon);
        feelsLikeTextView =  findViewById(R.id.feelLikeTemp);
        weatherHourRecycler =  findViewById(R.id.forecastWeather);
        sunriseTextView =  findViewById(R.id.sunriseTxt);
        sunsetTextView =  findViewById(R.id.sunsetTxt);
        humidityTextView =  findViewById(R.id.humidityTextView);
        todayBox =  findViewById(R.id.todayBox);
        tomorrowBox =  findViewById(R.id.tommorowBox);
        next5DaysBox =  findViewById(R.id.next5Days);
        pressureTextView =  findViewById(R.id.pressureText);
        windTextView =  findViewById(R.id.windTxt);
        Toolbar toolbar = findViewById(R.id.tool_bar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        // Get a reference to the AutoCompleteTextView in the layout
        citySearch = findViewById(R.id.autocomplete_country);

        requestLocationPermission();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert lm != null;
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

       lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1000, locationListener);



        todayBox.setOnClickListener(v -> {
            updateWeatherDay(currentWeather);
            WeatherForecastAdapter adapter = new WeatherForecastAdapter(currentWeatherArrayList, getApplicationContext());
            weatherHourRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
            weatherHourRecycler.setAdapter(adapter);

        });

        tomorrowBox.setOnClickListener(v -> {

            if(tomorrowWeatherArrayList.size() > 0){
                updateWeatherDay(tomorrowWeatherArrayList.get(4));
                WeatherForecastAdapter adapter = new WeatherForecastAdapter(tomorrowWeatherArrayList, getApplicationContext());
                weatherHourRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                weatherHourRecycler.setAdapter(adapter);
            }

        });

        next5DaysBox.setOnClickListener(v -> {
             Intent forecastActivity = new Intent(getApplicationContext(), ForecastActivity.class);
             forecastActivity.putExtra("listForecast", weatherArrayList);
             startActivity(forecastActivity);
        });

        GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> callForecast = retrofitInterface.getForecast("Porto", API_KEY, "Metric");


        Call<ResponseBody> call = retrofitInterface.getWeather("Porto", API_KEY, "Metric");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String stringResponse = response.body().string();
                    Weather weather = serializeJson(stringResponse);
                    currentWeather = weather;

                    WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                    db.checkWeatherType(weather.getmWeatherType());
                    db.addWeather(weather);

                    updateWeatherDay(currentWeather);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            String cityText = citySearch.getText().toString();

            GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<ResponseBody> call = retrofitInterface.getWeather(cityText, API_KEY, "Metric");

            getWeatherByCity(call);
            Call<ResponseBody> callForecast = retrofitInterface.getForecast(cityText, API_KEY, "Metric");

            WeatherForecastAdapter adapter = new WeatherForecastAdapter(getWeatherForecast(callForecast), getApplicationContext());
            weatherHourRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
            weatherHourRecycler.setAdapter(adapter);




            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWeatherByCity(Call<ResponseBody> call){

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    String stringResponse = response.body().string();
                    Weather weather = serializeJson(stringResponse);
                    currentWeather = weather;

                    WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                    db.checkWeatherType(weather.getmWeatherType());
                    db.addWeather(weather);

                    updateWeatherDay(currentWeather);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    public Weather serializeJson(String response){

        Weather weather = new Weather();
        TypeWeather typeWeather = new TypeWeather();

        try {

            JSONObject jsonObject = new JSONObject(response);
            JSONObject mainObj = jsonObject.getJSONObject("main");


            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject sysObj = jsonObject.getJSONObject("sys");
            JSONObject windObj = jsonObject.getJSONObject("wind");
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
            Double windSpeed = windObj.getDouble("speed");
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
            weather.setmSpeedWind(windSpeed);


        } catch (JSONException e){
            e.printStackTrace();
        }

        return weather;

    }

    public ArrayList<Weather> getWeatherForecast(Call<ResponseBody> call){

        weatherArrayList.clear();
        tomorrowWeatherArrayList.clear();

        ArrayList<Weather> listWeather = new ArrayList<>();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Response", call.request().url().toString());

                try {
                    String stringResponse = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Weather weather = serializeJsonForecast(jsonObject1);

                       // Log.d("Values", String.valueOf(weather.getDateCalendar().get(Calendar.HOUR_OF_DAY)) + "/" + String.valueOf(weather.getDateCalendar().get(Calendar.MINUTE)));
                        if(isDateSame(weather.getDateCalendar(), cal)){
                            listWeather.add(weather);
                        } else {
                            weatherArrayList.add(weather);
                        }

                        cal.add(Calendar.DATE, +1);
                        if(isDateSame(weather.getDateCalendar(), cal)){
                            tomorrowWeatherArrayList.add(weather);
                        }

                        cal.add(Calendar.DATE, -1);

                    }

                    currentWeatherArrayList = listWeather;

                    WeatherForecastAdapter adapter = new WeatherForecastAdapter(currentWeatherArrayList, getApplicationContext());
                    weatherHourRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                    weatherHourRecycler.setAdapter(adapter);

                } catch (IOException e){
                    e.printStackTrace();
                } catch (JSONException json){
                    Log.d("JsonException", json.getMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });

        return listWeather;

    }


    public Weather serializeJsonForecast(JSONObject jsonObject){

        Weather weather = new Weather();
        TypeWeather typeWeather = new TypeWeather();

        try {

            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");

            String date = jsonObject.getString("dt_txt");

            Double mainTemp = jsonObjectMain.getDouble("temp");
            Double feelLikeTemp = jsonObjectMain.getDouble("feels_like");
            Double maxTemp = jsonObjectMain.getDouble("temp_max");
            Double minTemp = jsonObjectMain.getDouble("temp_min");
            Double humidity = jsonObjectMain.getDouble("humidity");
            Double pressure = jsonObjectMain.getDouble("pressure");

            JSONObject windObject = jsonObject.getJSONObject("wind");
            Double windSpeed = windObject.getDouble("speed");

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
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

            weather.setmMainTemp(mainTemp);
            weather.setmMinTemp(minTemp);
            weather.setmMaxTemp(maxTemp);
            weather.setmPressure(pressure);
            weather.setmHumidity(humidity);
            weather.setmFeelsLikeTemp(feelLikeTemp);
            weather.setmDate(date);
            weather.setmSpeedWind(windSpeed);


        } catch (JSONException e){
            e.printStackTrace();
        }

        return weather;

    }

    private boolean isDateSame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    private void updateWeatherDay(Weather newWeather){

        String mainTemp = String.format("%.0f", newWeather.getmMainTemp());

        tempTextView.setText(mainTemp + "º");
        pressureTextView.setText(newWeather.getmPressure().toString());
        windTextView.setText(newWeather.getmSpeedWind().toString());
        weatherTypeTextView.setText(newWeather.getmWeatherType().getMain());
        minMaxTempTextView.setText(newWeather.getmMinTemp() + "º / " + newWeather.getmMaxTemp() + "º");
        feelsLikeTextView.setText("Feels like " + newWeather.getmFeelsLikeTemp() + "º");
        if(!newWeather.checkSun()){
            sunriseTextView.setText(currentWeather.getmSunrise());
            sunsetTextView.setText(currentWeather.getmSunset());
        } else {
            sunriseTextView.setText(newWeather.getmSunrise());
            sunsetTextView.setText(newWeather.getmSunset());
        }
        humidityTextView.setText(newWeather.getmHumidity().toString() + "%");

        Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + newWeather.getmWeatherType().getIcon() +  "@2x.png").into(weatherIcon);

    }

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            weatherArrayList.clear();
            tomorrowWeatherArrayList.clear();

             double longitude = location.getLongitude();
             double latitude = location.getLatitude();

            GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<ResponseBody> call = retrofitInterface.getWeatherCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try{
                        String stringResponse = response.body().string();
                        Weather weather = serializeJson(stringResponse);
                        currentWeather = weather;

                        WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                        db.checkWeatherType(weather.getmWeatherType());
                        db.addWeather(weather);

                        updateWeatherDay(currentWeather);

                        GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                        Call<ResponseBody> callForecast = retrofitInterface.getForecastCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");

                        getWeatherForecast(callForecast);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


}
