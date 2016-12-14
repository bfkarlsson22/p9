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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends Activity {

    private Button sendDB;
    private Button btLogOut;
    Context mContext = this;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String name;
    String userName;
    String partner;
    public DataSenderMobile dataSenderMobile;
    private Button mButtonNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSenderMobile = new DataSenderMobile(mContext);
        startService(new Intent(this, DataReceiverMobile.class));

        mButtonNotify = (Button) findViewById(R.id.bNotify);
        mButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
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
        sendDB = (Button) findViewById(R.id.b_db);
        sendDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMessage();
            }
        });
        getUserInfoFromFB();
        listenForMsg();


    }

    public void getUserInfoFromFB(){

            userName = mAuth.getCurrentUser().getUid().toString();
            DatabaseReference userRef = mDatabase.getReference("user/" + userName );
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    name = dataSnapshot.child("Name").getValue().toString();
                    partner = dataSnapshot.child("Partner").getValue().toString();
                    Log.d("7777", name+partner);
                    dataSenderMobile.sendUserInfo(userName, partner);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    public void writeMessage(){

        String message = "Good job Brandur, this message stuff is awesome";
        DatabaseReference messageRef = mDatabase.getReference("messages/" +userName); // change userName to partner after dev

        Long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",currentTime);
        messageMap.put("reply","false");
        messageRef.push().setValue(messageMap);

    }
    public void writeMessage2(String message, String reply){
        DatabaseReference messageRef = mDatabase.getReference("messages/" +userName); // change userName to partner after dev
        Long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);

        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",currentTime);
        messageMap.put("reply","false");
        messageRef.push().setValue(messageMap);

    }

    public void writeToFB(String message, String time, String reply){

        DatabaseReference messageRef = mDatabase.getReference("messages/" +userName); // change userName to partner after dev
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",time);
        messageMap.put("reply",reply);
        messageRef.push().setValue(messageMap);
    }

    public void listenForMsg(){
        DatabaseReference listenerRef = mDatabase.getReference("messages/" + userName + "/");
        ChildEventListener messageListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null){
                    String message = dataSnapshot.child("message").getValue().toString();
                    String reply = dataSnapshot.child("reply").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    Log.d("9999", message+reply+time);

                    dataSenderMobile.sendMessage(message, time, reply, userName, partner);}

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

    public void listenForSteps(){
        DatabaseReference stepListenerRef = mDatabase.getReference("steps/" + partner);
        ChildEventListener stepListener = new ChildEventListener() {
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
