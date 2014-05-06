package com.example.ParkingBuddy.ParkingData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Location;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.util.Scanner;


/**
 * Created by javi on 3/8/14.
 */
public class ParkingData
{
    private Context context;
    private boolean userLocation=false;
    private Location school;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     *
     * @param con
     */
    public ParkingData(Context con)
    {
        context=con;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    /**
     *
     * @return If the user location has been saved.
     */
    public boolean locationSaved()
    {
        userLocation= sharedPreferences.getBoolean("location",false);
        return userLocation;
    }

    /**
     * Will save the user location in the database.
     * @param location
     */
    public void saveLocation(Location location)
    {
        String longitude = Double.toString(location.getLongitude());
        String latitude =Double.toString(location.getLatitude());
        editor.putBoolean("location",true);
        editor.putString("long",longitude);
        editor.putString("lat",latitude);
        editor.commit();
    }

    /**
     *
     * @return The User Location
     */
    public Location getUserLocation()
    {
        Location location= new Location("user location");
        location.setLongitude(Double.parseDouble(sharedPreferences.getString("long","0.0")));
        location.setLatitude(Double.parseDouble(sharedPreferences.getString("lat","0.0")));
        return location;
    }

    /**
     * Will remove the users location from the database.
     */
    public void deleteUserLocation()
    {
        editor.putBoolean("location",false);
        editor.putString("long", "0.0");
        editor.putString("lat","0.0");
        editor.commit();
    }

    /**
     *
     * @return The closest parking lot to the user
     */
    public String getCarParkingInformation()
    {
        Location location[]=getLocationData();
        Location userLocation=getUserLocation();
        String result="";
        double shortest=1000;
        for(int i=0;i<25;i++){
            if(location[i].distanceTo(userLocation)<shortest){
                shortest=location[i].distanceTo(userLocation);
                result=location[i].getProvider();
            }
        }
           return result;
    }

    /**
     * Read from a file in the assets folder and returns a array of locations objects.
     * @return A array of parking lot data
     */
    private Location [] getLocationData(){
        //returns a array of location objects for all the parking structures
        AssetManager assetManager = context.getResources().getAssets();
        Location location[]= new Location[25];
        try {
            Scanner scanner= new Scanner(assetManager.open("parkingData"));

            for(int i=0;i<25;i++)
            {
                Location parkingLot= new Location(scanner.next());
                parkingLot.setLatitude(scanner.nextDouble());
                parkingLot.setLongitude(scanner.nextDouble());
                location[i]=parkingLot;
            }
        }
        catch (IOException e) {
        }
        return location;
    }

    /**
     *
     * @return The floor the user is parked on.
     */
    public String getFloor()
    {
        if(hasAltitude()){
            Location carLocation=getUserLocation();
            Float altitude=getAltitude();
            if(carLocation.getProvider().contains("g3s")){
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
        }
        //need raw data
        return "";
    }

    /**
     *
     * @return Altitude
     */
    public float getAltitude(){
        return sharedPreferences.getFloat("altitude", 0);
    }

    /**
     *
     * @return If the altitude is stored
     */
    public boolean hasAltitude(){
        return sharedPreferences.getBoolean("has altitude", false);
    }

    /**
     * Save the altitude in the database
     * @param altitude
     */
    public void setAltitude(float altitude){
        editor.putFloat("altitude", altitude);
        editor.putBoolean("has altitude",true);
        editor.commit();
    }
    /**
     *
     */
    public void deleteAltitude()
    {
        editor.putBoolean("has altitude",false);
        editor.commit();

    }
    public void markerPlaced(){
        editor.putBoolean("marker",true);
        editor.commit();
    }
    public void makerDeleted()
    {
        editor.putBoolean("marker",false);
        editor.commit();
    }

    public boolean hasMarker()
    {
        return sharedPreferences.getBoolean("marker",false);
    }


}
