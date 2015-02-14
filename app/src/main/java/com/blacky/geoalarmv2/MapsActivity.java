package com.blacky.geoalarmv2;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String ACTION = "action";
    public static final String ALARM_ADD = "alarmAdd";
    public static final String CIRCLE = "circle";
    static GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location myLocation;
    private LatLng myLatLng;
    private Marker marker;
    final static String COORDINATE_TAG = "COORDINATE";
    static List<Alarm> alarmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            apiClient.connect();
            setUpMapIfNeeded();
        } else Toast.makeText(this, R.string.goops_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Intent newAlarm = new Intent(getApplicationContext(), NewAlarmActivity.class)
                            .putExtra(COORDINATE_TAG, latLng);
                    startActivity(newAlarm);
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (myLocation != null) {
            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions()
                    .title("I'm here!")
                    .position(myLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14));
        }
        if (getIntent().getStringExtra(ACTION) != null)
            if (getIntent().getStringExtra(ACTION).equals(ALARM_ADD)) {
                CircleSerial circle = (CircleSerial) getIntent().getSerializableExtra(CIRCLE);
//                alarmList.add(mMap.addCircle(circle.toCircleOpts()));
            }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        marker.remove();
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                .title("I'm here!")
                .position(myLatLng));
    }
}

class CircleSerial implements Serializable {
    double latitude;
    double longitude;
    double radius;

    CircleSerial(double latitude, double longitude, double radius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public CircleOptions toCircleOpts() {
        return new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius);
    }

    public MarkerOptions toMarkerOpts(){
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude));
    }
}

class Alarm{
    String name;
    CircleOptions region;
    MarkerOptions marker;
    String description;

    Alarm(String name, CircleOptions region, MarkerOptions marker, String description) {
        this.name = name;
        this.region = region;
        this.marker = marker;
        this.description = description;
    }
}