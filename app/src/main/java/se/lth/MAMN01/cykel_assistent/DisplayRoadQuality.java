package se.lth.MAMN01.cykel_assistent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayRoadQuality extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Vibrator vib;
    private TextView textView;
    private float gravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_road_quality);
        Intent intent = getIntent();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        textView = findViewById(R.id.shakingDetails);
        gravity = 9.80655f;

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        vib.cancel();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent != null) {
            float y_acceleration = sensorEvent.values[1];

            if(y_acceleration > 9.9f) {
                //vib.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                textView.setText("The road is uphill!");
            } else if(y_acceleration < 9.7f ) {
                //vib.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                textView.setText("The road is downhill!");
            }else if (y_acceleration < 9.9f && y_acceleration > 9.7f) {
                textView.setText("The road is flat!");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}