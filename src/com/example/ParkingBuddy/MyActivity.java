package com.example.ParkingBuddy;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //getting the best last know location
        /*
        LocationManager locationManager = (LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        Criteria criteria=new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider= locationManager.getBestProvider(criteria,true);
        Location location= locationManager.getLastKnownLocation(provider);
        */
        //get the last gps based locatoin
        LocationManager locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        Location location =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Toast toast=Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_LONG);
        toast.show();
        //starts google maps
        GoogleMap map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
               //centers it to our last know location
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(location.getLatitude(),location.getLongitude())).zoom(17).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //
        Button saveLocation=(Button)findViewById(R.id.save);
        Button clearLocation=(Button)findViewById(R.id.clear);
        saveLocation.setOnClickListener(handler1);
        clearLocation.setOnClickListener(handler2);
    }

    View.OnClickListener handler1 = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

        }
    };
    View.OnClickListener handler2 = new View.OnClickListener(){
        @Override
        public void onClick(View view){

        }
    };

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
