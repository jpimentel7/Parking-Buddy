package com.example.ParkingBuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.ParkingBuddy.ParkingData.ParkingData;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MyActivity extends Activity
{
    /**
     *
     */
    ParkingData parkingData;
    LocationManager locationManager;
    GoogleMap map;
    final static String TAG="test";
    boolean markerPlaced=false;
    // all the sensors we are testing for
    boolean hasPedometer;
    boolean hasNetwork;
    boolean hasGpsEnable;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //will set everything up
        //checkRequirements();
        configure();
        //check to see if there is a valid saved location and sets a marker if there is
        //for when the user closes the app and restarts
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set when app restarted");
            setMarker();
        }
        //
        Button saveLocation=(Button)findViewById(R.id.save);
        Button clearLocation=(Button)findViewById(R.id.clear);
        saveLocation.setOnClickListener(saveHandler);
        clearLocation.setOnClickListener(clearHandler);
      }

    View.OnClickListener saveHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            //get the current location and sets a marker
           getLocation();
        }
    };
    View.OnClickListener clearHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            //remove everything on the map including the marker
            map.clear();
            markerPlaced=false;
            parkingData.deleteUserLocation();
        }
    };
    private void configure()
    {
        //this method will init everything

        //get the last known location
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //initis the sharedpreferences
        parkingData= new ParkingData(getApplicationContext());
        //centers google maps
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setMyLocationEnabled(true);
    }
    public void checkRequirements(){
        //checks for gps
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false){

        }
        //checks for pedometer
        SensorManager sensorManager;

    }
    public void alertUser(){

    }


    public void getLocation()
    {
        //this methods requests a location update and when the location is aquired
        //it is passed to setMarker and then the updates are canceled
        if(locationManager==null){
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        }
        Log.e(TAG,"location is being requested");

        LocationListener locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setMarker(location);
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
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

    }

    public void setMarker(Location location)
    {
        //will save the location to the shared preferences
        parkingData.saveLocation(location);
        //will add a maker with the users location if there is not a marker in place
        //should check how old the location data is maybe if older then 10 hours delete
        if(parkingData.locationSaved()&&(markerPlaced==false)){
            // will display the maker
            Log.e(TAG,"location maker set");
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()
                    )).title("My Car");
            map.addMarker(marker);
            markerPlaced=true;
        }

    }
    public void setMarker()
    {
        //will add a maker with the users location if there is not a marker in place
        //will get the last saved location
        if(parkingData.locationSaved()&&(markerPlaced==false)){
            // will display the maker
            Log.e(TAG,"location maker set");
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()
                    )).title("My Car");
            map.addMarker(marker);
            markerPlaced=true;
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set Onrestarted");
            setMarker();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set onResume");
            setMarker();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
