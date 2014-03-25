package com.example.ParkingBuddy.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
    private int count=0;
    private static final String TAG ="service";
    LocationManager locationManager;
    Location carLocation;
    ParkingData parkingData;
    LocationListener locationListener;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sensorManager=(SensorManager) getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData=new ParkingData(getApplicationContext());
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
            Log.e(TAG,"look i dectected a change and updated count:"+count);
            count++;
            //the location is saved after the third step and the location manager is started
            if(count==2){
                startLocationManager();
            }


        }


    }
    private void startLocationManager()
    {
        Log.e(TAG,"i updated the location");
        locationManager=(LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                parkingData.saveLocation(carLocation);
                //gives a quick vibrate to let the user know his location has been saved
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 300 milliseconds
                v.vibrate(300);
                stopSelf();

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