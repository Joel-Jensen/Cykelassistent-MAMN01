package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
   public int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#F0773D\">" + getString(R.string.app_name) + "</font>"));

        TextView phoneNumber = findViewById(R.id.editPhone);
        TextView minSpeed = findViewById(R.id.editMinSpeed);
        TextView maxSpeed = findViewById(R.id.editMaxSpeed);
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean startIntent = true;

                if( TextUtils.isEmpty(minSpeed.getText())) {
                    minSpeed.setError("Fyll i");
                    return;
                }

                int speed = (int) Integer.parseInt(minSpeed.getText().toString());
                if(speed < 2 || speed > 40) {
                    minSpeed.setError("Måste vara mellan 2-40");
                    startIntent = false;
                }

                if( TextUtils.isEmpty(maxSpeed.getText())) {
                    maxSpeed.setError("Fyll i");
                    return;
                }

                speed = (int) Integer.parseInt(maxSpeed.getText().toString());
                if(speed < 2 || speed > 40) {
                    maxSpeed.setError("Måste vara mellan 2-40");
                    startIntent = false;
                }

                if( TextUtils.isEmpty(phoneNumber.getText())) {
                    phoneNumber.setError("Fyll i");
                    startIntent = false;
                }

                if(startIntent) {
                    Intent intent = new Intent(getApplicationContext(), FallDetectionView.class);
                    startActivity(intent);
                }
            }
        });
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