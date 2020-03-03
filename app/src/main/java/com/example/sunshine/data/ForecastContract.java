package com.example.sunshine.data;

import android.provider.BaseColumns;

public class ForecastContract {

    private ForecastContract(){}

    public static final class ForecastEntry implements BaseColumns {

        public static final String TABLE_NAME = "forecast";
        public static final String COLUMN_WEATHER_ID = "weatherId";
        public static final String COLUMN_WEATHER_TYPE = "weatherType";
        public static final String COLUMN_MAIN_TEMP = "mainTemp";
        public static final String COLUMN_TEMP_MIN = "tempMin";
        public static final String COLUMN_TEMP_MAX = "tempMax";
        public static final String COLUMN_FEELS_LIKE_TEMP= "feelsLikeTemp";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_DATE = "date";

    }
}
