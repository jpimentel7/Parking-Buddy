package com.example.ParkingBuddy.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import com.example.ParkingBuddy.ParkingData.ParkingData;

/**
 * Created by javier on 3/18/14.
 */
public class PedoHandler extends Service implements SensorEventListener
{
    private SensorManager sensorManager;
    private static final String TAG ="service";
    boolean stepDetected=false;
    LocationManager locationManager;
    ParkingData parkingData;
    LocationListener locationListener;
    PackageManager packageManager;
    //need to save the pressure too


    @Override
    public void onCreate()
    {
        super.onCreate();
        sensorManager=(SensorManager) getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData=new ParkingData(getApplicationContext());
        packageManager= getApplicationContext().getPackageManager();
        Log.e(TAG, "look i started");
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor((Sensor.TYPE_STEP_DETECTOR)));
        locationManager.removeUpdates(locationListener);
        Log.e(TAG,"look i was called");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e(TAG,"look i detected a change and the value is:"+event.values[0]);

        if(event.values[0]==1.0f)
        {
            if(stepDetected == false){
                stepDetected=true;
                Log.e(TAG,"look i dectected a change and updated count:");
                startLocationManager();
                if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))
                startService(new Intent(this, PressureHandler.class));
            }

        }


    }
    private void startLocationManager()
    {

        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG,"i updated the location");
                //will only stop the service if the user is near school
                //in case the pedometer is set off by accident
                Location school=new Location("CSUN");
                school.setLatitude(34.242739);
                school.setLongitude(-118.526223);
                //have to change to like 6000
                if(location.distanceTo(school)<1000000000){
                parkingData.saveLocation(location);
                //gives a quick vibrate to let the user know his location has been saved
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 300 milliseconds
                v.vibrate(300);
                stopSelf();
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}