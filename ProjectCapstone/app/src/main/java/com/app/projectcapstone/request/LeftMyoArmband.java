package com.app.projectcapstone.request;

import com.app.projectcapstone.model.myo.EmgData;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * Created by QuyPH on 2/22/2016.
 */
public class LeftMyoArmband implements Serializable {
   @SerializedName("left")
   private List<EmgData> left;

   public LeftMyoArmband(List<EmgData> left) {
      this.left = left;
   }

   public List<EmgData> getLeft() {
      return left;
   }

   public void setLeft(List<EmgData> left) {
      this.left = left;
   }

   @Override
   public String toString() {
      return "LeftMyoArmband{" +
            "left=" + left.size() +
            '}';
   }
}
