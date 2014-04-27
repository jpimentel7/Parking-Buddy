package com.example.ParkingBuddy.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import com.example.ParkingBuddy.ParkingData.ParkingData;

/**
 * Created by javi on 4/9/14.
 */
public class PressureHandler extends Service implements SensorEventListener {
    SensorManager sensorManager;
    ParkingData parkingData;
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager=(SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData= new ParkingData(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        parkingData.setAltitude(event.values[0]);
        stopSelf();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
