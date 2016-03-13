package com.app.projectcapstone.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.app.projectcapstone.MainActivity;
import com.app.projectcapstone.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by QuyPH on 3/13/2016.
 */
public class GCMHandleService extends IntentService {
   private static final String LOG_TAG = GCMHandleService.class.getSimpleName();
   public static final int NOTIFICATION_ID = 1;

   public GCMHandleService() {
      super(LOG_TAG);
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      Bundle extras = intent.getExtras();

      if (!extras.isEmpty()) {
         GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
         String messageType = gcm.getMessageType(intent);
         if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            // Post notification of received message.
            sendNotification(extras.getString("message"));
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GCMWakefulReceiver.completeWakefulIntent(intent);
         }
      }
   }

   private void sendNotification(String text) {
      Intent myIntent;
      String notificationTitle = "Test notification";

      // Prepare intent to transfer data
      myIntent = new Intent(this, MainActivity.class);

      NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancel(0);

      myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

      NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
      inboxStyle.addLine(text);
      inboxStyle.addLine(text);

      Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

      NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(largeIcon)
            .setContentTitle(notificationTitle)
            .setStyle(inboxStyle)
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setDefaults(Notification.DEFAULT_ALL);
      mBuilder.setContentIntent(contentIntent);
      mBuilder.setAutoCancel(true);
      notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   }
}
