package com.app.projectcapstone.utils;

import android.database.sqlite.*;
import android.app.Activity;

import java.io.Serializable;

/**
 * Created by QuyPH on 3/9/2016.
 */
public class DBUtils {

   private static DBUtils instance;
   private Activity activity;

   public static DBUtils getInstance(Activity activity) {
      if (instance == null) {
         instance = new DBUtils();
      }
      instance.activity = activity;
      return instance;
   }


   private SQLiteDatabase database = null;

   public void CreateDB() {
      database = activity.openOrCreateDatabase("Myo1.db", Activity.MODE_PRIVATE, null);
   }

   public void DropDB() {
      String dropDB = "DROP DATABASE Myo1.db";
      database.execSQL(dropDB);
   }

   //1 meaningLeft
   public void CreateMeaningLeft() {
      String tbMeaningLeft = "CREATE TABLE meaningLeft (meaningLeft integer primary key)";
      database.execSQL(tbMeaningLeft);
   }

   //2 meaningRignt
   public void CreateMeaningRight() {
      String tbMeaningRight = "CREATE TABLE meaningRight (meaningRight integer primary key)";
      database.execSQL(tbMeaningRight);
   }


   //3 leftSignal
   public void CreateLeftSignal() {
      String tbMeaningLeft = "CREATE TABLE leftSignal (emgCode text primary key,meaningLeft text not null constraint meaningLeft references meaningLeft(meaningLeft) on delete cascade,isCustom integer not null)";
      database.execSQL(tbMeaningLeft);
   }

   //4 rightSignal
   public void CreateRightSignal() {
      String tbMeaningRight = "CREATE TABLE rightSignal (emgCode text primary key,meaningRight text not null constraint meaningRight references meaningRight(meaningRight) on delete cascade,isCustom integer not null)";
      database.execSQL(tbMeaningRight);
   }


   //5 dataContent
   public void CreateDataContent() {
      String tbDataContent = "CREATE TABLE dataContent (meaningCode integer primary key autoincrement,meaning text not null,library integer not null)";
      database.execSQL(tbDataContent);
   }

   //6 wordSignal
   public void CreateWordSignal() {
      String tbWordSignal = "CREATE TABLE wordSignal (meaningLeft integer,meaningRight integer,meaningCode integer not null constraint meaningCode references dataContent(meaningCode) on delete cascade, primary key(meaningLeft,meaningRight))";
      database.execSQL(tbWordSignal);
   }

   //7 customSignal
   public void CreateCustomSignal() {
      String tbCustomSignal = "CREATE TABLE customSignal(customLeft integer,customRight integer,meaningCode integer not null constraint meaningCode references customContent(meaningCode) on delete cascade,primary key(customLeft,customRight))";
      database.execSQL(tbCustomSignal);
   }

   //8 customContent
   public void CreateCustomContent() {
      String tbCustomContent = "CREATE TABLE customContent (meaningCode integer primary key autoincrement,meaning text not null,custId integer not null,status integer not null)";
      database.execSQL(tbCustomContent);
   }

}
