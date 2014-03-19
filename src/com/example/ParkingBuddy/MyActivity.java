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
import com.example.ParkingBuddy.ParkingData.ParkingData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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
    //makes sure we dont put two makers
    boolean markerPlaced=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //will set everything up
        configure();
        //check to see if there is a valid saved location and sets a marker if there is
        //for when the user closes the app and restarts
        if((parkingData.locationSaved())&&(markerPlaced==false))
        {
            Log.e(TAG,"location has been set when app restarted");
            setMarker();
        }
        Button saveLocation=(Button)findViewById(R.id.save);
        Button clearLocation=(Button)findViewById(R.id.clear);
        saveLocation.setOnClickListener(saveHandler);
        clearLocation.setOnClickListener(clearHandler);
      }

    View.OnClickListener saveHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            //checks the location data and set the marker
           setMarker();
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
        //
        parkingData= new ParkingData(getApplicationContext());
        //get the last gps based location
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        userLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //centers google maps
        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void setMarker()
    {
        //check to see if it is null ! GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();

        //will add the user location to the sharepref if the location is not null
        //if the location is null it will update it
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
    }
}
