package com.app.projectcapstone.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.projectcapstone.EditDialog;
import com.app.projectcapstone.MainActivity;
import com.app.projectcapstone.MeaningListAdapter;
import com.app.projectcapstone.R;
import com.app.projectcapstone.constant.Constant;
import com.app.projectcapstone.constant.PreferenceConstant;
import com.app.projectcapstone.listener.RequestApiListener;
import com.app.projectcapstone.manager.MyoHolderManager;
import com.app.projectcapstone.manager.PreferenceManager;
import com.app.projectcapstone.model.myo.EmgData;
import com.app.projectcapstone.request.LeftMyoArmband;
import com.app.projectcapstone.request.MyEditData;
import com.app.projectcapstone.request.MyoSignal;
import com.app.projectcapstone.request.RightMyoArmband;
import com.app.projectcapstone.response.Response;
import com.app.projectcapstone.utils.DBUtils;
import com.app.projectcapstone.utils.RequestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuyPH on 2/24/2016.
 */
public class OnlineFragment extends Fragment {
   private static final String TAG = "OnlineFragment";

   private static OnlineFragment instance;

   public static OnlineFragment getInstance() {
      if (instance == null) {
         instance = new OnlineFragment();
      }
      return instance;
   }

   private View view;
   private Button btnUpdateOnline;
   private List<String> listResult;
   private EditDialog editDialog;

   private RecyclerView recyclerView;
   private RecyclerView.LayoutManager layoutManager;
   private MeaningListAdapter meaningListAdapter;

   private List<String> listResultChange = new ArrayList<String>();
   private List<Integer> listPositionChange = new ArrayList<Integer>();

   private EditDialog.EditDialogListener editDialogListener = new EditDialog.EditDialogListener() {
      @Override
      public void updateResult(String inputText, int position) {
         meaningListAdapter.getMeaningList().set(position, inputText);
         listResultChange.add(listResult.get(position));
         listPositionChange.add(position);
         meaningListAdapter.notifyItemChanged(position);

         Log.d(TAG, "updateResult: " + listResult.get(position));

         if (editDialog.isVisible()) {
            editDialog.dismiss();
            //   RequestUtils.getInstance().sendDataChanged(requestApiListenerEdit, listResult.get(position), String.valueOf(position));
         }


      }
   };

   private Myo1Fragment myo1Fragment;
   private Myo2Fragment myo2Fragment;

   private BluetoothAdapter mBluetoothAdapter;
   private Gson gson;

   public static int restConditionLeftHand = 0;
   public static int restConditionRightHand = 0;

   private RightMyoArmband mRightMyoArmband;//cai nay cung la 1 list right emgData
   private List<EmgData> rEmgDataList;
   private LeftMyoArmband mLeftMyoArmband;
   private List<EmgData> lEmgDataList;

   private Button btnDetectOnline;

   public static boolean isCallingAPI = false;
   public static boolean isClickDetect = false;

   private MyoAdapter mAdapter;

