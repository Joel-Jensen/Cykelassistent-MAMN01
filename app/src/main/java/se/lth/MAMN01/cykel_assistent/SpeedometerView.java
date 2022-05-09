package se.lth.MAMN01.cykel_assistent;

import static se.lth.MAMN01.cykel_assistent.Speedometer.ABOVE_THRESHOLD;
import static se.lth.MAMN01.cykel_assistent.Speedometer.BELOW_THRESHOLD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DecimalFormat;

public class SpeedometerView extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private static final int FASTEST_UPDATE_INTERVAL = 1;
    private TextView speedometerTV;
    private EditText upperBound, lowerBound;
    private Button setBoundaries;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DecimalFormat df;
    private MediaPlayer alertSound;
    private Speedometer speedometer;

    // Variable to determine if we are tracking location.
    private boolean requestingLocationUpdates = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);
        speedometerTV = findViewById(R.id.speedometerView);
        upperBound = findViewById(R.id.upperBound);
        lowerBound = findViewById(R.id.lowerBound);
        setBoundaries = findViewById(R.id.setBoundaries);
        setBoundaries.setText("Set Boundaries");
        setBoundaries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speedometer.setHighestLimit(Double.parseDouble(upperBound.getText().toString()));
                speedometer.setLowestLimit(Double.parseDouble(lowerBound.getText().toString()));
            }
        });
        df = new DecimalFormat("##.##");
        speedometer = new Speedometer(5, 7);

        // Set all properties of LocationRequest.
        setLocationRequestProperties();

        // Checks for all permissions and updates GPS if possible. If not, requests permissions.
        updateGPS();

        // Initiates a callback-loop for regular updates.
        createLocationCallback();

        startLocationUpdates();
    }


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
            switch (speedometer.onSpeedUpdate(location.getSpeed())) {
                case ABOVE_THRESHOLD:
                    speedometerTV.setText("Speed: "
                            + df.format(toKilometersPerHour(location.getSpeed()))
                            + "km/h");
                    alertSound.release();
                    alertSound = MediaPlayer.create(SpeedometerView.this, R.raw.too_fast);
                    alertSound.start();
                    break;
                case BELOW_THRESHOLD:
                    alertSound.release();
                    alertSound = MediaPlayer.create(SpeedometerView.this, R.raw.too_slow);
                    alertSound.start();
            }
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

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
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
}