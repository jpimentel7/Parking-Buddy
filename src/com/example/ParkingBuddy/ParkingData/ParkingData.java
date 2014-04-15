package com.example.ParkingBuddy.ParkingData;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by javi on 3/8/14.
 */
public class ParkingData
{
    private Context context;
    private boolean userLocation=false;
    private Location school;
    //Parking lots
    Location g1;
    Location g2;
    Location g3;
    Location g4;
    Location g5;
    Location g6;
    Location g7;
    Location g8;
    Location g9;


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
        editor.putBoolean("location",true);
        editor.putString("long",longitude);
        editor.putString("lat",latitude);
        editor.commit();
    }

    public Location getUserLocation()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Location location= new Location("user location");
        location.setLongitude(Double.parseDouble(sharedPreferences.getString("long","0.0")));
        location.setLatitude(Double.parseDouble(sharedPreferences.getString("lat","0.0")));
        return location;
    }

    public void deleteUserLocation()
    {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("location",false);
        editor.putString("long", "0.0");
        editor.putString("lat","0.0");
        editor.commit();
    }

    public String ParkingInformation(Location location)
    {
        String parkingLocation="";
            if(location.distanceTo(school)>100)
            {
                if(location.distanceTo(g1)<15)
                {
                //user is parked at g1
                    parkingLocation="G1";
                }
                else if(location.distanceTo(g2)<5)
                {
                //user is parked at g2
                    parkingLocation="G2";
                }
            }
            else
            {
                parkingLocation="My Car";
            }
        //will save the data
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("parking data",parkingLocation);
        editor.putBoolean("parking data set",true);
        editor.commit();
        ///
        return parkingLocation;
    }
    public String getParkingInformation()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String parkingInfo="";
        if(sharedPreferences.getBoolean("parking data set",false))
        {
            parkingInfo=sharedPreferences.getString("parking data","");
        }
        return parkingInfo;
    }
    public boolean hasParkingData(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("parking data set",false);
    }
    public boolean hasAltitude(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("altitude",false);
    }
    public void setAltitude(int pressure)
    {
        //set the altitude
        int altitude=0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("altitude",altitude);
        editor.putBoolean("altitude set",true);
        editor.commit();
    }
    public int getAltitude()
    {
        int altitude=0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getBoolean("altitude set",false))
        {
        altitude =sharedPreferences.getInt("altitude",-1);

        }
        return altitude;
    }
    public int getFloor()
    {
        String parkingData;
        int altitude=-1;
        if((hasAltitude())&&(hasParkingData()))
        {
            parkingData=getParkingInformation();
            if(parkingData=="g1")
            {
                if((altitude<1)&&(altitude>2))
                {
                    //the user is parked on the first floor
                    return 1;
                }
            }
            else if(parkingData=="g3")
            {
                if((altitude<1)&&(altitude>2))
                {
                    //the user is parked on the first floor
                    return 1;
                }
            }
        }
        return altitude;
    }
    public boolean isStaff()
    {
        boolean staffLot=false;
        if(hasParkingData()){
            if(getParkingInformation()=="g1")
            {
                return true;
            }
        }
        return staffLot;
    }
    public boolean atSchool(Location location)
    {
        boolean atSchool=false;
        if(location.distanceTo(school)>100){
            atSchool=true;
        }
        return atSchool;
    }
    public boolean alertUser()
    {
        return false;
    }
    public void setAlertUser()
    {

    }

}
