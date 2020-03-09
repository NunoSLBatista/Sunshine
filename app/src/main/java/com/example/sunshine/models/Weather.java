package com.example.sunshine.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Weather implements Serializable {

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("weather")
    private List<Weather2> weatherList;

    @SerializedName("dt_txt")
    private String date;

    private Integer cityId;

    private Boolean isExpanded = false;


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public List<Weather2> getWeatherList() {
        return weatherList;
    }

    public void setWeatherList(List<Weather2> weatherList) {
        this.weatherList = weatherList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public Calendar timestampDate() {
        long timestampLong = Long.parseLong(date)*1000;
        Date d = new Date(timestampLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    public Calendar getDateCalendar() throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d = dateFormat.parse(this.getDate());
        cal.setTime(d);
        return cal;
    }
    public String getDateFormated() throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = dateFormat.parse(this.getDate());
        cal.setTime(d);
        return String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", cal.get(Calendar.MINUTE));
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
