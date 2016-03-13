package com.app.projectcapstone.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by QuyPH on 3/13/2016.
 */
public class GCMWakefulReceiver extends WakefulBroadcastReceiver {
   private static final String LOG_TAG = GCMWakefulReceiver.class.getSimpleName();

   @Override
   public void onReceive(Context context, Intent intent) {
      Log.w(LOG_TAG, "Receive GCM: " + intent);
      // Explicitly specify that GcmIntentService will handle the intent.
      ComponentName comp = new ComponentName(context.getPackageName(), GCMHandleService.class.getName());
      // Start the service, keeping the device awake while it is launching.
      startWakefulService(context, (intent.setComponent(comp)));
   }
}
