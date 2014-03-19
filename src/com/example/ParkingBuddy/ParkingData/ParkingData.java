package com.example.ParkingBuddy.ParkingData;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by javi on 3/8/14.
 */
public class ParkingData {

    private Context context;
    private String firstLine;
    private boolean userLocation=false;
    private SharedPreferences prefs;


    public ParkingData(Context con)
    {
        context=con;
    }

    public boolean locationSaved()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        userLocation= sharedPreferences.getBoolean("location",false);
        return userLocation;
    }

    public void saveLocation(Location location)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String longitude = Double.toString(location.getLongitude());
        String latitude =Double.toString(location.getLatitude());
        editor.putBoolean("Location",true);
        editor.putString("long",longitude);
        editor.putString("lat",latitude);
        editor.commit();


    }

    public Location getUserLocation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Location location= new Location("user location");
        location.setLongitude(Double.parseDouble(sharedPreferences.getString("long","0.0")));
        location.setLatitude(Double.parseDouble(sharedPreferences.getString("lat","0.0")));
        return location;
    }

    public void setUserLocation(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Location",false);
        editor.commit();

    }



}
