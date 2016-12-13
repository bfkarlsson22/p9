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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends Activity {

    private Button sendDB;
    private Button btLogOut;
    Context mContext = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String uid;
    String email;
    String groups;
    String name;
    String userName;
    String partner;
    HashMap<String, String> userInfo = new HashMap<>();

    public DataSenderMobile messageSender;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageSender = new DataSenderMobile(mContext);

        startService(new Intent(this, DataReceiverMobile.class));

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
                } else {
                    DatabaseReference userRef = mDatabase.getReference("user/"+user.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                userInfo.put(data.getKey(),data.getValue().toString());
                            }
                            listenForSteps();
                            Log.d("USER INFO",userInfo.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        sendDB = (Button) findViewById(R.id.b_db);
        sendDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMessage();

            }
        });
        //getUserInfoFromFB();
        //getGroupMember();

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

        String message = "Good job Brandur, this message stuff is awesome";
        DatabaseReference messageRef = mDatabase.getReference("messages/" + groups + "/"+userName); // change userName to partner after dev

        Long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",currentTime);
        messageMap.put("reply","false");
        messageRef.push().setValue(messageMap);

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
                    listenForSteps();
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
        DatabaseReference listenerRef = mDatabase.getReference("messages/" + groups + "/" + userName + "/");
        ChildEventListener messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){

                 //   HashMap<String, String> messageMap = new HashMap<>();
               //     messageMap = dataSnapshot.getValue();
                    String message = dataSnapshot.child("message").getValue().toString();
                    String reply = dataSnapshot.child("reply").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    Log.d("9999", message+reply+time);


                    if(reply.equals("false")){

                    messageSender.sendMessage(message, time, reply);

                    }}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                String check = dataSnapshot.getValue().toString();
            //        messageSender.sendMessage(check);

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

    public void listenForSteps(){
        Log.d("USER INFO",userInfo.toString());
        //Log.d("PARTNER",userInfo.get("Partner"));
        DatabaseReference stepListenerRef = mDatabase.getReference("steps/"+userInfo.get("Partner"));
        ChildEventListener stepListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("STEP",dataSnapshot.toString());
                Log.d("STEP","STEP");
                //String unit = dataSnapshot.child("UNIT").getValue().toString();
                //String value = dataSnapshot.child("VALUE").getValue().toString();

                //DataSenderMobile dataSenderMobile = new DataSenderMobile(mContext);
                //dataSenderMobile.sendData(unit,value);
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
        };
        stepListenerRef.addChildEventListener(stepListener);

    }

    public void listenForMin(){
        DatabaseReference minListenerRef = mDatabase.getReference("active minutes/" + partner);
        ChildEventListener minListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        };
        minListenerRef.addChildEventListener(minListener);

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
        Intent intent = new Intent(mContext,LoginActivity.class);
        startActivity(intent);
    }

}
