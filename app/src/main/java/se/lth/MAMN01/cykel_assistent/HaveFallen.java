package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import java.security.Policy;

// https://www.tutorialspoint.com/how-to-turn-on-flash-light-programmatically-in-android
public class HaveFallen extends AppCompatActivity {

    private CameraManager mCameraManager;
    private String mCameraId;
    private boolean flashLightOn = false;
    private Handler h;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_fallen);
        getSupportActionBar().hide();

        activateFlashlight();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlashLight();
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
        intent.setData(Uri.parse("tel: 0722344344"));
        startActivity(intent);
    }
}