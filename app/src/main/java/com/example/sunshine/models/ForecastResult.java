package com.example.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ForecastResult implements Serializable {

    String daysWeek[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    @SerializedName("cod")
    private String cod;

    @SerializedName("list")
    private ArrayList<Weather> listWeather;

    @SerializedName("city")
    private City city;

    private String date;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public ArrayList<Weather> getListWeather() {
        return listWeather;
    }

    public void setListWeather(ArrayList<Weather> listWeather) {
        this.listWeather = listWeather;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
        return daysWeek[cal.get(Calendar.DAY_OF_WEEK) - 1] + ", " + cal.get(Calendar.DAY_OF_MONTH);
    }


}
