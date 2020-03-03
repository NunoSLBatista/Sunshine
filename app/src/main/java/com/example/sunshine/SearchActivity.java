package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
    // Get the string array
        String[] countries = getResources().getStringArray(R.array.countries_array);
    // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textView.setAdapter(adapter);

    }



}
