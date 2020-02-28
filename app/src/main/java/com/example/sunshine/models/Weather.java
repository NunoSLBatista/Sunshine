package com.example.sunshine.models;

import com.google.gson.annotations.SerializedName;

public class Weather {

    private Integer mCityId;
    private TypeWeather mWeatherType;
    private Double mMainTemp;
    private Double mMinTemp;
    private Double mMaxTemp;
    private Double mPressure;
    private Double mHumidity;
    private String mDate;
    private String mSunrise;
    private String mSunset;
    private Double mFeelsLikeTemp;

    public Weather(Integer mCityId, TypeWeather mWeatherType, Double mMainTemp, Double mMinTemp, Double mMaxTemp, Double mPressure, Double mHumidity, String mDate, String mSunrise, String mSunset) {
        this.mCityId = mCityId;
        this.mWeatherType = mWeatherType;
        this.mMainTemp = mMainTemp;
        this.mMinTemp = mMinTemp;
        this.mMaxTemp = mMaxTemp;
        this.mPressure = mPressure;
        this.mHumidity = mHumidity;
        this.mDate = mDate;
        this.mSunrise = mSunrise;
        this.mSunset = mSunset;
    }

    public Weather(){

    }

    public Double getmFeelsLikeTemp() {
        return mFeelsLikeTemp;
    }

    public void setmFeelsLikeTemp(Double mFeelsLikeTemp) {
        this.mFeelsLikeTemp = mFeelsLikeTemp;
    }

    public Integer getmCityId() {
        return mCityId;
    }

    public void setmCityId(Integer mCityId) {
        this.mCityId = mCityId;
    }

    public TypeWeather getmWeatherType() {
        return mWeatherType;
    }

    public void setmWeatherType(TypeWeather mWeatherType) {
        this.mWeatherType = mWeatherType;
    }

    public Double getmMainTemp() {
        return mMainTemp;
    }

    public void setmMainTemp(Double mMainTemp) {
        this.mMainTemp = mMainTemp;
    }

    public Double getmMinTemp() {
        return mMinTemp;
    }

    public void setmMinTemp(Double mMinTemp) {
        this.mMinTemp = mMinTemp;
    }

    public Double getmMaxTemp() {
        return mMaxTemp;
    }

    public void setmMaxTemp(Double mMaxTemp) {
        this.mMaxTemp = mMaxTemp;
    }

    public Double getmPressure() {
        return mPressure;
    }

    public void setmPressure(Double mPressure) {
        this.mPressure = mPressure;
    }

    public Double getmHumidity() {
        return mHumidity;
    }

    public void setmHumidity(Double mHumidity) {
        this.mHumidity = mHumidity;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmSunrise() {
        return mSunrise;
    }

    public void setmSunrise(String mSunrise) {
        this.mSunrise = mSunrise;
    }

    public String getmSunset() {
        return mSunset;
    }

    public void setmSunset(String mSunset) {
        this.mSunset = mSunset;
    }

}
