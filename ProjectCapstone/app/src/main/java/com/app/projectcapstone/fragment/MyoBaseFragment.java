package com.app.projectcapstone.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.projectcapstone.callback.MyoGattCallback;
import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.model.myo.GestureSaveMethod;
import com.app.projectcapstone.model.myo.MyoCommandList;
import com.thalmic.myo.Myo;

import java.util.Set;

/**
 * Created by QuyPH on 2/25/2016.
 */
public abstract class MyoBaseFragment extends Fragment {
   private static final String TAG = MyoBaseFragment.class.getSimpleName();

   protected byte[] emgDataBytes = new byte[16];
   protected TextView statusText;
   protected Button btnVibrate;
   protected Button btnEMG;
   protected Button btnStopEmg;

   protected Handler mHandler;
   private String hand;

   protected View view;
   protected BluetoothGatt mBluetoothGatt;
   protected MyoGattCallback mMyoCallback;

   protected GestureSaveMethod saveMethod;
   protected BluetoothAdapter mBluetoothAdapter;
   protected Myo myo;
   protected MyoCommandList commandList = new MyoCommandList();

   protected Set<BluetoothDevice> pairedDevices;

   protected int numberOfHz = Constant.DEFAULT_HZ_BREAK_EVENT;

   public MyoBaseFragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      this.mBluetoothAdapter = mBluetoothAdapter;
      this.myo = myo;
      this.hand = myo.getArm().toString();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      mHandler = new Handler();
      view = inflater.inflate(getLayoutId(), container, false);
      return view;
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      pairedDevices = mBluetoothAdapter.getBondedDevices();
      if (pairedDevices.isEmpty()) {
         Log.w(TAG, "No paired device!");

      } else {
         for (BluetoothDevice device : pairedDevices) {
            Log.w(TAG, "Device: " + device.getName() + " | " + device.getAddress() + " | " + device.getBondState());
         }
      }
      initView(view);
      bindingData(view);
   }

   public void onClickVibration(View v) {
      if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendVibration3())) {
         Log.d(TAG, "False Vibrate");
      }
   }

   public void onClickUnlock(View v) {
      if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendUnLock())) {
         Log.d(TAG, "False UnLock");
      }
   }

   public void onClickEMG() {
      if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendEmgOnly())) {
         Log.d(TAG, "False EMG");
      } else {
         saveMethod = new GestureSaveMethod();
         if (saveMethod.getSaveState() == GestureSaveMethod.SaveState.Have_Saved) {
            //gestureText.setText("DETECT Ready");
         } else {
            //gestureText.setText("Teach me \'Gesture\'");
         }
      }
   }

   public void onClickNoEMG() {
      if (mBluetoothGatt == null
            || !mMyoCallback.setMyoControlCommand(commandList.sendUnsetData())
            || !mMyoCallback.setMyoControlCommand(commandList.sendNormalSleep())) {
         Log.d(TAG, "False Data Stop");
      }
   }

   public void closeBLEGatt() {
      if (mBluetoothGatt == null) {
         return;
      }
      mMyoCallback.stopCallback();
      mBluetoothGatt.close();
      mBluetoothGatt = null;
   }

   @Override
   public void onStop() {
      super.onStop();
      this.closeBLEGatt();
   }

   public Myo getMyo() {
      return myo;
   }

   public void setMyo(Myo myo) {
      this.myo = myo;
   }

   protected abstract int getLayoutId();

   protected abstract void initView(View view);

   protected abstract void bindingData(View view);
}
