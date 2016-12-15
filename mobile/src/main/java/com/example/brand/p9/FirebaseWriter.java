package com.example.brand.p9;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.R.attr.data;


public class FirebaseWriter {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public FirebaseWriter(){
        //DEFAULT CONSTRUCTOR
    }

    public void writeStep(String user, double value, String unit, Long time){
        DatabaseReference databaseReference = firebaseDatabase.getReference("STEPS/"+user);
        HashMap<String, String> stepData= new HashMap<>();

        stepData.put("UNIT",unit);
        stepData.put("VALUE", String.valueOf(value));
        stepData.put("TIME", String.valueOf(time));
        databaseReference.push().setValue(stepData);
    }

}
