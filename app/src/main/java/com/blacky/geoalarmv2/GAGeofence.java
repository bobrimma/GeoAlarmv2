package com.blacky.geoalarmv2;

import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class GAGeofence implements Serializable {
    private int id;
    private boolean enabled;
    private double latitude;
    private double longitude;
    private float radius;
    private int transType;
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public GAGeofence(int id, boolean enabled, double latitude, double longitude, float radius, int transType) {
        this.id = id;
        this.enabled = enabled;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.transType = transType;

    }

    public double getGAGeofenceLatitude() {
        return latitude;
    }

    public int getGAGeofenceID() {
        return id;
    }

    public double getGAGeofenceLongitude() {
        return longitude;
    }

    public float getGAGeofenceRadius() {
        return radius;
    }

    public boolean isEnabled() {
        return enabled;
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


