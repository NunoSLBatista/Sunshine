package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.sunshine.adapters.WeatherForecastAdapter;
import com.example.sunshine.data.WeatherDbHelper;
import com.example.sunshine.models.City;
import com.example.sunshine.models.ForecastResult;
import com.example.sunshine.models.Weather;
import com.example.sunshine.models.WeatherResult;
import com.example.sunshine.network.GetDataService;
import com.example.sunshine.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "9d7443ba0f8ff7df7afafd12f4006dca";

    WeatherResult currentWeather;
    ArrayList<Weather> currentWeatherArrayList = new ArrayList<>();
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
    TextView cityTextView;
    WeatherForecastAdapter adapter;
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
        cityTextView = findViewById(R.id.cityTextView);
        windTextView =  findViewById(R.id.windTxt);
        Toolbar toolbar = findViewById(R.id.tool_bar);


        adapter = new WeatherForecastAdapter(getApplicationContext());
        weatherHourRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        weatherHourRecycler.setAdapter(adapter);


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
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

       lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1000, locationListener);

        todayBox.setOnClickListener(v -> {
            updateWeatherDay(currentWeather);
            adapter.setWeatherList(currentWeatherArrayList);
            adapter.notifyDataSetChanged();

        });

        tomorrowBox.setOnClickListener(v -> {

            if(tomorrowWeatherArrayList.size() > 0){
                updateWeatherDay(tomorrowWeatherArrayList.get(4));
                adapter.setWeatherList(tomorrowWeatherArrayList);
                adapter.notifyDataSetChanged();
            }

        });

        next5DaysBox.setOnClickListener(v -> {
             Intent forecastActivity = new Intent(getApplicationContext(), ForecastActivity.class);
             forecastActivity.putExtra("listForecast", weatherArrayList);
             startActivity(forecastActivity);
        });

        cityTextView.setOnClickListener(v -> {
            Intent cityActivity = new Intent(getApplicationContext(), TestActivity.class);
            startActivityForResult(cityActivity, 2000);
        });

        WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
        List<WeatherResult> weatherList = db.getAll();

        if(weatherList.size() > 0){

            WeatherResult weather = weatherList.get(weatherList.size() - 1);
            updateWeatherDay(weather);

            currentWeather = weather;
            ArrayList<Weather> tempArrayList = (ArrayList<Weather>) db.getAll2(weather);

            try {
               cal = currentWeather.getDateCalendar();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < tempArrayList.size(); i++){
                try {
                    if (isDateSame(tempArrayList.get(i).getDateCalendar(), cal)) {
                        currentWeatherArrayList.add(tempArrayList.get(i));
                    } else {
                        weatherArrayList.add(tempArrayList.get(i));
                    }

                    cal.add(Calendar.DATE, +1);
                    if (isDateSame(tempArrayList.get(i).getDateCalendar(), cal)) {
                        tomorrowWeatherArrayList.add(tempArrayList.get(i));
                    }
                    cal.add(Calendar.DATE, -1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            adapter.setWeatherList(currentWeatherArrayList);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

            String cityText = citySearch.getText().toString();

            GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

            Call<WeatherResult> call = retrofitInterface.getWeather(cityText, API_KEY, "Metric");
            getWeatherByCity(call);

            Call<ForecastResult> callForecast = retrofitInterface.getForecast(cityText, API_KEY, "Metric");

            ArrayList<Weather> weatherForecast = getWeatherForecast(callForecast);
            if(weatherForecast != null && weatherForecast.size() > 0){
                adapter.setWeatherList(weatherForecast);
                adapter.notifyDataSetChanged();
            } else {

            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWeatherByCity(Call<WeatherResult> call){

        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResult> call, @NonNull Response<WeatherResult> response) {

                if(response.raw().code() == 200){
                    currentWeather = response.body();
                    WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());

                    weatherDbHelper.checkWeatherType(response.body().getWeatherList().get(0));
                    try {
                        if(!weatherDbHelper.checkDates(currentWeather)){
                            weatherDbHelper.addWeather(currentWeather);
                            weatherDbHelper.checkCity(currentWeather);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    updateWeatherDay(currentWeather);
                } else {
                    Toast.makeText(getApplicationContext(), "Nome da cidade é inválido.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<WeatherResult> call, @NonNull Throwable t) {

                if(t.getMessage() != null && t.getMessage().contains("No address associated with hostname")){

                    WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                    List<WeatherResult> weatherList = db.getAll();
                    WeatherResult weather = weatherList.get(0);

                    String mainTemp = String.format(Locale.ENGLISH, "%.0f", weather.getMain().getTemp());
                    String tempText = mainTemp + "º";
                    String minMaxText = weather.getMain().getTempMin() + "º / " + weather.getMain().getTempMax() + "º";
                    String feelLikeText = "Feels like " + weather.getMain().getFeelsLike() + "º";

                    tempTextView.setText(tempText);
                    minMaxTempTextView.setText(minMaxText);
                    feelsLikeTextView.setText(feelLikeText);
                }
            }
        });

    }

    public ArrayList<Weather> getWeatherForecast(Call<ForecastResult> call){

        ArrayList<Weather> listWeather = new ArrayList<>();

        call.enqueue(new Callback<ForecastResult>() {
            @Override
            public void onResponse(@NonNull Call<ForecastResult> call, @NonNull Response<ForecastResult> response) {

                if(response.raw().code() == 200) {

                    weatherArrayList.clear();
                    tomorrowWeatherArrayList.clear();

                    for (int i = 0; i < response.body().getListWeather().size(); i++) {

                        Weather weather = response.body().getListWeather().get(i);
                        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());
                        weather.setCityId(response.body().getCity().getId());
                        weatherDbHelper.checkCity(currentWeather);

                        try {
                            if (!weatherDbHelper.checkDates(weather)) {
                                weatherDbHelper.addWeather2(weather);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (isDateSame(weather.getDateCalendar(), cal)) {
                                listWeather.add(weather);
                            } else {
                                weatherArrayList.add(weather);
                            }
                            cal.add(Calendar.DATE, +1);
                            if (isDateSame(weather.getDateCalendar(), cal)) {
                                tomorrowWeatherArrayList.add(weather);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        cal.add(Calendar.DATE, -1);
                    }
                    currentWeatherArrayList = listWeather;

                    adapter.setWeatherList(currentWeatherArrayList);
                    adapter.notifyDataSetChanged();
                }


            }
            @Override
            public void onFailure(@NonNull Call<ForecastResult> call, @NonNull Throwable t) {
            }
        });

        return listWeather;

    }

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<WeatherResult> call = retrofitInterface.getWeatherCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");

            call.enqueue(new Callback<WeatherResult>() {
                @Override
                public void onResponse(@NonNull Call<WeatherResult> call, @NonNull Response<WeatherResult> response) {

                    assert response.body() != null;
                    if(response.raw().code() == 200 && response.body().getCityId() != 0){
                        weatherArrayList.clear();
                        tomorrowWeatherArrayList.clear();
                        currentWeather = response.body();

                        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());

                        weatherDbHelper.checkWeatherType(response.body().getWeatherList().get(0));
                        try {
                            if(!weatherDbHelper.checkDates(currentWeather)){
                                weatherDbHelper.addWeather(currentWeather);
                                updateWeatherDay(currentWeather);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        updateWeatherDay(currentWeather);

                        Call<ForecastResult> callForecast = retrofitInterface.getForecastCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");
                        getWeatherForecast(callForecast);
                    } else {
                        Toast.makeText(getApplicationContext(), "The location is not avaible", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<WeatherResult> call, @NonNull Throwable t) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK && data != null){
            String cityName = data.getStringExtra(TestActivity.CITY_DATA);
            int cityId = data.getIntExtra(TestActivity.CITY_DATA2, 0);

            if(!isNetworkAvailable()){
                Log.d("No internet", "no");
                   WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());
                ArrayList<Weather> tempArrayList = weatherDbHelper.getForecastCity(String.valueOf(cityId));

                currentWeatherArrayList.clear();
                tomorrowWeatherArrayList.clear();
                weatherArrayList.clear();

                try {
                    cal = currentWeather.getDateCalendar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < tempArrayList.size(); i++){
                    try {
                        if (isDateSame(tempArrayList.get(i).getDateCalendar(), cal)) {
                            currentWeatherArrayList.add(tempArrayList.get(i));
                        } else {
                            weatherArrayList.add(tempArrayList.get(i));
                        }

                        cal.add(Calendar.DATE, +1);
                        if (isDateSame(tempArrayList.get(i).getDateCalendar(), cal)) {
                            tomorrowWeatherArrayList.add(tempArrayList.get(i));
                        }
                        cal.add(Calendar.DATE, -1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                adapter.setWeatherList(currentWeatherArrayList);
                adapter.notifyDataSetChanged();
            } else {
                GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<WeatherResult> call = retrofitInterface.getWeather(cityName, API_KEY, "Metric");
                Call<ForecastResult> callForecast = retrofitInterface.getForecast(cityName, API_KEY, "Metric");
                getWeatherByCity(call);
                getWeatherForecast(callForecast);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateWeatherDay(WeatherResult newWeather){

        String mainTemp = String.format(Locale.ENGLISH, "%.0f", newWeather.getMain().getTemp()) + "º";
        String pressure = newWeather.getMain().getPressure().toString();
        String windSpeed = newWeather.getWind().getSpeed().toString();
        String minMaxTemp = newWeather.getMain().getTempMin() + "º / " + newWeather.getMain().getTempMax() + "º";
        String feelsLikeText = "Feels like " + newWeather.getMain().getFeelsLike() + "º";
        String humidtyText = newWeather.getMain().getHumidity().toString() + "%";
        String cityText = newWeather.getCityName() + ", " + newWeather.getCity().getCountry();

        tempTextView.setText(mainTemp);
        pressureTextView.setText(pressure);
        windTextView.setText(windSpeed);
        weatherTypeTextView.setText(newWeather.getWeatherList().get(0).getMain());
        minMaxTempTextView.setText(minMaxTemp);
        feelsLikeTextView.setText(feelsLikeText);
        sunriseTextView.setText(newWeather.getCity().getmSunrise());
        sunsetTextView.setText(newWeather.getCity().getmSunset());
        humidityTextView.setText(humidtyText);
        cityTextView.setText(cityText);

        Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + newWeather.getWeatherList().get(0).getIcon() +  "@2x.png").into(weatherIcon);

    }

    private void updateWeatherDay(Weather newWeather){

        String mainTemp = String.format(Locale.ENGLISH, "%.0f", newWeather.getMain().getTemp()) + "º";
        String pressure = newWeather.getMain().getPressure().toString();
        String windSpeed = newWeather.getWind().getSpeed().toString();
        String minMaxTemp = newWeather.getMain().getTempMin() + "º / " + newWeather.getMain().getTempMax() + "º";
        String feelsLikeText = "Feels like " + newWeather.getMain().getFeelsLike() + "º";
        String humidtyText = newWeather.getMain().getHumidity().toString() + "%";
        //String cityText = newWeather.get() + ", " + newWeather.getCity().getCountry();

        tempTextView.setText(mainTemp);
        pressureTextView.setText(pressure);
        windTextView.setText(windSpeed);
        weatherTypeTextView.setText(newWeather.getWeatherList().get(0).getMain());
        minMaxTempTextView.setText(minMaxTemp);
        feelsLikeTextView.setText(feelsLikeText);
        humidityTextView.setText(humidtyText);

        Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + newWeather.getWeatherList().get(0).getIcon() +  "@2x.png").into(weatherIcon);

    }

    public boolean isDateSame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
