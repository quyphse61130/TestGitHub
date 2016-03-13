package com.app.projectcapstone.manager;

import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyPH on 3/5/2016.
 */
public class MyoHolderManager {
   private List<Myo> myoList;

   private static MyoHolderManager instance;

   public MyoHolderManager() {
      this.myoList = new ArrayList<>();
   }

   public static MyoHolderManager getInstance() {
      if (instance == null) {
         instance = new MyoHolderManager();
      }
      return instance;
   }

   public void addMyo(Myo myo) {
      this.myoList.add(myo);
   }

   public void removeMyo(Myo myo) {
      this.myoList.remove(myo);
   }

   public List<Myo> getMyoList() {
      return myoList;
   }
}
