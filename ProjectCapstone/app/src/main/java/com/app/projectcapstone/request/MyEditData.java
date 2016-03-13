package com.app.projectcapstone.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by QuyPH on 3/9/2016.
 */
public class MyEditData {
   @SerializedName("editResult")
   private List<String> list;

   @SerializedName("position")
   private List<Integer> position;

   public MyEditData(List<String> list, List<Integer> position) {
      this.list = list;
      this.position = position;
   }

   public List<String> getList() {
      return list;
   }

   public void setList(List<String> list) {
      this.list = list;
   }

   public List<Integer> getPosition() {
      return position;
   }

   public void setPosition(List<Integer> position) {
      this.position = position;
   }
}
