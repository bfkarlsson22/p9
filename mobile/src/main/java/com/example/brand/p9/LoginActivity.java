package com.example.brand.p9;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText mEtId;
    EditText mEtPassword;
    Button btLogin;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtId = (EditText) findViewById(R.id.mEtId);
        mEtPassword = (EditText) findViewById(R.id.mEtPassword);
        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = mEtId.getText().toString();
                String password = mEtPassword.getText().toString();
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("method","login");
                parameters.put("id",id);
                parameters.put("password",password);

                if(id.length() > 0 && password.length() > 0){
                    Log.d("All required","okay");
                    new ServerTask(context, new Callback() {
                        @Override
                        public void serverTaskDone(JSONObject result) {

                        }
                    },parameters).execute("patients");
                } else {
                    Toast.makeText(LoginActivity.this, "One or more inputs are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
