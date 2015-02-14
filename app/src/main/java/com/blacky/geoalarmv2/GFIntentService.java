package com.blacky.geoalarmv2;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GFIntentService extends IntentService {


    final static String INTENT_SERVICE_TAG = "GFIntentService";

    public GFIntentService() {
        super("GFIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();

            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();
            List<String> triggeredIds = new ArrayList<String>();

            for (Geofence geofence : triggeredGeofences) {
                Log.d("GEO", "onHandle:" + geofence.getRequestId());
                processGeofence(geofence, transition);
                triggeredIds.add(geofence.getRequestId());
            }

            if (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
                removeGeofences(triggeredIds);
        }
        else{
            Log.d(INTENT_SERVICE_TAG, "Geofencing event has error.");
        }

    }

    private void processGeofence(Geofence geofence, int transitionType) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());

        PendingIntent openMapsActivity = PendingIntent.getActivity(this, 0, new Intent(this, MapsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        int id = Integer.parseInt(geofence.getRequestId());

        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Geofence id: " + id)
                .setVibrate(new long[]{500, 500})
                .setContentIntent(openMapsActivity)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(transitionType * 100 + id, notificationBuilder.build());

        Log.d("GEO", "notification built:" + id);
    }

    private void removeGeofences(List<String> requestIds) {
        Intent intent = new Intent(getApplicationContext(), GeofencingService.class);

//        String[] ids = new String[0];
//        intent.putExtra(GeofencingService.EXTRA_REQUEST_IDS, requestIds.toArray(ids));
//        intent.putExtra(GeofencingService.EXTRA_ACTION, GeofencingService.Action.REMOVE);

        startService(intent);
    }
}
