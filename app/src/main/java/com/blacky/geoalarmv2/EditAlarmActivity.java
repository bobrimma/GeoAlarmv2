package com.blacky.geoalarmv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class EditAlarmActivity extends ActionBarActivity implements OnMapReadyCallback {

    private LatLng position;
    private MapFragment smallMapFragment;
    private SeekBar radiusSeek;
    private Circle radius;
    private Button delButton;
    private Button saveButton;
    private Switch alarmOn;
    private TextView tvName;
    private TextView tvDescription;
    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        position = getIntent().getParcelableExtra(MapsActivity.COORDINATE_TAG);
        alarmId = Integer.parseInt(getIntent().getStringExtra(MapsActivity.ALARM_TAG));
        smallMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.smallMap));
        smallMapFragment.getMapAsync(this);

        alarmOn = (Switch) findViewById(R.id.alarmOn);
        alarmOn.setChecked(AlarmStorage.isAlarmOn(alarmId));
        Log.d("alarmOn", "Before edit " + alarmOn.isChecked());

        delButton = (Button) findViewById(R.id.delButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        radiusSeek = (SeekBar) findViewById(R.id.seekBar);
        radiusSeek.setProgress((int) AlarmStorage.getAlarmRadius(alarmId));

        tvName = (TextView) findViewById(R.id.alarmName);
        tvName.setText(AlarmStorage.getAlarmName(alarmId).toString());

        tvDescription = (TextView) findViewById(R.id.alarmDesc);
        tvDescription.setText(AlarmStorage.getAlarmDescription(alarmId).toString());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int gfTransType = Geofence.GEOFENCE_TRANSITION_ENTER;
                float radius = radiusSeek.getProgress();
                Log.d("alarmOn", "After Edit " + alarmOn.isChecked());

                AlarmStorage.editAlarm(alarmId,tvName.getText().toString(), tvDescription.getText().toString(),radius, alarmOn.isChecked());
//                if (alarmOn.isChecked()) {
//                    Intent addGeofence = new Intent(getApplicationContext(), GeofencingService.class);
//                    addGeofence.putExtra(GeofencingService.EXTRA_ACTION, GeofencingService.ACTION_ADD);
//                    addGeofence.putExtra(GeofencingService.EXTRA_GEOFENCE, geofence);
//
//                    startService(addGeofence);
//                }
                finish();
                Toast.makeText(getApplicationContext(), "Alarm was edited", Toast.LENGTH_SHORT).show();
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmStorage.deleteAlarm(alarmId);
                finish();
                Toast.makeText(getApplicationContext(), "Alarm was deleted", Toast.LENGTH_SHORT).show();
            }
        });


        radiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//        alarmOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
        googleMap.addMarker(new MarkerOptions()
                .title("New alarm")
                .position(position));
        radius = googleMap.addCircle(new CircleOptions().center(position).radius(10));
    }
}