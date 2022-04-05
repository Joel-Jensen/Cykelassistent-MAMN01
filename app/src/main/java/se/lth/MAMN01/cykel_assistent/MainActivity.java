package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String ACCELERATOR_VALUE1 = "se.lth.MAMN01.cykel_assistent.VALUE1";
    public static final String ACCELERATOR_VALUE2 = "se.lth.MAMN01.cykel_assistent.VALUE2";
    public static final String ACCELERATOR_VALUE3 = "se.lth.MAMN01.cykel_assistent.VALUE3";

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showAcceleration (View view) {
        Intent intent = new Intent(this, DisplayAcceleratorValuesActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
        //String message = editText.getText().toString();
        //intent.putExtra(ACCELERATOR_VALUES, message);
        startActivity(intent);
    }
}