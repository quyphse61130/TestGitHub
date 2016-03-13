package com.app.projectcapstone.request;

import com.app.projectcapstone.model.myo.EmgData;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * Created by QuyPH on 2/22/2016.
 */
public class RightMyoArmband implements Serializable {
   @SerializedName("right")
   private List<EmgData> right;

   public RightMyoArmband() {
   }

   public RightMyoArmband(List<EmgData> right) {
      this.right = right;
   }

   public List<EmgData> getRight() {
      return right;
   }

   public void setRight(List<EmgData> right) {
      this.right = right;
   }

   @Override
   public String toString() {
      return "RightMyoArmband{" +
            "right=" + right.size() +
            '}';
   }
}
