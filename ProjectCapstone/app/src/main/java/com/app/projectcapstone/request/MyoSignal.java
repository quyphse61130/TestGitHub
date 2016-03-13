package com.app.projectcapstone.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QuyPH on 2/25/2016.
 */
public class MyoSignal {

   @SerializedName("lEmgJson")
   private LeftMyoArmband lEmgJson;

   @SerializedName("rEmgJson")
   private RightMyoArmband rEmgJson;

   public MyoSignal(LeftMyoArmband lEmgJson, RightMyoArmband rEmgJson) {
      this.lEmgJson = lEmgJson;
      this.rEmgJson = rEmgJson;
   }

   public LeftMyoArmband getlEmgJson() {
      return lEmgJson;
   }

   public void setlEmgJson(LeftMyoArmband lEmgJson) {
      this.lEmgJson = lEmgJson;
   }

   public RightMyoArmband getrEmgJson() {
      return rEmgJson;
   }

   public void setrEmgJson(RightMyoArmband rEmgJson) {
      this.rEmgJson = rEmgJson;
   }
}
