package com.example.sunshine.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ForecastResult implements Serializable {

    @SerializedName("cod")
    private String cod;

    @SerializedName("list")
    private ArrayList<Weather> listWeather;

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
}
