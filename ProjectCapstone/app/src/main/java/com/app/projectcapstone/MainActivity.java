package com.app.projectcapstone;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.projectcapstone.bluetooth.BluetoothFragmentActivity;
import com.app.projectcapstone.constant.PreferenceConstant;
import com.app.projectcapstone.fragment.OfflineFragment;
import com.app.projectcapstone.fragment.OnlineFragment;
import com.app.projectcapstone.fragment.TrainFragment;
import com.app.projectcapstone.gcm.GCMUtil;
import com.app.projectcapstone.manager.MyoHolderManager;
import com.app.projectcapstone.manager.PreferenceManager;
import com.app.projectcapstone.utils.DBUtils;
import com.app.projectcapstone.utils.Utils;
import com.thalmic.myo.Myo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
      implements NavigationView.OnNavigationItemSelectedListener {
   private static final String TAG = MainActivity.class.getSimpleName();
   private Menu menu;
   private int currentFragment = 0;

   private static final int INDEX_HOME = 1;
   private static final int INDEX_ONLINE = 2;
   private static final int INDEX_OFFLINE = 3;
   private static final int INDEX_TRAIN = 4;
   private static final int INDEX_SHARE = 5;
   private static final int INDEX_BLUETOOTH = 6;

   private HomeFragmentActivity homeFragmentActivity;
   private TrainFragment.ReplaceFragmentCallback replaceFragmentCallback;

   private BluetoothAdapter bluetoothAdapter;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      // Dang ky notification
      bindingNetwork();

//      String s = "";
//      if (deleteDatabase("Myo1.db") == true) {
//         s = "Da xoa CSDL";
//      } else {
//         s = "Co loi, hoac chua xoa CSDL";
//      }
//      Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
//
//
//      DBUtils.getInstance(MainActivity.this).CreateDB();
//      DBUtils.getInstance(MainActivity.this).CreateMeaningLeft();
//      DBUtils.getInstance(MainActivity.this).CreateMeaningRight();
//      DBUtils.getInstance(MainActivity.this).CreateLeftSignal();
//      DBUtils.getInstance(MainActivity.this).CreateRightSignal();
//      DBUtils.getInstance(MainActivity.this).CreateDataContent();
//      DBUtils.getInstance(MainActivity.this).CreateWordSignal();
//      DBUtils.getInstance(MainActivity.this).CreateCustomContent();
//      DBUtils.getInstance(MainActivity.this).CreateCustomSignal();


      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
      ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
      drawer.setDrawerListener(toggle);
      toggle.syncState();

      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
      navigationView.setNavigationItemSelectedListener(this);

      homeFragmentActivity = HomeFragmentActivity.getInstance();
      replaceFragmentCallback = new TrainFragment.ReplaceFragmentCallback() {
         @Override
         public void moveToOnlineFragment(List<Myo> myoList) {
            replaceFragment(OnlineFragment.getInstance(), INDEX_HOME);
         }
      };

   }


   //.check blue on or off
   public boolean bluetoothIsOn() {
      if (bluetoothAdapter.isEnabled()) {
         return true;
      } else {
         return false;
      }
   }

   //..tao dailog
   public AlertDialog createAlertDialog() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("BLUETOOTH");
      builder.setMessage("Turn on Bluetooth before, please...!");
      builder.setCancelable(false);
      builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intent, 1000);
            //  replaceFragment(BluetoothFragmentActivity.getInstance(), INDEX_HOME);
