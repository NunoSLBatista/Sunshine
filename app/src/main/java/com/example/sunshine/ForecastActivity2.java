package com.example.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.sunshine.adapters.Forecast2Adapter;
import com.example.sunshine.adapters.RotationPageTransformer;
import com.example.sunshine.models.ForecastResult;
import com.example.sunshine.models.Weather;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class ForecastActivity2 extends AppCompatActivity {

    private ViewPager2 viewPager;
    TabLayout tabLayout;

    private FragmentStateAdapter pagerAdapter;
    ArrayList<Weather> weatherArrayList = new ArrayList<Weather>();
    ArrayList<ForecastResult> dayWeathers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast2);


        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        weatherArrayList = (ArrayList<Weather>) getIntent().getSerializableExtra("listForecast");

       // weatherArrayList = onNewIntent(getIntent().getSerializableExtra("listForecast"));

        for(int i = 0; i < weatherArrayList.size(); i++){

            if(dayWeathers.size() > 0){

                try {

                    if(checkIfDayExists(weatherArrayList.get(i)) == -1){
                        ForecastResult forecastResult = new ForecastResult();
                        forecastResult.setListWeather(new ArrayList<Weather>());
                        forecastResult.getListWeather().add(weatherArrayList.get(i));
                        forecastResult.setDate(weatherArrayList.get(i).getDate());
                        dayWeathers.add(forecastResult);
                    } else {
                        dayWeathers.get(checkIfDayExists(weatherArrayList.get(i))).getListWeather().add(weatherArrayList.get(i));
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                ForecastResult forecastResult = new ForecastResult();
                forecastResult.setListWeather(new ArrayList<Weather>());
                forecastResult.getListWeather().add(weatherArrayList.get(i));
                forecastResult.setDate(weatherArrayList.get(i).getDate());
                dayWeathers.add(forecastResult);
            }

        }



        pagerAdapter = new Forecast2Adapter(this, dayWeathers);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(new RotationPageTransformer());
        Calendar cal = Calendar.getInstance();
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add(Calendar.DATE, +1);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.OnConfigureTabCallback() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                try {

                    if(isDateSame(dayWeathers.get(position).getDateCalendar(), cal)){
                        tab.setText("Today");
                    } else if(isDateSame(dayWeathers.get(position).getDateCalendar(), calTomorrow)){
                        tab.setText("Tomorrow");
                    } else {
                        tab.setText(dayWeathers.get(position).getDateFormated());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        tabLayoutMediator.attach();

    }

    public int checkIfDayExists(Weather weather) throws ParseException {

        for(int j = 0; j < dayWeathers.size(); j++){
                if(isDateSame(dayWeathers.get(j).getDateCalendar(), weather.getDateCalendar())){
                    return j;
                }
        }

        return -1;

    }

    public boolean isDateSame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

}
