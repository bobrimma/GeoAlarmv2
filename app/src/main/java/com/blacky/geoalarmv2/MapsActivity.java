package com.blacky.geoalarmv2;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    final static String COORDINATE_TAG = "COORDINATE";
    final static String CONNECTION_TAG = "Google API Connection";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location myLocation;
    private LatLng myLatLng;
    private Marker marker;


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
            setUpAlarmMarkers();
        } else Toast.makeText(this, R.string.goops_error, Toast.LENGTH_SHORT).show();
    }

    private void setUpAlarmMarkers() {
        Marker alarmMarker;
        Circle alarmCircle;
        LatLng alarmPosition;
        CircleOptions circleOptions;
        for (GAGeofence alarmGeofence : AlarmStorage.getSavedAlarms()) {
            alarmPosition = new LatLng(alarmGeofence.getGeofenceLatitude(), alarmGeofence.getGeofenceLongitude());
            alarmMarker = mMap.addMarker(new MarkerOptions()
                    .title("I'm here!")
                    .position(alarmPosition).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            if (alarmGeofence.isEnabled()) {
                circleOptions = new CircleOptions()
                        .center(alarmPosition)
                        .radius(alarmGeofence.getGeofenceRadius());
                alarmCircle = mMap.addCircle(circleOptions);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpAlarmMarkers();
        apiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            apiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Intent newAlarm = new Intent(MapsActivity.this, NewAlarmActivity.class)
                            .putExtra(COORDINATE_TAG, latLng);
                    startActivity(newAlarm);
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(CONNECTION_TAG, "Location services connected.");
        myLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (myLocation != null) {
            handleNewLocation(myLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();
        marker.remove();
        handleNewLocation(myLocation);
    }

    private void handleNewLocation(Location myLocation) {
        myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                .title("I'm here!")
                .position(myLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(CONNECTION_TAG, "Location services suspended.");
        Toast.makeText(getApplicationContext(), "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.i(CONNECTION_TAG, "SendIntentException");
                e.printStackTrace();
            }
        } else {
            Log.i(CONNECTION_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            Toast.makeText(getApplicationContext(), "Connection failed with code " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
