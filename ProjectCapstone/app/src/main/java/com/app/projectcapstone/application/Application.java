package com.app.projectcapstone.application;

import com.app.projectcapstone.manager.PreferenceManager;
import com.app.projectcapstone.utils.DBUtils;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class Application extends android.app.Application {
   private static Application application;

   public static Application getInstance() {
      return application;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      application = this;

      PreferenceManager.initialize(this);


   }
}
