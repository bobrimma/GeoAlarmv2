package com.blacky.geoalarmv2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {
    private static final List<GAGeofence> savedAlarms = new ArrayList<GAGeofence>();

    protected static void saveAlarm(GAGeofence geofence){
        savedAlarms.add(geofence);
    }

    protected static int getAlarmsNumber(){
        return  savedAlarms.size();
    }

    protected static List<LatLng> getAlarmsPositions(){
        List <LatLng> alarmsPosition =new ArrayList<LatLng>();
        for (GAGeofence alarm: savedAlarms){
            alarmsPosition.add(new LatLng(alarm.getGeofenceLatitude(), alarm.getGeofenceLongitude()));
        }
        return alarmsPosition;
    }

    protected static List<Double> getAlarmsRadius(){
        List <Double> alarmsRadius =new ArrayList<Double>();
        for (GAGeofence alarm: savedAlarms){
            alarmsRadius.add(alarm.getGeofenceRadius());
        }
        return alarmsRadius;
    }
    protected static List<Boolean> getAlarmsStatus(){
        List <Boolean> alarmsStatus=new ArrayList<Boolean>();
        for (GAGeofence alarm: savedAlarms){
            alarmsStatus.add(alarm.isEnabled());
        }
        return alarmsStatus;
    }
}
