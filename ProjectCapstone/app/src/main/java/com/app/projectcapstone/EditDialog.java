package com.app.projectcapstone;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.projectcapstone.constant.RequestConstant;

/**
 * Created by QuyPH on 3/7/2016.
 */
public class EditDialog extends DialogFragment {

   private EditText etYourTextChange;
   private TextView tvYourText;
   private Button btnSave;
   private Button btnCancel;
   private View view;
   private EditDialogListener editDialogListener;
   private int position;

   private String text;

   public EditDialog(String tvYourText, EditDialogListener editDialogListener, int position) {
      this.text = tvYourText;
      this.editDialogListener = editDialogListener;
      this.position=position;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      view = inflater.inflate(R.layout.fragment_edit, container, false);
      getDialog().setTitle("Edit text");

      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      etYourTextChange = (EditText) view.findViewById(R.id.et_yourtextchange);
      tvYourText = (TextView) view.findViewById(R.id.tv_yourtext);
      btnSave = (Button) view.findViewById(R.id.btn_saveEdit);
      btnCancel = (Button) view.findViewById(R.id.btn_cancelEdit);
      tvYourText.setText("Edit "+this.text+ " into");
      tvYourText.setTextSize(20);
      tvYourText.setTextColor(Color.BLUE);

      btnSave.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            editDialogListener.updateResult(etYourTextChange.getText().toString(), position);
         }
      });
      btnCancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dismiss();
         }
      });
   }


   public interface EditDialogListener {
      void updateResult(String inputText,int position);
   }
}
