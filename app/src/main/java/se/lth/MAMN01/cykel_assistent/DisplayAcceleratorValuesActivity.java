package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

public class DisplayAcceleratorValuesActivity extends AppCompatActivity implements SensorEventListener {

    float alpha = 0.8f;
    float [] gravity = new float[3];
    float [] linearAcceleration = new float[3];

    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_accelerator_values);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Intent intent = getIntent();
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        String [] values = new String[3];

        //gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        //gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        //gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

        //linearAcceleration[0] = sensorEvent.values[0] - gravity[0];
        //linearAcceleration[1] = sensorEvent.values[1] - gravity[1];
        //linearAcceleration[2] = sensorEvent.values[2] - gravity[2];

        for (int i = 0; i < linearAcceleration.length; i++) {
            //values[i] = Float.toString(linearAcceleration[i]);
            values[i] = Float.toString(sensorEvent.values[i]);
        }

        TextView xAcceleration = findViewById(R.id.x_acceleration);
        TextView yAcceleration = findViewById(R.id.y_acceleration);
        TextView zAcceleration = findViewById(R.id.z_acceleration);

        xAcceleration.setText(values[0]);
        yAcceleration.setText(values[1]);
        zAcceleration.setText(values[2]);

        xAcceleration.setTextColor(Color.RED);
        yAcceleration.setTextColor(Color.GREEN);
        zAcceleration.setTextColor(Color.BLUE);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}