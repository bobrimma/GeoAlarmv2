package com.blacky.geoalarmv2;

import android.app.IntentService;
import android.content.Intent;

public class GFIntentService extends IntentService {

    public GFIntentService() {
        super("GFIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }

}
