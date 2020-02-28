package com.example.sunshine.data;

import android.provider.BaseColumns;

public class WeatherTypeContract {

    private WeatherTypeContract (){}


    public static class WeatherTypeEntry implements BaseColumns {

        public static final String TABLE_NAME = "weatherType";
        public static final String COLUMN_ID = "weatherId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ICON = "icon";

    }


}
