package com.example.ParkingBuddy.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by javier on 3/18/14.
 */
public class PedoHandler extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private int count=0;
    private static final String TAG ="service";

    @Override
    public void onCreate()
    {
        super.onCreate();
        sensorManager=(SensorManager) getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),SensorManager.SENSOR_DELAY_NORMAL);
        Log.e(TAG, "look i started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this,sensorManager.getDefaultSensor((Sensor.TYPE_STEP_DETECTOR)));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e(TAG,"look i detected a change and the value is:"+event.values[0]);

        if(event.values[0]==1.0f)
        {
            Log.e(TAG,"look i dectected a change and update count:"+count);
            //this means there was a step detected
            count++;

        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public int getCount() {
        return count;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}