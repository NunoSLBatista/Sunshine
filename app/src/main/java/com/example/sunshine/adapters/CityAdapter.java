package com.example.sunshine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.R;
import com.example.sunshine.models.City;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder> {

    // List to store all the contact details
    private ArrayList<City> cityArrayList;
    private Context mContext;
    private CityAdapter.ClickListener myAdapterListener;


    // Counstructor for the Class
    public CityAdapter(ArrayList<City> cityArrayList, Context context, CityAdapter.ClickListener listener) {
        this.cityArrayList = cityArrayList;
        this.myAdapterListener = listener;
        this.mContext = context;
    }

    public interface ClickListener {
        void onDayClick(Context context, City city);
    }

    // This method creates views for the RecyclerView by inflating the layout
    // Into the viewHolders which helps to display the items in the RecyclerView
    @Override
    public CityAdapter.CityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.city_item, parent, false);
        return new CityAdapter.CityHolder(view);
    }

    @Override
    public int getItemCount() {
        return cityArrayList == null ? 0 : cityArrayList.size();
    }

    // This method is called when binding the data to the views being created in RecyclerView
    @Override
    public void onBindViewHolder(@NonNull CityAdapter.CityHolder holder, final int position) {
        final City city = cityArrayList.get(position);

        String cityText = city.getName() + ", " + city.getCountry();
        holder.nameCity.setText(cityText);
        holder.imageCity.setImageResource(R.drawable.porto_main);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapterListener.onDayClick(mContext, city);
            }
        });

    }

    // This is your ViewHolder class that helps to populate data to the view
    public class CityHolder extends RecyclerView.ViewHolder {

        private TextView nameCity;
        //private TextView nameCountry;
        private ImageView imageCity;

        public CityHolder(View itemView) {
            super(itemView);

            nameCity = itemView.findViewById(R.id.titleTextView);
           // nameCountry = itemView.findViewById(R.id.maxTemp);
            imageCity = itemView.findViewById(R.id.cityImageView);


        }
    }
}
