package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.security.Policy;
import java.util.LinkedList;
import java.util.List;

// https://www.tutorialspoint.com/how-to-turn-on-flash-light-programmatically-in-android
public class HaveFallen extends AppCompatActivity implements SensorEventListener {

    private CameraManager mCameraManager;
    private String mCameraId;
    private boolean flashLightOn = false;
    private Handler h;
    private Runnable runnable;
    private List<Double> samples;
    private long lastSample = 0;
    private int SAMPLES_PER_SECONDS = 10;
    private int NUMBER_OF_SAMPLES = 10;
    private long potentialCrashTimestamp = 0;
    private String phone;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private MediaPlayer alertSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_fallen);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        phone = extras.getString("phone");

        TextView smsSentText = (TextView) findViewById(R.id.smsSent);
        smsSentText.setText("Sms skickat till ICE: " + phone);

        activateFlashlight();

        samples = new LinkedList<>();
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        alertSound = MediaPlayer.create(getApplicationContext(), R.raw.warning);
        alertSound.setLooping(true);
        alertSound.start();
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlashLight();
        alertSound.release();
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        alertSound.start();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        alertSound.pause();
    }

    private void activateFlashlight() {
        getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        h = new Handler();
        runnable = new Runnable() {
            @Override
            public void run()
            {
                toggleFlashLight();
                h.postDelayed(this, 200);
            }
        };
        h.postDelayed(runnable, 200);
    }

    public void toggleFlashLight() {
        flashLightOn = !flashLightOn;
        try {
            mCameraManager.setTorchMode(mCameraId, flashLightOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void turnOffFlashLight() {
        h.removeCallbacks(runnable);
        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void callAmbulance(View view) {
        turnOffFlashLight();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: 112"));
        startActivity(intent);
    }

    public void callICE(View view) {
        turnOffFlashLight();

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: " + phone));
        startActivity(intent);
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

        int shakes = 0;
        for(int i=0; i < samples.size(); i++) {
            if(samples.get(i) < 5 || samples.get(i) > 20) {
                shakes++;
            }
        }

        if(shakes > NUMBER_OF_SAMPLES / 2) {
            finish();
        }
    }
}