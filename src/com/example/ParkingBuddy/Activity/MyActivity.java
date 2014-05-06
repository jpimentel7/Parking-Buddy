package com.example.ParkingBuddy.Activity;

import android.app.ActionBar;
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
import com.example.ParkingBuddy.R;
import com.example.ParkingBuddy.Services.PedoHandler;
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
     *My Activty control the google maps fragment as well
     * as the gps and pressure sensor.
     *
     * @author Javier Pimentel
     * @author
     * @author
     * @author
     */
    private ParkingData parkingData;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap map;
    private final static String TAG="test";
    private MenuItem menuItem;
    private boolean requestingLocation=false;
    private boolean hasGpsEnable;
    private boolean autoModeEnable=false;
    private PackageManager packageManager;

    /**
     *This method is called when the application is first launched
     * it checks if auto mode is supported, centers the maps to the users
     * location , and displays a warning if the gps is turned off.
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Remove the title from the action bar
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.main);
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        parkingData= new ParkingData(getApplicationContext());
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        packageManager = getApplicationContext().getPackageManager();
        Location userLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //If the user has their location off normally getLastKnownLocation will return null
        if(userLocation!=null)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            updateCamera();
        }
        else
        {
            updateCamera();
        }

        //Checks to see if the gps is turned on and displays a message if it is
        if((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)==false)&&(
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)==false))
        {
            hasGpsEnable=false;
            new AlertDialog.Builder(this)
                    .setTitle("GPS")
                    .setMessage("Must Enable Gps For The App To Work Correctly")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .show();
        }
        /*
            Will load a marker if there is a location saved and there is not
            a marker on the map. Will occur when the user restarts the map
            or when auto mode saves a location.
         */

        if((parkingData.locationSaved())&&(parkingData.hasMarker()))
        {
            setMarker();
        }

      }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }
    /**
     * Controls what happens when a button on the action bar is pressed.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.addLocation:
                /*
                    When the this button is pressed if there is no location saved
                    the app will request a location update and save the location.
                    If the phone has a barometer it will start a service to get the
                    altitude.
                 */
                if(parkingData.locationSaved()==false)
                {
                    menuItem = item;
                    menuItem.setActionView(R.layout.progress);
                    menuItem.expandActionView();
                    setLocation();
                    if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER))
                        startService(new Intent(this, PressureHandler.class));
                }
                else
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"Location Already Saved",Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case R.id.removeLocatoin:
                //Removes the marker from the map and also from the database
                if(parkingData.locationSaved()){
                    //Allows the user to turn on auto mode again
                    if(autoModeEnable==true)
                    {
                    autoModeEnable=false;
                    }
                    GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
                    map.clear();
                    parkingData.makerDeleted();
                    parkingData.deleteUserLocation();
                }
                else
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"No Location Saved",Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;

            case R.id.autoMode:
                /*
                    Will check to see if the phone has a step detector before starting auto mode
                    and if the user does not it will display a message.
                    If auto mode is running or if the location is already saved a message will be
                    displayed letting the user know that their location is already saved.
                */
                if((parkingData.locationSaved()==false)&&
                        (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))&&
                        (autoModeEnable == false))
                {
                    autoModeEnable=true;
                    Toast toast=Toast.makeText(getApplicationContext(),"Auto Mode On",Toast.LENGTH_SHORT);
                    toast.show();
                    startService(new Intent(this, PedoHandler.class));
                }
                else if((packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))== false)
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"Feature Not Supposed",Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (parkingData.locationSaved())
                {
                    Toast toast=Toast.makeText(getApplicationContext(),"Location Already Saved",Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                break;
        }
        return true;
    }
    /**
     *Request a location update then updates the view on the map.
     */
    private void updateCamera(){
        if(requestingLocation==false)
        {
            requestingLocation=true;
            Toast toast=Toast.makeText(getApplicationContext(),"Acquiring Location",Toast.LENGTH_SHORT);
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

    /**
     * Request a location update and when the location is found calls a function that
     * puts a pin on the map. The request will only be made if no other component
     * is making a request.
     *
     * */
    private void setLocation()
    {
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
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
           }
    }

    /**
     *Saves the location in the database and then checks if information is
     * available about the location and put that information on the pin.
     * @param location
     */
    private void setMarker(Location location)
    {
        if(parkingData.hasMarker()==false){
            String parkingInformation="";
            parkingInformation=parkingData.getCarParkingInformation();
            if((parkingInformation!="")&&(parkingData.hasAltitude())){
                parkingInformation=parkingInformation+"\n"+parkingData.getFloor();
            }
            /*
                parkinginfo will equal "" when there is no
                information about the parking lot in the database.
                For example when you park off campus.
              */
            if(parkingInformation==""){
                parkingInformation="My Car";
            }
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(),
                            parkingData.getUserLocation().getLongitude()))
                    .title(parkingInformation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(marker);
            parkingData.markerPlaced();
        }
    }

    /**
     * Will add a maker with the users location if there is not a marker in place.
     * This happens when the application is restarted or when using auto mode.
     */
    private void setMarker()
    {
        if(parkingData.locationSaved()&&(parkingData.hasMarker()==false)){
            String parkingInformation="";
            parkingInformation=parkingData.getCarParkingInformation();
            if((parkingInformation!="")&&(parkingData.hasAltitude())){
                parkingInformation=parkingInformation+"\n"+parkingData.getFloor();
            }
              /*
                parkinginfo will equal "" when there is no
                information about the parking lot in the database.
                For example when you park off campus.
              */
            if(parkingInformation==""){
                parkingInformation="My Car";
            }

            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parkingData.getUserLocation().getLatitude(), parkingData.getUserLocation().getLongitude()))
                    .title(parkingInformation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            map.addMarker(marker);
            parkingData.markerPlaced();
        }
    }

    /**
     *If there are no pins on the maps but there is a location save
     * a pin will be place on the map. Occurs when auto mode is called.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if((parkingData.locationSaved())&&(parkingData.hasMarker()==false))
        {
            setMarker();
        }
        updateCamera();
    }

    /**
     *If there are no pins on the maps but there is a location save
     * a pin will be place on the map. Occurs when auto mode is called.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if((parkingData.locationSaved())&&(parkingData.hasMarker()==false))
        {
            setMarker();
        }
        updateCamera();

    }
}