package com.blacky.geoalarmv2;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlarmStorage {
    // private static final List<GAGeofence> savedAlarms = new ArrayList<GAGeofence>();
    private static final Map<Integer, GAGeofence> savedAlarms = new Hashtable<>();


    protected static void saveAlarm(GAGeofence geofence) {
        savedAlarms.put(geofence.getGAGeofenceID(), geofence);
    }

    protected static int getAlarmsNumber() {
        return savedAlarms.size();
    }

    protected static List<LatLng> getAllAlarmsPositions() {
        List<LatLng> alarmsPosition = new ArrayList<LatLng>();
        for (Map.Entry<Integer, GAGeofence> alarmEntry : savedAlarms.entrySet()) {
            alarmsPosition.add(new LatLng(alarmEntry.getValue().getGAGeofenceLatitude(), alarmEntry.getValue().getGAGeofenceLongitude()));
        }
        return alarmsPosition;
    }

    protected static List<Float> getAllAlarmsRadius() {
        List<Float> alarmsRadius = new ArrayList<Float>();
        for (Map.Entry<Integer, GAGeofence> alarmEntry : savedAlarms.entrySet()) {
            alarmsRadius.add(alarmEntry.getValue().getGAGeofenceRadius());
        }
        return alarmsRadius;
    }

    protected static List<Boolean> getAllAlarmsStatus() {
        List<Boolean> alarmsStatus = new ArrayList<Boolean>();
        for (Map.Entry<Integer, GAGeofence> alarmEntry : savedAlarms.entrySet()) {
            alarmsStatus.add(alarmEntry.getValue().isEnabled());
        }
        return alarmsStatus;
    }
    protected static List<String> getAllAlarmsNames() {
        List<String> alarmsNames = new ArrayList<>();
        for (Map.Entry<Integer, GAGeofence> alarmEntry : savedAlarms.entrySet()) {
            alarmsNames.add(alarmEntry.getValue().getName());
        }
        return alarmsNames;
    }
    protected static LatLng getAlarmPosition(Integer id) {
        double alarmLatitude = savedAlarms.get(id).getGAGeofenceLatitude();
        double alarmLongitude = savedAlarms.get(id).getGAGeofenceLongitude();
        return new LatLng(alarmLatitude, alarmLongitude);
    }

    protected static List<Integer> getAllAlarmsIds() {
        return new ArrayList<>(savedAlarms.keySet());
    }

    protected static float getAlarmRadius(Integer id) {
        return savedAlarms.get(id).getGAGeofenceRadius();
    }

    protected static boolean isAlarmOn(Integer id) {
        return savedAlarms.get(id).isEnabled();
    }


    protected static String getAlarmName(Integer id) {
        return savedAlarms.get(id).getName();
    }

    protected static String isAlarmDescription(Integer id) {
        return savedAlarms.get(id).getDescription();
    }

    public static String getAlarmDescription(Integer id) {
        return savedAlarms.get(id).getDescription();
    }

    public static void editAlarm(Integer id,String newName, String newDescription, float newRadius, boolean enable){
        Log.d("alarmOn", "in Storage " + enable);
        GAGeofence alarm=savedAlarms.get(id);
        alarm.setName(newName);
        alarm.setDescription(newDescription);
        alarm.setRadius(newRadius);
        alarm.setEnabled(enable);
    }

    public static void deleteAlarm(Integer id){
        savedAlarms.remove(id);
    }

    public static void setAlarmOff(Integer id){
        savedAlarms.get(id).setEnabled(false);

    }
}
