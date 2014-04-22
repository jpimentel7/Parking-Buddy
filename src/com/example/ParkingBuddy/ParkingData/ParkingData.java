package com.example.ParkingBuddy.ParkingData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


/**
 * Created by javi on 3/8/14.
 */
public class ParkingData
{
    private Context context;
    private boolean userLocation=false;
    private Location school;

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
    protected Location getCarLocation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Location location= new Location(sharedPreferences.getString("car lotName",""));
        location.setLongitude(Double.parseDouble(sharedPreferences.getString("car long","0.0")));
        location.setLatitude(Double.parseDouble(sharedPreferences.getString("car lat","0.0")));
        return location;

    }
    public String getCarParkingInformation()
    {
        //finds the closest parking lot
        Location location[]=getLocationData();
        Location userLocation=getUserLocation();
        Location closestLot=new Location("");
        String result="";
        double shortest=1000;
        for(int i=0;i<28;i++){
            if(location[i].distanceTo(userLocation)<shortest){
                shortest=location[i].distanceTo(userLocation);
                result=location[i].getProvider();
                closestLot=location[i];
            }

        }
        //save the cars location to the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("car location",true);
        editor.putString("car lotName",closestLot.getProvider());
        editor.putString("car long", Double.toString(closestLot.getLongitude()));
        editor.putString("car lat",Double.toString(closestLot.getLatitude()));
        editor.commit();

        return result;
    }
    private Location [] getLocationData(){
        //returns a array of location objects for all the parking structures
        AssetManager assetManager = context.getResources().getAssets();
        Location location[]= new Location[28];
        try {
            Scanner scanner= new Scanner(assetManager.open("parkingData"));

            for(int i=0;i<28;i++)
            {
                Location parkingLot= new Location(scanner.next());
                parkingLot.setLatitude(scanner.nextDouble());
                parkingLot.setLongitude(scanner.nextDouble());
                location[i]=parkingLot;
            }
        }
        catch (IOException e) {
            Toast toast= Toast.makeText(context,"wtf it didnt work",Toast.LENGTH_LONG);
            toast.show();
        }
        return location;
    }

    public String getFloor()
    {
        Location carLocation=getCarLocation();
        Float altitude=getAltitude();
        if(carLocation.getProvider().contains("g3")){
            if((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
        }
        else if(carLocation.getProvider().contains("g8")){
            if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }

        }
        else if(carLocation.getProvider().contains("b3")){
            if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
        }
        else if(carLocation.getProvider().contains("b5")){
            if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
            else if ((altitude>0)&&(altitude<1)){

            }
        }
        //need raw data
        return "";
    }
    public boolean hasPressureSensor(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("hasPressureSensor",false);
    }
    public void setPressureSensor(Boolean hasSensor){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasPressureSensor",true);
        editor.commit();
    }
    public boolean hasPedometer(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("hasPedometer",false);
    }
    public void setPedometer(Boolean hasSensor){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasPedometer",true);
        editor.commit();
    }
    public float getAltitude(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getFloat("altitude", 0);
    }
    public void setAltitude(float altitude){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("altitude", altitude);
        editor.commit();
    }
}
