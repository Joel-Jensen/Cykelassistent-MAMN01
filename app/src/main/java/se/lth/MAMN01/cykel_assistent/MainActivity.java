package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    }
}