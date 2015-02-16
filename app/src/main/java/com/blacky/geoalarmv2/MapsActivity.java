package com.blacky.geoalarmv2;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    final static String COORDINATE_TAG = "COORDINATE";
    final static String CONNECTION_TAG = "Google API Connection";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String ALARM_TAG = "Alarm id";
    public static final String MARKER_TAG = "I'm here";

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location myLocation;
    private LatLng myLatLng;
    private Marker marker;
    private LocationRequest mLocationRequest;


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
            //   setUpMapIfNeeded();
            //  setUpAlarmMarkers();
        } else Toast.makeText(this, R.string.goops_error, Toast.LENGTH_SHORT).show();
    }

    private void setUpAlarmMarkers() {
        CircleOptions circleOptions;
        mMap.clear();
        List<LatLng> alarmsPosition = AlarmStorage.getAllAlarmsPositions();
        List<Float> alarmsRadius = AlarmStorage.getAllAlarmsRadius();
        List<Boolean> alarmsStatus = AlarmStorage.getAllAlarmsStatus();
        List<String> alarmsNames = AlarmStorage.getAllAlarmsNames();
        List<Integer> alarmsIds = AlarmStorage.getAllAlarmsIds();
        for (int i = 0; i < AlarmStorage.getAlarmsNumber(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(alarmsPosition.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.alarm_mark))
                    .title("" + alarmsIds.get(i))
                    .snippet(alarmsNames.get(i)));
            Log.d("alarmOn", "Maps Activity " + alarmsStatus.get(i));
            if (alarmsStatus.get(i)) {
                circleOptions = new CircleOptions()
                        .center(alarmsPosition.get(i))
                        .radius(alarmsRadius.get(i))
                        .fillColor(Color.TRANSPARENT)
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);
                mMap.addCircle(circleOptions);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpAlarmMarkers();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals(MARKER_TAG)) {
                        return false;
                    } else {
                        Intent editAlarm = new Intent(MapsActivity.this, EditAlarmActivity.class)
                                .putExtra(COORDINATE_TAG, marker.getPosition())
                                .putExtra(ALARM_TAG, marker.getTitle());
                        startActivity(editAlarm);
                        return true;
                    }
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                apiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        marker.remove();
        handleNewLocation(myLocation);
    }

    private void handleNewLocation(Location myLocation) {
        myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                .title(MARKER_TAG)
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location_icon)));
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
