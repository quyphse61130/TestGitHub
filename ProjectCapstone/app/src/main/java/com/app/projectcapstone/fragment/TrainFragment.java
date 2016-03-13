package com.app.projectcapstone.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.projectcapstone.R;
import com.app.projectcapstone.listener.RequestApiListener;
import com.app.projectcapstone.manager.MyoHolderManager;
import com.app.projectcapstone.model.myo.EmgData;
import com.app.projectcapstone.response.Response;
import com.app.projectcapstone.utils.RequestUtils;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyPH on 2/25/2016.
 */
public class TrainFragment extends Fragment {
   private static TrainFragment instance;

   public static TrainFragment getInstance(ReplaceFragmentCallback replaceFragmentCallback) {
      if (instance == null) {
         instance = new TrainFragment();
      }
      instance.replaceFragmentCallback = replaceFragmentCallback;
      return instance;
   }

   private View view;
   private Button btnDetectTrain;
   private Button btnStartTrain;
   private Button btnSaveTrain;

   private EditText etMeaning;
   private BluetoothAdapter mBluetoothAdapter;


   private ReplaceFragmentCallback replaceFragmentCallback;

   private static final String TAG = "TrainFragment";
   private TrainingMyo1Fragment trainingMyo1Fragment;
   private TrainingMyo2Fragment trainingMyo2Fragment;

   private List<EmgData> rEmgDataList;
   private List<EmgData> lEmgDataList;

   public static boolean isClickDetect = false;
   public static boolean isCallingAPI = false;
   public static boolean isClickSave = false;

   private RequestApiListener requestApiListener = new RequestApiListener() {
      @Override
      public void onRequestDone(Response response) {
         Log.w(TAG, response.getStringData());
         isCallingAPI = false;
      }

      @Override
      public void onPrepareRequest() {
         isClickDetect = false;
         isCallingAPI = true;
      }
   };

   private MyoAdapter mAdapter;

   private int currentConnectedMyo;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.activity_train, container, false);
      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      currentConnectedMyo = 0;

      etMeaning = (EditText) view.findViewById(R.id.et_meaning);
      btnDetectTrain = (Button) view.findViewById(R.id.btn_detectTrain);
      btnStartTrain = (Button) view.findViewById(R.id.btn_start_train);
      btnSaveTrain = (Button) view.findViewById(R.id.btn_saveTrain);

      BluetoothManager mBluetoothManager = (BluetoothManager) getContext().getSystemService(Activity.BLUETOOTH_SERVICE);
      mBluetoothAdapter = mBluetoothManager.getAdapter();
