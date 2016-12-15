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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by EmilSiegenfeldt on 15/12/2016.
 */

public class FirebaseListener extends Service {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    LocalStorageMobile localStorageMobile = new LocalStorageMobile(this);
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
                    localStorageMobile.storeData(dataToStore);
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
                localStorageMobile.storeData(dataToStore);
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
    private void listenForSteps(String partnerId) {
        if(!partnerId.equals("")){
            DatabaseReference databaseReference = firebaseDatabase.getReference("STEPS/"+partnerId);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("STEP","STEP");
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

}
