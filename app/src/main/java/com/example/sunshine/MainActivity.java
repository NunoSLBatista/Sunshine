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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.sunshine.adapters.WeatherForecastAdapter;
import com.example.sunshine.data.WeatherDbHelper;
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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY = "9d7443ba0f8ff7df7afafd12f4006dca";

    WeatherResult currentWeather;
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
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

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

        GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
        List<WeatherResult> weatherList = db.getAll();

        Log.d("size1", String.valueOf(weatherList.size()));

        if(weatherList.size() > 0){

            WeatherResult weather = weatherList.get(weatherList.size() - 1);

            /*
            currentWeatherArrayList = (ArrayList<Weather>) db.getAll2();
            adapter.setWeatherList(currentWeatherArrayList);
            adapter.notifyDataSetChanged();
*/
            updateWeatherDay(weather);

        }
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

            Call<WeatherResult> call = retrofitInterface.getWeather(cityText, API_KEY, "Metric");
            getWeatherByCity(call);

            Call<ForecastResult> callForecast = retrofitInterface.getForecast(cityText, API_KEY, "Metric");

            adapter.setWeatherList(getWeatherForecast(callForecast));
            adapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWeatherByCity(Call<WeatherResult> call){

        call.enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {

                currentWeather = response.body();
                WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());

                weatherDbHelper.checkWeatherType(response.body().getWeatherList().get(0));
                weatherDbHelper.addWeather(currentWeather);
                updateWeatherDay(currentWeather);

            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Check your Internet connection.", Toast.LENGTH_SHORT).show();

                WeatherDbHelper db = new WeatherDbHelper(getApplicationContext());
                List<WeatherResult> weatherList = db.getAll();

                WeatherResult weather = weatherList.get(0);

                String mainTemp = String.format("%.0f", weather.getMain().getTemp());
                tempTextView.setText(mainTemp + "º");
               // weatherTypeTextView.setText(weather.ge().getMain());
                minMaxTempTextView.setText(weather.getMain().getTempMin() + "º / " + weather.getMain().getTempMax() + "º");
                feelsLikeTextView.setText("Feels like " + weather.getMain().getFeelsLike() + "º");


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

    public ArrayList<Weather> getWeatherForecast(Call<ForecastResult> call){

        weatherArrayList.clear();
        tomorrowWeatherArrayList.clear();

        ArrayList<Weather> listWeather = new ArrayList<>();

        call.enqueue(new Callback<ForecastResult>() {
            @Override
            public void onResponse(Call<ForecastResult> call, Response<ForecastResult> response) {


                if(response.raw().code() != 404){

                    for(int i = 0; i < response.body().getListWeather().size(); i++) {

                        Weather weather = response.body().getListWeather().get(i);
                        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());
                        weatherDbHelper.addWeather2(weather);

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

                    }


                    currentWeatherArrayList = listWeather;

                    adapter.setWeatherList(currentWeatherArrayList);
                    adapter.notifyDataSetChanged();


            }
            @Override
            public void onFailure(Call<ForecastResult> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });

        return listWeather;

    }

    private boolean isDateSame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    private void updateWeatherDay(WeatherResult newWeather){

        String mainTemp = String.format("%.0f", newWeather.getMain().getTemp());

        tempTextView.setText(mainTemp + "º");
        pressureTextView.setText(newWeather.getMain().getPressure().toString());
        //windTextView.setText(newWeather.getWind().getSpeed().toString());
        weatherTypeTextView.setText(newWeather.getWeatherList().get(0).getMain());
        minMaxTempTextView.setText(newWeather.getMain().getTempMin() + "º / " + newWeather.getMain().getTempMax() + "º");
        feelsLikeTextView.setText("Feels like " + newWeather.getMain().getFeelsLike() + "º");
        sunriseTextView.setText(newWeather.getCity().getmSunrise());
        sunsetTextView.setText(newWeather.getCity().getmSunset());
        humidityTextView.setText(newWeather.getMain().getHumidity().toString() + "%");

        Picasso.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" + newWeather.getWeatherList().get(0).getIcon() +  "@2x.png").into(weatherIcon);

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

    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            weatherArrayList.clear();
            tomorrowWeatherArrayList.clear();

             double longitude = location.getLongitude();
             double latitude = location.getLatitude();

            GetDataService retrofitInterface = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<WeatherResult> call = retrofitInterface.getWeatherCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");

            call.enqueue(new Callback<WeatherResult>() {
                @Override
                public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {

                        currentWeather = response.body();

                        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(getApplicationContext());

                        weatherDbHelper.checkWeatherType(response.body().getWeatherList().get(0));
                        weatherDbHelper.addWeather(currentWeather);
                        updateWeatherDay(currentWeather);

                        Call<ForecastResult> callForecast = retrofitInterface.getForecastCoord(String.valueOf(latitude), String.valueOf(longitude), API_KEY, "Metric");
                        getWeatherForecast(callForecast);

                }

                @Override
                public void onFailure(Call<WeatherResult> call, Throwable t) {

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