//click Detect
      btnDetectTrain.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            replaceFragmentCallback.moveToOnlineFragment(MyoHolderManager.getInstance().getMyoList());
         }
      });

      //click Start
      btnStartTrain.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            isClickDetect = true;
            isClickSave = false;

            trainingMyo1Fragment.onClickEMG();
            trainingMyo2Fragment.onClickEMG();

            rEmgDataList = new ArrayList<>();
            trainingMyo1Fragment.setEmgDataList(rEmgDataList);

            lEmgDataList = new ArrayList<>();
            trainingMyo2Fragment.setEmgDataList(lEmgDataList);
         }
      });

      //click Save
      btnSaveTrain.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            trainingMyo1Fragment.onClickNoEMG();
            trainingMyo2Fragment.onClickNoEMG();

            rEmgDataList = trainingMyo1Fragment.getEmgDataList();
            lEmgDataList = trainingMyo2Fragment.getEmgDataList();

            sendTrainingData();
         }
      });

      if (MyoHolderManager.getInstance().getMyoList().size() < 2) {
         bindingMyoAmrband();
      } else {
         mAdapter = new MyoAdapter(getContext(), 2);
         ListView listView = (ListView) view.findViewById(R.id.lv_myo_train);
         listView.setAdapter(mAdapter);
      }

   }

   private void bindingMyoAmrband() {
      // First, we initialize the Hub singleton.
      Hub hub = Hub.getInstance();
      if (!hub.init(getContext())) {
         // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
         Toast.makeText(getContext(), "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
         getActivity().finish();
         return;
      }
      // Disable standard Myo locking policy. All poses will be delivered.
      hub.setLockingPolicy(Hub.LockingPolicy.NONE);
      final int attachingCount = 2;
      // Set the maximum number of simultaneously attached Myos to 2.
      hub.setMyoAttachAllowance(attachingCount);
      Log.i(TAG, "Attaching to " + attachingCount + " Myo armbands.");
      // attachToAdjacentMyos() attaches to Myo devices that are physically very near to the Bluetooth radio
      // until it has attached to the provided count.
      // DeviceListeners attached to the hub will receive onAttach() events once attaching has completed.
      hub.attachToAdjacentMyos(attachingCount);
      // Next, register for DeviceListener callbacks.
      hub.addListener(mListener);
      // Attach an adapter to the ListView for showing the state of each Myo.
      mAdapter = new MyoAdapter(getContext(), attachingCount);
      ListView listView = (ListView) view.findViewById(R.id.lv_myo_train);
      listView.setAdapter(mAdapter);
   }

   private DeviceListener mListener = new AbstractDeviceListener() {
      // Every time the SDK successfully attaches to a Myo armband, this function will be called.
      //
      // You can rely on the following rules:
      //  - onAttach() will only be called once for each Myo device
      //  - no other events will occur involving a given Myo device before onAttach() is called with it
      //
      // If you need to do some kind of per-Myo preparation before handling events, you can safely do it in onAttach().
      @Override
      public void onAttach(Myo myo, long timestamp) {
         // The object for a Myo is unique - in other words, it's safe to compare two Myo references to
         // see if they're referring to the same Myo.
         // Add the Myo object to our list of known Myo devices. This list is used to implement identifyMyo() below so
         // that we can give each Myo a nice short identifier.

//         mKnownMyos.add(myo);
//         currentConnectedMyo = mKnownMyos.size();
//         if (currentConnectedMyo == 1) {
//            trainingMyo1Fragment = TrainingMyo1Fragment.getInstance(myo, mBluetoothAdapter);
//            replaceFragment(R.id.ll1, trainingMyo1Fragment);
//         } else if (currentConnectedMyo == 2) {
//            trainingMyo2Fragment = TrainingMyo2Fragment.getInstance(myo, mBluetoothAdapter);
//            replaceFragment(R.id.ll2, trainingMyo2Fragment);
//         }

         MyoHolderManager.getInstance().addMyo(myo);
         if (MyoHolderManager.getInstance().getMyoList() != null && !MyoHolderManager.getInstance().getMyoList().isEmpty()) {
            for (Myo myo1 : MyoHolderManager.getInstance().getMyoList()) {
               mAdapter.setMessage(myo1, "Myo " + identifyMyo(myo1) + " has connected.");
               replaceMyo(myo1);
            }
         }

         // Now that we've added it to our list, get our short ID for it and print it out.
         Log.i(TAG, "Attached to " + myo.getMacAddress() + ", now known as Myo " + identifyMyo(myo) + ".");
      }

      @Override
      public void onConnect(Myo myo, long timestamp) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has connected.");
      }

      @Override
      public void onDisconnect(Myo myo, long timestamp) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " has disconnected.");
      }

      @Override
      public void onPose(Myo myo, long timestamp, Pose pose) {
         mAdapter.setMessage(myo, "Myo " + identifyMyo(myo) + " switched to pose " + pose.toString() + ".");
      }
   };

   @Override
   public void onResume() {
      super.onResume();
      restoringPreferences();
   }

   private void restoringPreferences() {

      /*String knownmyo = PreferenceManager.getInstance().getSharedPreferences().getString(PreferenceConstant.PREF_KNOWN_MYO, null);
      if (!TextUtils.isEmpty(knownmyo)) {*/
      Log.d(TAG, "restoringPreferences_train: " + MyoHolderManager.getInstance().getMyoList().size());
      if (MyoHolderManager.getInstance().getMyoList() != null && !MyoHolderManager.getInstance().getMyoList().isEmpty()) {
         for (Myo myo1 : MyoHolderManager.getInstance().getMyoList()) {
            mAdapter.setMessage(myo1, "Myo " + identifyMyo(myo1) + " has connected.");
            replaceMyo(myo1);
         }
      }
   }

   private void replaceMyo(Myo myo) {
      if (myo.getMacAddress().equals(MyoHolderManager.getInstance().getMyoList().get(0).getMacAddress())) {
         trainingMyo1Fragment = TrainingMyo1Fragment.getInstance(myo, mBluetoothAdapter);
         replaceFragment(R.id.ll1, trainingMyo1Fragment);
      } else {
         trainingMyo2Fragment = TrainingMyo2Fragment.getInstance(myo, mBluetoothAdapter);
         replaceFragment(R.id.ll2, trainingMyo2Fragment);
      }
   }

   public void startTrainingGesture(View view) {
      isClickDetect = true;
      isClickSave = false;

      trainingMyo1Fragment.onClickEMG();
      trainingMyo2Fragment.onClickEMG();

      rEmgDataList = new ArrayList<>();
      trainingMyo1Fragment.setEmgDataList(rEmgDataList);

      lEmgDataList = new ArrayList<>();
      trainingMyo2Fragment.setEmgDataList(lEmgDataList);
   }


   public void saveGesture(View view) {
      trainingMyo1Fragment.onClickNoEMG();
      trainingMyo2Fragment.onClickNoEMG();

      rEmgDataList = trainingMyo1Fragment.getEmgDataList();
      lEmgDataList = trainingMyo2Fragment.getEmgDataList();

      sendTrainingData();
   }

   // This is a utility function implemented for this sample that maps a Myo to a unique ID starting at 1.
   // It does so by looking for the Myo object in mKnownMyos, which onAttach() adds each Myo into as it is attached.
   private int identifyMyo(Myo myo) {
      return MyoHolderManager.getInstance().getMyoList().indexOf(myo) + 1;
   }

   private class MyoAdapter extends ArrayAdapter<String> {
      public MyoAdapter(Context context, int count) {
         super(context, android.R.layout.simple_list_item_1);
         // Initialize adapter with items for each expected Myo.
         for (int i = 0; i < count; i++) {
            add("Waiting");
         }
      }

      public void setMessage(Myo myo, String message) {
         // identifyMyo returns IDs starting at 1, but the adapter indices start at 0.
         int index = identifyMyo(myo) - 1;
         // Replace the message.
         remove(getItem(index));
         insert(message, index);
      }
   }

   private void replaceFragment(int containerResId, Fragment fragment) {
      FragmentManager fragmentManager = getChildFragmentManager();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.replace(containerResId, fragment);
      transaction.commit();
   }

   private void sendTrainingData() {

      if (rEmgDataList != null && lEmgDataList != null
            && !rEmgDataList.isEmpty() && !lEmgDataList.isEmpty()
            && !TextUtils.isEmpty(etMeaning.getText().toString())) {

         String leftData = lEmgDataList.get(lEmgDataList.size() - 1).formatData();
         String rightData = rEmgDataList.get(rEmgDataList.size() - 1).formatData();
         String meaning = etMeaning.getText().toString();
         RequestUtils.getInstance().sendEmgDataTrain(requestApiListener, leftData, rightData, meaning);
      }
   }

   public interface ReplaceFragmentCallback {
      void moveToOnlineFragment(List<Myo> myoList);
   }

}
