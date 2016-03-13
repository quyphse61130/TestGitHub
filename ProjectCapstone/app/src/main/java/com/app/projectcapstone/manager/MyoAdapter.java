package com.app.projectcapstone.manager;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.thalmic.myo.Myo;

/**
 * Created by QuyPH on 3/5/2016.
 */
public class MyoAdapter extends ArrayAdapter<String> {

   private Context context;
   private int count;

   private static MyoAdapter instance;

   public static MyoAdapter getInstance(Context context, int count) {
      if(instance==null){
         instance= new MyoAdapter(context,count);
      }
      return instance;
   }

   public MyoAdapter(Context context, int count) {
      super(context, android.R.layout.simple_list_item_1);
      // Initialize adapter with items for each expected Myo.
      for (int i = 0; i < count; i++) {
         add("Waiting");
      }
   }

   public void setMessage(Myo myo, String message) {
      // identifyMyo returns IDs starting at 1, but the adapter indices start at 0.
      int index = identifyMyo(myo) - 1;
      // Replace the message.
      remove(getItem(index));
      insert(message, index);
   }
   private int identifyMyo(Myo myo) {
      return MyoHolderManager.getInstance().getMyoList().indexOf(myo) + 1;
   }
}