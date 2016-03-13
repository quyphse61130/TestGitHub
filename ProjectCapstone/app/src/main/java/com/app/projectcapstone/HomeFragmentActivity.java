package com.app.projectcapstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by QuyPH on 2/24/2016.
 */
public class HomeFragmentActivity extends Fragment {
   private static HomeFragmentActivity instance;

   public static HomeFragmentActivity getInstance() {
      if(instance==null){
         instance= new HomeFragmentActivity();
      }
      return instance;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.activity_home,container,false);
   }


}
