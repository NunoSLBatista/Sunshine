package com.example.sunshine.data;

import android.provider.BaseColumns;

public class CityContract {

    private CityContract() {}

    public static class CityEntry implements BaseColumns {

        public static final String TABLE_NAME = "cities";
        public static final String COLUMN_ID = "cityId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_IMAGE = "image";

    }

}
