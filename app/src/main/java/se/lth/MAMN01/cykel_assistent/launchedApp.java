package se.lth.MAMN01.cykel_assistent;

import static se.lth.MAMN01.cykel_assistent.Speedometer.ABOVE_THRESHOLD;
import static se.lth.MAMN01.cykel_assistent.Speedometer.WITHIN_THRESHOLD;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class launchedApp extends AppCompatActivity implements SensorEventListener {
    TextView tvTimer;
    long timeInMilliseconds = 0;
    Handler customHandler = new Handler();
    private FallDetection fallDetection;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    public static final int DEFAULT_UPDATE_INTERVAL = 1;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int FASTEST_UPDATE_INTERVAL = 1;
    private TextView speedometerTV;
    private TextView upperBound, lowerBound;
    private Button toggleTimer;
    private int maxSpeed, minSpeed;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private MediaPlayer alertSound;
    private Speedometer speedometer;
    // Variable to determine if we are tracking location.
    private boolean requestingLocationUpdates = false;
    private Vibrator vibrationService;
    private long[] patternSlowDown = {0, 2000, 1000, 2000};
    private long[] patternSpeedUp = {0, 200, 250, 200, 250, 200, 0, 200, 250, 200, 250, 200};
    private boolean paused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launched_app);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#F0773D\">" + getString(R.string.app_name) + "</font>"));

        Bundle extras = getIntent().getExtras();
        minSpeed = extras.getInt("minSpeed");
        maxSpeed = extras.getInt("maxSpeed");

        tvTimer = (TextView) findViewById(R.id.tvTimer);
        fallDetection = new FallDetection(() -> {
            if(! paused) {
                toggleTimer(null);
                Intent intent = new Intent(this, HaveFallenCountdownActivity.class);
                intent.putExtra("phone", extras.getString("phone"));
                startActivity(intent);
            }
        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        speedometerTV = findViewById(R.id.speedometerView);
        upperBound = findViewById(R.id.upperBound);
        lowerBound = findViewById(R.id.lowerBound);
        toggleTimer = findViewById(R.id.toggleTimer);
        speedometer = new Speedometer(minSpeed, maxSpeed);


        // Set all properties of LocationRequest.
        setLocationRequestProperties();

        // Checks for all permissions and updates GPS if possible. If not, requests permissions.
        updateGPS();

        // Initiates a callback-loop for regular updates.
        createLocationCallback();

        startLocationUpdates();
        updateSpeedLimit();
        customHandler.post(updateTimerThread);
    }

    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void toggleTimer(View v) {
        paused = !paused;
        if(paused) {
            customHandler.removeCallbacks(updateTimerThread);
            toggleTimer.setText("Starta");
        } else {
            customHandler.post(updateTimerThread);
            toggleTimer.setText("Stopp");
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void increaseLow(View v) {
        minSpeed++;
        if(minSpeed == maxSpeed) {
            maxSpeed++;
        }
        updateSpeedLimit();
    }

    public void decreaseLow(View v) {
        minSpeed--;
        if(minSpeed < 0) {
            minSpeed = 0;
        }
        updateSpeedLimit();
    }

    public void increaseHigh(View v) {
        maxSpeed++;
        updateSpeedLimit();
    }

    public void decreaseHigh(View v) {
        maxSpeed--;

        if(maxSpeed < 1) {
            maxSpeed = 1;
        }

        if(maxSpeed == minSpeed) {
            minSpeed--;
        }
        updateSpeedLimit();
    }

    private void updateSpeedLimit() {
        speedometer.setHighestLimit(maxSpeed);
        speedometer.setLowestLimit(minSpeed);
        upperBound.setText(Integer.toString(maxSpeed));
        lowerBound.setText(Integer.toString(minSpeed));
    }


    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds += 1000;
            tvTimer.setText(getDateFromMillis(timeInMilliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Save the location
                updateUI(locationResult.getLastLocation());
            }
        };
    }

    private void updateUI(Location location) {
        if (location.hasSpeed()) {
            int kmph = (int) toKilometersPerHour(location.getSpeed());
            speedometerTV.setText(Integer.toString(kmph) + " km/h");

            if(paused) {
                return;
            }

            int speed = speedometer.onSpeedUpdate(kmph);
            if(speed == WITHIN_THRESHOLD) {
                return;
            }

            int sound;
            long[] vibrationPatter;
            if(speed == ABOVE_THRESHOLD) {
                sound = R.raw.slower;
                vibrationPatter = patternSlowDown;
            } else {
                sound = R.raw.faster;
                vibrationPatter = patternSpeedUp;
            }

            if(alertSound != null) {
                alertSound.release();
            }

            alertSound = MediaPlayer.create(launchedApp.this, sound);
            alertSound.start();

            vibrationService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrationService.vibrate(VibrationEffect.createWaveform(vibrationPatter, -1));
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission to use GPS",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                updateUI(location);
            }
        });
    }

    public double toKilometersPerHour(double speed){
        return speed * 3.6;
    }

    private void setLocationRequestProperties(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        fallDetection.onSensorChanged(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}