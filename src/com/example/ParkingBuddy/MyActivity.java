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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    MenuItem menuItem;
    // all the sensors we are testing for
    boolean requestingLocation=false;
    boolean hasGpsEnable;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //will only start automode if it is available
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        //initis the sharedpreferences
        parkingData= new ParkingData(getApplicationContext());
        ///have to check to ssee if location is null
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //centers google maps
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        if(userLocation!=null)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else
        {
            updateCamera();
        }

        //checks to see if the gps is turned on
        if((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false)&&(
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)==false))
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


        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set when app restarted");
            setMarker();
        }


      }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }
    //testing new menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addLocation:
                menuItem = item;
                menuItem.setActionView(R.layout.progress);
                menuItem.expandActionView();
                setLocation();
                //estTask task = new TestTask();
                //task.execute("test");
                break;
            case R.id.removeLocatoin:
                GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                //remove everything on the map including the marker
                map.clear();
                markerPlaced=false;
                parkingData.deleteUserLocation();
                break;
            case R.id.autoMode:
                Toast toast=Toast.makeText(getApplicationContext(),"Auto Mode On",Toast.LENGTH_SHORT);
                toast.show();
                //stuff
                break;
            default:
                break;
        }
        return true;
    }

    public void updateCamera(){
        if(requestingLocation==false)
        {
            requestingLocation=true;
            Toast toast=Toast.makeText(getApplicationContext(),"Acquiring Location",Toast.LENGTH_LONG);
            toast.show();
            locationListener= new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e(TAG,"location found");
                    locationManager.removeUpdates(locationListener);
                    requestingLocation=false;
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(location.getLatitude(),location.getLongitude())).zoom(17).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
        }
    }
    public void setLocation()
    {
        //this methods requests a location update and when the location is aquired
        //it is passed to setMarker and then the updates are canceled

        if(requestingLocation==false){
            requestingLocation=true;
            Log.e(TAG,"location is being requested");

           locationListener= new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.e(TAG,"location found");
                    locationManager.removeUpdates(locationListener);
                    requestingLocation=false;
                    if(parkingData.locationSaved()==false){
                        parkingData.saveLocation(location);
                         setMarker(location);
                        menuItem.collapseActionView();
                        menuItem.setActionView(null);
                    }

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
    }

    public void setMarker(Location location)
    {
        //will turn off update
        locationManager.removeUpdates(locationListener);
        //will save the location to the shared preferences
        //will add a maker with the users location if there is not a marker in place
        //should check how old the location data is maybe if older then 10 hours delete
        if(markerPlaced==false){
            //sets the marker
            //new to make sure that if the user is not at school that we dont add the floor
            //also that if we could not get the floor or if the barometer is unavilable that we dont
            //set the floor
            String parkingInformation="";
            parkingInformation=parkingData.getCarParkingInformation();
            if((parkingInformation!="")&&(parkingData.hasPressureSensor())){
                parkingInformation=parkingInformation+"\n"+parkingData.getFloor();
            }
            if(parkingInformation==""){
                parkingInformation="My Car";
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()))
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
            if((parkingInformation!="")&&(parkingData.hasPressureSensor())){
                parkingInformation=parkingInformation+"\n"+parkingData.getFloor();
            }
            if(parkingInformation==""){
                parkingInformation="My Car";
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()))
                    .title(parkingInformation)
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
        updateCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set onResume");
            setMarker();
        }
        updateCamera();

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
