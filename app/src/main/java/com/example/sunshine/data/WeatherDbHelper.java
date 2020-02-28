package com.example.sunshine.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sunshine.data.WeatherContract.*;
import com.example.sunshine.data.WeatherTypeContract.*;
import com.example.sunshine.models.TypeWeather;
import com.example.sunshine.models.Weather;

import java.sql.Array;
import java.sql.Wrapper;
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

    private static final String SQL_CREATE_TYPE_WEATHER =
            "CREATE TABLE " + WeatherTypeEntry.TABLE_NAME + " (" +
                    WeatherTypeEntry.COLUMN_ID + " INTEGER, " +
                    WeatherTypeEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    WeatherTypeEntry.COLUMN_NAME + " TEXT, " +
                    WeatherTypeEntry.COLUMN_ICON + " TEXT)";

    private static  final String SQL_DELETE_TYPE_WEATHER =
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_WEATHER);
        db.execSQL(SQL_DELETE_TYPE_WEATHER);
        onCreate(db);
    }

    public long addWeather(Weather weather){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_CITY_ID, weather.getmCityId());
        values.put(WeatherEntry.COLUMN_WEATHER_TYPE, weather.getmWeatherType().getId());
        values.put(WeatherEntry.COLUMN_MAIN_TEMP, weather.getmMainTemp());
        values.put(WeatherEntry.COLUMN_TEMP_MAX, weather.getmMaxTemp());
        values.put(WeatherEntry.COLUMN_TEMP_MIN, weather.getmMinTemp());
        values.put(WeatherEntry.COLUMN_FEELS_LIKE_TEMP, weather.getmFeelsLikeTemp());
        values.put(WeatherEntry.COLUMN_PRESSURE, weather.getmPressure());
        values.put(WeatherEntry.COLUMN_HUMIDITY, weather.getmHumidity());
        values.put(WeatherEntry.COLUMN_DATE, weather.getmDate());
        values.put(WeatherEntry.COLUMN_SUNSET, weather.getmSunset());
        values.put(WeatherEntry.COLUMN_SUNRISE, weather.getmSunrise());

        //Insert the new row, returning the primery key value for the new row
        return db.insert(WeatherEntry.TABLE_NAME, null, values);

    }

    public List<Weather> getAll(String day, String city){

        SQLiteDatabase db = this.getWritableDatabase();

        List<Weather> weatherList = new ArrayList<>();

        String selection = WeatherEntry.COLUMN_CITY_ID + " = ?";
        String[] selectionArgs = { city };

        Cursor cursor = db.query(WeatherEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if(cursor.moveToFirst()){
            while (cursor.moveToNext()){
                Weather newWeather = new Weather();
                newWeather.setmCityId(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_CITY_ID)));
                newWeather.setmMainTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAIN_TEMP)));
                newWeather.setmMaxTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MAX)));
                newWeather.setmMinTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_TEMP_MIN)));
                newWeather.setmHumidity(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY)));
                newWeather.setmFeelsLikeTemp(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_FEELS_LIKE_TEMP)));
                newWeather.setmPressure(cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE)));
                newWeather.setmSunrise(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNRISE)));
                newWeather.setmSunset(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SUNSET)));
                newWeather.setmDate(cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));
                TypeWeather typeWeather = new TypeWeather();
                typeWeather.setId(cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_TYPE)));
                newWeather.setmWeatherType(this.getWeatherType(typeWeather.getId().toString()));
                weatherList.add(newWeather);
            }
        }

        cursor.close();

        return weatherList;

    }

    public void checkWeatherType(TypeWeather typeWeather){

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

    public TypeWeather getWeatherType(String id){

        SQLiteDatabase db = this.getWritableDatabase();

        TypeWeather typeWeather = new TypeWeather();

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
