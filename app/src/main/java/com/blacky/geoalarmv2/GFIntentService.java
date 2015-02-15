package com.blacky.geoalarmv2;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GFIntentService extends IntentService {

    public static final String GF_INTENT_SERVICE = "GFIntentService";

    public GFIntentService() {
        super(GF_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent gfEvent = GeofencingEvent.fromIntent(intent);
            if (gfEvent.hasError()){
                Log.d(GF_INTENT_SERVICE, "Geofencing event came with an error");
                return;
            }
            int gfTransition = gfEvent.getGeofenceTransition();
            List<Geofence> gfList= gfEvent.getTriggeringGeofences();
            List<String> triggeredGfIds = new ArrayList<>();
            for (Geofence gf: gfList){
                Log.d(GF_INTENT_SERVICE, "Triggering gf " + gf.getRequestId());
                processGeofence(gf, gfTransition);
                triggeredGfIds.add(gf.getRequestId());
            }
            removeGeofences(triggeredGfIds);
        }
    }

    private void processGeofence(Geofence geofence, int transitionType) {

        PendingIntent openActivityIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, MapsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        int id = Integer.parseInt(geofence.getRequestId());
        Notification ntf = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Geofence id: " + id)
                .setContentText("Transition type: enter. "+AlarmStorage.getAlarmName(id)+" : "+AlarmStorage.getAlarmDescription(id))
                .setVibrate(new long[]{500, 500})
                .setContentIntent(openActivityIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(transitionType * 100 + id, ntf);

        Log.d("GEO", "notification built:" + id);
    }

    private void removeGeofences(List<String> requestIds) {
        Intent intent = new Intent(getApplicationContext(), GeofencingService.class);
        intent.putStringArrayListExtra(GeofencingService.EXTRA_REQUEST_IDS, (ArrayList<String>) requestIds);
        intent.putExtra(GeofencingService.EXTRA_ACTION, GeofencingService.ACTION_REMOVE);
        for (String strId: requestIds){
            AlarmStorage.setAlarmOff(Integer.parseInt(strId));
        }
        startService(intent);
    }

}