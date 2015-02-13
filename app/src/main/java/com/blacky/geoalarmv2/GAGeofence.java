package com.blacky.geoalarmv2;

import com.google.android.gms.location.Geofence;

import java.io.Serializable;

public class GAGeofence implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private float radius;
    private int transType;

    public GAGeofence(int id, double latitude, double longitude, float radius, int transType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.transType = transType;
    }

    public Geofence toGeofence() {
        return new Geofence.Builder()
                .setRequestId(String.valueOf(id))
                .setTransitionTypes(transType)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latitude, longitude, radius)
                .build();
    }
}