//            Intent intent = new Intent(MainActivity.this, BluetoothFragmentActivity.class);
//            startActivity(intent);
//            finish();
         }
      });
      builder.setNeutralButton("Cancel", null);
      builder.setNegativeButton("Thoat", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            finish();
         }
      });
      AlertDialog dialog = builder.create();
      return dialog;
   }

   @Override
   public void onBackPressed() {
      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
      if (drawer.isDrawerOpen(GravityCompat.START)) {
         drawer.closeDrawer(GravityCompat.START);
      } else {
         Utils.showAlertDialog(MainActivity.this, "Are you sure.");
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.search, menu);
      this.menu = menu;
      return true;

   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.menu_search) {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @SuppressWarnings("StatementWithEmptyBody")
   @Override
   public boolean onNavigationItemSelected(MenuItem item) {
      // Handle navigation view item clicks here.
      int id = item.getItemId();

      if (id == R.id.nav_home) {
         replaceFragment(HomeFragmentActivity.getInstance(), currentFragment);
      } else if (id == R.id.nav_online) {
         Log.d(TAG, "onNavigationItemSelected: " + bluetoothIsOn());
         if (bluetoothIsOn()) {
            replaceFragment(OnlineFragment.getInstance(), currentFragment);
         } else {
            AlertDialog alertDialog = createAlertDialog();
            alertDialog.show();
         }

      } else if (id == R.id.nav_offline) {
         if (bluetoothIsOn()) {
            replaceFragment(OfflineFragment.getInstance(), currentFragment);
         } else {
            AlertDialog alertDialog = createAlertDialog();
            alertDialog.show();
         }

      } else if (id == R.id.nav_train) {
         if (bluetoothIsOn()) {
            replaceFragment(TrainFragment.getInstance(replaceFragmentCallback), currentFragment);
         } else {
            AlertDialog alertDialog = createAlertDialog();
            alertDialog.show();
         }

      } else if (id == R.id.nav_share) {

      } else if (id == R.id.nav_bluetooth) {
         replaceFragment(BluetoothFragmentActivity.getInstance(), currentFragment);
      }

      DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
      drawer.closeDrawer(GravityCompat.START);
      return true;
   }

   private void replaceFragment(Fragment fragment, int currentFragment) {
      if (this.menu != null) {
         if (fragment instanceof HomeFragmentActivity) {
            this.menu.findItem(R.id.menu_search).setVisible(true);
         } else {
            this.menu.findItem(R.id.menu_search).setVisible(false);
         }
      }
      FragmentManager fragmentManager = getSupportFragmentManager();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.replace(R.id.container, fragment);
      transaction.commit();
      this.currentFragment = currentFragment;
   }


   @Override
   protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putInt("CURRENT_FRAGMENT", currentFragment);
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      int currentFragmentState = savedInstanceState.getInt("CURRENT_FRAGMENT");
      switch (currentFragmentState) {
         case INDEX_HOME:
            replaceFragment(HomeFragmentActivity.getInstance(), INDEX_HOME);
            break;
         case INDEX_ONLINE:

            replaceFragment(OnlineFragment.getInstance(), INDEX_ONLINE);

            break;
         case INDEX_OFFLINE:
            replaceFragment(OfflineFragment.getInstance(), INDEX_OFFLINE);
            break;
         case INDEX_TRAIN:
            replaceFragment(TrainFragment.getInstance(replaceFragmentCallback), INDEX_TRAIN);
            break;
         case INDEX_BLUETOOTH:
            // replaceFragment(TrainFragment.getInstance(), INDEX_BLUETOOTH);
            break;
         default:
            break;
      }
   }

   @Override
   public boolean dispatchKeyEvent(KeyEvent event) {
      if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
         // Dummy database for testing
         String databasePath = getDatabasePath("Myo1.db").getAbsolutePath();
         String dummyDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Myo1.db";
         try {
            copyFile(databasePath, dummyDatabasePath);
            Log.w(TAG, "Dummy database successfully at location: " + dummyDatabasePath);
         } catch (IOException e) {
            Log.w(TAG, "Exception dummy database: " + e);
         }
      }
      return super.dispatchKeyEvent(event);
   }

   public void copyFile(String sourcePath, String destPath) throws IOException {
      File sourceFile = new File(sourcePath);
      File destFile = new File(destPath);
      if (!destFile.exists()) {
         destFile.createNewFile();
      }

      FileChannel source = null;
      FileChannel destination = null;

      try {
         source = new FileInputStream(sourceFile).getChannel();
         destination = new FileOutputStream(destFile).getChannel();
         destination.transferFrom(source, 0, source.size());
      } finally {
         if (source != null) {
            source.close();
         }
         if (destination != null) {
            destination.close();
         }
      }
   }

   private void bindingNetwork() {
      if (PreferenceManager.getInstance().getSharedPreferences().getString(PreferenceConstant.PREF_GCM_REGISTRATION_ID, null) == null) {
         new Thread(new Runnable() {
            @Override
            public void run() {
               if (GCMUtil.checkPlayServices(MainActivity.this)) {
                  if (GCMUtil.registerID(MainActivity.this)) {
                     final String notificationId = PreferenceManager.getInstance().getSharedPreferences().getString(PreferenceConstant.PREF_GCM_REGISTRATION_ID, null);
                     Log.w(TAG, "Push notification id: " + notificationId);
                  }
               } else {
                  Log.w(TAG, "Detect: Google Play Service isn't installed or need to be updated");
               }
            }
         }).start();
      }
   }

}
