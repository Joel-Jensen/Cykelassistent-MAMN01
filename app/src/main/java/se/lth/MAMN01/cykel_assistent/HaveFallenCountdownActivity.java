package se.lth.MAMN01.cykel_assistent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class HaveFallenCountdownActivity extends AppCompatActivity {

    CountDownTimer countDown;
    private MediaPlayer alertSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_fallen_countdown);
        getSupportActionBar().hide();

        TextView countDownText = findViewById(R.id.countDown);

        countDown = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                countDownText.setText(Integer.toString(seconds));

                if(seconds > 5) {
                    return;
                }

                int sound = 0;
                switch(seconds) {
                    case 5:
                        sound = R.raw.five;
                        break;
                    case 4:
                        sound = R.raw.four;
                        break;
                    case 3:
                        sound = R.raw.three;
                        break;
                    case 2:
                        sound = R.raw.two;
                        break;
                    case 1:
                        sound = R.raw.one;
                        break;
                    case 0:
                        sound = R.raw.activate;
                        break;
                }

                if(alertSound != null) {
                    alertSound.release();
                }

                alertSound = MediaPlayer.create(getApplicationContext(), sound);
                alertSound.start();
            }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), HaveFallen.class);
                Bundle extras = getIntent().getExtras();
                intent.putExtra("phone", extras.getString("phone"));
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