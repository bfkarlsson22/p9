package com.example.brand.p9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends Activity {

    private static final String COUNT_KEY = "com.example.key.count";

    private int count = 0;

    private String TAG = "HELLO";
    public GoogleApiClient mGoogleApiClient;
    private Button sendDB;

    private Button btLogOut;
    Context context = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        btLogOut = (Button) findViewById(R.id.btLogOut);
        btLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null){
                    loadLoginActivity();
                }
            }
        };

        mGoogleApiClient = new ApiClientBuilder(context).buildClient();

        sendDB = (Button) findViewById(R.id.b_db);
        sendDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToDB();
            }
        });


    }

    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                DataMap data = DataMapItem.fromDataItem(item).getDataMap();
                Log.d("DATA",data.toString());
                Log.d("DATA ITEM",item.getUri().toString());
                List<String> segments = item.getUri().getPathSegments();
                Log.d("Segments",segments.toString());

                if (item.getUri().getPath().compareTo("/count/") == 0) {
                    count++;
                    Log.d("COUNT", String.valueOf(count));
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    //updateCount(dataMap.getInt(COUNT_KEY));

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void writeToDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("1234");

        myRef.setValue("Hello, World1234!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void loadLoginActivity(){
        Intent intent = new Intent(context,LoginActivity.class);
        startActivity(intent);
    }
}
