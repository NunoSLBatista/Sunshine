package com.example.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sunshine.data.WeatherContract.*;
import com.example.sunshine.data.ForecastContract.*;
import com.example.sunshine.data.WeatherTypeContract.*;
import com.example.sunshine.models.City;
import com.example.sunshine.models.Main;
import com.example.sunshine.models.Weather;
import com.example.sunshine.models.Weather2;
import com.example.sunshine.models.WeatherResult;

import java.util.ArrayList;
import java.util.List;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Sunshine.db";

    private static final String SQL_CREATE_WEATHER =
            "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                    WeatherEntry.COLUMN_CITY_ID + " INTEGER, " +
                    WeatherEntry.COLUMN_WEATHER_TYPE + " INTEGER, " +
                    WeatherEntry.COLUMN_MAIN_TEMP + " REAL, " +
                    WeatherEntry.COLUMN_TEMP_MIN + " REAL, " +
                    WeatherEntry.COLUMN_TEMP_MAX + " REAL, " +
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
                    ForecastEntry.COLUMN_TEMP_MAX + " REAL, " +
                    ForecastEntry.COLUMN_PRESSURE + " REAL, " +
                    ForecastEntry.COLUMN_HUMIDITY + " REAL, " +
                    ForecastEntry.COLUMN_FEELS_LIKE_TEMP + " REAL, " +
                    ForecastEntry.COLUMN_DATE + " TEXT)";


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


    public WeatherDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WEATHER);
        db.execSQL(SQL_CREATE_TYPE_WEATHER);
        db.execSQL(SQL_CREATE_FORECAST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WEATHER);
        db.execSQL(SQL_DELETE_TYPE_WEATHER);
        db.execSQL(SQL_DELETE_FORECAST);
        onCreate(db);
    }



    public long addWeather(WeatherResult weather){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_CITY_ID, weather.getCity().getId());
        values.put(WeatherEntry.COLUMN_WEATHER_TYPE, weather.getWeatherList().get(0).getId());
        values.put(WeatherEntry.COLUMN_MAIN_TEMP, weather.getMain().getTemp());
        values.put(WeatherEntry.COLUMN_TEMP_MAX, weather.getMain().getTempMax());
        values.put(WeatherEntry.COLUMN_TEMP_MIN, weather.getMain().getTempMin());
        values.put(WeatherEntry.COLUMN_FEELS_LIKE_TEMP, weather.getMain().getFeelsLike());
        values.put(WeatherEntry.COLUMN_PRESSURE, weather.getMain().getPressure());
        values.put(WeatherEntry.COLUMN_HUMIDITY, weather.getMain().getHumidity());
        //values.put(WeatherEntry.COLUMN_DATE, weather.getMain().g);
        values.put(WeatherEntry.COLUMN_SUNSET, weather.getCity().getSunset());
        values.put(WeatherEntry.COLUMN_SUNRISE, weather.getCity().getSunrise());

        //Insert the new row, returning the primery key value for the new row
        return db.insert(WeatherEntry.TABLE_NAME, null, values);

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
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)));
                newWeather.getCity().setSunrise(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNRISE)));
                newWeather.getCity().setSunrise(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNSET)));
              //  newWeather.setmDate(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));
                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;

    }

    public long addWeather2(Weather weather){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ForecastEntry.COLUMN_WEATHER_TYPE, weather.getWeatherList().get(0).getId());
        values.put(ForecastEntry.COLUMN_MAIN_TEMP, weather.getMain().getTemp());
        values.put(ForecastEntry.COLUMN_TEMP_MAX, weather.getMain().getTempMax());
        values.put(ForecastEntry.COLUMN_TEMP_MIN, weather.getMain().getTempMin());
        values.put(ForecastEntry.COLUMN_FEELS_LIKE_TEMP, weather.getMain().getFeelsLike());
        values.put(ForecastEntry.COLUMN_PRESSURE, weather.getMain().getPressure());
        values.put(ForecastEntry.COLUMN_HUMIDITY, weather.getMain().getHumidity());
        values.put(WeatherEntry.COLUMN_DATE, weather.getDate());

        //Insert the new row, returning the primery key value for the new row
        return db.insert(ForecastEntry.TABLE_NAME, null, values);

    }


    public List<Weather> getAll2(){

        SQLiteDatabase db = this.getWritableDatabase();

        List<Weather> weatherList = new ArrayList<>();


        Cursor cursor = db.query(ForecastEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst()){
            while (cursor.moveToNext()){
                Weather newWeather = new Weather();
                newWeather.setMain( new Main());
                newWeather.setWeatherList(new ArrayList<>());
                newWeather.getMain().setTemp(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_MAIN_TEMP)));
                newWeather.getMain().setTempMax(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MAX)));
                newWeather.getMain().setTempMin(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_TEMP_MIN)));
                newWeather.getMain().setHumidity(cursor.getInt(cursor.getColumnIndex(ForecastEntry.COLUMN_HUMIDITY)));
                newWeather.getMain().setFeelsLike(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.getMain().setPressure(cursor.getDouble(cursor.getColumnIndex(ForecastEntry.COLUMN_PRESSURE)));

                List<Weather2> weather2s = new ArrayList<>();
                weather2s.add(this.getWeatherType(cursor.getString(cursor.getColumnIndex(ForecastEntry.COLUMN_WEATHER_TYPE))));
                newWeather.setWeatherList(weather2s);
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;

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



}
