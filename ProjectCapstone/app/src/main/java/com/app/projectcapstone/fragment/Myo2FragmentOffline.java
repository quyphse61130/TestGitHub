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
import com.google.gson.Gson;
import com.thalmic.myo.Myo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyPH on 3/11/2016.
 */
public class Myo2FragmentOffline extends MyoBaseFragment {

   public static final String TAG = Myo2FragmentOffline.class.getSimpleName();
   public static Myo2FragmentOffline instance;
   private List<EmgData> emgDataList;
   private Gson gson;
   private Myo2FragmentEndEventCallback myo1FragmentEndEventCallback;

   public Myo2FragmentOffline(Myo myo, BluetoothAdapter mBluetoothAdapter) {
      super(myo, mBluetoothAdapter);
      mHandler = new Handler();
      emgDataList = new ArrayList<>();
   }

   public static Myo2FragmentOffline getInstance(Myo myo, BluetoothAdapter mBluetoothAdapter, Myo2FragmentEndEventCallback myo1FragmentEndEventCallback) {
      if (instance == null) {
         instance = new Myo2FragmentOffline(myo, mBluetoothAdapter);
      }

      instance.gson = new Gson();
      instance.myo1FragmentEndEventCallback = myo1FragmentEndEventCallback;
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

   //ham lay du lieu
   private MyoGattCallback.IdentifyEmgDataCallback identifyEmgDataCallback = new MyoGattCallback.IdentifyEmgDataCallback() {
      @Override
      public void onReceiveText(final byte[] emgDataBytes) {
         EmgCharacteristicData emgCharacteristicData = new EmgCharacteristicData(emgDataBytes);
         EmgData emgData = emgCharacteristicData.getEmg8Data_abs();
         emgDataList.add(emgData);
         Log.w(TAG, "Size right: " + emgDataList.size() + " | " + emgData.toString());

         if (emgData.getSumEmgData() < Constant.DEFAULT_END_EVENT) {
            myo1FragmentEndEventCallback.onEndEvent(emgDataList);
            emgDataList.clear();
         } else {
            OfflineFragment.restConditionRightHand = 0;
         }
      }
   };

   public interface Myo2FragmentEndEventCallback {
      void onEndEvent(List<EmgData> rEmgDataList);

   }

   public List<EmgData> getEmgDataList() {
      return emgDataList;
   }
}
