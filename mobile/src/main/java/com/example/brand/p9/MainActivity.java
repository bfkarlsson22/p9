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


    private int count = 0;
    public GoogleApiClient mGoogleApiClient;
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
    public DataSenderMobile messageSender;
    private Button mButtonNotify;
    String mReplyMessage;
    String mReplyTime = "empty";
    String mReply;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageSender = new DataSenderMobile(mContext);

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


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
               // mReplyMessage = null;
            } else {
                mReplyMessage = extras.getString("message");
                mReplyTime = extras.getString("time");
                mReply = extras.getString("reply");
                Log.d("2222", mReplyMessage+mReplyTime+mReply);
                writeToFB(mReplyMessage, mReplyTime, mReply);
            }
        } else {
            mReplyMessage = (String) savedInstanceState.getSerializable("message");
        }

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

    public void writeToFB(String message, String time, String reply){

        DatabaseReference messageRef = mDatabase.getReference("messages/" +userName); // change userName to partner after dev
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",time);
        messageMap.put("reply",reply);
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

                    if(mReplyMessage !=null){
                        writeToFB(mReplyMessage, mReplyTime, mReply );
                    }
                } else {
                    getGroupMember();
                }
                listenForMsg();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                    if(!mReplyTime.equals(null) && !mReplyTime.equals(time)){

                    messageSender.sendMessage(message, time, reply);}



                   /* if(mReplyTime.equals("empty") ){
                   if(reply.equals("false") || reply.equals("true")){

                    messageSender.sendMessage(message, time, reply);

                    }}else{
                    if(!mReplyTime.equals(time)){
                        messageSender.sendMessage(message, time, reply);
                    }
                    }*/
                }
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

    public void sendSteps(){
        int steps = 1;
        DatabaseReference stepRef = mDatabase.getReference("steps/"+userName);
        stepRef.push().setValue(steps);
    }

    public void sendActiveMinutes(){
        int min = 1;
        DatabaseReference minRef = mDatabase.getReference("active minutes/"+userName);
        minRef.push().setValue(min);
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
