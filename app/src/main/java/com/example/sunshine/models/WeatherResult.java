package com.example.sunshine.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeatherResult implements Serializable {

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("sys")
    private City city;

    @SerializedName("weather")
    private List<Weather2> weatherList;

    @SerializedName("cod")
    private Integer cod;

    @SerializedName("message")
    private String message;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Weather2> getWeatherList() {
        return weatherList;
    }

    public void setWeatherList(List<Weather2> weatherList) {
        this.weatherList = weatherList;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
