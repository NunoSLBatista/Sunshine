package com.example.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class City implements Serializable {

    @SerializedName("id")
    private Integer id;

    @SerializedName("sunrise")
    private String sunrise;

    @SerializedName("sunset")
    private String sunset;

    @SerializedName("country")
    private String country;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getmSunrise() {
        long timestampLong = Long.parseLong(sunrise)*1000;
        Date d = new Date(timestampLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE));
    }

    public String getmSunset() {
        long timestampLong = Long.parseLong(sunrise)*1000;
        Date d = new Date(timestampLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return String.format("%02d", c.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", c.get(Calendar.MINUTE));
    }

}
