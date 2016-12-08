package com.example.brand.p9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {


    private int count = 0;
    public GoogleApiClient mGoogleApiClient;
    private Button sendDB;
    private Button btLogOut;
    Context context = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String uid;
    String email;
    String groups;
    String name;
    String userName;
    String partner;




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
                writeMessage();

            }
        });
        getUserInfoFromFB();
        getGroupMember();

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

    public void getUserInfoFromFB(){


            String userName1 = mAuth.getCurrentUser().getEmail();
            String parts[] = userName1.split("@");
            String user = parts[0];
            DatabaseReference userRef = mDatabase.getReference("user/" + user);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    name = user.getName();
                    email = user.getEmail();
                    uid = user.getUid();
                    groups = user.getGroups();
                    userName = user.getUsername();
                    Log.d("9999", name + " " + email + " " + uid + " " + groups + " " + userName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    public void writeMessage(){

        String message = "hello";
        DatabaseReference messageRef = mDatabase.getReference("messages/" + groups + "/"+partner);
        messageRef.push().setValue(message);

    }

    public void getGroupMember(){

        DatabaseReference groupRef = mDatabase.getReference("groups/" + groups + "/members/");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String ble = dataSnapshot.getValue().toString();
                    Log.d("9999", ble);
                    HashMap<String, String> groupMembers;
                    groupMembers = (HashMap<String, String>) dataSnapshot.getValue();
                    String m1 = groupMembers.get("m1");
                    String m2 = groupMembers.get("m2");
                    Log.d("9999", m1 + " " + m2);

                    if (userName.equals(m1)) {
                        partner = m2;
                    } else {
                        partner = m1;
                    }
                    Log.d("9999", partner);
                    listenForMsg();
                } else {
                    getGroupMember();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void listenForMsg(){
        DatabaseReference listenerRef = mDatabase.getReference("messages/" + groups + "/" + userName);
        ChildEventListener messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String check = dataSnapshot.getValue(String.class);
                Log.d("8888", "onchildadded: " + check);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String check = dataSnapshot.getValue(String.class);
                Log.d("8888", "onchildchanged: " + check);
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
        };

        listenerRef.addChildEventListener(messageListener);

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