   private View.OnClickListener onClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
         FragmentManager fm = getChildFragmentManager();
         int position = recyclerView.getChildLayoutPosition(v);
         editDialog = new EditDialog(listResult.get(position), editDialogListener, position);
         if (!editDialog.isVisible()) {
            editDialog.show(fm, "d");
         }
      }
   };

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.activity_online, container, false);
      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      this.gson = new Gson();

      btnDetectOnline = (Button) view.findViewById(R.id.btn_detectOnline);
      btnUpdateOnline = (Button) view.findViewById(R.id.btn_updateOnline);
      recyclerView = (RecyclerView) view.findViewById(R.id.lv_listResult);

      BluetoothManager mBluetoothManager = (BluetoothManager) getContext().getSystemService(Activity.BLUETOOTH_SERVICE);// run blurooth with low energy
      mBluetoothAdapter = mBluetoothManager.getAdapter();

      btnDetectOnline.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            restConditionLeftHand = 0;
            restConditionRightHand = 0;

            isClickDetect = true;

            myo1Fragment.onClickEMG();
            myo2Fragment.onClickEMG();

            rEmgDataList = new ArrayList<>();
            lEmgDataList = new ArrayList<>();
         }
      });

      btnUpdateOnline.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            String stringResultChange = "";
            String positionResultChange = "";
            int i = 1;
            for (String s : listResultChange) {

               if (i == listResultChange.size()) {
                  stringResultChange += s;
               } else {
                  stringResultChange += s + "-";
               }
               i++;

            }
            Log.d(TAG, "tesst11111111: " + stringResultChange);

            int y = 1;
            for (int s : listPositionChange) {
               if (y == listPositionChange.size()) {
                  positionResultChange += String.valueOf(s);
               } else {
                  positionResultChange += String.valueOf(s) + "-";
               }
               y++;

            }

            if (!TextUtils.isEmpty(stringResultChange) && !TextUtils.isEmpty(positionResultChange)) {
               RequestUtils.getInstance().sendDataChanged(requestApiListenerEdit, stringResultChange, positionResultChange);
            } else {
               Log.w(TAG, "stringEditData: " + stringResultChange);
               Log.w(TAG, "positionEditData: " + positionResultChange);
            }
         }
      });

      if (MyoHolderManager.getInstance().getMyoList().size() < 2) {
         bindingMyoAmrband();
      } else {
         mAdapter = new MyoAdapter(getContext(), 2);
         ListView listView = (ListView) view.findViewById(R.id.lv_list_myo_online);
         listView.setAdapter(mAdapter);
      }


      /*lv_listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentManager fm = getChildFragmentManager();

            editDialog = new EditDialog(listResult.get(position), editDialogListener, position);
            if (!editDialog.isVisible()) {
               editDialog.show(fm, "d");
            }
//            for (int i = 0; i < lv_listResult.getCount(); i++) {
//               Log.d(TAG, "position: "+position);
//               Log.d(TAG, "position: "+i);
//               if (position == i) {
//                  lv_listResult.getChildAt(i).setBackgroundColor(Color.CYAN);
//               } else {
//                  //do nothing
//                  //lv_listResult.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
//               }
//            }
         }
      });*/

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
      ListView listView = (ListView) view.findViewById(R.id.lv_list_myo_online);
      listView.setAdapter(mAdapter);
   }

   private Myo1Fragment.Myo1FragmentEndEventCallback myo1FragmentEndEventCallback = new Myo1Fragment.Myo1FragmentEndEventCallback() {
      @Override
      public void onEndEvent(List<EmgData> rEmgDataList) {
         OnlineFragment.this.rEmgDataList.addAll(rEmgDataList);
         restConditionRightHand++;

         if (restConditionRightHand >= Constant.REST_CONDITION) {//sao ;lai co 12
            mRightMyoArmband = new RightMyoArmband(OnlineFragment.this.rEmgDataList);
            sendRequestArmbandApi();
         }
      }
   };

   private Myo2Fragment.Myo2FragmentEndEventCallback myo2FragmentEndEventCallback = new Myo2Fragment.Myo2FragmentEndEventCallback() {
      @Override
      public void onEndEvent(List<EmgData> lEmgDataList) {
         OnlineFragment.this.lEmgDataList.addAll(lEmgDataList);
         restConditionLeftHand++;

         if (restConditionLeftHand >= Constant.REST_CONDITION) {
            mLeftMyoArmband = new LeftMyoArmband(OnlineFragment.this.lEmgDataList);
            sendRequestArmbandApi();
         }
      }
   };

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
      Log.d(TAG, "restoringPreferences_online: " + MyoHolderManager.getInstance().getMyoList().size());
      if (MyoHolderManager.getInstance().getMyoList() != null && !MyoHolderManager.getInstance().getMyoList().isEmpty()) {
         for (Myo myo1 : MyoHolderManager.getInstance().getMyoList()) {

            mAdapter.setMessage(myo1, "Myo " + identifyMyo(myo1) + " has connected.");
            replaceMyo(myo1);
         }
      }
   }

   @Override
   public void onPause() {
      super.onPause();
   }

   //kiem tra share co key chua
   //chua co thi quet . co roi lay chuoi json.


   @Override
   public void onDestroy() {
      super.onDestroy();
      //  set value key == null;
   }

   private void replaceMyo(Myo myo) {
      if (myo.getMacAddress().equals(MyoHolderManager.getInstance().getMyoList().get(0).getMacAddress())) {
         myo1Fragment = Myo1Fragment.getInstance(myo, mBluetoothAdapter, myo1FragmentEndEventCallback);
         replaceFragment(R.id.ll1_online, myo1Fragment);
      } else {
         myo2Fragment = Myo2Fragment.getInstance(myo, mBluetoothAdapter, myo2FragmentEndEventCallback);
         replaceFragment(R.id.ll2_online, myo2Fragment);
      }
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

   private void sendRequestArmbandApi() {
      Log.d(TAG, "sendRequestArmbandApi1111: " + isCallingAPI);

      if (!isCallingAPI && mLeftMyoArmband != null && mRightMyoArmband != null
            && !mLeftMyoArmband.getLeft().isEmpty() && !mRightMyoArmband.getRight().isEmpty()
            && restConditionLeftHand >= Constant.REST_CONDITION && restConditionRightHand >= Constant.REST_CONDITION) {//<12 thi sao
         int leftSize = mLeftMyoArmband.getLeft().size();
         int rightSize = mRightMyoArmband.getRight().size();
         // int minDataSize = (mLeftMyoArmband.getLeft().size() <= mRightMyoArmband.getRight().size()) ? mLeftMyoArmband.getLeft().size() : mRightMyoArmband.getRight().size();
         int minDataSize = (leftSize <= rightSize) ? leftSize : rightSize;
         Log.d(TAG, "leftSize: " + leftSize);
         Log.d(TAG, "rightSize: " + rightSize);
         Log.d(TAG, "dataSize: " + minDataSize);


         //left nho
//         if (leftSize == minDataSize) {
////            for (int i = minDataSize; i < rightSize; i++) {
////               mRightMyoArmband.getRight().remove(i);
////               rightSize = mRightMyoArmband.getRight().size();
////            }
//            while (minDataSize < rightSize) {
//               mRightMyoArmband.getRight().remove(minDataSize);
//               minDataSize++;
//               rightSize = mRightMyoArmband.getRight().size();
//            }
//
//         }
//
//         if (rightSize == minDataSize) {
////            for (int i = minDataSize; i < leftSize; i++) {
////               Log.d(TAG, "sendRequestArmbandApi12345646: ");
////               mLeftMyoArmband.getLeft().remove(i);
////               leftSize = mLeftMyoArmband.getLeft().size();
////            }
//            while (minDataSize < leftSize) {
//               mLeftMyoArmband.getLeft().remove(minDataSize);
//               minDataSize++;
//               leftSize = mLeftMyoArmband.getLeft().size();
//            }
//
//         }

         if (leftSize < rightSize) {
            int disSize = rightSize - leftSize;
            for (int i = 0; i < disSize; i++) {
               mRightMyoArmband.getRight().remove(rightSize - i - 1);
            }
         } else {
            int disSize = leftSize - rightSize;
            for (int i = 0; i < disSize; i++) {
               mLeftMyoArmband.getLeft().remove(leftSize - i - 1);
            }
         }


         Log.w(TAG, "myoSignalContent size left 111: " + mLeftMyoArmband.getLeft().size() + " | right: " + mRightMyoArmband.getRight().size());
         //   if (mLeftMyoArmband.getLeft().size() == mRightMyoArmband.getRight().size()) {
         MyoSignal myoSignal = new MyoSignal(mLeftMyoArmband, mRightMyoArmband);
         String myoSignalContent = this.gson.toJson(myoSignal);

         if (!TextUtils.isEmpty(myoSignalContent)) {
            RequestUtils.getInstance().sendEmgData(requestApiListener, myoSignalContent);
         } else {
            Log.w(TAG, "myoSignalContent: " + myoSignalContent);
         }
         //  } else {
         //      Toast.makeText(getContext(), "Lam lai di ba", Toast.LENGTH_LONG).show();
         //  }

      }
   }

   private RequestApiListener requestApiListenerEdit = new RequestApiListener() {
      @Override
      public void onRequestDone(Response response) {
         listPositionChange.clear();
         listResultChange.clear();

      }

      @Override
      public void onPrepareRequest() {

      }
   };

   private RequestApiListener requestApiListener = new RequestApiListener() {
      @Override
      public void onRequestDone(Response response) {
         Log.w(TAG, response.toString());
         myo1Fragment.getEmgDataList().clear();
         myo2Fragment.getEmgDataList().clear();
         mLeftMyoArmband = null;
         mRightMyoArmband = null;
         isCallingAPI = false;
         Log.d(TAG, "onRequestDone: " + response.getStringData());
         String data = "['Pham','Pham','Pham']";

         try {
            listResult = gson.fromJson(response.getStringData(), List.class);
         } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Log.e(TAG, "onRequestDone: nogesture data");
            listResult = new ArrayList<>();
         }

         /*arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, listResult);
         lv_listResult.setAdapter(arrayAdapter);*/

         recyclerView.setHasFixedSize(true);

         layoutManager = new LinearLayoutManager(getContext());
         recyclerView.setLayoutManager(layoutManager);

         meaningListAdapter = new MeaningListAdapter(listResult, onClickListener);
         recyclerView.setAdapter(meaningListAdapter);
      }

      @Override
      public void onPrepareRequest() {
         myo1Fragment.onClickNoEMG();
         myo2Fragment.onClickNoEMG();
         isClickDetect = false;
         isCallingAPI = true;
         listResult = new ArrayList<>();
      }
   };


}
