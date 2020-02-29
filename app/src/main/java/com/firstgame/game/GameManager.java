package com.firstgame.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.firstgame.menu.MainActivity;

import java.util.List;

public abstract class GameManager {

    private MainActivity activity;

    private SensorManager manager;
    private Sensor sensor;
    private AccelerometerListener listener;

    public GameManager(MainActivity activity){
        this.activity = activity;
        manager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new AccelerometerListener();
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private float[] accelValues = new float[3];

    public abstract void onLogicLoop(float timePassed);

    public MainActivity getActivity(){
        return activity;
    }

    public abstract List<Player> getPlayers();

    class AccelerometerListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            accelValues[0] = event.values[0];
            accelValues[1] = event.values[1];
            accelValues[2] = event.values[2];
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public void onResume() {
        if (manager != null) {
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        if (manager != null) {
            manager.unregisterListener(listener);
        }
    }

    public abstract void onStop();

    public abstract void onDestroy();

    public float[] getAccelValues() {
        return accelValues;
    }
}
