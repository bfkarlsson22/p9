package com.example.brand.p9;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class FirebaseWriter {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public FirebaseWriter(){
        //DEFAULT CONSTRUCTOR
    }

    public void writeStep(String user, Long time){
        DatabaseReference databaseReference = firebaseDatabase.getReference("STEPS/"+user);

        HashMap<String, String> stepData= new HashMap<>();
        stepData.put("TIME", String.valueOf(time));
        databaseReference.push().setValue(stepData);
    }
    public void writeLog(String user, HashMap<String, String> logItem){
        DatabaseReference databaseReference = firebaseDatabase.getReference("LOG/"+user);
        databaseReference.push().setValue(logItem);
    }

    public void writeMessage(String message, String reply, String partnerUID){

        Long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);
        DatabaseReference messageRef = firebaseDatabase.getReference("messages/" +partnerUID); // change userName to partner after dev
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",currentTime);
        messageMap.put("reply",reply);
        messageRef.push().setValue(messageMap);
    }
}
