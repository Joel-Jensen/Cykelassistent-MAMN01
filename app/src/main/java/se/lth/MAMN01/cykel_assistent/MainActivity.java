package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
        TextView minSpeedView = findViewById(R.id.editMinSpeed);
        TextView maxSpeedView = findViewById(R.id.editMaxSpeed);
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean startIntent = true;

                if( TextUtils.isEmpty(minSpeedView.getText())) {
                    minSpeedView.setError("Fyll i");
                    return;
                }

                int minSpeed = (int) Integer.parseInt(minSpeedView.getText().toString());
                if(minSpeed < 2 || minSpeed > 40) {
                    minSpeedView.setError("Måste vara mellan 2-40");
                    startIntent = false;
                }

                if( TextUtils.isEmpty(maxSpeedView.getText())) {
                    maxSpeedView.setError("Fyll i");
                    return;
                }

                int maxSpeed = (int) Integer.parseInt(maxSpeedView.getText().toString());
                if(maxSpeed < 2 || maxSpeed > 60) {
                    maxSpeedView.setError("Måste vara mellan 2-60");
                    startIntent = false;
                }

                if(maxSpeed <= minSpeed) {
                    maxSpeedView.setError("Måste vara högre än min");
                    startIntent = false;
                }

                if( TextUtils.isEmpty(phoneNumber.getText())) {
                    phoneNumber.setError("Fyll i");
                    startIntent = false;
                }

                if(startIntent) {
                    Intent intent = new Intent(getApplicationContext(), launchedApp.class);
                    intent.putExtra("phone", phoneNumber.getText().toString());
                    intent.putExtra("minSpeed", Integer.parseInt(minSpeedView.getText().toString()));
                    intent.putExtra("maxSpeed", Integer.parseInt(maxSpeedView.getText().toString()));
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