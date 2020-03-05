package com.example.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sunshine.data.WeatherContract.*;
import com.example.sunshine.data.ForecastContract.*;
import com.example.sunshine.data.WeatherTypeContract.*;
import com.example.sunshine.data.CityContract.*;
import com.example.sunshine.models.City;
import com.example.sunshine.models.Main;
import com.example.sunshine.models.Weather;
import com.example.sunshine.models.Weather2;
import com.example.sunshine.models.WeatherResult;
import com.example.sunshine.models.Wind;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Sunshine.db";

    private static final String SQL_CREATE_WEATHER =
            "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                    WeatherEntry.COLUMN_CITY_ID + " INTEGER, " +
                    WeatherEntry.COLUMN_WEATHER_TYPE + " INTEGER, " +
                    WeatherEntry.COLUMN_MAIN_TEMP + " REAL, " +
                    WeatherEntry.COLUMN_TEMP_MIN + " REAL, " +
                    WeatherEntry.COLUMN_TEMP_MAX + " REAL, " +
                    WeatherEntry.COLUMN_WIND_SPEED + " REAL, " +
                    WeatherEntry.COLUMN_PRESSURE + " REAL, " +
                    WeatherEntry.COLUMN_HUMIDITY + " REAL, " +
                    WeatherEntry.COLUMN_FEELS_LIKE_TEMP + " REAL, " +
                    WeatherEntry.COLUMN_DATE + " TEXT, " +
                    WeatherEntry.COLUMN_SUNSET + " TEXT, " +
                    WeatherEntry.COLUMN_SUNRISE + " TEXT)";

    private static final String SQL_CREATE_FORECAST =
            "CREATE TABLE " + ForecastEntry.TABLE_NAME + " (" +
                    ForecastEntry.COLUMN_WEATHER_ID + " INTEGER, " +
                    ForecastEntry.COLUMN_WEATHER_TYPE + " INTEGER, " +
                    ForecastEntry.COLUMN_MAIN_TEMP + " REAL, " +
                    ForecastEntry.COLUMN_TEMP_MIN + " REAL, " +
                    ForecastEntry.COUMN_CITY_ID + " INTEGER, " +
                    ForecastEntry.COLUMN_WIND_SPEED + " REAL, " +
                    ForecastEntry.COLUMN_TEMP_MAX + " REAL, " +
                    ForecastEntry.COLUMN_PRESSURE + " REAL, " +
                    ForecastEntry.COLUMN_HUMIDITY + " REAL, " +
                    ForecastEntry.COLUMN_FEELS_LIKE_TEMP + " REAL, " +
                    ForecastEntry.COLUMN_DATE + " TEXT)";

    private static final String SQL_CREATE_CITY =
            "CREATE TABLE " + CityEntry.TABLE_NAME + " (" +
                    CityEntry.COLUMN_ID + " INTEGER, " +
                    CityEntry.COLUMN_NAME + " TEXT, " +
                    CityEntry.COLUMN_COUNTRY + " TEXT, " +
                    CityEntry.COLUMN_IMAGE + " BLOB); ";

    private static final String SQL_CREATE_TYPE_WEATHER =
            "CREATE TABLE " + WeatherTypeEntry.TABLE_NAME + " (" +
                    WeatherTypeEntry.COLUMN_ID + " INTEGER, " +
                    WeatherTypeEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    WeatherTypeEntry.COLUMN_NAME + " TEXT, " +
                    WeatherTypeEntry.COLUMN_ICON + " TEXT)";

    private static  final String SQL_DELETE_TYPE_WEATHER =
            "DROP TABLE IF EXISTS " + ForecastEntry.TABLE_NAME;

    private static  final String SQL_DELETE_FORECAST =
            "DROP TABLE IF EXISTS " + WeatherTypeEntry.TABLE_NAME;

    private static  final String SQL_DELETE_WEATHER =
            "DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME;

    private static  final String SQL_DELETE_CITY =
            "DROP TABLE IF EXISTS " + CityEntry.TABLE_NAME;


    public WeatherDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WEATHER);
        db.execSQL(SQL_CREATE_TYPE_WEATHER);
        db.execSQL(SQL_CREATE_FORECAST);
        db.execSQL(SQL_CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WEATHER);
        db.execSQL(SQL_DELETE_TYPE_WEATHER);
        db.execSQL(SQL_DELETE_FORECAST);
        db.execSQL(SQL_DELETE_CITY);
        onCreate(db);
    }

    public void addWeather(WeatherResult weather){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_CITY_ID, weather.getCityId());
        values.put(WeatherEntry.COLUMN_WEATHER_TYPE, weather.getWeatherList().get(0).getId());
        values.put(WeatherEntry.COLUMN_MAIN_TEMP, weather.getMain().getTemp());
        values.put(WeatherEntry.COLUMN_WIND_SPEED, weather.getWind().getSpeed());
        values.put(WeatherEntry.COLUMN_TEMP_MAX, weather.getMain().getTempMax());
        values.put(WeatherEntry.COLUMN_TEMP_MIN, weather.getMain().getTempMin());
        values.put(WeatherEntry.COLUMN_FEELS_LIKE_TEMP, weather.getMain().getFeelsLike());
        values.put(WeatherEntry.COLUMN_PRESSURE, weather.getMain().getPressure());
        values.put(WeatherEntry.COLUMN_HUMIDITY, weather.getMain().getHumidity());
        values.put(WeatherEntry.COLUMN_DATE, weather.getDate());
        values.put(WeatherEntry.COLUMN_SUNSET, weather.getCity().getSunset());
        values.put(WeatherEntry.COLUMN_SUNRISE, weather.getCity().getSunrise());

        //Insert the new row, returning the primery key value for the new row
       db.insert(WeatherEntry.TABLE_NAME, null, values);

    }


    public List<WeatherResult> getAll(){

        SQLiteDatabase db = this.getWritableDatabase();

        List<WeatherResult> weatherList = new ArrayList<>();


        Cursor cursor = db.query(WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst()){
            while (cursor.moveToNext()){
                WeatherResult newWeather = new WeatherResult();
                newWeather.setCity(new City());
                newWeather.getCity().setId(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_CITY_ID)));
                newWeather.setMain( new Main());
                newWeather.setWeatherList(new ArrayList<>());
                newWeather.setWind(new Wind());
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN)));
                newWeather.getWind().setSpeed(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)));
                newWeather.getCity().setSunrise(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNRISE)));
                newWeather.getCity().setSunset(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNSET)));
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));
                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;

    }


    public City getCityId(String name){

        SQLiteDatabase db = this.getWritableDatabase();

        City city = new City();
        String selection = CityEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.query(CityEntry.TABLE_NAME, null, selection, selectionArgs, null, null,null, null);

        if(cursor.moveToFirst()){
            city.setId(cursor.getInt(cursor.getColumnIndex(CityEntry.COLUMN_ID)));
            city.setName(cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_NAME)));
            city.setCountry(cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_COUNTRY)));
        }

        cursor.close();
        return city;
    }

    public long addWeather2(Weather weather){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ForecastEntry.COLUMN_WEATHER_TYPE, weather.getWeatherList().get(0).getId());
        values.put(ForecastEntry.COLUMN_MAIN_TEMP, weather.getMain().getTemp());
        values.put(ForecastEntry.COUMN_CITY_ID, weather.getCityId());
        values.put(ForecastEntry.COLUMN_TEMP_MAX, weather.getMain().getTempMax());
        values.put(ForecastEntry.COLUMN_WIND_SPEED, weather.getWind().getSpeed());
        values.put(ForecastEntry.COLUMN_TEMP_MIN, weather.getMain().getTempMin());
        values.put(ForecastEntry.COLUMN_FEELS_LIKE_TEMP, weather.getMain().getFeelsLike());
        values.put(ForecastEntry.COLUMN_PRESSURE, weather.getMain().getPressure());
        values.put(ForecastEntry.COLUMN_HUMIDITY, weather.getMain().getHumidity());
        values.put(WeatherEntry.COLUMN_DATE, weather.getDate());

        //Insert the new row, returning the primery key value for the new row
        return db.insert(ForecastEntry.TABLE_NAME, null, values);

    }


    public List<Weather> getAll2(WeatherResult weatherResult) {

        SQLiteDatabase db = this.getWritableDatabase();

        List<Weather> weatherList = new ArrayList<>();

        String selection = ForecastEntry.COUMN_CITY_ID + " = ?";
        String[] args = {weatherResult.getCity().getId().toString()};

        Cursor cursor = db.query(ForecastEntry.TABLE_NAME,
                null,
                selection,
                args,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Weather newWeather = new Weather();
                newWeather.setMain(new Main());
                newWeather.setWeatherList(new ArrayList<>());
                newWeather.setWind(new Wind());
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MIN)));
                newWeather.getWind().setSpeed(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_WIND_SPEED)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(ForecastEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_PRESSURE)));
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_DATE)));

                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;

    }

    public Boolean checkDates(WeatherResult weatherResult) throws ParseException {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = WeatherEntry.COLUMN_CITY_ID + " = ?";
        String[] selectionArgs = {weatherResult.getCityId().toString()};

        Cursor cursor = db.query(WeatherEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Weather newWeather = new Weather();
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));

                if(isDaySame(newWeather.getDateCalendar(), weatherResult.getDateCalendar())){
                    return true;
                }

            }
        }

        cursor.close();
        return false;

    }

    public ArrayList<WeatherResult> getWeatherCity(String cityId){

        ArrayList<WeatherResult> weatherList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = WeatherEntry.COLUMN_CITY_ID + " = ?";
        String[] selectionArgs = {cityId};

        Cursor cursor = db.query(WeatherEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){
            do {
                WeatherResult newWeather = new WeatherResult();
                newWeather.setCity(new City());
                newWeather.getCity().setId(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_CITY_ID)));
                newWeather.setMain( new Main());
                newWeather.setWeatherList(new ArrayList<>());
                newWeather.setWind(new Wind());
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN)));
                newWeather.getWind().setSpeed(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)));
                newWeather.getCity().setSunrise(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNRISE)));
                newWeather.getCity().setSunset(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNSET)));
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));
                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            } while(cursor.moveToNext());
        }

        cursor.close();

        return weatherList;
    }

    public ArrayList<Weather> getForecastCity(String cityId){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Weather> weatherList = new ArrayList<>();

        String selection = ForecastEntry.COUMN_CITY_ID + " = ?";
        String[] args = {cityId};

        Cursor cursor = db.query(ForecastEntry.TABLE_NAME,
                null,
                selection,
                args,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Weather newWeather = new Weather();
                newWeather.setMain(new Main());
                newWeather.setWeatherList(new ArrayList<>());
                newWeather.setWind(new Wind());
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MIN)));
                newWeather.getWind().setSpeed(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_WIND_SPEED)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(ForecastEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_PRESSURE)));
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_DATE)));

                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;
    }

    public Boolean checkDates(Weather weather) throws ParseException {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ForecastEntry.COUMN_CITY_ID + " = ?";
        String[] selectionArgs = {weather.getCityId().toString()};

        Cursor cursor = db.query(ForecastEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                Weather newWeather = new Weather();
                newWeather.setDate(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_DATE)));

                if(isDateSame(newWeather.getDateCalendar(), weather.getDateCalendar())){
                    return true;
                }

            }
        }

        cursor.close();
        return false;

    }

    public void checkCity(WeatherResult weatherResult){

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = CityEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {weatherResult.getCityId().toString()};

        Cursor cursor = db.query(CityEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(!cursor.moveToFirst()){
            ContentValues values = new ContentValues();

            values.put(CityEntry.COLUMN_ID, weatherResult.getCityId());
            values.put(CityEntry.COLUMN_NAME, weatherResult.getCityName());
            values.put(CityEntry.COLUMN_COUNTRY, weatherResult.getCity().getCountry());

            db.insert(CityEntry.TABLE_NAME, null, values);
        }
        cursor.close();

    }

    public City getCity(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        City city = new City();

        String selection = CityEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(CityEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null, null);

        if(cursor.moveToFirst()){
            city.setName(cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_NAME)));
            city.setCountry(cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_COUNTRY)));
            city.setId(cursor.getInt(cursor.getColumnIndex(CityEntry.COLUMN_ID)));
        }
        cursor.close();

        return city;
    }

    public ArrayList<City> getAllCities(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<City> cityArrayList = new ArrayList<>();

        Cursor cursor = db.query(CityEntry.TABLE_NAME, null, null, null, null, null, null, null);

        if(cursor.moveToFirst()){
            do {
                City newCity = new City();
                Integer cityId = cursor.getInt(cursor.getColumnIndex(CityEntry.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_NAME));
                String country = cursor.getString(cursor.getColumnIndex(CityEntry.COLUMN_COUNTRY));

                newCity.setId(cityId);
                newCity.setName(name);
                newCity.setCountry(country);

                cityArrayList.add(newCity);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return cityArrayList;
    }

    public void checkWeatherType(Weather2 typeWeather){

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = WeatherTypeEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {typeWeather.getId().toString()};

        Cursor cursor = db.query(WeatherTypeEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(!cursor.moveToFirst()){

            ContentValues values = new ContentValues();

            values.put(WeatherTypeEntry.COLUMN_ID, typeWeather.getId());
            values.put(WeatherTypeEntry.COLUMN_NAME, typeWeather.getMain());
            values.put(WeatherTypeEntry.COLUMN_DESCRIPTION, typeWeather.getDescription());
            values.put(WeatherTypeEntry.COLUMN_ICON, typeWeather.getIcon());

            db.insert(WeatherTypeEntry.TABLE_NAME, null, values);

        }

        cursor.close();

    }

    public Weather2 getWeatherType(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        Weather2 typeWeather = new Weather2();

        String selection = WeatherTypeEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(WeatherTypeEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){

           String name = cursor.getString(cursor.getColumnIndex(WeatherTypeEntry.COLUMN_NAME));
           String description = cursor.getString(cursor.getColumnIndex(WeatherTypeEntry.COLUMN_DESCRIPTION));
           String icon = cursor.getString(cursor.getColumnIndex(WeatherTypeEntry.COLUMN_ICON));

           typeWeather.setId(Integer.valueOf(id));
           typeWeather.setDescription(description);
           typeWeather.setIcon(icon);
           typeWeather.setMain(name);


        }

        return typeWeather;

    }

    public boolean isDaySame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    public boolean isDateSame(Calendar c1, Calendar c2) {
        return (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) && c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY));
    }





}
