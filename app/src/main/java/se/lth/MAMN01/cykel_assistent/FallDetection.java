package se.lth.MAMN01.cykel_assistent;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class FallDetection implements AccelerometerChanged  {

    private List<Double> samples;
    private long lastSample = 0;
    private Callback callbackOnFallen;
    private int SAMPLES_PER_SECONDS = 10;
    private int NUMBER_OF_SAMPLES = 20;
    private long potentialCrashTimestamp = 0;

    public FallDetection(Callback callbackOnFallen) {
        samples = new LinkedList<>();
        this.callbackOnFallen = callbackOnFallen;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(lastSample + 1000 / SAMPLES_PER_SECONDS > System.currentTimeMillis()) {
            return;
        }
        float[] aVectors = sensorEvent.values;
        double vector = Math.sqrt(Math.pow(aVectors[0], 2) + Math.pow(aVectors[1], 2) + Math.pow(aVectors[2], 2));
        samples.add(vector);
        lastSample = System.currentTimeMillis();
        removeOldSample();
        calculateMotionChanges();
    }

    private void removeOldSample() {
        if(samples.size() > NUMBER_OF_SAMPLES) {
            samples.remove(0);
        }
    }

    private void calculateMotionChanges() {
        if(samples.size() < NUMBER_OF_SAMPLES) {
            return;
        }

        for(int i=0; i < samples.size(); i++) {
            if(samples.get(i) < 2 || samples.get(i) > 30) {
                potentialCrashTimestamp = System.currentTimeMillis();
            }
        }

        if(potentialCrashTimestamp + 10000 > System.currentTimeMillis()) {
            boolean lyingStill = true;
            for(double v : samples) {
                if(v < 8.8 || v > 10.6) {
                    lyingStill = false;
                }
            }
            if(lyingStill) {
                samples = new LinkedList<>();
                potentialCrashTimestamp = 0;
                callbackOnFallen.methodToCallBack();
            }
        }
    }

}
