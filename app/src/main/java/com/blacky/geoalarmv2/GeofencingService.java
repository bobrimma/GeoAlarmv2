package com.blacky.geoalarmv2;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeofencingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String LOG_TAG = "GFService";
    public static final String EXTRA_REQUEST_IDS = "requestId";
    public static final String EXTRA_GEOFENCE = "geofence";
    public static final String EXTRA_GEOFENCE_ID = "geofenceId";
    public static final String EXTRA_ACTION = "action";
    public static final int ACTION_ADD = 0;
    public static final int ACTION_REMOVE = 1;
    public static final int ACTION_REMOVE_BY_ID = 2;
    private int action;
    private GoogleApiClient locationClient;
    private List<Geofence> gfList = new ArrayList<>();
    private List<String> gfToRemove = new ArrayList<>();

    public GeofencingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Location service started");
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationClient.connect();
        action = intent.getIntExtra(EXTRA_ACTION, -1);
        switch (action) {
            case ACTION_ADD: {
                GAGeofence geofence = (GAGeofence) intent.getSerializableExtra(EXTRA_GEOFENCE);
                gfList.add(geofence.toGeofence());
                break;
            }
            case ACTION_REMOVE: {
                gfToRemove = intent.getStringArrayListExtra(EXTRA_REQUEST_IDS);
                break;
            }
            case ACTION_REMOVE_BY_ID: {
                gfToRemove.add(intent.getStringExtra(EXTRA_GEOFENCE_ID));
                break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "Location client connected");
        switch (action) {
            case ACTION_ADD: {
                Log.d(LOG_TAG, "Adding geofence to the client");
                GeofencingRequest gfRequest;
                gfRequest = new GeofencingRequest.Builder().addGeofences(gfList).build();
                LocationServices.GeofencingApi
                        .addGeofences(locationClient, gfRequest, getPendingIntent())
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    locationClient.disconnect();
                                    stopSelf();
                                }
                            }
                        });
                break;
            }
            case ACTION_REMOVE: ACTION_REMOVE_BY_ID:{
                Log.d(LOG_TAG, "Removing gf-s from the client");
                LocationServices.GeofencingApi
                        .removeGeofences(locationClient, gfToRemove)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()){
                                    locationClient.disconnect();
                                    stopSelf();
                                }
                            }
                        });
                break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private PendingIntent getPendingIntent() {
        Intent transitionService = new Intent(this, GFIntentService.class);
        return PendingIntent.getService(this, 0, transitionService, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}