package com.example.ParkingBuddy.Services;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.example.ParkingBuddy.ParkingData.ParkingData;

/**
 * Created by javi on 4/9/14.
 */
public class PressureHandler extends Activity implements SensorEventListener {
    SensorManager sensorManager;
    ParkingData parkingData;
    public void PressureHandler(){
        sensorManager=(SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);
        parkingData= new ParkingData(getApplicationContext());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
       parkingData.setAltitude(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
