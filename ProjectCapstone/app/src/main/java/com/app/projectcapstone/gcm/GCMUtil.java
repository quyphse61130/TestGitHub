package com.app.projectcapstone.gcm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.app.projectcapstone.constant.PreferenceConstant;
import com.app.projectcapstone.manager.PreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by QuyPH on 3/13/2016.
 */
public class GCMUtil {
   private static final String LOG_TAG = GCMUtil.class.getSimpleName();
   private static final String GCM_SENDER_ID = "34776973278";

   public static boolean checkPlayServices(Context context) {
      int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
      if (resultCode == ConnectionResult.SUCCESS) {
         Log.d(LOG_TAG, "checkPlayServices() This device support gcm.");
         return true;
      }
      Log.w(LOG_TAG, "checkPlayServices() This device don't support gcm.");
      return false;
   }

   public static boolean registerID(Context context) {
      boolean isRegisterSuccess = false;
      // Check need to clear previous gcm registration id cause application has been upgraded, gcm with previous registration id may not working
      if (PreferenceManager.getInstance().getSharedPreferences().getInt(PreferenceConstant.PREF_APP_VERSION, -1) != getAppVersion(context)) {
         // Clear previous gcm registration id
         PreferenceManager.getInstance().getSharePreferenceEditor().putString(PreferenceConstant.PREF_GCM_REGISTRATION_ID, null).commit();
      }
      // Check Google cloud message registered
      if (PreferenceManager.getInstance().getSharedPreferences().getString(PreferenceConstant.PREF_GCM_REGISTRATION_ID, null) == null) {
         GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
         try {
            String registrationId = gcm.register(GCM_SENDER_ID);
            // Save to preferences
            PreferenceManager.getInstance().getSharePreferenceEditor().putString(PreferenceConstant.PREF_GCM_REGISTRATION_ID, registrationId).commit();
            int appVersion = getAppVersion(context);
            PreferenceManager.getInstance().getSharePreferenceEditor().putInt(PreferenceConstant.PREF_APP_VERSION, appVersion).commit();
            isRegisterSuccess = true;
         } catch (IOException e) {
            // Register gcm registration id failed
            Log.w(LOG_TAG, "Exception: " + e.getMessage());
         }
      } else {
         isRegisterSuccess = true;
      }
      return isRegisterSuccess;
   }

   private static int getAppVersion(Context context) {
      try {
         PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
         return packageInfo.versionCode;
      } catch (PackageManager.NameNotFoundException e) {
         // should never happen
         throw new RuntimeException("Could not get package name: " + e);
      }
   }
}
