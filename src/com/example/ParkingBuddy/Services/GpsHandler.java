package com.example.ParkingBuddy.Services;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by javier on 3/18/14.
 */
public class GpsHandler extends Activity implements LocationListener {
    LocationManager locationManager;
    Location lastKnownLocation=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }
    public Location getLocation()
    {
        //insert a null check
        return lastKnownLocation;
    }
    @Override
    public void onLocationChanged(Location location)
    {
        lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
