package com.blacky.geoalarmv2;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends Activity {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        TextView alarmNameTV = (TextView) findViewById(R.id.alarmName);
        Button alarmStop = (Button) findViewById(R.id.turnOffButton);
        int alarmId = getIntent().getIntExtra(GFIntentService.EXTRA_ID, -1);
        if (alarmId != -1){
            alarmNameTV.setText(AlarmStorage.getAlarmName(alarmId));
        }
        alarmStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                finish();
            }
        });
    }
}
