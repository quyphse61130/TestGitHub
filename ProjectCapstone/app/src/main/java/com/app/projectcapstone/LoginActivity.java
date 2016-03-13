package com.app.projectcapstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.projectcapstone.constant.RequestConstant;
import com.app.projectcapstone.listener.RequestApiListener;
import com.app.projectcapstone.listener.RequestListener;
import com.app.projectcapstone.response.Response;
import com.app.projectcapstone.utils.RequestUtils;

/**
 * Created by QuyPH on 3/11/2016.
 */
public class LoginActivity extends Activity {

   private TextView txtError;
   private EditText etPassword;
   private EditText etUsername;
   private Button btnLogin;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      txtError = (TextView) findViewById(R.id.tv_error);
      etPassword = (EditText) findViewById(R.id.et_password);
      etUsername= (EditText) findViewById(R.id.et_username);
      btnLogin = (Button) findViewById(R.id.btn_login);

      btnLogin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            RequestUtils.getInstance().sendLoginData(requestListener, etUsername.getText().toString(),etPassword.getText().toString());
         }
      });

   }

   private RequestApiListener requestListener= new RequestApiListener() {

      @Override
      public void onPrepareRequest() {


      }

      @Override
      public void onRequestDone(Response response) {
        // Log.d("onRequestDone: ",response.toString());
       //  Log.d("onRequestDone: ",response.getMessage());
         Log.d("onRequestDone: ",response.getStringData());
        // Log.d("onRequestDone: ",response.getStringData());
      boolean loginState= Boolean.parseBoolean(response.getStringData());

         if(loginState){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
         }else{
            txtError.setText("Invalid username or password!!!");
            etPassword.setText("");
         }

      }
   };
   private void showToast(String message) {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
   }


}
