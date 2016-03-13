package com.app.projectcapstone.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by QuyPH on 2/22/2016.
 */
public class Response {
   @SerializedName("data")
   private Object data;
   @SerializedName("statusCode")
   private int statusCode;
   @SerializedName("message")
   private String message;

   private String stringData;

   public Response(int statusCode) {
      this.statusCode = statusCode;
   }

   public Response(Object data, int statusCode, String message) {
      this.data = data;
      this.statusCode = statusCode;
      this.message = message;
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getStringData() {
      return stringData;
   }

   public void setStringData(String stringData) {
      this.stringData = stringData;
   }

   @Override
   public String toString() {
      return "Response{" +
            "data=" + data +
            ", statusCode=" + statusCode +
            ", message='" + message + '\'' +
            '}';
   }
}
