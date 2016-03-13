package com.app.projectcapstone.utils;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by QuyPH on 2/18/2016.
 */
public class BluetoothUtils {
   private static final String TAG = BluetoothUtils.class.getSimpleName();
   private static BluetoothUtils instance;

   public static BluetoothUtils getInstance() {
      if (instance == null) {
         instance = new BluetoothUtils();
      }
      return instance;
   }

   //For Pairing
   public void pairDevice(BluetoothDevice device) {
      try {
         Log.d("pairDevice()", "Start Pairing...");
         Method m = device.getClass().getMethod("createBond", (Class[]) null);
         m.invoke(device, (Object[]) null);
         Log.d("pairDevice()", "Pairing finished.");
      } catch (Exception e) {
         Log.e("pairDevice()", e.getMessage());
      }
   }


   //For UnPairing
   public void unpairDevice(BluetoothDevice device) {
      try {
         Log.d("unpairDevice()", "Start Un-Pairing...");
         Method m = device.getClass().getMethod("removeBond", (Class[]) null);
         m.invoke(device, (Object[]) null);
         Log.d("unpairDevice()", "Un-Pairing finished.");
      } catch (Exception e) {
         Log.e(TAG, e.getMessage());
      }
   }
}
