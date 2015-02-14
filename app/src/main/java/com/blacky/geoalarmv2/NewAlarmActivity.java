package com.blacky.geoalarmv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class NewAlarmActivity extends ActionBarActivity implements OnMapReadyCallback {

    private LatLng position;
    private MapFragment smallMapFragment;
    private SeekBar radiusSeek;
    private Circle radius;
    private Button addButton;
    private int gfId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        position = getIntent().getParcelableExtra(MapsActivity.COORDINATE_TAG);
        smallMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.smallMap));
        smallMapFragment.getMapAsync(this);
        final Switch alarmOn = (Switch) findViewById(R.id.alarmOn);
        addButton = (Button) findViewById(R.id.addButton);
        radiusSeek = (SeekBar) findViewById(R.id.seekBar);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int gfTransType = Geofence.GEOFENCE_TRANSITION_ENTER;
                float radius = radiusSeek.getProgress();
                GAGeofence geofence = new GAGeofence
                        (gfId, true, position.latitude, position.longitude, radius, gfTransType);
                AlarmStorage.saveAlarm(geofence);

                Intent addGeofence = new Intent(getApplicationContext(), GeofencingService.class);
                addGeofence.putExtra(GeofencingService.EXTRA_ACTION, GeofencingService.ACTION_ADD);
                addGeofence.putExtra(GeofencingService.EXTRA_GEOFENCE, geofence);
                gfId++;
                startService(addGeofence);


                finish();
                Toast.makeText(getApplicationContext(), "Alarm was saved", Toast.LENGTH_SHORT).show();
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