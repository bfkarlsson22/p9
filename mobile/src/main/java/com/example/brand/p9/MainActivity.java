package com.example.brand.p9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private Button btMockPartnerStep;
    private Button btMockUserStep;
    private Button btLogOut;
    Context context = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    LocalStorageMobile localStorageMobile = new LocalStorageMobile(context);

    String userName;
    String UID;
    String partnerID;

    FirebaseWriter firebaseWriter = new FirebaseWriter();


    public DataSenderMobile dataSenderMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSenderMobile = new DataSenderMobile(context);
        userName = localStorageMobile.getSettings().get("USERNAME");
        UID = localStorageMobile.getSettings().get("UID");
        partnerID = localStorageMobile.getSettings().get("PARTNERID");

        startService(new Intent(this, DataReceiverMobile.class));
        startService(new Intent(this, FirebaseListener.class));

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

        btMockPartnerStep = (Button) findViewById(R.id.b_mock_partner_step);
        btMockPartnerStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String partner = "GOdpKr4fhQO7L3j9zLPMas4ViMK2";
                Long time = System.currentTimeMillis();
                firebaseWriter.writeStep(partner,time);

            }
        });

        btMockUserStep = (Button) findViewById(R.id.b_mock_user_step);
        btMockUserStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = "T9veiBXYCgeYlJGgrxndudTZDYG3";
                int id = (int) System.currentTimeMillis();
                Long time = System.currentTimeMillis();
                localStorageMobile.storeStepData(time,id,user);
            }
        });

        //listenForMsg();

    }

    public void listenForMsg(){
        DatabaseReference listenerRef = mDatabase.getReference("messages/" + UID + "/");
        ChildEventListener messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                    String message = dataSnapshot.child("message").getValue().toString();
                    String reply = dataSnapshot.child("reply").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    Log.d("9999", message+reply+time);

                    dataSenderMobile.sendMessage(message, time, reply);}

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                String check = dataSnapshot.getValue().toString();

                    Log.d("5555", "onchildchanged: " + check);
            }}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listenerRef.limitToLast(1).addChildEventListener(messageListener);

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
