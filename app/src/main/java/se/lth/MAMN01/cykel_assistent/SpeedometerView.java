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
        if(location.hasSpeed()) {
            speedometerTV.setText("Speed: "
                    + df.format(toKilometersPerHour(location.getSpeed()))
                    + "km/h");
            switch (speedometer.onSpeedUpdate(location.getSpeed())) {
                case ABOVE_THRESHOLD:
                    alertSound.release();
                    alertSound = MediaPlayer.create(SpeedometerView.this, R.raw.toofast);
                    alertSound.start();
                    break;
                case BELOW_THRESHOLD:
                    alertSound.release();
                    alertSound = MediaPlayer.create(SpeedometerView.this, R.raw.tooslow);
                    alertSound.start();

            }

            checkSpeedInterval(toKilometersPerHour(location.getSpeed()));
        }

    }

    private void checkSpeedInterval(double speed) {


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