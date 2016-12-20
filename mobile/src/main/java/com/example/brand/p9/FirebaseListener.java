package com.example.brand.p9;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by EmilSiegenfeldt on 15/12/2016.
 */

public class FirebaseListener extends Service {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    LocalStorageMobile localStorageMobile = new LocalStorageMobile(this);
    DataSenderMobile dataSenderMobile = new DataSenderMobile(this);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        if(firebaseAuth.getCurrentUser().getUid() != null) {
            listenForUserData();
            listenForMsg();

        }

    }
    private void listenForUserData(){
            DatabaseReference databaseReference = firebaseDatabase.getReference("USERS/"+firebaseAuth.getCurrentUser().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> dataToStore = new HashMap<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        dataToStore.put(data.getKey(), data.getValue().toString());
                        if (data.getKey().equals("PARTNER") && !data.getValue().toString().equals("")) {
                            listenForPartnerData(true, data.getValue().toString());
                            listenForSteps(data.getValue().toString());
                        } else {
                            listenForPartnerData(false, "");
                            localStorageMobile.deletePartnerSettings();
                        }
                    }
                    dataToStore.put("UID",firebaseAuth.getCurrentUser().getUid());
                    localStorageMobile.storeSettings(dataToStore);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    private void listenForPartnerData(Boolean listen, String partner){
        DatabaseReference databaseReference = firebaseDatabase.getReference("USERS/"+partner);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> dataToStore = new HashMap<>();
                dataToStore.put("PARTNERNAME",dataSnapshot.child("NAME").getValue().toString());
                dataToStore.put("PARTNERGOAL",dataSnapshot.child("GOAL").getValue().toString());

                LocalStorageMobile localStorageMobile = new LocalStorageMobile(getApplicationContext());
                localStorageMobile.storeSettings(dataToStore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if(listen){
            databaseReference.addValueEventListener(eventListener);
        } else {
            databaseReference.removeEventListener(eventListener);
        }
    }

    private void listenForSteps(final String partnerId) {
        if(!partnerId.equals("")){
            DatabaseReference databaseReference = firebaseDatabase.getReference("STEPS/"+partnerId);

            HashMap<String, String> settings = localStorageMobile.getSettings();
            Query query;
            if(settings.get("LATEST STEP") != null){
                query = databaseReference.orderByKey().startAt(settings.get("LATEST STEP"));
            } else {
                query = databaseReference.orderByKey();
            }
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Long time = Long.parseLong(dataSnapshot.child("TIME").getValue().toString());
                    localStorageMobile.updateDaily(partnerId,time,"STEP");

                    HashMap<String, String> latestStep = new HashMap<>();
                    latestStep.put("LATEST STEP",dataSnapshot.getKey());
                    localStorageMobile.storeSettings(latestStep);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public void listenForMsg(){
        DatabaseReference listenerRef = firebaseDatabase.getReference("messages/" + firebaseAuth.getCurrentUser().getUid()+"/");
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





}
