package com.example.ParkingBuddy.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import com.example.ParkingBuddy.ParkingData.ParkingData;

import static android.hardware.SensorManager.getAltitude;

/**
 * Created by javi on 4/9/14.
 */
public class PressureHandler extends Service implements SensorEventListener {
    SensorManager sensorManager;
    ParkingData parkingData;
    boolean pressureSave=false;
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager=(SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData= new ParkingData(getApplicationContext());
    }

    /**
     * Unregisters the sensor when the service is done.
     */
    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();

    }

    /**
     * Save the users altitude to the database.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(pressureSave == false){
            pressureSave=true;
            parkingData.setAltitude(getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,event.values[0]));
            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
