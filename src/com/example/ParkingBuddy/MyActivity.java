package com.example.ParkingBuddy;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.ParkingBuddy.ParkingData.ParkingData;
import com.example.ParkingBuddy.Services.GpsHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    ParkingData parkingData;
    Location userLocation;
    LocationManager locationManager;
    GoogleMap map;
    final static String TAG="test";

    //makes sure we dont put too makers
    boolean markerPlaced=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        parkingData= new ParkingData(getApplicationContext());
        //get the last gps based location
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        userLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //centers google maps
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //check to see if there is a valid saved location and sets a marker if there is
        /*
        if((parkingData.locationSaved())&&(!markerPlaced)){
            Log.e(TAG,"i should not be outputing");
            setMarker();
        }*/

        Button saveLocation=(Button)findViewById(R.id.save);
        Button clearLocation=(Button)findViewById(R.id.clear);
        saveLocation.setOnClickListener(handler1);
        clearLocation.setOnClickListener(handler2);
      }

    View.OnClickListener handler1 = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
           setMarker();
        }
    };
    View.OnClickListener handler2 = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
            map.clear();
            markerPlaced=false;
            parkingData.deleteUserLocation();
        }
    };
    public void setMarker()
    {

        //GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        //will add the user location to the sharepref
        if(userLocation!=null){
            Log.e(TAG,"location is null wtf");
            parkingData.saveLocation(userLocation);
        }else{
            Log.e(TAG,"location is being requested");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    userLocation=location;
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
            });
        }
        //will add a maker with the users location
        //should check how old the location data is maybe if older then 10 hours delete
        if(parkingData.locationSaved()&&(!markerPlaced)){
            // will display the maker
            Log.e(TAG,"location is set");
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

    }

    @Override
    protected void onResume() {
        super.onResume();
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
        //so that we know to replace the marker when the app restarts
        //markerPlaced=false;
    }
}
