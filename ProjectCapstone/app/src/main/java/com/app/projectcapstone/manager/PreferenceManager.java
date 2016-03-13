package com.app.projectcapstone.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by QuyPH on 3/5/2016.
 */
public class PreferenceManager {
   private static PreferenceManager instance;
   private SharedPreferences sharedPreferences;

   public PreferenceManager(Context context) {
      sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
   }

   public static void initialize(Context context) {
      instance = new PreferenceManager(context);
   }

   public static PreferenceManager getInstance() {
      return instance;
   }

   public SharedPreferences getSharedPreferences() {
      return sharedPreferences;
   }

   public SharedPreferences.Editor getSharePreferenceEditor() {
      return sharedPreferences.edit();
   }
}
