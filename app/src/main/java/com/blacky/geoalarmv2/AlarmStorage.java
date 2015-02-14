package com.blacky.geoalarmv2;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class AlarmStorage {
    private static final List<GAGeofence> savedAlarms = new ArrayList<GAGeofence>();

    protected static List<GAGeofence> getSavedAlarms(){
        return  savedAlarms;
    }

    protected static void saveAlarm(GAGeofence geofence){
        savedAlarms.add(geofence);
    }
}
