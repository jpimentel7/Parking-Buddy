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

    @Override
    public void onCreate()
    {
        super.onCreate();
        sensorManager=(SensorManager) getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData=new ParkingData(getApplicationContext());
        packageManager= getApplicationContext().getPackageManager();
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor((Sensor.TYPE_STEP_DETECTOR)));
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    /**
     * If a step a location location manager will be called to request
     * a location update. If the phone has barometer a service will
     * be started to save the users altitude.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.values[0]==1.0f)
        {
            if(stepDetected == false){
                stepDetected=true;
                locationManager();
                if(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER))
                startService(new Intent(this, PressureHandler.class));
            }

        }


    }
    /**
     * Requests a single location update and if the user is within a certain amount of
     * meter from school the function will save the users location to the database and will
     * also terminate the service.
     */
    private void locationManager()
    {

        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Location school=new Location("CSUN");
                school.setLatitude(34.242739);
                school.setLongitude(-118.526223);
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

        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}