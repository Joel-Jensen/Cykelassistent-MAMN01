package se.lth.MAMN01.cykel_assistent;

import android.hardware.SensorEvent;

public interface AccelerometerChanged {
    void onSensorChanged(SensorEvent sensorEvent);
}
