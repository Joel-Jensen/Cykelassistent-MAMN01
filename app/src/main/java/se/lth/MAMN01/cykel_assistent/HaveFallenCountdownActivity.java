package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class HaveFallenCountdownActivity extends AppCompatActivity {

    CountDownTimer countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_fallen_countdown);
        getSupportActionBar().hide();

        TextView countDownText = findViewById(R.id.countDown);

        countDown = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                countDownText.setText(Long.toString(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), HaveFallen.class);
                startActivity(intent);
                finish();
            }

        }.start();
    }

    public void cancel(View v) {
        countDown.cancel();
        finish();
    }
}