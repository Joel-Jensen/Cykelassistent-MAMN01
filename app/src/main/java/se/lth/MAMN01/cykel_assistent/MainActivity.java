package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showAcceleration (View view) {
        Intent intent = new Intent(this, DisplayAcceleratorValuesActivity.class);
        startActivity(intent);
    }

    public void showRoadQuality (View view) {
        Intent intent = new Intent(this, DisplayRoadQuality.class);
        startActivity(intent);
    }

    public void showFallDetection(View view) {
        Intent intent = new Intent(this, FallDetectionView.class);
        startActivity(intent);
    }

    public void showSpeedometer(View view) {
        Intent intent = new Intent(this, SpeedometerView.class);
        startActivity(intent);
    }

    public void launchApplication(View view) {
        Intent intent = new Intent(this, launchedApp.class);
        startActivity(intent);
    }
}