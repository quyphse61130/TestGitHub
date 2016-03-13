package com.app.projectcapstone.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.app.projectcapstone.R;
import com.app.projectcapstone.callback.MyoGattCallback;
import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.model.myo.EmgCharacteristicData;
import com.app.projectcapstone.model.myo.EmgData;
import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyPH on 2/27/2016.
 */
public class Myo2Fragment extends MyoBaseFragment {
   private static final String LOG_TAG = Myo2Fragment.class.getSimpleName();
   private static Myo2Fragment instance;
   private List<EmgData> emgDataList;

   private Myo2FragmentEndEventCallback myo2FragmentEndEventCallback;
   private Thread processThread = new Thread();

   public Myo2Fragment(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      super(myo, mBluetoothAdapter);
      mHandler = new Handler();
      emgDataList = new ArrayList<>();
   }

   public static Myo2Fragment getInstance(Myo myo, BluetoothAdapter mBluetoothAdapter, Myo2FragmentEndEventCallback myo2FragmentEndEventCallback) {
      if (instance == null) {
         instance = new Myo2Fragment(myo, mBluetoothAdapter);
      }
      instance.myo2FragmentEndEventCallback = myo2FragmentEndEventCallback;
      return instance;
   }


   @Override
   protected int getLayoutId() {
      return R.layout.fragment_myo2;
   }

   @Override
   protected void initView(View view) {
      statusText = (TextView) view.findViewById(R.id.emgDataTextView);
      btnVibrate = (Button) view.findViewById(R.id.bVibrate);
      btnEMG = (Button) view.findViewById(R.id.bEMG);
      btnStopEmg = (Button) view.findViewById(R.id.bStopEmg);
   }

   private MyoGattCallback.IdentifyEmgDataCallback identifyEmgDataCallback = new MyoGattCallback.IdentifyEmgDataCallback() {

      @Override
      public void onReceiveText(final byte[] emgDataBytes) {
         //   if (OnlineFragment.isClickDetect) {
         if (numberOfHz == Constant.DEFAULT_HZ_BREAK_EVENT) {//lam gi
            EmgCharacteristicData emgCharacteristicData = new EmgCharacteristicData(emgDataBytes);
            EmgData emgData = emgCharacteristicData.getEmg8Data_abs();
            emgDataList.add(emgData);
            Log.w(LOG_TAG, "Size left: " + emgDataList.size() + " | " + emgData.toString());

            if (emgData.getSumEmgData() < Constant.DEFAULT_END_EVENT) {
               myo2FragmentEndEventCallback.onEndEvent(emgDataList);
               emgDataList.clear();
            } else {
               OnlineFragment.restConditionRightHand = 0;
            }
            numberOfHz = Constant.DEFAULT_HZ_START_EVENT;
         } else {
            numberOfHz++;
         }
         //  }
      }
   };

   @Override
   protected void bindingData(View view) {
      for (BluetoothDevice bluetoothDevice : pairedDevices) {
         if (myo.getMacAddress().equals(bluetoothDevice.getAddress())) {

            mMyoCallback = new MyoGattCallback(mHandler, statusText, identifyEmgDataCallback);
            mBluetoothGatt = bluetoothDevice.connectGatt(getActivity(), false, mMyoCallback);
            mMyoCallback.setBluetoothGatt(mBluetoothGatt);
         }
      }

      btnVibrate.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onClickVibration(v);
         }
      });

      btnEMG.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onClickEMG();
         }
      });
      btnStopEmg.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            onClickNoEMG();
         }
      });
   }

   public interface Myo2FragmentEndEventCallback {
      void onEndEvent(List<EmgData> lEmgDataList);

   }

   public List<EmgData> getEmgDataList() {
      return emgDataList;
   }

   public void setEmgDataList(List<EmgData> emgDataList) {
      this.emgDataList = emgDataList;
   }
}
