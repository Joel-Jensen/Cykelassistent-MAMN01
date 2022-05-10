package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#F0773D\">" + getString(R.string.app_name) + "</font>"));
    }

    public void showFallDetection(View view) {
        Intent intent = new Intent(this, FallDetectionView.class);
        startActivity(intent);
    }

    public void showSpeedometer(View view) {
        Intent intent = new Intent(this, SpeedometerView.class);
        startActivity(intent);
    }
}