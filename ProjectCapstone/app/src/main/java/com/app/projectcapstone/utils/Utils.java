package com.app.projectcapstone.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.app.projectcapstone.application.Application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by QuyPH on 3/1/2016.
 */
public class Utils {
   public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
   private static final String PARAMETER_SEPARATOR = "&";
   private static final String NAME_VALUE_SEPARATOR = "=";

   private static Utils instance;

   public static Utils getInstance() {
      if (instance == null) {
         instance = new Utils();
      }

      return instance;
   }

   public static String formatParams(
         Map<String, String> parameters,
         String encoding) {
      final StringBuilder result = new StringBuilder();

      try {
         Set paramsSet = parameters.entrySet();
         Map.Entry<String, String> entryParam = null;
         for (Object parameter : paramsSet) {
            entryParam = (Map.Entry<String, String>) parameter;

            String encodedName = URLEncoder.encode(entryParam.getKey(), encoding);
            String value = entryParam.getValue();
            String encodedValue = value != null ? URLEncoder.encode(value, encoding) : "";
            if (result.length() > 0)
               result.append(PARAMETER_SEPARATOR);
            result.append(encodedName);
            result.append(NAME_VALUE_SEPARATOR);
            result.append(encodedValue);
         }
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return result.toString();
   }
   public static void showAlertDialog(final Activity activity,String msg){
      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
            activity);

      // set dialog message
      alertDialogBuilder
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
                  // if this button is clicked, close
                  // current activity
                  activity.finish();
               }
            })
            .setNegativeButton("No",new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog,int id) {
                  // if this button is clicked, just close
                  // the dialog box and do nothing
                  dialog.cancel();
               }
            });

      // create alert dialog
      AlertDialog alertDialog = alertDialogBuilder.create();

      // show it
      alertDialog.show();
   }

}
