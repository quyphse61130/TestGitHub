package com.app.projectcapstone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by QuyPH on 3/13/2016.
 */
public class MeaningListAdapter extends RecyclerView.Adapter<MeaningListAdapter.ViewHolder> {

   private List<String> meaningList;
   private View.OnClickListener onClickListener;

   public MeaningListAdapter(List<String> meaningList, View.OnClickListener onClickListener) {
      this.meaningList = meaningList;
      this.onClickListener = onClickListener;
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
      view.setOnClickListener(onClickListener);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      if (this.meaningList != null && !this.meaningList.isEmpty()) {
         holder.tvMeaning.setText(this.meaningList.get(position));
      }
   }

   @Override
   public int getItemCount() {
      return this.meaningList.size();
   }

   public List<String> getMeaningList() {
      return meaningList;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {

      public TextView tvMeaning;

      public ViewHolder(View itemView) {
         super(itemView);
         this.tvMeaning = (TextView) itemView.findViewById(android.R.id.text1);
      }
   }
}
