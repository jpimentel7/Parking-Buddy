package com.example.ParkingBuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.ParkingBuddy.ParkingData.ParkingData;
import com.example.ParkingBuddy.Services.PressureHandler;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    LocationListener locationListener;
    PressureHandler pressureHandler;
    GoogleMap map;
    final static String TAG="test";
    boolean markerPlaced=false;
    // all the sensors we are testing for
    boolean hasPedometer;
    boolean hasBarometer;
    boolean hasGpsEnable;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //will set everything up
        configure();
        //will only start automode if it is available
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
           setLocation();
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

        //checks to see if the gps is turned on
        if((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false))
        {
            hasGpsEnable=false;
            new AlertDialog.Builder(this)
                    .setTitle("GPS")
                    .setMessage("Must Enable Gps For The App To Work Correctly")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .show();
        }

        //checks for pedometer and pressure sensor
        PackageManager manager = getPackageManager();
        hasPedometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        hasBarometer=manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
        if(hasBarometer==false){
            parkingData.setPedometer(false);
        }
        if(hasBarometer==true){
            pressureHandler=new PressureHandler();
            parkingData.setPressureSensor(false);
        }
    }
    public void setLocation()
    {
        //this methods requests a location update and when the location is aquired
        //it is passed to setMarker and then the updates are canceled
        if(locationManager==null){
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        }
        Log.e(TAG,"location is being requested");

       locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG,"location found");
                setMarker(location);

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    public void setMarker(Location location)
    {
        //will turn off update
        locationManager.removeUpdates(locationListener);
        //will save the location to the shared preferences
        parkingData.saveLocation(location);
        //will add a maker with the users location if there is not a marker in place
        //should check how old the location data is maybe if older then 10 hours delete
        if(markerPlaced==false){
            //sets the marker
            //new to make sure that if the user is not at school that we dont add the floor
            //also that if we could not get the floor or if the barometer is unavilable that we dont
            //set the floor
            String parkingInformation="";
            parkingInformation=parkingData.getCarParkingInformation();
            if(parkingData.hasPressureSensor()){
                parkingInformation=parkingInformation+"/n"+parkingData.getFloor();
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()))
                    // csun information.title(parkingData.getParkingInformation()+parkingData.getFloor())
                    .title(parkingInformation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

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
            String parkingInformation="";
            parkingInformation=parkingData.getCarParkingInformation();
            if(parkingData.hasPressureSensor()){
                parkingInformation=parkingInformation+"/n"+parkingData.getFloor();
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()))
                            // csun information.title(parkingData.getParkingInformation()+parkingData.getFloor())
                    .title("My Car")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

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
        //recenters the map
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setMyLocationEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set onResume");
            setMarker();
        }
        //recenters the map
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setMyLocationEnabled(true);
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
